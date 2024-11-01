package src.game.ui.menus;

import src.main.GamePanel;
import src.game.constants.Config;

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

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Config.MAIN_BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Select World", SwingConstants.CENTER);
        titleLabel.setFont(Config.LABEL_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loadGameButton = createMenuButton("Load Selected World");
        createNewWorldButton = createMenuButton("Create New World");
        backButton = createMenuButton("Back");

        File saveDir = new File(Config.SAVE_DIRECTORY);
        if (!saveDir.exists()) saveDir.mkdir();

        String[] savedWorlds = saveDir.list((dir, name) -> name.endsWith(".json"));
        if (savedWorlds == null) savedWorlds = new String[]{};

        worldList = new JList<>(savedWorlds);
        worldList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        worldList.setBackground(new Color(30, 30, 30));
        worldList.setForeground(Color.WHITE);
        JScrollPane worldScrollPane = new JScrollPane(worldList);
        worldScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(worldScrollPane);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(loadGameButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createNewWorldButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(backButton);
        add(Box.createVerticalGlue());
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT));
        button.setMaximumSize(new Dimension(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(Config.MAIN_FONT);
        button.setBackground(Config.BUTTON_BACKGROUND_COLOR);
        button.setForeground(Config.BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        button.addActionListener(this);
        return button;
    }

    private void loadSelectedWorld() {
        String selectedWorld = worldList.getSelectedValue();
        if (selectedWorld != null) {
            String playerName = JOptionPane.showInputDialog(this, Config.ENTER_PLAYER_NAME_PROMPT);
            if (playerName != null && !playerName.trim().isEmpty()) {
                GamePanel gamePanel = new GamePanel(playerName, true, Config.SAVE_DIRECTORY + selectedWorld, this.parentMenu);

                // Set GamePanel as the content pane and start the game
                JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                mainFrame.setContentPane(gamePanel);
                mainFrame.revalidate();
                mainFrame.repaint();
                gamePanel.startGameThread();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a world to load.");
        }
    }

    private void createNewWorld() {
        String worldName = JOptionPane.showInputDialog(this, Config.ENTER_WORLD_NAME_PROMPT);
        if (worldName != null && !worldName.trim().isEmpty()) {
            String filePath = Config.SAVE_DIRECTORY + worldName + ".json";
            File newWorldFile = new File(filePath);

            if (newWorldFile.exists()) {
                JOptionPane.showMessageDialog(this, Config.WORLD_EXISTS_MESSAGE);
                return;
            }

            try {
                if (newWorldFile.createNewFile()) {
                    GamePanel gamePanel = new GamePanel(worldName, true, filePath, this.parentMenu);

                    // Set GamePanel as the content pane and start the game
                    JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    mainFrame.setContentPane(gamePanel);
                    mainFrame.revalidate();
                    mainFrame.repaint();
                    gamePanel.startGameThread();

                    gamePanel.saveGame(filePath);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create the world file.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, Config.CREATE_WORLD_ERROR_MESSAGE);
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
            parentMenu.showMainMenu();
        }
    }
}
