package main.command;

import main.communication.ClientHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandThreadMonitor extends Thread {

    // Our client
    ClientHandler client;

    // We should be running
    private boolean running = true;

    // The threads we want to be monitoring
    private Map<Integer, MonitoringThread> monitoring = new ConcurrentHashMap<>();

    // Track the current id
    private int currentId = 0;

    // Lock the monitoring
    private boolean monitoringLocked = false;

    public CommandThreadMonitor(ClientHandler client) {
        this.client = client;
    }

    @Override
    public void run() {
        // While we are still running
        while (running) {

            // Loop through all of the threads we want to monitor
            for (Integer i : monitoring.keySet()) {
                MonitoringThread t = monitoring.get(i);

                // Update each thread's runtime
                t.updateTimeAlive(System.currentTimeMillis());

                // Check to see if the thread is finished
                if (t.isFinished()) {
                    // Remove the thread from our map
                    monitoring.remove(i);
                }
                // Check to see if it has timed out
                else if (t.hasTimedOut()) {
                    // If it has, then kill it
                    t.stop();

                    // Send a message back to the client letting them know what happened
                    // Use the generic command handler to compile a return message
                    client.sendByteArray(t.getTimeoutError());

                    // Remove the thread from out map
                    monitoring.remove(i);
                }
            }

            // Slow down the loop and detect interrupts
            try {
                Thread.sleep(50);
            }
            catch (InterruptedException e) {
                break;
            }
        }
    }

    public void addThread(CommandHandler thread) {
        // Setup the monitoring thread
        MonitoringThread t = new MonitoringThread(thread);

        // Add the monitoring thread to our list
        monitoring.put(currentId, new MonitoringThread(thread));

        // Increment the current id
        currentId++;
        currentId %= 1000;

        // Start the thread
        t.start();
    }

    public void addThread(CommandHandler thread, long timeout) {
        // Setup the monitoring thread
        MonitoringThread t = new MonitoringThread(thread, timeout);

        // While monitoring locked, stop
        while (monitoringLocked) {

        }

        // Lock the monitoring
        monitoringLocked = true;

        // Add the monitoring thread to our list
        monitoring.put(currentId, new MonitoringThread(thread));

        monitoringLocked = false;

        // Increment the current id
        currentId++;
        currentId %= 1000;

        // Start the thread
        t.start();
    }

    public void endProcess() {
        running = false;
    }

    /**
     * Class that bundles all the information we need to track a thread and the time it has spent alive.
     */
    private class MonitoringThread {

        private long timeout = 5000;
        private long timeAlive = 0;
        private long lastTimeCheck;

        private CommandHandler thread;

        private MonitoringThread(CommandHandler thread) {
            this.thread = thread;

            // lastTimeCheck tracks the delta
            lastTimeCheck = System.currentTimeMillis();
        }

        private MonitoringThread(CommandHandler thread, long timeout) {
            this.thread = thread;
            this.timeout = timeout;

            // lastTimeCheck tracks the delta
            lastTimeCheck = System.currentTimeMillis();
        }

        public void updateTimeAlive(long currentTime) {
            // Update the time alive using the time passed
            timeAlive += currentTime - lastTimeCheck;

            // Change the last time check to equal the current time
            lastTimeCheck = currentTime;
        }

        public byte[] getTimeoutError() {
            return thread.compileTimeoutError();
        }

        public boolean hasTimedOut() {
            return timeAlive > timeout;
        }

        public boolean isFinished() {
            return thread.isFinished();
        }

        public void start() {
            thread.start();
        }

        public void stop() {
            thread.stop();
        }
    }


}
