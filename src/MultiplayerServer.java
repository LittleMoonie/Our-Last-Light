package src;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiplayerServer {
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

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
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    executor.submit(clientHandler);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Register new client
    public boolean registerClient(String playerName, ClientHandler clientHandler) {
        if (clients.putIfAbsent(playerName, clientHandler) == null) {
            System.out.println("Player " + playerName + " connected.");
            return true;
        } else {
            System.out.println("Name already taken: " + playerName);
            return false;
        }
    }

    // Remove disconnected client
    public void removeClient(String playerName) {
        clients.remove(playerName);
        System.out.println("Player " + playerName + " disconnected.");
    }

    public void broadcastPlayerPosition(String playerName, int x, int y) {
        String positionMessage = "UPDATE_POSITION:" + playerName + "," + x + "," + y;
        for (ClientHandler client : clients.values()) {
            client.sendData(positionMessage);
        }
    }


    String getLocalIpAddress() {
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            return localAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unknown IP";
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
