package main.communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The TCP socket server that accepts requests from the client
 */
public class ServerHandler implements Runnable {

    public static int PORT = 5545;
    public static String VERSION = "1.4";

    boolean running = true;
    ServerSocket serverSocket = null;

    volatile boolean handlersLock = false;
    public volatile ArrayList<ClientHandler> handlers = new ArrayList<>();
    int handlerCount = 0;

    public ServerHandler() {
        // Start the terminate handler
        TerminateHandler terminateHandler = new TerminateHandler(this);
        terminateHandler.start();
    }

    @Override
    public void run() {
        try {
            // Initialize the server
            System.out.println("ServerHandler V" + VERSION + ": " + PORT);
            this.serverSocket = new ServerSocket(PORT);

            // Keep trying to connect toc clients!
            while (running && !this.serverSocket.isClosed()) {

                try {
                    // Create and run a new client using an accepted connection
                    Socket clientSocket = this.serverSocket.accept();

                    ClientHandler client = new ClientHandler(this, clientSocket);
                    client.start();

                    handlersLock = true;
                    handlers.add(client);
                    handlersLock = false;

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            System.out.println("Server Handler Error");
            e.printStackTrace();
        }
        // Cleanup
        finally {
            // Close all sockets when we finish
            try {
                if (this.serverSocket != null) { this.serverSocket.close(); }

                TerminateHandler.TerminateServer();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        // Stop the server from running
        this.running = false;

        try {
            // Close the server connection
            if (this.serverSocket != null) { this.serverSocket.close(); }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // Start up a new server
        new ServerHandler().run();
    }
}
