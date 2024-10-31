package src;

import javax.swing.JFrame;
import java.awt.*;

public class GameWindow extends JFrame {
    public GameWindow(GamePanel gamePanel) {
        setTitle("Procedural World Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Remove title bar and borders

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode displayMode = gd.getDisplayMode();

        setContentPane(gamePanel);
        pack();

        gd.setFullScreenWindow(this);
        if (displayMode != null) {
            gd.setDisplayMode(displayMode);
        }

        gamePanel.setGameWindow(this);
    }
}
