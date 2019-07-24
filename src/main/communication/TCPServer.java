package main.communication;

import main.command.GenericCommandHandler;
import main.communication.request.*;
import command.Result;
import main.communication.result.UnknownRequestResult;
import main.entity.EntityLoader;
import main.entity.EntityUpdater;
import main.util.FileGetter;
import main.util.InMemoryClassLoader;
import util.Serializer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The TCP socket server that accepts requests from the client
 */
public class TCPServer implements Runnable {

    private Boolean shouldRun;
    private GenericCommandHandler commandHandler;

    public TCPServer() {
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
            BufferedReader inFromClient = null;
            DataOutputStream outToClient = null;
            try {
                //Initialize the socket
                System.out.println("Initializing Socket");
                welcomeSocket = new ServerSocket(6789);
                //Accept input and process it
                System.out.println("Accepting Connection");
                connectionSocket = welcomeSocket.accept();
                inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                while (shouldRun) {
//                    System.out.println("Reading Input");
                    String requestData = readJsonObject(inFromClient);
//                    System.out.println("Request Data: " + requestData);
                    ClientBundle clientBundle = Serializer.deserialize(requestData, ClientBundle.class);
//
                    Result result;

                    if (clientBundle == null) {
                        continue;
                    }

                    if (clientBundle.getType() == RequestType.COMMAND) {
                        // If it is a command request then deserialize it accordingly and give it to the ICommandFactory
                        CommandRequest commandRequest = Serializer.deserialize(clientBundle.getSerializedData(), CommandRequest.class);
                        result = commandHandler.handleCommand(commandRequest);
                    } else if (clientBundle.getType() == RequestType.ENTITY_REGISTER) {
                        // If it is a entity request then deserialize it accordingly, register the entity with the server
                        EntityRequest entityRequest = Serializer.deserialize(clientBundle.getSerializedData(), EntityRequest.class);
                        EntityLoader loader = new EntityLoader();
                        result = loader.registerEntity(entityRequest);
                    } else if(clientBundle.getType() == RequestType.ENTITY_UPDATE) {
                        System.out.println(clientBundle.getSerializedData());
                        EntityUpdateRequest request = Serializer.deserialize(clientBundle.getSerializedData(), EntityUpdateRequest.class);
                        EntityUpdater updater = new EntityUpdater();
                        result = updater.updateEntity(request);
                    } else if (clientBundle.getType() == RequestType.FILE_GET) {
                        // If it is a get file request then deserialize it accordingly and get the file contents for the provided command name
                        FileRequest fileRequest = Serializer.deserialize(clientBundle.getSerializedData(), FileRequest.class);
                        FileGetter fileGetter = new FileGetter();
                        result = fileGetter.getFile(fileRequest);
                    } else if (clientBundle.getType() == RequestType.FILE_UPDATE) {
                        System.out.println("Request Data: " + requestData);
                        // If it is a update request then deserialize it accordingly, reload the new class and update it
                        UpdateRequest updateRequest = Serializer.deserialize(clientBundle.getSerializedData(), UpdateRequest.class);
                        InMemoryClassLoader loader = new InMemoryClassLoader();
                        result = loader.updateClass(updateRequest);
                    } else {
                        result = new UnknownRequestResult();
                    }

                    // Write the result to the client
                    ClientBundle response = new ClientBundle();
                    String resultData = Serializer.serialize(result);
                    response.setSerializedData(resultData);
                    response.setType(clientBundle.getType());
                    String responseData = Serializer.serialize(response);
//                    System.out.println("Writing Response: " + responseData);
                    outToClient.writeBytes(responseData + '\n');
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
        TCPServer server = new TCPServer();
        server.run();
    }
}
