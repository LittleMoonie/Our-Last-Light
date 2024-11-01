package src.game.ui.menus;

import src.main.GamePanel;
import src.game.network.MultiplayerClient;
import src.game.network.MultiplayerServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MultiplayerMenu extends JPanel implements ActionListener {
    private JButton hostGameButton;
    private JButton joinGameButton;
    private JButton backButton;
    private JList<String> serverList;
    private DefaultListModel<String> serverListModel;
    private MainMenu parentMenu;
    private Timer serverDiscoveryTimer;

    private DatagramSocket discoverySocket;
    private boolean running;

    public MultiplayerMenu(MainMenu parent) {
        this.parentMenu = parent;

        // Initialize buttons
        hostGameButton = createMenuButton("Host Game");
        joinGameButton = createMenuButton("Join Selected Game");
        backButton = createMenuButton("Back");

        // Initialize server list
        serverListModel = new DefaultListModel<>();
        serverList = new JList<>(serverListModel);
        serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JScrollPane(serverList));
        add(hostGameButton);
        add(joinGameButton);
        add(backButton);

        try {
            discoverySocket = new DatagramSocket(5001);
            running = true;
            new Thread(this::listenForDiscoveryRequests).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startServerDiscovery();
    }

    // Helper method for creating styled buttons
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.addActionListener(this);
        return button;
    }

    private void listenForDiscoveryRequests() {
        byte[] buffer = new byte[256];
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                discoverySocket.receive(packet);
                String request = new String(packet.getData(), 0, packet.getLength());

                if ("DISCOVER_SERVER".equals(request)) {
                    String response = "Game Server"; // Server info
                    byte[] responseData = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, packet.getAddress(), packet.getPort());
                    discoverySocket.send(responsePacket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Continuously refresh LAN servers
    private void startServerDiscovery() {
        serverDiscoveryTimer = new Timer();
        serverDiscoveryTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<String> availableServers = MultiplayerClient.discoverLANServers(); // Replace with actual discovery method
                updateServerList(availableServers);
            }
        }, 0, 3000); // Refresh every 3 seconds
    }

    // Update the server list displayed in the UI
    private void updateServerList(List<String> availableServers) {
        serverListModel.clear();
        for (String server : availableServers) {
            serverListModel.addElement(server);
        }
    }

    private void hostGame() {
        String playerName = JOptionPane.showInputDialog(this, "Enter your name:");
        if (playerName != null && !playerName.trim().isEmpty()) {
            MultiplayerServer server = new MultiplayerServer();
            server.start();

            JFileChooser fileChooser = new JFileChooser("saves/");
            int option = fileChooser.showOpenDialog(this);
            File selectedFile = null;
            if (option == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
            }

            String worldPath = (selectedFile != null) ? selectedFile.getAbsolutePath() : "saves/new_world.json";
            GamePanel gamePanel = new GamePanel(playerName, true, worldPath);
            gamePanel.setServer(server);
            parentMenu.launchGamePanel(gamePanel);
            gamePanel.saveGame(worldPath); // Automatically save the world when hosting
        }
    }

    private void joinGame() {
        String selectedServer = serverList.getSelectedValue();
        if (selectedServer != null) {
            String playerName = JOptionPane.showInputDialog(this, "Enter your name:");
            if (playerName != null && !playerName.trim().isEmpty()) {
                GamePanel gamePanel = new GamePanel(playerName, false, null);
                MultiplayerClient client = new MultiplayerClient(selectedServer, gamePanel);
                gamePanel.setClient(client);
                parentMenu.launchGamePanel(gamePanel);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a server to join.");
        }
    }

    public void stop() {
        running = false;
        if (discoverySocket != null && !discoverySocket.isClosed()) {
            discoverySocket.close();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == hostGameButton) {
            hostGame();
        } else if (e.getSource() == joinGameButton) {
            joinGame();
        } else if (e.getSource() == backButton) {
            parentMenu.showMainMenu();
            serverDiscoveryTimer.cancel(); // Stop refreshing the server list
        }
    }
}
