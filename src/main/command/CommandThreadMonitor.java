package main.command;

import main.communication.ClientHandler;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandThreadMonitor extends Thread {

    // Our client
    ClientHandler client;

    // We should be running
    private boolean running = true;

    // The threads we want to be monitoring, concurrent so multiple threads modifying it has no adverse effect
    private Map<Integer, LinkedList<MonitoringThread>> monitoring = new ConcurrentHashMap<>();

    public CommandThreadMonitor(ClientHandler client) {
        this.client = client;
    }

    @Override
    public void run() {
        // While we are still running
        while (running) {

            // Loop through all of the threads we want to monitor
            for (Integer i : monitoring.keySet()) {
                LinkedList<MonitoringThread> monitoringQueue = monitoring.get(i);

                MonitoringThread t = monitoringQueue.peek();

                // If there is no thread we are monitoring, stop
                if (t == null) {
                    continue;
                }

                // Update each thread's runtime
                t.updateTimeAlive(System.currentTimeMillis());

                // Check to see if the thread is finished
                if (t.isFinished()) {
                    // Remove the thread from the queue
                    monitoringQueue.remove();

                    // Start the next thread, if there is one
                    if (monitoringQueue.peek() != null) {
                        monitoringQueue.peek().start();
                    }
                }
                // Check to see if it has timed out
                else if (t.hasTimedOut()) {
                    // If it has, then kill it
                    t.stop();

                    // Send a message back to the client letting them know what happened
                    // Use the generic command handler to compile a return message
                    client.sendByteArray(t.getTimeoutError());

                    // Remove all the threads from the queue, if there was a timeout we want to stop the robot
                    monitoringQueue.clear();
                }
            }

            // Slow down the loop and detect interrupts
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException e) {
                break;
            }
        }

        // Stop all command threads
        for (Integer i : monitoring.keySet()) {
            LinkedList<MonitoringThread> monitoringQueue = monitoring.get(i);

            MonitoringThread t = monitoringQueue.peek();

            // If there is no thread we are monitoring, stop
            if (t == null) {
                continue;
            }

            t.stop();
        }
    }

    public void addThread(MonitorableThread thread) {
        // Setup the monitoring thread
        MonitoringThread t = new MonitoringThread(thread);

        addMonitoringThread(t);
    }

    public void addThread(MonitorableThread thread, long timeout) {
        // Setup the monitoring thread
        MonitoringThread t = new MonitoringThread(thread, timeout);

        addMonitoringThread(t);
    }

    /**
     * Adds the given monitoring thread to the queue of threads for that robot.
     * If there are no threads in the robot's queue, it will immediately start the thread.
     * @param t The thread to add.
     */
    private void addMonitoringThread(MonitoringThread t) {
        int entityId = t.getEntityId();

        // If there is no linked list for the entity id yet
        if (!monitoring.keySet().contains(entityId)) {
            // Add one to the monitoring
            monitoring.put(entityId, new LinkedList<>());
        }

        // Add the monitoring thread to our list
        LinkedList<MonitoringThread> monitoringQueue = monitoring.get(entityId);

        // Add the thread to the queue
        monitoringQueue.add(t);

        // If the monitoring queue has only the thread we just added, start the thread
        if (monitoringQueue.size() == 1) {
            t.start();
        }
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

        private MonitorableThread thread;

        private MonitoringThread(MonitorableThread thread) {
            this.thread = thread;
        }

        private MonitoringThread(MonitorableThread thread, long timeout) {
            this.thread = thread;
            this.timeout = timeout;
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

        public int getEntityId() { return thread.getEntityId(); }

        public boolean hasTimedOut() {
            // If we have been alive longer than our timeout
            if (timeAlive > timeout) {
                // Set out timed out to true on the thread
                thread.setTimedOut(true);

                // Return true
                return true;
            }

            // Otherwise, return false
            return false;
        }

        public boolean isFinished() {
            return thread.isFinished();
        }

        public void start() {
            // Time alive starts now
            lastTimeCheck = System.currentTimeMillis();

            // Start the thread
            thread.start();
        }

        public void stop() {
            thread.stop();
        }
    }


}
