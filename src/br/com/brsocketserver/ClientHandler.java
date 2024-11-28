package br.com.brsocketserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.clientUsername = reader.readLine();
            clients.add(this);
            broadcastMessage("SERVER: " + this.clientUsername + " has entered!");
        } catch (IOException e) {
            closeEverything(this.socket, this.reader, this.writer);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (this.socket.isConnected()) {
            try {
                messageFromClient = this.reader.readLine();
                System.out.println("read: " + messageFromClient + " from " + this.clientUsername);
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(this.socket, this.reader, this.writer);
                break;
            }
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clients) {
            try {
                if (!clientHandler.clientUsername.equals(this.clientUsername)) {
                    clientHandler.writer.write(message);
                    clientHandler.writer.newLine();
                    clientHandler.writer.flush();
                    System.out.println("sending: " + message + " to " + clientHandler.clientUsername);
                }
            } catch (IOException e) {
                closeEverything(this.socket, this.reader, this.writer);
            }
        }
    }

    private void removeClient() {
        clients.remove(this);
        broadcastMessage("SERVER: " + this.clientUsername + " has left!");
    }

    private void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer) {
        removeClient();
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
            System.out.println("closed everyting for client: " + this.clientUsername);
        } catch (IOException e) {
            System.out.println(e.getCause().toString());
        }
    }

}
