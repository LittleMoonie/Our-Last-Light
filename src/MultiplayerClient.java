package src;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class MultiplayerClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean running = true;
    private GamePanel gamePanel;

    // Map to store other players
    private Map<String, Player> otherPlayers = new ConcurrentHashMap<>();

    public MultiplayerClient(String serverAddress, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        try {
            socket = new Socket(serverAddress, 5000); // Connect to server
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Send player name to server
            out.println(gamePanel.getPlayer().name);

            // Start listening to server messages
            new Thread(this::listenToServer).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenToServer() {
        try {
            String inputLine;
            while (running && (inputLine = in.readLine()) != null) {
                // Process input from server
                if (inputLine.startsWith("PLAYER_UPDATE:")) {
                    String[] parts = inputLine.substring(14).split(",");
                    String playerName = parts[0];
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    updateOtherPlayer(playerName, x, y);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayerPosition(Player player) {
        String positionData = "POSITION:" + player.x + "," + player.y;
        out.println(positionData);
    }

    // Method to get other players
    public Iterable<Player> getOtherPlayers() {
        return otherPlayers.values();
    }

    // Method to update other player's position
    private void updateOtherPlayer(String playerName, int x, int y) {
        Player otherPlayer = otherPlayers.get(playerName);
        if (otherPlayer == null) {
            otherPlayer = new Player(null, playerName); // World is null for other players
            otherPlayers.put(playerName, otherPlayer);
        }
        otherPlayer.x = x;
        otherPlayer.y = y;
    }

    public void disconnect() {
        running = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
