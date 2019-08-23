package main.communication;

import main.command.CommandHandler;
import main.command.CommandThreadMonitor;
import main.entity.EntityLoader;
import main.entity.EntitySetup;
import main.util.InMemoryClassLoader;
import util.ByteManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler extends Thread {

    private Boolean running = true;

    // Sockets and I/O
    private Socket connectionSocket;
    private DataInputStream inFromClient;
    private DataOutputStream outToClient;

    // Our command thread monitor
    private CommandThreadMonitor monitor;

    // Mutex locking on the bytes to send to the server
    private boolean manipulatingToSend = false;

    // Track data to be sent
    private ArrayList<byte[]> toSend = new ArrayList<>();

    public ClientHandler(Socket connection) {
        // Save the connection socket
        this.connectionSocket = connection;

        // Create our command thread monitor
        monitor = new CommandThreadMonitor(this);
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
//                    System.out.println(requestType);

                // Check that the type is not recognizable
                if (!this.requestTypeRecognized(requestType)) {
                    // Then write that we don't recognize this type to the engine
                    outToClient.write(ByteManager.convertIntToByteArray(RequestType.UNRECOGNIZED.getNumVal()));

                    // Move on
                    continue;
                }

                // Read in the byte count as an int from the client
                int byteCount = inFromClient.readInt();
//                    System.out.println(byteCount);

                // Create a byte array with the number of bytes to read
                byte[] bytes = new byte[byteCount];

                // Read the bytes
                inFromClient.read(bytes);

                // The byte result
                byte[] result = new byte[]{};

                // Setup the entity
                if (requestType == RequestType.ENTITY_SETUP.getNumVal()) {
                    // Get the result from the entity setup class
                    result = new EntitySetup().setupEntitiesWithBytes(bytes);
                }
                // If we want to register an entity, do so.
                else if (requestType == RequestType.ENTITY_REGISTER.getNumVal()) {
                    result = new EntityLoader().registerEntity(bytes);
                }
                // If we want to run a command, do so.
                else if (requestType == RequestType.COMMAND.getNumVal()) {
                    // Create the handler, passing it ourselves, its bytes, etc.
                    CommandHandler handler = new CommandHandler(this, bytes);

                    // Add the thread to the thread monitor, this will start it
                    monitor.addThread(handler);
                }
                // If we want to update a file in the framework, do so.
                else if (requestType == RequestType.FILE_UPDATE.getNumVal()) {
                    result = new InMemoryClassLoader().updateClass(bytes);
                }

                // If the result has data to write, write it
                if (result.length > 0) {
                    outToClient.write(result);
                }

                // Also write all of the data that is in toSend
                // Wait for manipulating to send to be false
                while (manipulatingToSend) {
                    // Do nothing, wait until we can add our data
                }

                // Mutex lock
                manipulatingToSend = true;

                // Send all of the data
                for (byte[] send: toSend) {
                    outToClient.write(send);
                }

                // Clear the toSend
                toSend.clear();

                // Mutex unlock
                manipulatingToSend = false;

//                    } else if(clientBundle.getType() == RequestType.ENTITY_UPDATE) {
//                        System.out.println(clientBundle.getSerializedData());
//                        EntityUpdateRequest request = Serializer.deserialize(clientBundle.getSerializedData(), EntityUpdateRequest.class);
//                        EntityUpdater updater = new EntityUpdater();
//                        result = updater.updateEntity(request);
//                    } else if (clientBundle.getType() == RequestType.FILE_GET) {
//                        // If it is a get file request then deserialize it accordingly and get the file contents for the provided command name
//                        FileRequest fileRequest = Serializer.deserialize(clientBundle.getSerializedData(), FileRequest.class);
//                        FileGetter fileGetter = new FileGetter();
//                        result = fileGetter.getFile(fileRequest);
//                    }

            }
        }
        catch (EOFException e) {
            System.out.println("Socket: Connection was dropped.");
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            System.out.println("Failed to read");
            e.printStackTrace();
        }
        finally {
            // Stop and finish our thread monitor
            monitor.endProcess();

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
        while (manipulatingToSend) {
            // Do nothing, wait until we can add our data
        }
        // Mutex lock
        manipulatingToSend = true;

        // Add our to send
        toSend.add(result);

        // Mutex unlock
        manipulatingToSend = false;
    }

    private boolean requestTypeRecognized(int requestType) {
        // We recognize the request type if it matches one of our enums, otherwise, we don't
        boolean recognized = (requestType == RequestType.ENTITY_SETUP.getNumVal()
                || requestType == RequestType.ENTITY_REGISTER.getNumVal()
                || requestType == RequestType.COMMAND.getNumVal()
                || requestType == RequestType.FILE_UPDATE.getNumVal());


        return recognized;
    }

}
