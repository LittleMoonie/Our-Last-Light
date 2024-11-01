package src.main;

import src.game.constants.GameConstants;
import src.game.data.SaveHandler;
import src.game.entities.Player;
import src.game.network.MultiplayerClient;
import src.game.network.MultiplayerServer;
import src.game.ui.menus.InGameMenu;
import src.game.ui.Minimap;
import src.game.ui.menus.MainMenu;
import src.game.world.Tile;
import src.game.world.World;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    private final int TILE_SIZE = GameConstants.TILE_SIZE;
    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;

    private long gameTime = 0;
    private Thread gameThread;
    private Player player;
    private World world;
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

    public GamePanel(String playerName, boolean isHost, String saveFilePath) {
        this.playerName = playerName;
        this.isNewGame = isHost;
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

        if (isHost) {
            if (!isNewGame && saveFilePath != null) {
                SaveHandler.loadGame(this, saveFilePath);
            } else {
                world = new World(System.currentTimeMillis());
                player = new Player(world, playerName);
                minimap = new Minimap(world, player);
            }
        } else {
            player = new Player(null, playerName);
        }

        loadLoadingIndicator();
    }

    private void loadLoadingIndicator() {
        try {
            loadingIndicator = ImageIO.read(getClass().getResourceAsStream("/resources/loading.png"));
        } catch (IOException e) {
            loadingIndicator = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = loadingIndicator.createGraphics();
            g.setColor(new Color(255, 255, 255, 128));
            g.fillOval(0, 0, 50, 50);
            g.dispose();
        }
    }

    public void saveGame() {
        SaveHandler.saveGame(this, saveFilePath);
    }

    public void loadGame() {
        SaveHandler.loadGame(this, saveFilePath);
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

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

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

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameWindow instanceof MainMenu) {
            ((MainMenu) gameWindow).showInGameMenu(this);
        }
    }

    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }

    public long getGameTime() {
        return gameTime;
    }

    public World getWorld() {
        return world;
    }
}
