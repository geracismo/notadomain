package manager;

import task.ClientTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectionManager implements Runnable {

    private final ExecutorService exec;
    private final LoggerManager logger;
    private final int port;
    private boolean started;

    public ConnectionManager(ExecutorService exec, int port, LoggerManager logger) {
        this.exec = exec;
        this.port = port;
        this.logger = logger;
        this.started = false;
        startThread();
    }

    private void startThread() {
        new Thread(this).start();
        this.logger.info("Start server");
    }

    @Override
    public void run() {
        this.started = true;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.port);
            while (!this.exec.isShutdown()) {
                Socket client = serverSocket.accept();
                this.logger.info("A new client is connected (" + client.getInetAddress().getCanonicalHostName() + ")");
                try {
                    //invia il task all'exec, e torna in ascolto
                    this.exec.submit(new ClientTask(client, this.logger));
                } catch (IOException e) {
                    client.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(serverSocket != null) {
                try {
                    serverSocket.close();
                    this.started = false;
                    this.logger.info("Server stopped");
                } catch (IOException e) {
                    this.logger.error("Error during stopping server", e);
                }
            }
        }
    }

    public boolean isStarted() { return this.started; }

    public void stopServer() {
        this.exec.shutdown();
        try {
            this.exec.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            this.logger.error("Error during shutdown executors", e);
        }
        this.exec.shutdownNow();
        this.started = false;
        Socket stopSocket = null;
        try {
            stopSocket = new Socket("localhost", this.port);
        } catch (IOException e) {
            this.logger.error("Error during stopping server", e);
        } finally {
            if(stopSocket != null) {
                try {
                    stopSocket.close();
                } catch (IOException e) {
                    this.logger.error("Error during stopping server", e);
                }
            }
        }
    }
}
