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
    private GenericCommandHandler commandHandler;

    public TCPServerBytes() {
        this.shouldRun = true;
        this.commandHandler = new GenericCommandHandler();
    }

    public void setCommandHandler(GenericCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Runs the server including routing different types of requests to the right place
     */
    @Override
    public void run() {

        while (true) {

            ServerSocket welcomeSocket = null;
            Socket connectionSocket = null;
            DataInputStream inFromClient = null;
            DataOutputStream outToClient = null;

            try {
                // Initialize the socket
                System.out.println("Initializing Socket");
                welcomeSocket = new ServerSocket(6789);
                // Accept input and process it
                System.out.println("Accepting Connection");
                connectionSocket = welcomeSocket.accept();
                inFromClient = new DataInputStream(connectionSocket.getInputStream());
//                inFromClient = new Scanner(new InputStreamReader(connectionSocket.getInputStream()));
                outToClient = new DataOutputStream(connectionSocket.getOutputStream());

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
//                    } else if (clientBundle.getType() == RequestType.FILE_UPDATE) {
//                        System.out.println("Request Data: " + requestData);
//                        // If it is a update request then deserialize it accordingly, reload the new class and update it
//                        UpdateRequest updateRequest = Serializer.deserialize(clientBundle.getSerializedData(), UpdateRequest.class);
//                        InMemoryClassLoader loader = new InMemoryClassLoader();
//                        result = loader.updateClass(updateRequest);
//                    } else {
//                        result = new UnknownRequestResult();
//                    }
//
//                    // Write the result to the client
//                    ClientBundle response = new ClientBundle();
//                    String resultData = Serializer.serialize(result);
//                    response.setSerializedData(resultData);
//                    response.setType(clientBundle.getType());
//                    String responseData = Serializer.serialize(response);
////                    System.out.println("Writing Response: " + responseData);
//                    outToClient.writeBytes(responseData + '\n');

                }
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
            break;
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
        TCPServerBytes server = new TCPServerBytes();
        server.run();
    }
}
