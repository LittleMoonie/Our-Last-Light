package src.game.ui.menus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame implements ActionListener {
    private JButton singlePlayerButton;
    private JButton multiplayerButton;
    private JButton optionsButton;
    private JButton exitButton;

    public MainMenu() {
        setTitle("Main Menu");
        setSize(800, 600); // Initial window size for menus
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Title Logo
        JLabel titleLabel = new JLabel("Game Title", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);

        // Background panel
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.setBackground(new Color(20, 20, 20)); // Dark background like Minecraft

        // Center alignment
        backgroundPanel.add(Box.createVerticalGlue()); // Push items to center

        // Buttons with Minecraft-like styling
        singlePlayerButton = createMenuButton("Single Player");
        multiplayerButton = createMenuButton("Multiplayer");
        optionsButton = createMenuButton("Options");
        exitButton = createMenuButton("Exit");

        backgroundPanel.add(titleLabel);
        backgroundPanel.add(singlePlayerButton);
        backgroundPanel.add(multiplayerButton);
        backgroundPanel.add(optionsButton);
        backgroundPanel.add(exitButton);

        backgroundPanel.add(Box.createVerticalGlue()); // Push items to center

        add(backgroundPanel);

        // Button actions
        singlePlayerButton.addActionListener(this);
        multiplayerButton.addActionListener(this);
        optionsButton.addActionListener(this);
        exitButton.addActionListener(this);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == singlePlayerButton) {
            new SinglePlayerMenu(this).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == multiplayerButton) {
            new MultiplayerMenu(this).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == optionsButton) {
            new OptionsMenu(this).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }
}
