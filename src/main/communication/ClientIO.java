package main.communication;

public class ClientIO extends Thread {
    // The Client Handler
    protected ClientHandler handler;
    protected byte[] buffer = new byte[1024 * 100];

    public ClientIO(ClientHandler handler) {
        this.handler = handler;
    }

    public void endProcess() {
        Thread.currentThread().interrupt();
    }

}
