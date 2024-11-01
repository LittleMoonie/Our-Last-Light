package src.game.ui.menus;

import src.main.GamePanel;
import src.game.constants.Config;

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
        button.setPreferredSize(new Dimension(Config.BUTTON_WIDTH, 40));
        button.setFont(Config.MAIN_FONT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Config.BUTTON_BACKGROUND_COLOR);
        button.setForeground(Config.BUTTON_TEXT_COLOR);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnButton) {
            parentMenu.returnToGame(); // Return to the game
        } else if (e.getSource() == saveGameButton) {
            String filePath = Config.SAVE_DIRECTORY + gamePanel.getWorldName() + ".json";
            gamePanel.saveGame(filePath); // Save the game
            parentMenu.returnToGame(); // Return to the game after saving
        } else if (e.getSource() == exitGameButton) {
            System.exit(0); // Exit the game
        }
    }
}
