package src.game.ui;

import src.main.GamePanel;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    public GameWindow(GamePanel gamePanel) {
        setTitle("Don't Starve Alone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Full-screen

        // Get the screen device
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        // Ensure the device supports full-screen
        if (graphicsDevice.isFullScreenSupported()) {
            graphicsDevice.setFullScreenWindow(this);
            setContentPane(gamePanel);
            setVisible(true);
        } else {
            System.out.println("Full-screen mode not supported.");
            setSize(800, 600);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        gamePanel.setGameWindow(this); // Pass GameWindow to GamePanel
    }

    public void close() {
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        graphicsDevice.setFullScreenWindow(null);
        dispose();
    }
}
