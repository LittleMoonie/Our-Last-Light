package src;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiplayerServer {
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private List<ClientHandler> connectedClients;
    private List<Player> listOfOtherPlayers;

    public MultiplayerServer() {
        executor = Executors.newCachedThreadPool();
        connectedClients = Collections.synchronizedList(new ArrayList<>());
        listOfOtherPlayers = Collections.synchronizedList(new ArrayList<>());
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(5000); // Use port 5000
            executor.submit(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        // Handle client connection
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        ClientHandler clientHandler = new ClientHandler(clientSocket, this);
        connectedClients.add(clientHandler);
        executor.submit(clientHandler);
    }

    public void broadcastPlayerPosition(Player player) {
        // Serialize player position and send to all connected clients except the sender
        String positionData = "PLAYER_UPDATE:" + player.name + "," + player.x + "," + player.y;
        synchronized (connectedClients) {
            for (ClientHandler clientHandler : connectedClients) {
                if (clientHandler.getPlayer() != player) {
                    clientHandler.sendData(positionData);
                }
            }
        }
    }

    public List<Player> getConnectedPlayers() {
        synchronized (listOfOtherPlayers) {
            return new ArrayList<>(listOfOtherPlayers);
        }
    }

    public void addPlayer(Player player) {
        synchronized (listOfOtherPlayers) {
            listOfOtherPlayers.add(player);
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        connectedClients.remove(clientHandler);
        if (clientHandler.getPlayer() != null) {
            synchronized (listOfOtherPlayers) {
                listOfOtherPlayers.remove(clientHandler.getPlayer());
            }
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
