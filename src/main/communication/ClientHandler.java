package main.communication;

import entity.GenericEntityMap;
import main.command.CommandHandler;
import main.command.CommandThreadMonitor;
import main.entity.EntityLoader;
import main.entity.EntitySetup;
import main.entity.EntityTypeMap;
import main.util.InMemoryClassLoader;
import util.ByteManager;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientHandler {

    private final ServerHandler handler;

    private Boolean running = true;

    // Sockets and I/O
    private Socket connectionSocket;
    private DataInputStream inFromClient;
    private DataOutputStream outToClient;
    private int requestType;
    private int byteCount;
    private byte[] buffer = new byte[1024 * 100];
    private ClientReader reader;
    private ClientWriter writer;

    // Our command thread monitor
    public CommandThreadMonitor monitor;

    // Mutex locking on the bytes to send to the server
    private volatile boolean writingToOut = false;

    public ClientHandler(ServerHandler handler, Socket connection) {
        // Save the connection socket
        this.handler = handler;
        this.connectionSocket = connection;

        try {
            // TCP no delay means it should send packets without waiting for additional ones
            this.connectionSocket.setTcpNoDelay(true);

            // Create our command thread monitor
            monitor = new CommandThreadMonitor(this);

            // Create a new reader and writer using the socket
            reader = new ClientReader(this, new DataInputStream(this.connectionSocket.getInputStream()));
            writer = new ClientWriter(this, new DataOutputStream(this.connectionSocket.getOutputStream()));

            // Start up all our different threads
            monitor.start();
            reader.start();
            writer.start();
        }
        catch (IOException e) {
            System.out.println("Error seting up the socket for reading and writing:");
            e.printStackTrace();
        }
    }

    /**
     * Write data to the client in the next send frame
     * @param data A byte array to be sent
     */
    public void write(byte[] data) {
        writer.write(data);
    }

    public void endProcess() {
        // Close the connection to the socket
        try {
            if (connectionSocket != null) { connectionSocket.close(); }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Stop the server from running, this will eventually kill the threads
        running = false;

        // Kill each of our threads
        monitor.endProcess();
        reader.endProcess();
        writer.endProcess();
    }

    public boolean isRunning() {
        return running;
    }

}
