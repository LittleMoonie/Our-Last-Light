package src.game.ui.menus;

import src.main.GamePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame implements ActionListener {
    private JPanel mainPanel;
    private OptionsMenu optionsMenu;
    private MultiplayerMenu multiplayerMenu;
    private SinglePlayerMenu singlePlayerMenu;
    private GamePanel currentGamePanel;
    private InGameMenu inGameMenu;

    public MainMenu() {
        setTitle("Main Menu");
        setSize(800, 600); // Adjust to your game's preferred resolution
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize other menus
        optionsMenu = new OptionsMenu(this);
        multiplayerMenu = new MultiplayerMenu(this);
        singlePlayerMenu = new SinglePlayerMenu(this);

        // Main panel setup with BoxLayout for vertical alignment
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(20, 20, 20)); // Dark background color

        // Add title label
        JLabel titleLabel = new JLabel("Don't Starve Alone", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalGlue()); // Spacer to center contents vertically
        mainPanel.add(titleLabel);

        // Add menu buttons
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        mainPanel.add(createMenuButton("Single Player"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createMenuButton("Multiplayer"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createMenuButton("Options"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createMenuButton("Exit"));
        mainPanel.add(Box.createVerticalGlue()); // Spacer to center contents vertically

        // Set the main panel as the content pane
        setContentPane(mainPanel);
        setVisible(true);
    }

    // Helper method to create a styled menu button
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50)); // Ensure consistent button sizing
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        button.addActionListener(this);
        return button;
    }

    // Show options menu
    public void showOptionsMenu() {
        setContentPane(optionsMenu);
        revalidate();
        repaint();
    }

    // Show single-player menu
    public void showSinglePlayerMenu() {
        setContentPane(singlePlayerMenu);
        revalidate();
        repaint();
    }

    // Show multiplayer menu
    public void showMultiplayerMenu() {
        setContentPane(multiplayerMenu);
        revalidate();
        repaint();
    }

    // Show main menu
    public void showMainMenu() {
        setContentPane(mainPanel);
        revalidate();
        repaint();
    }

    // Display the in-game menu
    public void showInGameMenu(GamePanel gamePanel) {
        if (inGameMenu == null) {
            inGameMenu = new InGameMenu(this, gamePanel);
        }
        setContentPane(inGameMenu);
        revalidate();
        repaint();
    }

    // Return to the game panel after closing in-game menu
    public void returnToGame() {
        if (currentGamePanel != null) {
            setContentPane(currentGamePanel);
            revalidate();
            repaint();
        }
    }

    // Launch the game with the specified GamePanel
    public void launchGamePanel(GamePanel gamePanel) {
        this.currentGamePanel = gamePanel;
        setContentPane(gamePanel);
        revalidate();
        repaint();
        gamePanel.startGameThread();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = ((JButton) e.getSource()).getText();
        switch (action) {
            case "Single Player":
                showSinglePlayerMenu();
                break;
            case "Multiplayer":
                showMultiplayerMenu();
                break;
            case "Options":
                showOptionsMenu();
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }
}
