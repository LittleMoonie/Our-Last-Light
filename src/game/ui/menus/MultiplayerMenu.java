package src.game.ui.menus;

import src.main.GamePanel;
import src.game.network.MultiplayerClient;
import src.game.network.MultiplayerServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MultiplayerMenu extends JFrame implements ActionListener {
    private JButton hostGameButton;
    private JButton joinGameButton;
    private JButton backButton;
    private MainMenu parentMenu;

    public MultiplayerMenu(MainMenu parent) {
        this.parentMenu = parent;
        setTitle("Multiplayer (LAN)");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        hostGameButton = createMenuButton("Host Game");
        joinGameButton = createMenuButton("Join Game");
        backButton = createMenuButton("Back");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(hostGameButton);
        panel.add(joinGameButton);
        panel.add(backButton);
        add(panel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == hostGameButton) {
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
                createGameWindow(gamePanel);
            }
        } else if (e.getSource() == joinGameButton) {
            String serverAddress = JOptionPane.showInputDialog(this, "Enter server IP address:");
            if (serverAddress != null && !serverAddress.trim().isEmpty()) {
                String playerName = JOptionPane.showInputDialog(this, "Enter your name:");
                if (playerName != null && !playerName.trim().isEmpty()) {
                    GamePanel gamePanel = new GamePanel(playerName, false, null);
                    MultiplayerClient client = new MultiplayerClient(serverAddress, gamePanel);
                    gamePanel.setClient(client);
                    createGameWindow(gamePanel);
                }
            }
        } else if (e.getSource() == backButton) {
            parentMenu.setVisible(true);
            this.dispose();
        }
    }

    private void createGameWindow(GamePanel gamePanel) {
        JFrame gameWindow = new JFrame("Multiplayer Game");
        gameWindow.setContentPane(gamePanel);
        gameWindow.setSize(800, 600);
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setVisible(true);
        this.dispose();
        parentMenu.dispose();
        gamePanel.startGameThread();
    }
}
