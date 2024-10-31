package src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SinglePlayerMenu extends JFrame implements ActionListener {
    private JButton newGameButton;
    private JButton loadGameButton;
    private JButton backButton;
    private MainMenu parentMenu;

    public SinglePlayerMenu(MainMenu parent) {
        this.parentMenu = parent;
        setTitle("Single Player Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        newGameButton = new JButton("New Game");
        loadGameButton = new JButton("Load Game");
        backButton = new JButton("Back");

        newGameButton.addActionListener(this);
        loadGameButton.addActionListener(this);
        backButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(newGameButton);
        panel.add(loadGameButton);
        panel.add(backButton);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameButton) {
            // Start a new game
            GamePanel gamePanel = new GamePanel("PlayerName", true);
            gamePanel.startGameThread();
            JFrame gameWindow = new JFrame("Single Player Game");
            gameWindow.setContentPane(gamePanel);
            gameWindow.setSize(800, 600);
            gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameWindow.setVisible(true);
            this.dispose();
            parentMenu.dispose();
        } else if (e.getSource() == loadGameButton) {
            // Load an existing game
            String saveFilePath = JOptionPane.showInputDialog(this, "Enter save file name:");
            if (saveFilePath != null && !saveFilePath.trim().isEmpty()) {
                GamePanel gamePanel = new GamePanel(saveFilePath, false);
                gamePanel.startGameThread();
                JFrame gameWindow = new JFrame("Loaded Game");
                gameWindow.setContentPane(gamePanel);
                gameWindow.setSize(800, 600);
                gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameWindow.setVisible(true);
                this.dispose();
                parentMenu.dispose();
            }
        } else if (e.getSource() == backButton) {
            parentMenu.setVisible(true);
            this.dispose();
        }
    }
}
