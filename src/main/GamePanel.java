package src.main;

import src.game.components.*;
import src.game.components.stats.*;
import src.game.constants.Config;
import src.game.data.GameData;
import src.game.entities.PlayerEntity;
import src.game.entities.Entity;
import src.game.network.MultiplayerClient;
import src.game.network.MultiplayerServer;
import src.game.systems.*;
import src.game.systems.stats.*;
import src.game.world.World;
import src.game.ui.menus.MainMenu;
import src.game.ui.GameWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.System;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    private PlayerEntity playerEntity;
    private World world;
    private List<Entity> entities; // List of all entities in the game

    // Game systems
    private InputSystem inputSystem;
    private MovementSystem movementSystem;
    private InventorySystem inventorySystem;
    private MinimapSystem minimapSystem;
    private HealthSystem healthSystem;
    private HungerSystem hungerSystem;
    private ThirstSystem thirstSystem;
    private StaminaSystem staminaSystem;
    private SanitySystem sanitySystem;

    private Thread gameThread;
    private MultiplayerServer server;
    private MultiplayerClient client;
    private long gameTime;
    private String saveFilePath;
    private GameWindow gameWindow;

    public GamePanel(String playerName, boolean isHost, String saveFilePath, MainMenu mainMenu) {
        // Initialize entities and systems
        entities = new ArrayList<>();
        world = new World(System.currentTimeMillis());
        inputSystem = new InputSystem();
        movementSystem = new MovementSystem(world);
        inventorySystem = new InventorySystem();
        minimapSystem = new MinimapSystem(world, (int) Config.MINIMAP_UPDATE_INTERVAL);
        healthSystem = new HealthSystem();
        hungerSystem = new HungerSystem();
        thirstSystem = new ThirstSystem();
        staminaSystem = new StaminaSystem();
        sanitySystem = new SanitySystem();

        // Create the player entity and add it to entities list
        playerEntity = new PlayerEntity(playerName, 100, 100);
        entities.add(playerEntity);

        setPreferredSize(new Dimension(Config.FRAME_SIZE.width, Config.FRAME_SIZE.height));
        setBackground(Config.MAIN_BACKGROUND_COLOR);
        setDoubleBuffered(true);
        addKeyListener(this);
        setFocusable(true);
    }

    public void initializeFromData(GameData gameData) {
        for (Map.Entry<String, GameData.PlayerData> entry : gameData.players.entrySet()) {
            String playerName = entry.getKey();
            GameData.PlayerData playerData = entry.getValue();

            Entity playerEntity = getOrCreatePlayerEntity(playerName);

            PositionComponent position = playerEntity.getComponent(PositionComponent.class);
            position.setX(playerData.playerX);
            position.setY(playerData.playerY);

            InventoryComponent inventory = playerEntity.getComponent(InventoryComponent.class);
            inventory.setItems(playerData.inventory);

            HealthComponent health = playerEntity.getComponent(HealthComponent.class);
            health.setHealth(playerData.health);

            HungerComponent hunger = playerEntity.getComponent(HungerComponent.class);
            hunger.setHunger(playerData.hunger);

            ThirstComponent thirst = playerEntity.getComponent(ThirstComponent.class);
            thirst.setThirst(playerData.thirst);

            StaminaComponent stamina = playerEntity.getComponent(StaminaComponent.class);
            stamina.setStamina(playerData.stamina);

            SanityComponent sanity = playerEntity.getComponent(SanityComponent.class);
            sanity.setSanity(playerData.sanity);
        }

        this.gameTime = gameData.gameTime;
        this.world = new World(gameData.worldSeed);
    }

    // Method to find a player entity by name
    public PlayerEntity findPlayerEntityByName(String name) {
        for (Entity entity : entities) {
            NameComponent nameComponent = entity.getComponent(NameComponent.class);
            if (nameComponent != null && nameComponent.getName().equals(name)) {
                return (PlayerEntity) entity;
            }
        }
        return null;
    }

    // Helper method to get or create a player entity by name
    private Entity getOrCreatePlayerEntity(String playerName) {
        Entity playerEntity = findPlayerEntityByName(playerName);
        if (playerEntity == null) {
            playerEntity = new Entity();
            playerEntity.addComponent(new NameComponent(playerName));
            playerEntity.addComponent(new PositionComponent(0, 0));  // Default position, adjust as needed
            playerEntity.addComponent(new InventoryComponent());
            playerEntity.addComponent(new HealthComponent(100, 100)); // current and max health
            playerEntity.addComponent(new HungerComponent(100, 100)); // current and max hunger
            playerEntity.addComponent(new ThirstComponent(100, 100)); // current and max thirst
            playerEntity.addComponent(new StaminaComponent(100, 100)); // current and max stamina
            playerEntity.addComponent(new SanityComponent(100, 100)); // current and max sanity
            entities.add(playerEntity);
        }
        return playerEntity;
    }

    public String getWorldName() {
        return (world != null) ? world.getWorldName() : "UnnamedWorld";
    }

    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    public Entity getPlayerEntity() {
        return playerEntity;
    }

    // Get the current game time
    public long getGameTime() {
        return gameTime;
    }

    // Get the world instance
    public World getWorld() {
        return world;
    }

    // Get player entities (assuming you have a list or collection of entities)
    public List<Entity> getPlayerEntities() {
        // If `playerEntity` is a single entity, wrap it in a list and return it
        return Collections.singletonList(playerEntity);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGameThread() {
        gameThread = null;
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000 / 60.0;
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

    private void update() {
        inputSystem.update(playerEntity, 1.0f);
        movementSystem.update(playerEntity, 1.0f);
        inventorySystem.update(playerEntity, 1.0f);
        minimapSystem.update(playerEntity, 1.0f);
        healthSystem.update(playerEntity, 1.0f);
        hungerSystem.update(playerEntity, 1.0f);
        thirstSystem.update(playerEntity, 1.0f);
        staminaSystem.update(playerEntity, 1.0f);
        sanitySystem.update(playerEntity, 1.0f);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        inputSystem.keyPressed(playerEntity, e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        inputSystem.keyReleased(playerEntity, e);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (world != null && playerEntity != null) {
            PositionComponent position = playerEntity.getComponent(PositionComponent.class);
            int cameraX = position.getX() - getWidth() / 2;
            int cameraY = position.getY() - getHeight() / 2;

            world.render(g2, cameraX, cameraY, getWidth(), getHeight());
        } else {
            g.drawString("Loading...", getWidth() / 2, getHeight() / 2);
        }
    }

    public void setServer(MultiplayerServer server) {
        this.server = server;
    }

    public void setClient(MultiplayerClient client) {
        this.client = client;
    }

    public void saveGame(String saveFilePath) {
        this.saveFilePath = saveFilePath;
        System.out.println("Game saved to: " + saveFilePath);
    }
}
