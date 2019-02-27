package main.communication;

import main.command.CommandHandler;
import main.communication.request.CommandRequest;
import main.communication.request.EntityRequest;
import main.communication.request.FileRequest;
import main.communication.request.UpdateRequest;
import command.Result;
import main.communication.result.UnknownRequestResult;
import main.entity.EntityLoader;
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
    private CommandHandler commandHandler;

    public TCPServer() {
        this.shouldRun = true;
        this.commandHandler = new CommandHandler();
    }

    public void setCommandHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Runs the server including routing different types of requests to the right place
     */
    @Override
    public void run() {
        ServerSocket welcomeSocket;
        try {
            //Initialize the socket
            System.out.println("Initializing Socket");
            welcomeSocket = new ServerSocket(6789);
            //Accept input and process it
            System.out.println("Accepting Connection");
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            while (shouldRun) {
                System.out.println("Reading Input");
                /*String requestData = */ readJsonObject(inFromClient);
//                System.out.println("Request Data: " + requestData);
//                ClientBundle clientBundle = Serializer.deserialize(requestData, ClientBundle.class);
//
//                Result result;
//
//                if(clientBundle == null) {
//                    continue;
//                }
//                if(clientBundle.getType() == RequestType.COMMAND) {
//                    // If it is a command request then deserialize it accordingly and give it to the ICommandFactory
//                    CommandRequest commandRequest = Serializer.deserialize(clientBundle.getSerializedRequest(), CommandRequest.class);
//                    result = commandHandler.handleCommand(commandRequest);
//                }
//                else if(clientBundle.getType() == RequestType.FILE_UPDATE) {
//                    // If it is a update request then deserialize it accordingly, reload the new class and update it
//                    UpdateRequest updateRequest = Serializer.deserialize(clientBundle.getSerializedRequest(), UpdateRequest.class);
//                    InMemoryClassLoader loader = new InMemoryClassLoader();
//                    result = loader.updateClass(updateRequest);
//                }
//                else if (clientBundle.getType() == RequestType.ENTITY) {
//                    // If it is a entity request then deserialize it accordingly, register the entity with the server
//                    EntityRequest entityRequest = Serializer.deserialize(clientBundle.getSerializedRequest(), EntityRequest.class);
//                    EntityLoader loader = new EntityLoader();
//                    result = loader.registerEntity(entityRequest);
//                }
//                else if (clientBundle.getType() == RequestType.FILE_GET) {
//                    // If it is a get file request then deserialize it accordingly and get the file contents for the provided command name
//                    FileRequest fileRequest = Serializer.deserialize(clientBundle.getSerializedRequest(), FileRequest.class);
//                    FileGetter fileGetter = new FileGetter();
//                    result = fileGetter.getFile(fileRequest);
//                }
//                else {
//                    result = new UnknownRequestResult();
//                }
//
//                // Write the result to the client
//                String resultData = Serializer.serialize(result);
//                System.out.println("Writing Response: " + resultData);
//                outToClient.writeBytes(resultData + '\n');
            }
            welcomeSocket.close();
        } catch (Exception e) {
            System.out.println("Failed to read");
            e.printStackTrace();
        }
    }

    public void readJsonObject(BufferedReader in) throws IOException {
        StringBuilder jsonObj = new StringBuilder();

        int bracketCount = 0;
        int quoteCount = 0;

        while (true) {
            // Read in the next line
            char received = (char)in.read();

            // Add the received to the json obj string
            jsonObj.append(received);

            // If we received only a new line, continue
            if (received == '\n') {
                continue;
            }

            if (received == '{' && quoteCount == 0) { bracketCount++; }
            else if (received == '}' && quoteCount == 0) {
                bracketCount--;

                // If the bracket count it 0, stop! =)
                if (bracketCount == 0 && !jsonObj.toString().equals("\n")) {
                    System.out.println("New JSON: " + jsonObj.toString());
                    jsonObj = new StringBuilder();
//                break;
                }
            }
//            else if (received == '"') {
//
//                if (quoteCount == 0) { quoteCount = 1; }
//                else { quoteCount = 0; }
//            }


        }

//        return jsonObj;
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
