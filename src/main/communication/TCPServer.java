package main.communication;

import main.command.ICommandFactory;
import main.command.CommandFactory;
import main.command.CommandHandler;
import main.communication.request.CommandRequest;
import main.communication.request.EntityRequest;
import main.communication.request.FileRequest;
import main.communication.request.UpdateRequest;
import command.CommandResult;
import main.communication.result.EntityResult;
import main.communication.result.FileResult;
import main.communication.result.UpdateResult;
import main.entity.EntityLoader;
import main.util.FileGetter;
import main.util.MyClassLoader;
import util.Serializer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The TCP socket server that accepts requests from the client
 */
public class TCPServer implements Runnable {

    private ICommandFactory ICommandFactory;
    private Boolean shouldRun;

    public TCPServer() {
        this.shouldRun = true;
        this.ICommandFactory = new CommandFactory();
    }

    public void setICommandFactory(ICommandFactory ICommandFactory) {
        this.ICommandFactory = ICommandFactory;
    }

    /**
     * Runs the server including routing different types of requests to the right place
     */
    @Override
    public void run() {
        ServerSocket welcomeSocket;
        try {
            //Initialize the socket
            welcomeSocket = new ServerSocket(6789);
            while (shouldRun) {
                //Accept input and process it
                Socket connectionSocket = welcomeSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                String requestData = inFromClient.readLine();
                ClientBundle clientBundle = Serializer.deserialize(requestData, ClientBundle.class);
                String resultData;

                if(clientBundle.getType() == RequestType.COMMAND) {
                    //If it is a command request then deserialize it accordingly and give it to the ICommandFactory
                    CommandRequest commandRequest = Serializer.deserialize(clientBundle.getSerializedRequest(), CommandRequest.class);
                    CommandHandler handler = new CommandHandler();
                    CommandResult result = handler.handleCommand(commandRequest, ICommandFactory);
                    resultData = Serializer.serialize(result);
                }
                else if(clientBundle.getType() == RequestType.FILE_UPDATE){
                    //If it is a update request then deserialize it accordingly, reload the new class and update it
                    UpdateRequest updateRequest = Serializer.deserialize(clientBundle.getSerializedRequest(), UpdateRequest.class);
                    MyClassLoader loader = new MyClassLoader();
                    UpdateResult result = loader.updateClass(updateRequest);
                    resultData = Serializer.serialize(result);
                }
                else if (clientBundle.getType() == RequestType.ENTITY){
                    //If it is a entity request then deserialize it accordingly, register the entity with the server
                    EntityRequest entityRequest = Serializer.deserialize(clientBundle.getSerializedRequest(), EntityRequest.class);
                    EntityLoader loader = new EntityLoader();
                    EntityResult result = loader.registerEntity(entityRequest);
                    resultData = Serializer.serialize(result);
                }
                else {
                    //If it is a get file request then deserialize it accordingly and get the file contents for the provided command name
                    FileRequest fileRequest = Serializer.deserialize(clientBundle.getSerializedRequest(), FileRequest.class);
                    FileGetter fileGetter = new FileGetter();
                    FileResult result = fileGetter.getFile(fileRequest);
                    resultData = Serializer.serialize(result);
                }
                //Write the result to the client
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                outToClient.writeBytes(resultData + '\n');
            }
            welcomeSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
