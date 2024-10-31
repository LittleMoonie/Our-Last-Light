package src;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class MultiplayerServer {
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public MultiplayerServer() {
        executor = Executors.newCachedThreadPool();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(5000);
            executor.submit(() -> {
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = null;
                    try {
                        clientSocket = serverSocket.accept();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    handleClient(clientSocket);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        ClientHandler clientHandler = new ClientHandler(clientSocket, this);
        executor.submit(clientHandler);
    }

    public synchronized boolean registerClient(String playerName, ClientHandler clientHandler) {
        if (clients.containsKey(playerName)) {
            return false; // Player name already exists
        }
        clients.put(playerName, clientHandler);
        broadcastPlayerJoin(playerName);
        return true;
    }

    public void removeClient(String playerName) {
        clients.remove(playerName);
        broadcastPlayerLeave(playerName);
    }

    public void broadcastPlayerPosition(String playerName, int x, int y) {
        String positionUpdate = "UPDATE_POSITION:" + playerName + "," + x + "," + y;
        for (ClientHandler client : clients.values()) {
            client.sendData(positionUpdate);
        }
    }

    private void broadcastPlayerJoin(String playerName) {
        for (ClientHandler client : clients.values()) {
            client.sendData("JOIN:" + playerName);
        }
    }

    private void broadcastPlayerLeave(String playerName) {
        for (ClientHandler client : clients.values()) {
            client.sendData("LEAVE:" + playerName);
        }
    }

    public void stop() {
        try {
            serverSocket.close();
            executor.shutdownNow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
