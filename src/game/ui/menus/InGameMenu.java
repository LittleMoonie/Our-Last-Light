package src.game.ui.menus;

import src.main.GamePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InGameMenu extends JPanel implements ActionListener {
    private JButton returnButton;
    private JButton saveGameButton;
    private JButton exitGameButton;
    private GamePanel gamePanel;
    private MainMenu parentMenu;

    public InGameMenu(MainMenu parentMenu, GamePanel gamePanel) {
        this.parentMenu = parentMenu;
        this.gamePanel = gamePanel;

        returnButton = createMenuButton("Return to Game");
        saveGameButton = createMenuButton("Save Game");
        exitGameButton = createMenuButton("Exit Game");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(returnButton);
        add(saveGameButton);
        add(exitGameButton);
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
            parentMenu.returnToGame(); // Go back to the game
        } else if (e.getSource() == saveGameButton) {
            String filePath = "saves/" + gamePanel.getWorldName() + ".json";
            gamePanel.saveGame(filePath); // Automatically save the game
        } else if (e.getSource() == exitGameButton) {
            System.exit(0);
        }
    }
}
