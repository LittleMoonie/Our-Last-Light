package src.game.ui.menus;

import src.main.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class SinglePlayerMenu extends JPanel implements ActionListener {
    private JButton loadGameButton;
    private JButton createNewWorldButton;
    private JButton backButton;
    private JList<String> worldList;
    private MainMenu parentMenu;

    public SinglePlayerMenu(MainMenu parent) {
        this.parentMenu = parent;

        // Set up the panel layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(20, 20, 20)); // Dark background

        // Title label
        JLabel titleLabel = new JLabel("Select World", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Initialize buttons
        loadGameButton = createMenuButton("Load Selected World");
        createNewWorldButton = createMenuButton("Create New World");
        backButton = createMenuButton("Back");

        // Load worlds
        File saveDir = new File("saves/");
        if (!saveDir.exists()) saveDir.mkdir();

        String[] savedWorlds = saveDir.list((dir, name) -> name.endsWith(".json"));
        if (savedWorlds == null) savedWorlds = new String[]{};

        worldList = new JList<>(savedWorlds);
        worldList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        worldList.setBackground(new Color(30, 30, 30));
        worldList.setForeground(Color.WHITE);
        JScrollPane worldScrollPane = new JScrollPane(worldList);
        worldScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to the panel
        add(Box.createVerticalGlue()); // Center the layout vertically
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(worldScrollPane);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(loadGameButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createNewWorldButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(backButton);
        add(Box.createVerticalGlue()); // Center the layout vertically
    }

    // Helper method to create styled menu buttons
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        button.addActionListener(this);
        return button;
    }

    private void loadSelectedWorld() {
        String selectedWorld = worldList.getSelectedValue();
        if (selectedWorld != null) {
            String playerName = JOptionPane.showInputDialog(this, "Enter your player name:");
            if (playerName != null && !playerName.trim().isEmpty()) {
                GamePanel gamePanel = new GamePanel(playerName, true, "saves/" + selectedWorld);
                parentMenu.launchGamePanel(gamePanel);
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

            if (newWorldFile.exists()) {
                JOptionPane.showMessageDialog(this, "A world with this name already exists. Please choose a different name.");
                return;
            }

            try {
                if (newWorldFile.createNewFile()) {
                    GamePanel gamePanel = new GamePanel(worldName, true, filePath);
                    parentMenu.launchGamePanel(gamePanel);
                    gamePanel.saveGame(filePath); // Automatically save the newly created world
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create the world file.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while creating the world.");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadGameButton) {
            loadSelectedWorld();
        } else if (e.getSource() == createNewWorldButton) {
            createNewWorld();
        } else if (e.getSource() == backButton) {
            parentMenu.showMainMenu(); // Return to the main menu
        }
    }
}
