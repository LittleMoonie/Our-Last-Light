package src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SinglePlayerMenu extends JFrame implements ActionListener {
    private JButton loadGameButton;
    private JButton backButton;
    private MainMenu parentMenu;
    private JList<String> worldList;

    public SinglePlayerMenu(MainMenu parent) {
        this.parentMenu = parent;
        setTitle("Select World");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        loadGameButton = new JButton("Load Selected World");
        backButton = new JButton("Back");

        loadGameButton.addActionListener(this);
        backButton.addActionListener(this);

        // Populate the world list from save files
        File saveDir = new File("saves/");
        String[] savedWorlds = saveDir.list((dir, name) -> name.endsWith(".json"));
        worldList = new JList<>(savedWorlds);

        JPanel panel = new JPanel();
        panel.add(new JScrollPane(worldList));
        panel.add(loadGameButton);
        panel.add(backButton);
        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadGameButton) {
            String selectedWorld = worldList.getSelectedValue();
            if (selectedWorld != null) {
                String playerName = JOptionPane.showInputDialog(this, "Enter your player name:");
                if (playerName != null && !playerName.trim().isEmpty()) {
                    GamePanel gamePanel = new GamePanel(playerName, true, "saves/" + selectedWorld);
                    gamePanel.startGameThread();

                    JFrame gameWindow = new JFrame("Game - " + selectedWorld);
                    gameWindow.setContentPane(gamePanel);
                    gameWindow.setSize(800, 600);
                    gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    gameWindow.setVisible(true);

                    this.dispose();
                    parentMenu.dispose();
                }
            }
        } else if (e.getSource() == backButton) {
            parentMenu.setVisible(true);
            this.dispose();
        }
    }

}
