package main.communication;

import javax.management.RuntimeErrorException;
import java.io.DataOutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is a class that handles writing to the stream of the client.
 * It will send messages 20 times per second. Bytes added to the buffer in between those time frames in included.
 */
public class ClientWriter extends ClientIO {

    //
    private DataOutputStream out;

    final long sendRate = 50;

    volatile private byte[] buffer = new byte[1024 * 100];
    volatile private int bufferIndex = 0;

    Timer timer = new Timer();

    public ClientWriter(ClientHandler handler, DataOutputStream out) {
        super(handler);
        this.out = out;
    }

    @Override
    public void run() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

            }
        }, sendRate);
    }

    public synchronized void write(byte[] data) {
        writeOrSend(data, true);
    }

    /**
     * This function writes to the buffer, or sends the buffer and resets the writing position
     * to the first index (0).
     * This is the way to send information to the client.
     * @param write
     * @param data
     */
    public synchronized void writeOrSend(byte[] data, boolean write) {
        // If we want to write,
        if (write) {
            // Loop through and copy the bytes into the buffer to be sent
            for (int i = 0; i < data.length; i++) {
                buffer[bufferIndex + i] = data[i];
            }

            // Move the buffer index over
            bufferIndex += data.length;
        }
        else { // Send the information to the client if we want to send it
            // Catch exceptions from writing
            try {
                out.write(data, 0, bufferIndex);
            }
            catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }
}
