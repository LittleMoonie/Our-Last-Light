package src.game.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class MultiplayerServer {
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, ClientHandler> clients;

    public MultiplayerServer() {
        clients = new ConcurrentHashMap<>();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("Server started on port 5000");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean registerClient(String playerName, ClientHandler handler) {
        // If the name is already taken, deny registration
        if (clients.containsKey(playerName)) {
            return false;
        }
        // Register the client
        clients.put(playerName, handler);
        System.out.println("Player " + playerName + " has joined.");
        return true;
    }

    public void removeClient(String playerName) {
        clients.remove(playerName);
        System.out.println("Player " + playerName + " has left.");
    }

    public void broadcastPlayerPosition(String playerName, int x, int y) {
        String message = "UPDATE_POSITION:" + playerName + "," + x + "," + y;
        for (ClientHandler client : clients.values()) {
            if (!client.equals(clients.get(playerName))) { // Exclude the sending client
                client.sendData(message);
            }
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            clients.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
