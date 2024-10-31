package src.game.ui.menus;

import src.main.GamePanel;

import javax.swing.*;
import java.awt.*;
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

        returnButton = createMenuButton("Return to Game");
        openToLanButton = createMenuButton("Open to LAN");
        optionsButton = createMenuButton("Options");
        saveGameButton = createMenuButton("Save Game");
        leaveWorldButton = createMenuButton("Leave World");
        exitGameButton = createMenuButton("Exit Game");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(returnButton);
        panel.add(openToLanButton);
        panel.add(optionsButton);
        panel.add(saveGameButton);
        panel.add(leaveWorldButton);
        panel.add(exitGameButton);

        add(panel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnButton) {
            dispose();
        } else if (e.getSource() == openToLanButton) {
            // LAN functionality placeholder
        } else if (e.getSource() == optionsButton) {
            OptionsMenu optionsMenu = new OptionsMenu(this);
            optionsMenu.setVisible(true);
        } else if (e.getSource() == saveGameButton) {
            String saveFileName = JOptionPane.showInputDialog(this, "Enter save file name:");
            if (saveFileName != null && !saveFileName.trim().isEmpty()) {
                gamePanel.saveGame("saves/" + saveFileName + ".json");
            }
        } else if (e.getSource() == leaveWorldButton) {
            gamePanel.stopGameThread();
            gameWindow.dispose();
            new MainMenu().setVisible(true);
            dispose();
        } else if (e.getSource() == exitGameButton) {
            System.exit(0);
        }
    }
}
