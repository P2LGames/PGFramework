package main.communication;

import entity.GenericEntityMap;
import main.command.CommandHandler;
import main.entity.EntityLoader;
import main.entity.EntitySetup;
import main.entity.EntityTypeMap;
import main.util.InMemoryClassLoader;
import util.ByteManager;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

/**
 * This class handles reading and routing everything received from the client to its proper place.
 * It will read into a buffer, and the info in that buffer will be passed to the appropriate place for handling.
 * All information that needs to be routed back to the client will be done so by a different thread.
 */
public class ClientReader extends ClientIO {

    // The stream from the client
    private DataInputStream in;

    // Variables used to read the request
    private int requestType;
    private int byteCount;

    public ClientReader(ClientHandler handler, DataInputStream in) {
        super(handler);
        this.in = in;
    }

    @Override
    public void run() {
        try {
            while (handler.isRunning()) {

                // Read in the next byte from the client the int returned will be between 0-255
                requestType = in.readShort();
                if (requestType != RequestType.COMMAND.getNumVal()) {
                    System.out.println("Request Type: " + requestType);
                }

                // Check that the type is not recognizable
                if (!this.requestTypeRecognized(requestType)) {
                    // Then write that we don't recognize this type to the engine
                    this.handler.write(ByteManager.convertIntToByteArray(RequestType.UNRECOGNIZED.getNumVal()));

                    // Move on
                    continue;
                }

                // Read in the byte count as an int from the client
                byteCount = in.readInt();
                if (requestType != RequestType.COMMAND.getNumVal()) {
                    System.out.println("Byte Count: " + byteCount);
                }

                // Create a byte array with the number of bytes to read and read the bytes
                in.readFully(buffer);
//                for (int i = 0; i < byteCount; i++) {
//                    bytes[i] = inFromClient.readByte();
//                }

                // The byte result
                byte[] result = new byte[]{};

                // Setup the entity
                if (requestType == RequestType.ENTITY_SETUP.getNumVal()) {
                    // Get the result from the entity setup class
                    result = new EntitySetup().setupEntitiesWithBytes(EntityTypeMap.getInstance(), buffer);
                }
                // If we want to register an entity, do so.
                else if (requestType == RequestType.ENTITY_REGISTER.getNumVal()) {
                    result = new EntityLoader().registerEntity(EntityTypeMap.getInstance(), GenericEntityMap.getInstance(), buffer);
                }
                // If we want to run a command, do so.
                else if (requestType == RequestType.COMMAND.getNumVal()) {
                    // Create the handler, passing it ourselves, its bytes, etc.
                    CommandHandler handler = new CommandHandler(this.handler, GenericEntityMap.getInstance(), buffer);

                    // Add the thread to the thread monitor, this will start it
                    this.handler.monitor.addThread(handler);
                }
                // If we want to update a file in the framework, do so.
                else if (requestType == RequestType.FILE_UPDATE.getNumVal()) {
                    // Create the handler, passing it ourselves, its bytes, etc.
                    InMemoryClassLoader handler = new InMemoryClassLoader(this.handler, GenericEntityMap.getInstance(), buffer);

                    // Add the thread to the thread monitor, this will start it
                    this.handler.monitor.addThread(handler);
                }
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
    }

    private boolean requestTypeRecognized(int requestType) {
        // We recognize the request type if it matches one of our enums, otherwise, we don't
        boolean recognized = (requestType == RequestType.ENTITY_SETUP.getNumVal()
                || requestType == RequestType.ENTITY_REGISTER.getNumVal()
                || requestType == RequestType.COMMAND.getNumVal()
                || requestType == RequestType.FILE_UPDATE.getNumVal());

        return recognized;
    }

    @Override
    public void endProcess() {
        // Close our stream
        try {
            if (in != null) { in.close(); }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // Super end process will kill this thread
        super.endProcess();
    }
}
