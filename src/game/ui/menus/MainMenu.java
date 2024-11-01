package src.game.ui.menus;

import src.main.GamePanel;
import src.game.constants.Config;

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
        setSize(Config.FRAME_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        optionsMenu = new OptionsMenu(this);
        multiplayerMenu = new MultiplayerMenu(this);
        singlePlayerMenu = new SinglePlayerMenu(this);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Config.MAIN_BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Don't Starve Alone", SwingConstants.CENTER);
        titleLabel.setFont(Config.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(createMenuButton("Single Player"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createMenuButton("Multiplayer"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createMenuButton("Options"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createMenuButton("Exit"));
        mainPanel.add(Box.createVerticalGlue());

        setContentPane(mainPanel);
        setVisible(true);
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

    public void showOptionsMenu() {
        setContentPane(optionsMenu);
        revalidate();
        repaint();
    }

    public void showSinglePlayerMenu() {
        setContentPane(singlePlayerMenu);
        revalidate();
        repaint();
    }

    public void showMultiplayerMenu() {
        setContentPane(multiplayerMenu);
        revalidate();
        repaint();
    }

    public void showMainMenu() {
        setContentPane(mainPanel);
        revalidate();
        repaint();
    }

    public void showInGameMenu(GamePanel gamePanel) {
        if (inGameMenu == null) {
            inGameMenu = new InGameMenu(this, gamePanel);  // Create only if not existing
        }
        setContentPane(inGameMenu);
        revalidate();
        repaint();
    }

    public void returnToGame() {
        if (currentGamePanel != null) {  // Ensure currentGamePanel is set
            setContentPane(currentGamePanel);
            revalidate();
            repaint();
        }
    }

    public void launchGamePanel(GamePanel gamePanel, String worldName) {
        // Stop the existing game thread if switching to a new game
        if (currentGamePanel != null) {
            currentGamePanel.stopGameThread();
        }

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
