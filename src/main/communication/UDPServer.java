package main.communication;

import command.Result;
import main.command.GenericCommandHandler;
import main.communication.request.CommandRequest;
import main.communication.request.EntityUpdateRequest;
import main.entity.EntityUpdater;
import util.Serializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * The UDP server for handling non-essential frequent command or other update requests
 */
public class UDPServer {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private GenericCommandHandler commandHandler;

    public UDPServer() throws SocketException {
        socket = new DatagramSocket(4445);
        commandHandler = new GenericCommandHandler();
    }

    public void setCommandHandler(GenericCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void run() throws IOException {
        running = true;

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received = new String(packet.getData(), 0, packet.getLength());

            ClientBundle clientBundle = Serializer.deserialize(received, ClientBundle.class);
            if(clientBundle == null) {
                continue;
            }

            Result result;

            if(clientBundle.getType() == RequestType.COMMAND) {
                CommandRequest request = Serializer.deserialize(received, CommandRequest.class);
                result = commandHandler.handleCommand(request);
            }
            else if(clientBundle.getType() == RequestType.ENTITY_UPDATE) {
                EntityUpdateRequest request = Serializer.deserialize(received, EntityUpdateRequest.class);
                EntityUpdater updater = new EntityUpdater();
                result = updater.updateEntity(request);
            }
            else {
                System.out.println("Bad request type for UDP server");
                continue;
            }
            ClientBundle response = new ClientBundle();
            String resultString = Serializer.serialize(result);
            clientBundle.setSerializedRequest(resultString);
            clientBundle.setType(clientBundle.getType());
            String responseString = Serializer.serialize(response);
            byte[] resultData = responseString.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(resultData, resultData.length);

            socket.send(responsePacket);
        }
        socket.close();
    }
}
