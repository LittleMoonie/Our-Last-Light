package src;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    // Screen settings
    final int TILE_SIZE = GameConstants.TILE_SIZE;
    int SCREEN_WIDTH;
    int SCREEN_HEIGHT;

    private long gameTime = 0;

    Thread gameThread;
    Player player;
    World world;
    Minimap minimap;

    private BufferedImage loadingIndicator;

    public GamePanel() {
        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = (int) screenSize.getWidth();
        SCREEN_HEIGHT = (int) screenSize.getHeight();

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(this);
        this.setFocusable(true);

        world = new World();
        player = new Player(world);
        minimap = new Minimap(world, player);

        try {
            loadingIndicator = ImageIO.read(getClass().getResourceAsStream("/resources/loading.png"));
        } catch (IOException e) {
            e.printStackTrace();
            // Create a placeholder image if loading fails
            loadingIndicator = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = loadingIndicator.createGraphics();
            g.setColor(new Color(255, 255, 255, 128)); // Semi-transparent white
            g.fillOval(0, 0, 50, 50);
            g.dispose();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Game loop
    public void run() {
        double drawInterval = 1000000000 / 60.0; // 60 FPS
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        player.update();
        minimap.update();
        gameTime += 1;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Disable anti-aliasing for performance
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Calculate the visible area in tile coordinates
        int tilesX = SCREEN_WIDTH / TILE_SIZE;
        int tilesY = SCREEN_HEIGHT / TILE_SIZE;
        int startTileX = (player.x - SCREEN_WIDTH / 2) / TILE_SIZE - 1;
        int startTileY = (player.y - SCREEN_HEIGHT / 2) / TILE_SIZE - 1;
        int endTileX = startTileX + tilesX + 2;
        int endTileY = startTileY + tilesY + 2;

        // Render visible tiles
        for (int i = startTileX; i <= endTileX; i++) {
            for (int j = startTileY; j <= endTileY; j++) {
                int tileX = i * TILE_SIZE;
                int tileY = j * TILE_SIZE;
                // Request immediate tile generation for visible tiles
                Tile tile = world.getTileAt(tileX, tileY);

                if (tile != null) {
                    int screenX = tileX - (player.x - SCREEN_WIDTH / 2);
                    int screenY = tileY - (player.y - SCREEN_HEIGHT / 2);

                    g2.drawImage(tile.getImage(), screenX, screenY, null);
                }
            }
        }

        // Draw player at the center of the screen
        g2.drawImage(player.getImage(),
                SCREEN_WIDTH / 2 - player.size / 2,
                SCREEN_HEIGHT / 2 - player.size / 2,
                player.size, player.size, null);

        // Draw minimap
        minimap.draw(g2);

        // Draw inventory
        player.inventory.draw(g2, SCREEN_WIDTH);

        // Draw loading indicator if tiles are being loaded
        if (world.isLoadingTiles()) {
            int indicatorSize = 50;
            int x = SCREEN_WIDTH - indicatorSize - 20;
            int y = SCREEN_HEIGHT - indicatorSize - 20;
            g2.drawImage(loadingIndicator, x, y, indicatorSize, indicatorSize, null);
        }

        g2.dispose();
    }

    // KeyListener methods
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        player.keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }
}
