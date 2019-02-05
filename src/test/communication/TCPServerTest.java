package test.communication;

import main.command.CommandHandler;
import main.communication.ClientBundle;
import main.communication.RequestType;
import command.CommandResult;
import main.communication.TCPServer;
import main.communication.request.CommandRequest;
import org.junit.Test;
import util.Serializer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class TCPServerTest {


    @Test
    public void testConnection() throws Exception {
        //Mock the command factory
        CommandHandler handler = mock(CommandHandler.class);
        CommandResult expectedCommandResult = new CommandResult("I can talk!!");
        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setCommand("testCommand");
        commandRequest.setEntityId("testID");
        commandRequest.setHasParameter(false);
        when(handler.handleCommand(commandRequest)).thenReturn(expectedCommandResult);


        //Create a new TCP server
        TCPServer server = new TCPServer();
        server.setCommandHandler(handler);
        new Thread(server).start();
        Socket clientSocket = new Socket();
        clientSocket.setSoTimeout(2000);
        clientSocket.connect(new InetSocketAddress("localhost", 6789));

        //Create the data and write it over
        ClientBundle clientBundle = new ClientBundle();
        clientBundle.setType(RequestType.COMMAND);
        clientBundle.setSerializedRequest(Serializer.serialize(commandRequest));
        String requestData = Serializer.serialize(clientBundle);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        outToServer.writeBytes(requestData + "\n");

        //Read the data from the server
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String resultData = inFromServer.readLine();

        //Verify correctness
        CommandResult actualCommandResult = Serializer.deserialize(resultData, CommandResult.class);
        assertEquals(expectedCommandResult, actualCommandResult);
        server.stop();
        clientSocket.close();
    }
}