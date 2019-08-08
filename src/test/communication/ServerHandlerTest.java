package test.communication;

import command.CommandResult;
import main.command.GenericCommandHandler;
import main.communication.*;
import main.communication.request.CommandRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.ByteManager;
import util.Serializer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServerHandlerTest {

    ServerHandler server;

    @Before
    public void setup() {
        // Sets up and starts a server
        server = new ServerHandler();
        new Thread(server).start();
    }

    @After
    public void tearDown() {
        // Stop the server
        server.stop();
    }

    @Test
    public void testConnection() {
        /**
         * What am I trying to test here? That I can connect to the server using the address and port, and get a valid response.
         * How do I do that? Create a socket, and connect that socket to the server, it will create a client handler for me.
         * Further testing: Pass in byte data and test sample response
         */
        try {
            // Create a client socket
            Socket clientSocket = new Socket();

            // Set it's timeout
            clientSocket.setSoTimeout(2000);

            // Connect to the server
            clientSocket.connect(new InetSocketAddress("localhost", ServerHandler.PORT));

            // Write a byte to the framework
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.write(ByteManager.convertIntToByteArray(-1));
//            outToServer.writeBytes();

            // Read the data from the server
            DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
            int fromServer = inFromServer.readInt();

            // Asset that we get the unrecognized value
            assertEquals(-1, fromServer);

            // Close all connections
            clientSocket.close();
            outToServer.close();
            inFromServer.close();
        }
        catch(Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            Assert.fail();
        }
    }

    @Test
    public void multipleConnections() {
        /**
         * What am I trying to test here? That I can connect to the server using the address and port, and get a valid response.
         * How do I do that? Create a socket, and connect that socket to the server, it will create a client handler for me.
         * Further testing: Pass in byte data and test sample response
         */
        try {

            ArrayList<Socket> clients = new ArrayList<>();
            ArrayList<DataOutputStream> outs = new ArrayList<>();
            ArrayList<DataInputStream> ins = new ArrayList<>();

            for (int i = 0; i < 1000; i++) {
                // Create a client socket
                Socket clientSocket = new Socket();

                // Set its timeout
                clientSocket.setSoTimeout(2000);

                // Connect to the server
                clientSocket.connect(new InetSocketAddress("localhost", ServerHandler.PORT));

                // Write a byte to the framework
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                byte[] bytes = new byte[] { 0, 10 };
//                System.out.println(bytes.length);
                outToServer.write(bytes);
//            outToServer.writeBytes();

                // Read the data from the server
                DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
                int fromServer = inFromServer.readInt();

                // Asset that we get the unrecognized value
                assertEquals(-1, fromServer);

                // Save the socket and in and outs so we can close them late
                clients.add(clientSocket);
                outs.add(outToServer);
                ins.add(inFromServer);
            }

            // Close all connections
            for (Socket s: clients) {
                s.close();
            }
            for (DataOutputStream o: outs) {
                o.close();
            }
            for (DataInputStream i: ins) {
                i.close();
            }

        }
        catch(Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            Assert.fail();
        }
    }

}