package src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InGameMenu extends JFrame implements ActionListener {
    private JButton returnButton;
    private JButton openToLanButton;
    private JButton optionsButton;
    private JButton saveGameButton;
    private JButton leaveWorldButton;
    private JButton exitGameButton;
    private GamePanel gamePanel;
    private JFrame gameWindow;

    public InGameMenu(JFrame gameWindow, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.gameWindow = gameWindow;

        setTitle("In-Game Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        returnButton = new JButton("Return to Game");
        openToLanButton = new JButton("Open to LAN");
        optionsButton = new JButton("Options");
        saveGameButton = new JButton("Save Game");
        leaveWorldButton = new JButton("Leave World");
        exitGameButton = new JButton("Exit Game");

        returnButton.addActionListener(this);
        openToLanButton.addActionListener(this);
        optionsButton.addActionListener(this);
        saveGameButton.addActionListener(this);
        leaveWorldButton.addActionListener(this);
        exitGameButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(returnButton);
        panel.add(openToLanButton);
        panel.add(optionsButton);
        panel.add(saveGameButton);
        panel.add(leaveWorldButton);
        panel.add(exitGameButton);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnButton) {
            dispose();
        } else if (e.getSource() == openToLanButton) {
            // Implement open to LAN functionality
        } else if (e.getSource() == optionsButton) {
            OptionsMenu optionsMenu = new OptionsMenu(this);
            optionsMenu.setVisible(true);
        } else if (e.getSource() == saveGameButton) {
            String saveFilePath = JOptionPane.showInputDialog(this, "Enter save file name:");
            if (saveFilePath != null && !saveFilePath.trim().isEmpty()) {
                gamePanel.saveGame(saveFilePath);
            }
        } else if (e.getSource() == leaveWorldButton) {
            gamePanel.stopGameThread();
            dispose();
            JFrame mainMenu = new MainMenu(); // Create a new MainMenu instance
            mainMenu.setVisible(true);
            gameWindow.dispose(); // Close the main game window
        } else if (e.getSource() == exitGameButton) {
            System.exit(0);
        }
    }
}
