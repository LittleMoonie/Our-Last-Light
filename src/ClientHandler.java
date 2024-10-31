package src;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private MultiplayerServer server;
    private BufferedReader in;
    private PrintWriter out;
    private volatile boolean running = true;
    private Player player;

    public ClientHandler(Socket clientSocket, MultiplayerServer server) {
        this.clientSocket = clientSocket;
        this.server = server;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(String data) {
        out.println(data);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void run() {
        try {
            // Initialize player
            String playerName = in.readLine(); // First message is the player's name
            player = new Player(null, playerName); // World will be set later
            server.addPlayer(player);

            String inputLine;
            while (running && (inputLine = in.readLine()) != null) {
                // Process input from client
                System.out.println("Received from client: " + inputLine);
                // For example, parse player position updates
                if (inputLine.startsWith("POSITION:")) {
                    String[] parts = inputLine.substring(9).split(",");
                    player.x = Integer.parseInt(parts[0]);
                    player.y = Integer.parseInt(parts[1]);
                    server.broadcastPlayerPosition(player);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        server.removeClient(this);
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
