package src.game.ui.menus;

import src.game.ui.GameWindow;
import src.main.GamePanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class SinglePlayerMenu extends JFrame implements ActionListener {
    private JButton loadGameButton;
    private JButton createNewWorldButton;
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
        createNewWorldButton = new JButton("Create New World");
        backButton = new JButton("Back");

        loadGameButton.addActionListener(this);
        createNewWorldButton.addActionListener(this);
        backButton.addActionListener(this);

        // Ensure the "saves" directory exists
        File saveDir = new File("saves/");
        if (!saveDir.exists()) {
            saveDir.mkdir(); // Create the "saves" folder if it doesn't exist
        }

        // Only load JSON files from "/saves"
        String[] savedWorlds = saveDir.list((dir, name) -> name.endsWith(".json"));
        if (savedWorlds == null || savedWorlds.length == 0) {
            savedWorlds = new String[] {}; // Use an empty array if no JSON files found
        }

        worldList = new JList<>(savedWorlds);
        worldList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Single selection

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JScrollPane(worldList));
        panel.add(loadGameButton);
        panel.add(createNewWorldButton);
        panel.add(backButton);
        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadGameButton) {
            loadSelectedWorld();
        } else if (e.getSource() == createNewWorldButton) {
            createNewWorld();
        } else if (e.getSource() == backButton) {
            parentMenu.setVisible(true);
            this.dispose();
        }
    }

    private void loadSelectedWorld() {
        String selectedWorld = worldList.getSelectedValue();
        if (selectedWorld != null) {
            String playerName = JOptionPane.showInputDialog(this, "Enter your player name:");
            if (playerName != null && !playerName.trim().isEmpty()) {
                GamePanel gamePanel = new GamePanel(playerName, true, "saves/" + selectedWorld);
                new GameWindow(gamePanel); // Opens in full-screen mode
                gamePanel.startGameThread();
                this.dispose();
                parentMenu.dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a world to load.");
        }
    }

    private void createNewWorld() {
        String worldName = JOptionPane.showInputDialog(this, "Enter a name for your new world:");
        if (worldName != null && !worldName.trim().isEmpty()) {
            String filePath = "saves/" + worldName + ".json";
            File newWorldFile = new File(filePath);

            // Check if the world file already exists
            if (newWorldFile.exists()) {
                JOptionPane.showMessageDialog(this, "A world with this name already exists. Please choose a different name.");
                return;
            }

            try {
                // Create the new world file
                if (newWorldFile.createNewFile()) {
                    // Optionally, auto-select and load the new world
                    GamePanel gamePanel = new GamePanel(worldName, true, filePath);
                    new GameWindow(gamePanel); // Opens in full-screen mode
                    gamePanel.startGameThread();
                    this.dispose();
                    parentMenu.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create the world file.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while creating the world.");
            }
        }
    }
}
