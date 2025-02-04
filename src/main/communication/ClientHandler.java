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

public class ClientHandler extends Thread {

    private final ServerHandler handler;

    private GenericEntityMap entityMap = new GenericEntityMap();
    private EntityTypeMap entityTypeMap = new EntityTypeMap();

    private Boolean running = true;

    // Sockets and I/O
    private Socket connectionSocket;
    private DataInputStream inFromClient;
    private DataOutputStream outToClient;

    // Our command thread monitor
    private CommandThreadMonitor monitor;

    // Mutex locking on the bytes to send to the server
    private volatile boolean writingToOut = false;

    // My entity IDs in the server
    private ArrayList<String> myEntityIds = new ArrayList<>();

    public ClientHandler(ServerHandler handler, Socket connection) {
        // Save the connection socket
        this.handler = handler;
        this.connectionSocket = connection;
        try {
            this.connectionSocket.setTcpNoDelay(true);
        }
        catch (SocketException e) {
            System.out.println("Couldn't disable Nagle's algorithm");
        }

        // Create our command thread monitor
        monitor = new CommandThreadMonitor(this);

        // Start the monitor
        monitor.start();
    }

    /**
     * Runs the server including routing different types of requests to the right place
     */
    @Override
    public void run() {

        try {
            // Setup the in and out from the socket
            this.inFromClient = new DataInputStream(this.connectionSocket.getInputStream());
            this.outToClient = new DataOutputStream(this.connectionSocket.getOutputStream());

            while (running) {

                // Read in the next byte from the client the int returned will be between 0-255
                int requestType = inFromClient.readShort();
                if (requestType != RequestType.COMMAND.getNumVal()) {
                    System.out.println("Request Type: " + requestType);
                }

                // Check that the type is not recognizable
                if (!this.requestTypeRecognized(requestType)) {
                    // Then write that we don't recognize this type to the engine
                    outToClient.write(ByteManager.convertIntToByteArray(RequestType.UNRECOGNIZED.getNumVal()));

                    // Move on
                    continue;
                }

                // Read in the byte count as an int from the client
                int byteCount = inFromClient.readInt();
                if (requestType != RequestType.COMMAND.getNumVal()) {
                    System.out.println("Byte Count: " + byteCount);
                }

                // Create a byte array with the number of bytes to read and read the bytes
                byte[] bytes = new byte[byteCount];
                inFromClient.readFully(bytes);
//                for (int i = 0; i < byteCount; i++) {
//                    bytes[i] = inFromClient.readByte();
//                }

                // The byte result
                byte[] result = new byte[]{};

                // Setup the entity
                if (requestType == RequestType.ENTITY_SETUP.getNumVal()) {
                    // Get the result from the entity setup class
                    result = new EntitySetup().setupEntitiesWithBytes(entityTypeMap, bytes);
                }
                // If we want to register an entity, do so.
                else if (requestType == RequestType.ENTITY_REGISTER.getNumVal()) {
                    result = new EntityLoader().registerEntity(this, entityTypeMap, entityMap, bytes);
                }
                // If we want to run a command, do so.
                else if (requestType == RequestType.COMMAND.getNumVal()) {
                    // Create the handler, passing it ourselves, its bytes, etc.
                    CommandHandler handler = new CommandHandler(this, entityMap, bytes);

                    // Add the thread to the thread monitor, this will start it
                    monitor.addThread(handler);
                }
                // If we want to update a file in the framework, do so.
                else if (requestType == RequestType.FILE_UPDATE.getNumVal()) {
                    // Create the handler, passing it ourselves, its bytes, etc.
                    InMemoryClassLoader handler = new InMemoryClassLoader(this, entityMap, bytes);

                    // Add the thread to the thread monitor, this will start it
                    monitor.addThread(handler);
                }

                // Also write all of the data that is in toSend
                // Lock writing out
                writingLock();

                // If the result has data to write, write it
                if (result.length > 0) {
                    outToClient.write(result);
                }

                // Unlock writing
                writingUnlock();

            }
        }
        catch (EOFException e) {
            //System.out.println("Client Socket: Connection was dropped.");
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            System.out.println("Failed to read");
            e.printStackTrace();
        }
        finally {

            // Stop and finish our thread monitor and ourselves
            monitor.endProcess();

            endProcess();

            // Try to close sockets, clean up connections
            try {
                if (inFromClient != null) { inFromClient.close(); }
                if (outToClient != null) { outToClient.close(); }
                if (connectionSocket != null) { connectionSocket.close(); }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void sendByteArray(byte[] result) {
        // Lock writing out
        writingLock();

        try {
            // If there was something in the result
            if (result.length > 0) {
                // Write it out
                outToClient.write(result);
            }
        }
        catch (IOException e) {
            System.out.println("Failed to write from command");
            e.printStackTrace();
        }

        // Unlock writing
        writingUnlock();
    }

    private boolean requestTypeRecognized(int requestType) {
        // We recognize the request type if it matches one of our enums, otherwise, we don't
        boolean recognized = (requestType == RequestType.ENTITY_SETUP.getNumVal()
                || requestType == RequestType.ENTITY_REGISTER.getNumVal()
                || requestType == RequestType.COMMAND.getNumVal()
                || requestType == RequestType.FILE_UPDATE.getNumVal());

        return recognized;
    }

    private void writingLock() {
        while (writingToOut) {
            // Do nothing, wait until we can add our data
        }

        // Mutex lock
        writingToOut = true;
    }

    private void writingUnlock() {
        writingToOut = false;
    }

    public void addEntity(int entityId) {
        myEntityIds.add(Integer.toString(entityId));
    }

    public void endProcess() {

        TerminateHandler.TerminateServer();

        running = false;
    }

    public boolean isRunning() {
        return running;
    }

}
