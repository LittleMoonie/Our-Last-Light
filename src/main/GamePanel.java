package src.main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import src.game.constants.Config;
import src.game.data.GameData;
import src.game.data.SaveHandler;
import src.game.entities.Player;
import src.game.network.MultiplayerClient;
import src.game.network.MultiplayerServer;
import src.game.ui.GameWindow;
import src.game.ui.Minimap;
import src.game.ui.menus.MainMenu;
import src.game.world.World;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    private final int TILE_SIZE = Config.TILE_SIZE;
    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;

    private long gameTime = 0;
    private Thread gameThread;
    private Player player;
    private World world;
    private String worldName;
    private Minimap minimap;
    private BufferedImage loadingIndicator;

    private String playerName;
    private boolean isNewGame;
    private String saveFilePath;

    private JFrame gameWindow;
    private MultiplayerServer server;
    private MultiplayerClient client;
    private ConcurrentHashMap<String, Player> otherPlayers = new ConcurrentHashMap<>();
    private boolean isMultiplayer = false;
    private final MainMenu mainMenu;

    public GamePanel(String playerName, boolean isHost, String saveFilePath, String worldName, MainMenu mainMenu) {
        this.playerName = playerName;
        this.isNewGame = isHost;
        this.saveFilePath = saveFilePath;
        this.mainMenu = mainMenu;
        this.worldName = worldName;
        initializeGame(isHost);
        setFocusable(true);
        requestFocusInWindow();
    }

    private void initializeGame(boolean isHost) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = (int) screenSize.getWidth();
        SCREEN_HEIGHT = (int) screenSize.getHeight();
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Config.MAIN_BACKGROUND_COLOR);
        setDoubleBuffered(true);
        addKeyListener(this);
        setFocusable(true);

        if (isHost) {
            if (!isNewGame && saveFilePath != null) {
                SaveHandler.loadGame(this, saveFilePath);
            } else {
                // Use worldName when creating a new World instance
                world = new World(System.currentTimeMillis(), worldName);
                player = new Player(world, playerName);
                minimap = new Minimap(world, player);

                // Save initial world state
                world.saveWorldData();
            }
        } else {
            player = new Player(null, playerName);
        }

        loadLoadingIndicator();
    }

    private void loadLoadingIndicator() {
        try {
            loadingIndicator = ImageIO.read(getClass().getResourceAsStream(Config.LOADING_IMAGE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
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

    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    public void initializeFromData(GameData gameData) {
        player = new Player(world, gameData.playerName);
        player.x = gameData.playerX;
        player.y = gameData.playerY;

        gameTime = gameData.gameTime;
        world = new World(gameData.worldSeed, worldName);
        player.inventory.setItems(gameData.inventory);

        minimap = new Minimap(world, player);
    }

    public Player getPlayer() {
        return player;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void showInGameMenu() {
        mainMenu.showInGameMenu(this);
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / 60.0;
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

            // Update world periodically to check for regrowth
            world.updateWorld();

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void update() {
        player.update();
        minimap.update();
        gameTime += 1;

        if (player.collecting) {
            player.collectResource();
            player.collecting = false;
        }

        if (isMultiplayer && server != null) {
            server.broadcastPlayerPosition(player.name, player.x, player.y);
        }

        if (isMultiplayer && client != null) {
            client.sendPlayerPosition(player);
        }
    }

    public void stopGameThread() {
        gameThread = null;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> showInGameMenu();
            case KeyEvent.VK_M -> minimap.toggleExpanded();
            case KeyEvent.VK_E -> player.collecting = true;
            default -> player.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }

    public long getGameTime() {
        return gameTime;
    }

    public World getWorld() {
        return world;
    }

    public String getWorldName() {
        return world != null ? "World_" + world.getWorldSeed() : "UnnamedWorld";
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void saveGame(String filePath) {
        SaveHandler.saveGame(this, filePath);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (world != null && player != null) {
            int cameraX = Math.max(0, player.x - SCREEN_WIDTH / 2);
            int cameraY = Math.max(0, player.y - SCREEN_HEIGHT / 2);

            world.render(g2, cameraX, cameraY, SCREEN_WIDTH, SCREEN_HEIGHT);

            g2.drawImage(player.getImage(),
                    SCREEN_WIDTH / 2 - Config.PLAYER_SIZE / 2,
                    SCREEN_HEIGHT / 2 - Config.PLAYER_SIZE / 2,
                    Config.PLAYER_SIZE, Config.PLAYER_SIZE, null);

            if (minimap != null) {
                minimap.draw(g2);
            }

            if (player.inventory != null) {
                player.inventory.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
            }
        } else {
            g.drawString("Loading...", getWidth() / 2, getHeight() / 2);
        }
    }
}
