package test.communication;

import main.communication.command.ClientBundle;
import main.communication.command.CommandResult;
import main.util.Serializer;
import main.communication.TCPServer;
import main.communication.command.CommandRequest;
import org.junit.Test;
import test.testutils.MockFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.junit.Assert.*;

public class TCPServerTest {


    @Test
    public void testConnection() throws Exception {
        TCPServer server = new TCPServer();
        server.setFactory(new MockFactory());
        new Thread(server).start();
        Socket clientSocket = new Socket();
        clientSocket.setSoTimeout(2000);
        clientSocket.connect(new InetSocketAddress("localhost", 6789));
        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setCommand("testCommand");
        commandRequest.setEntityID("testID");
        ClientBundle clientBundle = new ClientBundle();
        clientBundle.setType(ClientBundle.RequestType.COMMAND);
        clientBundle.setSerializedRequest(Serializer.serialize(commandRequest));
        String requestData = Serializer.serialize(clientBundle);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        outToServer.writeBytes(requestData + '\n');
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String resultData = inFromClient.readLine();
        CommandResult actualCommandResult = Serializer.deserialize(resultData, CommandResult.class);
        CommandResult expectedCommandResult = new CommandResult("I can talk!!");
        assertEquals(expectedCommandResult, actualCommandResult);
        server.stop();
        clientSocket.close();
    }
}