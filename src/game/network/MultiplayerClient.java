package src.game.network;

import src.game.entities.Player;
import src.main.GamePanel;

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

    private ConcurrentHashMap<String, Player> otherPlayers = new ConcurrentHashMap<>();

    public MultiplayerClient(String serverAddress, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.playerName = gamePanel.getPlayer().name;

        try {
            socket = new Socket(serverAddress, 5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(playerName);

            String response = in.readLine();
            if ("ERROR:NAME_TAKEN".equals(response)) {
                JOptionPane.showMessageDialog(null, "Name already taken. Please choose a different name.");
                socket.close();
                return;
            }

            new Thread(this::listenToServer).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    // Method to discover LAN servers using UDP broadcast
    public static List<String> discoverLANServers() {
        List<String> availableServers = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] requestData = "DISCOVER_SERVER".getBytes();
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, InetAddress.getByName("255.255.255.255"), 5001);
            socket.send(requestPacket);

            socket.setSoTimeout(2000); // Wait for responses within 2 seconds
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
