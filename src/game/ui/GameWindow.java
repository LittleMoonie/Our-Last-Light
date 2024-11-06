package src.game.ui;

import src.main.GamePanel;
import src.game.constants.Config;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    public GameWindow(GamePanel gamePanel) {
        setTitle("Our Last Light - World Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Config.FRAME_SIZE);
        setLocationRelativeTo(null);

        add(gamePanel);
        setVisible(true);
    }
}
