package main.communication;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * The TCP socket server that accepts requests from the client
 */
public class ServerHandler implements Runnable {

    public static int PORT = 5545;

    boolean running = true;
    ServerSocket serverSocket = null;

    ArrayList<ClientHandler> handlers = new ArrayList<>();

    public ServerHandler() {}

    @Override
    public void run() {
        try {
            // Initialize the server
            System.out.println("ServerHandler: " + PORT);
            this.serverSocket = new ServerSocket(PORT);

            // Keep trying to connect toc clients!
            while (running && !this.serverSocket.isClosed()) {

                // Loop through the handlers and remove the disconnected ones
                for (int i = handlers.size() - 1; i > 0; i--) {
                    if (!handlers.get(i).isRunning()) {
                        handlers.remove(i);
                    }
                }

                System.out.println("Current Client Count: " + handlers.size());

                try {
                    System.out.println("Accepting Connection");

                    // Create and run a new client using an accepted connection
                    ClientHandler client = new ClientHandler(this.serverSocket.accept());
                    client.start();

                    handlers.add(client);
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
