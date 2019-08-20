package main.communication;

import main.command.GenericCommandHandler;
import main.entity.EntityLoader;
import main.entity.EntitySetup;
import main.util.InMemoryClassLoader;
import util.ByteManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler extends Thread {

    private Boolean running = true;

    private Socket connectionSocket;
    private DataInputStream inFromClient;
    private DataOutputStream outToClient;

    public ClientHandler(Socket connection) {
        // Save the connection socket
        this.connectionSocket = connection;
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
                    System.out.println(requestType);
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
                    System.out.println("Test");
                    result = new EntityLoader().registerEntity(bytes);
                }
                // If we want to run a command, do so.
                else if (requestType == RequestType.COMMAND.getNumVal()) {
                    result = new GenericCommandHandler().handleCommand(bytes);
                }
                // If we want to update a file in the framework, do so.
                else if (requestType == RequestType.FILE_UPDATE.getNumVal()) {
                    result = new InMemoryClassLoader().updateClass(bytes);
                }

                // If the result has data to write, write it
                if (result.length > 0) {
                    outToClient.write(result);
                }

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

    private boolean requestTypeRecognized(int requestType) {
        // We recognize the request type if it matches one of our enums, otherwise, we don't
        boolean recognized = (requestType == RequestType.ENTITY_SETUP.getNumVal()
                || requestType == RequestType.ENTITY_REGISTER.getNumVal()
                || requestType == RequestType.COMMAND.getNumVal()
                || requestType == RequestType.FILE_UPDATE.getNumVal());


        return recognized;
    }

}
