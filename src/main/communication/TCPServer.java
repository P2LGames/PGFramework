package main.communication;

import main.command.CommandRouter;
import main.command.Router;
import main.communication.command.*;
import main.util.CommandHandler;
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

    private Router router;
    private Boolean shouldRun;

    public TCPServer() {
        this.shouldRun = true;
        this.router = new CommandRouter();
    }

    public void setRouter(Router router) {
        this.router = router;
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
                    //If it is a command data then deserialize it accordingly and give it to the router
                    CommandRequest commandRequest = Serializer.deserialize(clientBundle.getSerializedRequest(), CommandRequest.class);
                    CommandHandler handler = new CommandHandler();
                    CommandResult result = handler.handleCommand(commandRequest, router);
                    resultData = Serializer.serialize(result);
                }
                else {
                    //If it is a update data then deserialize it accordingly, reload the new class and update it
                    UpdateRequest updateRequest = Serializer.deserialize(clientBundle.getSerializedRequest(), UpdateRequest.class);
                    ClassLoader parentClassLoader = MyClassLoader.class.getClassLoader();
                    MyClassLoader loader = new MyClassLoader(parentClassLoader);
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

    public static void main(String[] args) throws Exception {
        TCPServer server = new TCPServer();
        server.run();
    }
}
