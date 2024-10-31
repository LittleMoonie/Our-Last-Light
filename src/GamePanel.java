package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    private String playerName;
    private boolean isNewGame;
    private String saveFilePath;

    private ObjectMapper objectMapper = new ObjectMapper();

    private JFrame gameWindow;

    // Multiplayer fields
    private MultiplayerServer server;
    private MultiplayerClient client;
    private ConcurrentHashMap<String, Player> otherPlayers = new ConcurrentHashMap<>();

    private boolean isMultiplayer = false;


    public GamePanel(String playerName, boolean isHost, String saveFilePath) {
        this.playerName = playerName;
        this.isNewGame = isHost; // Only host initializes a new game or loads save data
        this.saveFilePath = saveFilePath;
        initializeGame(isHost);
    }

    private void initializeGame(boolean isHost) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = (int) screenSize.getWidth();
        SCREEN_HEIGHT = (int) screenSize.getHeight();

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(this);
        this.setFocusable(true);

        // Host initializes or loads the world first, then creates the player
        if (isHost) {
            if (!isNewGame && saveFilePath != null) {
                loadGame(saveFilePath);
            } else {
                long worldSeed = System.currentTimeMillis();
                world = new World(worldSeed);  // Initialize world first
                player = new Player(world, playerName);  // Create player after world is ready
                minimap = new Minimap(world, player);
            }
        } else {
            player = new Player(null, playerName);  // Client mode: player with no world
        }

        loadLoadingIndicator();
    }


    private void loadLoadingIndicator() {
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

    public void setServer(MultiplayerServer server) {
        this.server = server;
        this.isMultiplayer = true;
    }

    public void setClient(MultiplayerClient client) {
        this.client = client;
        this.isMultiplayer = true;
    }

    public Player getPlayer() {
        return player;
    }

    public void updateOtherPlayer(String playerName, int x, int y) {
        Player otherPlayer = otherPlayers.get(playerName);
        if (otherPlayer == null) {
            otherPlayer = new Player(null, playerName); // World is null for now
            otherPlayers.put(playerName, otherPlayer);
        }
        otherPlayer.x = x;
        otherPlayer.y = y;
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

        // Host-specific logic
        if (isMultiplayer && server != null) {
            server.broadcastPlayerPosition(player.name, player.x, player.y);
        }

        // Client-specific logic
        if (isMultiplayer && client != null) {
            client.sendPlayerPosition(player);
        }
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
                Tile tile = world.getTileAt(tileX, tileY, true);

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

        // Draw other players in multiplayer mode
        if (isMultiplayer) {
            if (server != null) {
                // Draw other players connected to the server
                for (Player otherPlayer : otherPlayers.values()) {
                    drawOtherPlayer(g2, otherPlayer);
                }
            }
            if (client != null) {
                // Draw other players received from the server
                for (Player otherPlayer : client.getOtherPlayers().values()) { // Use client.getOtherPlayers() here
                    drawOtherPlayer(g2, otherPlayer);
                }
            }
        }


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

    private void drawOtherPlayer(Graphics2D g2, Player otherPlayer) {
        int screenX = otherPlayer.x - player.x + SCREEN_WIDTH / 2 - otherPlayer.size / 2;
        int screenY = otherPlayer.y - player.y + SCREEN_HEIGHT / 2 - otherPlayer.size / 2;
        g2.drawImage(otherPlayer.getImage(), screenX, screenY, otherPlayer.size, otherPlayer.size, null);
    }

    public void saveGame(String saveFilePath) {
        GameData gameData = new GameData();
        gameData.playerX = player.x;
        gameData.playerY = player.y;
        gameData.playerName = player.name;
        gameData.gameTime = gameTime;
        gameData.worldSeed = world.getWorldSeed();
        gameData.inventory = player.inventory.getItems(); // Save the inventory

        try {
            objectMapper.writeValue(new File(saveFilePath), gameData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGame(String saveFilePath) {
        try {
            GameData gameData = objectMapper.readValue(new File(saveFilePath), GameData.class);
            // Initialize game state from loaded data
            world = new World(gameData.worldSeed);
            player = new Player(world, gameData.playerName);
            player.x = gameData.playerX;
            player.y = gameData.playerY;
            gameTime = gameData.gameTime;
            minimap = new Minimap(world, player);

            // Load the inventory
            if (gameData.inventory != null) {
                player.inventory.setItems(gameData.inventory);
            } else {
                player.inventory.setItems(new HashMap<>());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGameWindow(JFrame gameWindow) {
        this.gameWindow = gameWindow;
    }

    public void stopGameThread() {
        gameThread = null;
    }

    // KeyListener methods
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        player.keyPressed(e);

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            InGameMenu inGameMenu = new InGameMenu(gameWindow, this);
            inGameMenu.setVisible(true);
        }

    }

    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }
}
