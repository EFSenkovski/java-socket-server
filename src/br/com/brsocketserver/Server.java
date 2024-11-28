package br.com.brsocketserver;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    private ServerSocket serverSocket;

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            while (!serverSocket.isClosed()) {
                var clientSocket = serverSocket.accept();
                System.out.println("Client connected!");
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("Server closed");
            }
        } catch (IOException e) {
            System.out.println(e.getCause().toString());
        }
    }

    public static void main(String[] args) throws IOException {
        var server = new Server(1234);
        server.startServer();
    }
}
