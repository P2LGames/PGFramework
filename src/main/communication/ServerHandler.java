package main.communication;

import java.io.*;
import java.net.ServerSocket;

/**
 * The TCP socket server that accepts requests from the client
 */
public class ServerHandler {

    ServerSocket serverSocket = null;

    public ServerHandler() {
        /**
         * I feel like I might need to track individual clients and see when they disconnect so I can clean them up maybe?
         * Or those things can be handled inside of ClientHandler, in which case, all the things should be fine once that thread dies down.
         * That's the cleaner way let's do that.
         */

        try {
            // Initialize the server
            System.out.println("Initializing Socket");
            this.serverSocket = new ServerSocket(6789);

            // Keep trying to connect toc clients!
            while (true) {

                try {
                    // Create a new client out of the connection
                    ClientHandler client = new ClientHandler(this.serverSocket.accept());

                    // Run the client
                    client.run();
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

    public static void main(String[] args) {
        // Start up a new server
        ServerHandler server = new ServerHandler();
    }
}
