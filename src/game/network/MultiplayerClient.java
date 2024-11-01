package src.game.network;

import src.game.entities.Player;
import src.main.GamePanel;
import src.game.constants.Config;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MultiplayerClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GamePanel gamePanel;
    private String playerName;
    private String worldName;

    private ConcurrentHashMap<String, Player> otherPlayers = new ConcurrentHashMap<>();

    public MultiplayerClient(String serverAddress, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.playerName = gamePanel.getPlayer().name;
        this.worldName = requestWorldNameFromServer(serverAddress);
        gamePanel.setWorldName(this.worldName);

        try {
            socket = new Socket(serverAddress, Config.SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(playerName);

            String response = in.readLine();
            if ("ERROR:NAME_TAKEN".equals(response)) {
                JOptionPane.showMessageDialog(null, Config.NAME_ALREADY_TAKEN_MESSAGE);
                socket.close();
                return;
            }

            new Thread(this::listenToServer).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String requestWorldNameFromServer(String serverAddress) {
        String receivedWorldName = null;

        try (Socket socket = new Socket(serverAddress, Config.SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Read the world name from the server
            receivedWorldName = in.readLine(); // Read the first line which is the world name
            System.out.println("Received world name from server: " + receivedWorldName);

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }

        return receivedWorldName != null ? receivedWorldName : "UnknownWorld";
    }

    public String getWorldName() {
        return worldName;
    }

    private void listenToServer() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("UPDATE_POSITION:")) {
                    String[] parts = inputLine.split(":")[1].split(",");
                    String name = parts[0];
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    updateOtherPlayer(name, x, y);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateOtherPlayer(String playerName, int x, int y) {
        if (!playerName.equals(this.playerName)) {
            Player otherPlayer = otherPlayers.get(playerName);
            if (otherPlayer == null) {
                otherPlayer = new Player(null, playerName);
                otherPlayers.put(playerName, otherPlayer);
            }
            otherPlayer.x = x;
            otherPlayer.y = y;
        }
    }

    public void sendPlayerPosition(Player player) {
        out.println("POSITION:" + player.x + "," + player.y);
    }

    public ConcurrentHashMap<String, Player> getOtherPlayers() {
        return otherPlayers;
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> discoverLANServers() {
        List<String> availableServers = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] requestData = "DISCOVER_SERVER".getBytes();
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, InetAddress.getByName("255.255.255.255"), Config.DISCOVERY_PORT);
            socket.send(requestPacket);

            socket.setSoTimeout(2000);
            byte[] buffer = new byte[256];
            while (true) {
                try {
                    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(responsePacket);
                    String serverResponse = new String(responsePacket.getData(), 0, responsePacket.getLength());
                    availableServers.add(responsePacket.getAddress().getHostAddress() + " - " + serverResponse);
                } catch (SocketTimeoutException e) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return availableServers;
    }
}
