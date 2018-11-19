package main.communication;

import main.command.CommandFactory;
import main.command.Factory;
import main.communication.command.*;
import main.command.CommandHandler;
import main.util.MyClassLoader;
import main.util.Serializer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The TCP socket server that accepts requests from the client
 */
public class TCPServer implements Runnable {

    private Factory factory;
    private Boolean shouldRun;

    public TCPServer() {
        this.shouldRun = true;
        this.factory = new CommandFactory();
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
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

                if(clientBundle.getType() == ClientBundle.RequestType.COMMAND) {
                    //If it is a command data then deserialize it accordingly and give it to the factory
                    CommandRequest commandRequest = Serializer.deserialize(clientBundle.getSerializedRequest(), CommandRequest.class);
                    CommandHandler handler = new CommandHandler();
                    CommandResult result = handler.handleCommand(commandRequest, factory);
                    resultData = Serializer.serialize(result);
                }
                else {
                    //If it is a update data then deserialize it accordingly, reload the new class and update it
                    UpdateRequest updateRequest = Serializer.deserialize(clientBundle.getSerializedRequest(), UpdateRequest.class);
                    MyClassLoader loader = new MyClassLoader();
                    UpdateResult result = loader.updateClass(updateRequest);
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
