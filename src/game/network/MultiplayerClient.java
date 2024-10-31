package src.game.network;

import src.game.entities.Player;
import src.main.GamePanel;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class MultiplayerClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GamePanel gamePanel;
    private String playerName;

    // Store other players in a ConcurrentHashMap
    private ConcurrentHashMap<String, Player> otherPlayers = new ConcurrentHashMap<>();

    public MultiplayerClient(String serverAddress, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.playerName = gamePanel.getPlayer().name;

        try {
            // Establish a connection to the server
            socket = new Socket(serverAddress, 5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Send player name to the server
            out.println(playerName);

            // Check for a name error
            String response = in.readLine();
            if ("ERROR:NAME_TAKEN".equals(response)) {
                JOptionPane.showMessageDialog(null, "Name already taken. Please choose a different name.");
                socket.close();
                return;
            }

            // Start listening for updates
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

                    // Update other players' positions
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
                otherPlayer = new Player(null, playerName); // World is null for now
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
}
