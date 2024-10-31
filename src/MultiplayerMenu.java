package src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        hostGameButton = new JButton("Host Game");
        joinGameButton = new JButton("Join Game");
        backButton = new JButton("Back");

        hostGameButton.addActionListener(this);
        joinGameButton.addActionListener(this);
        backButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(hostGameButton);
        panel.add(joinGameButton);
        panel.add(backButton);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == hostGameButton) {
            String playerName = JOptionPane.showInputDialog(this, "Enter your name:");
            if (playerName != null && !playerName.trim().isEmpty()) {
                MultiplayerServer server = new MultiplayerServer();
                server.start();

                // Show IP address in a dialog
                String localIpAddress = server.getLocalIpAddress();
                JOptionPane.showMessageDialog(this, "LAN server hosted on IP: " + localIpAddress,
                        "Hosting LAN Game", JOptionPane.INFORMATION_MESSAGE);

                GamePanel gamePanel = new GamePanel(playerName, true);
                gamePanel.setServer(server);
                JFrame gameWindow = new JFrame("Hosted Multiplayer Game");
                gameWindow.setContentPane(gamePanel);
                gameWindow.setSize(800, 600);
                gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameWindow.setVisible(true);
                this.dispose();
                parentMenu.dispose();
                gamePanel.startGameThread();
            }
        }  else if (e.getSource() == joinGameButton) {
            String serverAddress = JOptionPane.showInputDialog(this, "Enter server IP address:");
            if (serverAddress != null && !serverAddress.trim().isEmpty()) {
                String playerName = JOptionPane.showInputDialog(this, "Enter your name:");
                if (playerName != null && !playerName.trim().isEmpty()) {
                    GamePanel gamePanel = new GamePanel(playerName, false);
                    MultiplayerClient client = new MultiplayerClient(serverAddress, gamePanel);
                    gamePanel.setClient(client);
                    JFrame gameWindow = new JFrame("Joined Multiplayer Game");
                    gameWindow.setContentPane(gamePanel);
                    gameWindow.setSize(800, 600);
                    gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    gameWindow.setVisible(true);
                    this.dispose();
                    parentMenu.dispose();
                    gamePanel.startGameThread();
                }
            }
        } else if (e.getSource() == backButton) {
            parentMenu.setVisible(true);
            this.dispose();
        }
    }
}
