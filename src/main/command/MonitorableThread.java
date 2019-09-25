package main.command;

public interface MonitorableThread {
    void run();
    void start();
    void stop();

    int getEntityId();

    byte[] compileTimeoutError();
    boolean isFinished();
    void setTimedOut(boolean timedOut);
}
