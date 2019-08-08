package test.communication;

import main.command.GenericCommandHandler;
import main.communication.ClientBundle;
import main.communication.RequestType;
import command.CommandResult;
import main.communication.TCPServer;
import main.communication.request.CommandRequest;
import org.junit.Assert;
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
    public void testConnection() {
        try {
            //Mock the command factory
            GenericCommandHandler handler = mock(GenericCommandHandler.class);
            CommandResult expectedCommandResult = new CommandResult("I can talk!!", "testID");
            CommandRequest commandRequest = new CommandRequest();
            commandRequest.setCommand("testCommand");
            commandRequest.setEntityId("testID");
            ClientBundle expectedClientBundle = new ClientBundle();
            expectedClientBundle.setSerializedData(Serializer.serialize(expectedCommandResult));
            expectedClientBundle.setType(RequestType.COMMAND);
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
            clientBundle.setSerializedData(Serializer.serialize(commandRequest));
            String requestData = Serializer.serialize(clientBundle);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes(requestData + "\n");

            //Read the data from the server
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String resultData = inFromServer.readLine();

            //Verify correctness
            ClientBundle actualClientBundle = Serializer.deserialize(resultData, ClientBundle.class);
            assertEquals(expectedClientBundle, actualClientBundle);
            server.stop();
            clientSocket.close();
        } catch(Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            Assert.fail();
        }
    }
}