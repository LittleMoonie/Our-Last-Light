package src;

import javax.swing.JFrame;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Create loading screen
        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.show();

        // Initialize game components
        GamePanel gamePanel = new GamePanel();

        // Close loading screen
        loadingScreen.close();

        // Create full-screen window
        JFrame frame = new JFrame("Procedural World Game - Full Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true); // Remove title bar and borders

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode displayMode = gd.getDisplayMode();

        frame.setContentPane(gamePanel);
        frame.pack();

        gd.setFullScreenWindow(frame);
        if (displayMode != null) {
            gd.setDisplayMode(displayMode);
        }

        gamePanel.startGameThread();
    }
}


