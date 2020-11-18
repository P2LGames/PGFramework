package main.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminateHandler extends Thread {

    ServerHandler serverHandler;
    int handlerCount = 0;
    int noClientCheckCount = 0;
    int noClientCheckCountMax = 5;

    int sleepTime = 60000;

    public TerminateHandler(ServerHandler handler) {
        serverHandler = handler;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(sleepTime);

                // Wait for the handlers to be accessible
                while (serverHandler.handlersLock) {}

                // Loop through the handlers and remove the disconnected client handlers
                for (int i = serverHandler.handlers.size() - 1; i > 0; i--) {
                    if (!serverHandler.handlers.get(i).isRunning()) {
                        serverHandler.handlers.remove(i);
                    }
                }

                // Update the handler count, if it is zero, increment the
                handlerCount = serverHandler.handlers.size();
                if (handlerCount == 0) {
                    System.out.println("Handler count 0, incrementing server terminate value.");
                    noClientCheckCount++;

                    // If we have gotten to the max, then we want to terminate this server
                    if (noClientCheckCount >= noClientCheckCountMax) {
                        TerminateServer();
                    }
                }
                else {
                    noClientCheckCount = 0;
                }
            }
            catch (Exception e) {

            }
        }
    }

    public static void TerminateServer() {
        // Kill the server instance
        System.out.println("Killing server...");
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("bash", "-c", "sudo shutdown -h now");

        try {
            // Start the process and get the output
            Process process = builder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            // Read the output line by line
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            // Wait for the process to end, respond accordingly
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                System.out.println(output);
                System.exit(0);
            }
            else {
                System.out.println("Failed to kill server");
            }
        }
        catch (IOException | InterruptedException e) {
            System.out.println("Failed to kill server");
        }
    }

}
