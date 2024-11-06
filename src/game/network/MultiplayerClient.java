//package src.game.network;
//
//import src.main.GamePanel;
//import src.game.constants.Config;
//import src.game.entities.Entity;
//import src.game.components.PositionComponent;
//import src.game.components.PlayerComponent;
//
//import javax.swing.*;
//import java.io.*;
//import java.net.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class MultiplayerClient {
//    private Socket socket;
//    private BufferedReader in;
//    private PrintWriter out;
//    private GamePanel gamePanel;
//    private String playerName;
//
//    // Map to store other players as entities with PositionComponents
//    private ConcurrentHashMap<String, Entity> otherPlayers = new ConcurrentHashMap<>();
//
//    public MultiplayerClient(String serverAddress, GamePanel gamePanel) {
//        this.gamePanel = gamePanel;
//        Entity playerEntity = gamePanel.getPlayerEntity(); // Assuming GamePanel holds a player entity
//        PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);
//        PositionComponent positionComponent = playerEntity.getComponent(PositionComponent.class);
//
//        if (playerComponent != null) {
//            this.playerName = playerComponent.name;
//        }
//
//        try {
//            socket = new Socket(serverAddress, Config.SERVER_PORT);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            out = new PrintWriter(socket.getOutputStream(), true);
//
//            // Send player name to server
//            out.println(playerName);
//
//            String response = in.readLine();
//            if ("ERROR:NAME_TAKEN".equals(response)) {
//                JOptionPane.showMessageDialog(null, Config.NAME_ALREADY_TAKEN_MESSAGE);
//                socket.close();
//                return;
//            }
//
//            new Thread(this::listenToServer).start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void listenToServer() {
//        try {
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                if (inputLine.startsWith("UPDATE_POSITION:")) {
//                    String[] parts = inputLine.split(":")[1].split(",");
//                    String name = parts[0];
//                    int x = Integer.parseInt(parts[1]);
//                    int y = Integer.parseInt(parts[2]);
//                    updateOtherPlayerPosition(name, x, y);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void updateOtherPlayerPosition(String playerName, int x, int y) {
//        // Ignore updates for the current player
//        if (!playerName.equals(this.playerName)) {
//            Entity otherPlayer = otherPlayers.get(playerName);
//            if (otherPlayer == null) {
//                otherPlayer = createOtherPlayerEntity(playerName);
//                otherPlayers.put(playerName, otherPlayer);
//            }
//
//            // Update position of the other player
//            PositionComponent position = otherPlayer.getComponent(PositionComponent.class);
//            if (position != null) {
//                position.x = x;
//                position.y = y;
//            }
//        }
//    }
//
//    private Entity createOtherPlayerEntity(String playerName) {
//        // Create a new entity with a PlayerComponent and PositionComponent for a multiplayer player
//        Entity otherPlayer = new Entity();
//        otherPlayer.addComponent(new PlayerComponent(playerName));
//        otherPlayer.addComponent(new PositionComponent(0, 0)); // Initial position, will be updated by server
//
//        return otherPlayer;
//    }
//
//    public void sendPlayerPosition(Entity playerEntity) {
//        PositionComponent position = playerEntity.getComponent(PositionComponent.class);
//        if (position != null) {
//            out.println("POSITION:" + position.x + "," + position.y);
//        }
//    }
//
//    public ConcurrentHashMap<String, Entity> getOtherPlayers() {
//        return otherPlayers;
//    }
//
//    public void disconnect() {
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static List<String> discoverLANServers() {
//        List<String> availableServers = new ArrayList<>();
//        try (DatagramSocket socket = new DatagramSocket()) {
//            socket.setBroadcast(true);
//            byte[] requestData = "DISCOVER_SERVER".getBytes();
//            DatagramPacket requestPacket = new DatagramPacket(
//                    requestData, requestData.length, InetAddress.getByName("255.255.255.255"), Config.DISCOVERY_PORT);
//            socket.send(requestPacket);
//
//            socket.setSoTimeout(2000);
//            byte[] buffer = new byte[256];
//            while (true) {
//                try {
//                    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
//                    socket.receive(responsePacket);
//                    String serverResponse = new String(responsePacket.getData(), 0, responsePacket.getLength());
//                    availableServers.add(responsePacket.getAddress().getHostAddress() + " - " + serverResponse);
//                } catch (SocketTimeoutException e) {
//                    break;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return availableServers;
//    }
//}
