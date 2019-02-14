package main.communication;

import command.Result;
import main.command.GenericCommandHandler;
import main.communication.request.CommandRequest;
import util.Serializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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

            CommandRequest request = Serializer.deserialize(received, CommandRequest.class);
            Result result = commandHandler.handleCommand(request);
            String resultString = Serializer.serialize(result);
            byte[] resultData = resultString.getBytes();
            DatagramPacket response = new DatagramPacket(resultData, resultData.length);

            socket.send(response);
        }
        socket.close();
    }
}
