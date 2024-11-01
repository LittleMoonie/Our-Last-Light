package src.game.ui.menus;

import com.sun.tools.javac.Main;
import src.main.GamePanel;
import src.game.constants.Config;
import src.game.network.MultiplayerClient;
import src.game.network.MultiplayerServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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

    public MultiplayerMenu(MainMenu parent) {
        this.parentMenu = parent;

        hostGameButton = createMenuButton("Host Game");
        joinGameButton = createMenuButton("Join Selected Game");
        backButton = createMenuButton("Back");

        serverListModel = new DefaultListModel<>();
        serverList = new JList<>(serverListModel);
        serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JScrollPane(serverList));
        add(hostGameButton);
        add(joinGameButton);
        add(backButton);

        startServerDiscovery();
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setFont(Config.MAIN_FONT);
        button.setBackground(Config.BUTTON_BACKGROUND_COLOR);
        button.setForeground(Config.BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.addActionListener(this);
        return button;
    }

    private void startServerDiscovery() {
        serverDiscoveryTimer = new Timer();
        serverDiscoveryTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<String> availableServers = MultiplayerClient.discoverLANServers();
                updateServerList(availableServers);
            }
        }, 0, 3000);
    }

    private void updateServerList(List<String> availableServers) {
        serverListModel.clear();
        for (String server : availableServers) {
            serverListModel.addElement(server);
        }
    }

    private void hostGame() {
        String playerName = JOptionPane.showInputDialog(this, Config.ENTER_PLAYER_NAME_PROMPT);
        if (playerName != null && !playerName.trim().isEmpty()) {
            MultiplayerServer server = new MultiplayerServer();
            server.start();

            JFileChooser fileChooser = new JFileChooser(Config.SAVE_DIRECTORY);
            int option = fileChooser.showOpenDialog(this);
            File selectedFile = null;
            if (option == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
            }

            String worldPath = (selectedFile != null) ? selectedFile.getAbsolutePath() : Config.SAVE_DIRECTORY + "new_world.json";
            GamePanel gamePanel = new GamePanel(playerName, true, worldPath, this.parentMenu);
            gamePanel.setServer(server);
            parentMenu.launchGamePanel(playerName, true, worldPath);
            gamePanel.saveGame(worldPath);
        }
    }

    private void joinGame() {
        String selectedServer = serverList.getSelectedValue();
        if (selectedServer != null) {
            String playerName = JOptionPane.showInputDialog(this, Config.ENTER_PLAYER_NAME_PROMPT);
            if (playerName != null && !playerName.trim().isEmpty()) {
                GamePanel gamePanel = new GamePanel(playerName, false, null, this.parentMenu);
                MultiplayerClient client = new MultiplayerClient(selectedServer, gamePanel);
                gamePanel.setClient(client);
                parentMenu.launchGamePanel(playerName, false, null);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a server to join.");
        }
    }

    public void stop() {
        if (serverDiscoveryTimer != null) {
            serverDiscoveryTimer.cancel();
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
            stop();
        }
    }
}
