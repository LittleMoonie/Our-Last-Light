package src.game.ui;

import src.main.GamePanel;
import src.game.constants.Config;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    public GameWindow(GamePanel gamePanel) {
        setTitle("Our Last Light");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);

        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (graphicsDevice.isFullScreenSupported()) {
            graphicsDevice.setFullScreenWindow(this);
            setContentPane(gamePanel);
            setVisible(true);
        } else {
            System.out.println("Full-screen mode not supported.");
            setSize(Config.FRAME_SIZE);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        gamePanel.setGameWindow(this);
    }

    public void close() {
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        graphicsDevice.setFullScreenWindow(null);
        dispose();
    }
}
