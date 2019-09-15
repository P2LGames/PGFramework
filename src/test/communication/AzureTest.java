package test.communication;

import main.communication.ServerHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.ByteManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AzureTest {

    @Test
    public void testIndividualConnection() {

        try {
            // Create a client socket
            Socket clientSocket = new Socket();

            // Set it's timeout
            clientSocket.setSoTimeout(2000);

            // Connect to the server
            clientSocket.connect(new InetSocketAddress("pgframework.westus.azurecontainer.io", ServerHandler.PORT));

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
    public void testMultipleConnections() {
        /**
         * What am I trying to test here? That I can connect to the server using the address and port, and get a valid response.
         * How do I do that? Create a socket, and connect that socket to the server, it will create a client handler for me.
         * Further testing: Pass in byte data and test sample response
         */
        try {
            // Keep track of the clients and their in and output streams
            ArrayList<Socket> clients = new ArrayList<>();
            ArrayList<DataOutputStream> outs = new ArrayList<>();
            ArrayList<DataInputStream> ins = new ArrayList<>();

            for (int i = 0; i < 100; i++) { // 1 + 3 minimum threads per user, so 301 threads
                // Create a client socket
                Socket clientSocket = new Socket();

                // Set its timeout
                clientSocket.setSoTimeout(2000);

                // Connect to the server
                clientSocket.connect(new InetSocketAddress("pgframework.westus.azurecontainer.io", ServerHandler.PORT));

                // Write a byte to the framework
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                outToServer.write(ByteManager.convertIntToByteArray(-1));

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
            e.printStackTrace();
            Assert.fail();
        }
    }





}