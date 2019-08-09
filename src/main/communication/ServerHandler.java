package main.communication;

import java.io.*;
import java.net.ServerSocket;

/**
 * The TCP socket server that accepts requests from the client
 */
public class ServerHandler implements Runnable {

    public static int PORT = 6789;


    boolean running = true;
    ServerSocket serverSocket = null;

    public ServerHandler() {}

    @Override
    public void run() {
        try {
            // Initialize the server
            System.out.println("Initializing Socket: ServerHandler");
            this.serverSocket = new ServerSocket(PORT);

            // Keep trying to connect toc clients!
            while (running) {

                try {
                    System.out.println("Accepting Connection");
                    // Create and run a new client using an accepted connection
                    new ClientHandler(this.serverSocket.accept()).start();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // Cleanup
        finally {
            // Close all sockets when we finish
            try {
                if (this.serverSocket != null) { this.serverSocket.close(); }
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
