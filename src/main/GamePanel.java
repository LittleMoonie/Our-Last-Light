package src.main;

import src.game.constants.Config;
import src.game.world.World;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    private World world;
    private boolean isLoading = true;
    private Thread gameThread;

    public GamePanel() {
        world = new World(System.currentTimeMillis());
        setPreferredSize(new Dimension(Config.FRAME_SIZE.width, Config.FRAME_SIZE.height));
        setBackground(Config.MAIN_BACKGROUND_COLOR);
        setDoubleBuffered(true);
        setFocusable(true);

        // Start the game thread directly in the constructor
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (isLoading) {
            // Display loading progress
            String loadingText = "Loading... " + (int) (world.getGenerationProgress() * 100) + "%";
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            g2.drawString(loadingText, getWidth() / 2 - 50, getHeight() / 2);
        } else {
            // Render the world once loading is complete
            world.render(g2, getWidth(), getHeight());
        }
    }

    @Override
    public void run() {
        // Game loop to handle loading and repainting
        while (isLoading) {
            // Generate the next chunk to progress loading
            world.generateNextChunk();

            // Check if loading is complete
            if (world.getGenerationProgress() >= 1.0) {
                isLoading = false;
            }

            // Repaint the panel to update the loading screen
            repaint();

            // Control the speed of the loop to avoid overloading the CPU
            try {
                Thread.sleep(16); // Approx 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // After loading is complete, continuously repaint to render the game
        while (true) {
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
