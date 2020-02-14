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
    public static String VERSION = "1.2";

    boolean running = true;
    ServerSocket serverSocket = null;

    ArrayList<ClientHandler> handlers = new ArrayList<>();
//    ClientHandler client;
    int handlerCount = 0;

    public ServerHandler() {}

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
//                    String IP = clientSocket.getRemoteSocketAddress().toString();
//
//                    System.out.println(IP);
//                    if (IP.contains("10.240.255.56") || IP.contains("10.240.255.55")) {
//                        continue;
//                    }

                    ClientHandler client = new ClientHandler(this, clientSocket);
                    client.start();

                    handlers.add(client);

                    // Loop through the handlers and remove the disconnected ones
                    for (int i = handlers.size() - 1; i > 0; i--) {
                        if (!handlers.get(i).isRunning()) {
                            handlers.remove(i);
                        }
                    }

                    if (handlerCount != handlers.size()) {
                        handlerCount = handlers.size();

                        System.out.println("Current Client Count: " + handlers.size());
                    }

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
