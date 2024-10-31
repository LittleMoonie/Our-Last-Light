package src;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private MultiplayerServer server;
    private PrintWriter out;
    private String playerName;

    public ClientHandler(Socket clientSocket, MultiplayerServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            playerName = in.readLine(); // First message is the player name
            if (!server.registerClient(playerName, this)) {
                out.println("ERROR:NAME_TAKEN");
                return;
            }

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("POSITION:")) {
                    String[] parts = inputLine.split(":")[1].split(",");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    server.broadcastPlayerPosition(playerName, x, y);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.removeClient(playerName);
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendData(String data) {
        out.println(data);
    }
}
