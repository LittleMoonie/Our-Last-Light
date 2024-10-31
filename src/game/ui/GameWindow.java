package src.game.ui;

import src.main.GamePanel;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private GraphicsDevice graphicsDevice;

    public GameWindow(GamePanel gamePanel) {
        setTitle("Procedural World Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setup full-screen
        graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        setUndecorated(true); // Remove window borders for true full-screen

        // Ensure the device supports full-screen
        if (graphicsDevice.isFullScreenSupported()) {
            graphicsDevice.setFullScreenWindow(this);
            DisplayMode displayMode = graphicsDevice.getDisplayMode();
            setContentPane(gamePanel);
            pack();
            setDisplayMode(displayMode);
        } else {
            System.out.println("Full-screen mode not supported.");
            setSize(800, 600);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        gamePanel.setGameWindow(this);
    }

    private void setDisplayMode(DisplayMode displayMode) {
        if (displayMode != null) {
            graphicsDevice.setDisplayMode(displayMode);
        }
    }

    public void close() {
        graphicsDevice.setFullScreenWindow(null); // Exit full-screen mode
    }
}
