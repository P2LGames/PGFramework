package main.communication;

import command.Result;
import main.command.GenericCommandHandler;
import main.communication.request.*;
import main.communication.result.UnknownRequestResult;
import main.entity.EntityLoader;
import main.entity.EntitySetup;
import main.entity.EntityTypeMap;
import main.entity.EntityUpdater;
import main.util.FileGetter;
import main.util.InMemoryClassLoader;
import util.Serializer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * The TCP socket server that accepts requests from the client
 */
public class TCPServerBytes implements Runnable {

    private Boolean shouldRun;

    private DataInputStream inFromClient;
    private DataOutputStream outToClient;

    public TCPServerBytes(DataInputStream in, DataOutputStream out) {
        this.shouldRun = true;

        // Setup the in and out of the client connection
        this.inFromClient = in;
        this.outToClient = out;
    }

    /**
     * Runs the server including routing different types of requests to the right place
     */
    @Override
    public void run() {

        DataInputStream inFromClient = null;
        DataOutputStream outToClient = null;

        try {

            while (shouldRun) {

                // Read in the next byte from the client the int returned will be between 0-255
                int requestType = inFromClient.readShort();
//                    System.out.println(requestType);
                // Read in the byte count as an int from the client
                int byteCount = inFromClient.readInt();
//                    System.out.println(byteCount);

                // Create a byte array with the number of bytes to read
                byte[] bytes = new byte[byteCount];

                // Read the bytes
                inFromClient.read(bytes);

                // The byte result
                byte[] result = new byte[]{};

                // Setup the entity
                if (requestType == RequestType.ENTITY_SETUP.getNumVal()) {
                    // Get the result from the entity setup class
                    result = new EntitySetup().setupEntitiesWithBytes(bytes);
                }
                // If we want to register an entity, do so.
                else if (requestType == RequestType.ENTITY_REGISTER.getNumVal()) {
                    result = new EntityLoader().registerEntity(bytes);
                }
                // If we want to run a command, do so.
                else if (requestType == RequestType.COMMAND.getNumVal()) {
                    result = new GenericCommandHandler().handleCommand(bytes);
                }
                // If we want to update a file in the framework, do so.
                else if (requestType == RequestType.FILE_UPDATE.getNumVal()) {
                    result = new InMemoryClassLoader().updateClass(bytes);
                }

                // If the result has data to write, write it
                if (result.length > 0) {
                    outToClient.write(result);
                }
//                    } else if(clientBundle.getType() == RequestType.ENTITY_UPDATE) {
//                        System.out.println(clientBundle.getSerializedData());
//                        EntityUpdateRequest request = Serializer.deserialize(clientBundle.getSerializedData(), EntityUpdateRequest.class);
//                        EntityUpdater updater = new EntityUpdater();
//                        result = updater.updateEntity(request);
//                    } else if (clientBundle.getType() == RequestType.FILE_GET) {
//                        // If it is a get file request then deserialize it accordingly and get the file contents for the provided command name
//                        FileRequest fileRequest = Serializer.deserialize(clientBundle.getSerializedData(), FileRequest.class);
//                        FileGetter fileGetter = new FileGetter();
//                        result = fileGetter.getFile(fileRequest);
//                    }

            }

            // If we got here, then the connection closed
            welcomeSocket.close();

        }
        catch (IOException e) {
            e.printStackTrace();
            // Try to close sockets, clean up connections
            try {
                if (inFromClient != null) { inFromClient.close(); }
                if (outToClient != null) { outToClient.close(); }
                if (connectionSocket != null) { connectionSocket.close(); }
                if (welcomeSocket != null) { welcomeSocket.close(); }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        catch (Exception e) {
            System.out.println("Failed to read");
            e.printStackTrace();
        }

        while (true) {


        }
    }

    public String readJsonObject(BufferedReader in) throws IOException {
        StringBuilder jsonObj = new StringBuilder();

        int bracketCount = 0;
        int quoteCount = 0;

        while (shouldRun) {
            // Read in the next character
            char received = (char)in.read();

            // If we are in a bracket, append it, otherwise, only append if it is an opening bracket
            if (bracketCount > 0 || received == '{') {
                // Add the received to the json obj string
                jsonObj.append(received);
            }


            // If we received only a new line, continue
            if (received == '\n') {
                continue;
            }

            if (received == '{' && quoteCount == 0) { bracketCount++; }
            else if (received == '}' && quoteCount == 0) {
                bracketCount--;

                // If the bracket count it 0, stop! =)
                if (bracketCount == 0 && !jsonObj.toString().equals("\n")) {
//                    System.out.println("New JSON: " + jsonObj.toString());
//                    return jsonObj.toString();s
                    break;
                }
            }
//            else if (received == '"') {
//
//                if (quoteCount == 0) { quoteCount = 1; }
//                else { quoteCount = 0; }
//            }


        }

        return jsonObj.toString();
    }

    /**
     * Stops the server
     */
    public void stop() {
        this.shouldRun = false;
    }

    public static void main(String[] args) {
        while (true) {
            ServerSocket welcomeSocket = null;
            Socket connectionSocket = null;

            // Initialize the socket
            System.out.println("Initializing Socket");
            welcomeSocket = new ServerSocket(6789);

            // Accept input and process it
            System.out.println("Accepting Connection");
            connectionSocket = welcomeSocket.accept();
        }

        TCPServerBytes server = new TCPServerBytes();
        server.run();
    }
}
