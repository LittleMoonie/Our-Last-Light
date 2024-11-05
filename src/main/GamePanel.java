package src.main;

import src.game.components.*;
import src.game.components.stats.*;
import src.game.constants.Config;
import src.game.data.GameData;
import src.game.entities.Entity;
import src.game.entities.PlayerEntity;
import src.game.network.MultiplayerClient;
import src.game.network.MultiplayerServer;
import src.game.systems.*;
import src.game.systems.stats.*;
import src.game.world.World;
import src.game.ui.GameWindow;
import src.game.ui.menus.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.lang.System;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    private PlayerEntity playerEntity;
    private World world;
    private List<Entity> entities;

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
    private boolean isMultiplayer = false;
    private MultiplayerClient client;
    private long gameTime;
    private String saveFilePath;
    private GameWindow gameWindow;
    private boolean isInventoryOpen = false;

    public GamePanel(String playerName, boolean isHost, String saveFilePath, MainMenu mainMenu) {
        entities = new ArrayList<>();
        world = new World(System.currentTimeMillis());

        // Initialize systems
        inputSystem = new InputSystem();
        movementSystem = new MovementSystem(world);
        inventorySystem = new InventorySystem();
        minimapSystem = new MinimapSystem(world, (int) Config.MINIMAP_UPDATE_INTERVAL);
        healthSystem = new HealthSystem();
        hungerSystem = new HungerSystem();
        thirstSystem = new ThirstSystem();
        staminaSystem = new StaminaSystem();
        sanitySystem = new SanitySystem();

        // Create player entity and add it to entities
        playerEntity = new PlayerEntity(playerName, 100, 100); // Ensure this is of type PlayerEntity
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

    public World getWorld() {
        return world;
    }

    public String getWorldName() {
        return world != null ? world.getWorldName() : "UnnamedWorld";
    }

    public long getGameTime() {
        return gameTime;
    }

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    public List<Entity> getPlayerEntities() {
        return Collections.singletonList(playerEntity);
    }

    public Entity findPlayerEntityByName(String name) {
        for (Entity entity : entities) {
            NameComponent nameComponent = entity.getComponent(NameComponent.class);
            if (nameComponent != null && nameComponent.getName().equals(name)) {
                return entity;
            }
        }
        return null;
    }

    private Entity getOrCreatePlayerEntity(String playerName) {
        Entity playerEntity = findPlayerEntityByName(playerName);
        if (playerEntity == null) {
            playerEntity = new Entity();
            playerEntity.addComponent(new NameComponent(playerName));
            playerEntity.addComponent(new PositionComponent(0, 0));
            playerEntity.addComponent(new InventoryComponent());
            playerEntity.addComponent(new HealthComponent(100, 100));
            playerEntity.addComponent(new HungerComponent(100, 100));
            playerEntity.addComponent(new ThirstComponent(100, 100));
            playerEntity.addComponent(new StaminaComponent(100, 100));
            playerEntity.addComponent(new SanityComponent(100, 100));
            entities.add(playerEntity);
        }
        return playerEntity;
    }

    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    public void setServer(MultiplayerServer server) {
        this.server = server;
        this.isMultiplayer = true;
    }

    public void setClient(MultiplayerClient client) {
        this.client = client;
        this.isMultiplayer = true;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGameThread() {
        gameThread = null;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_M) {
            minimapSystem.toggleExpanded(playerEntity);
        } else if (keyCode == KeyEvent.VK_E) {
            isInventoryOpen = !isInventoryOpen;
        } else {
            inputSystem.keyPressed(playerEntity, e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        inputSystem.keyReleased(playerEntity, e);
    }

    @Override
    public void keyTyped(KeyEvent e) {}


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (world != null && playerEntity != null) {
            PositionComponent position = playerEntity.getComponent(PositionComponent.class);
            int cameraX = position.getX() - getWidth() / 2;
            int cameraY = position.getY() - getHeight() / 2;

            // Draw the world around the player
            world.render(g2, cameraX, cameraY, getWidth(), getHeight());

            // Draw the player image at the center of the screen
            BufferedImage playerImage = playerEntity.getPlayerImage();
            if (playerImage != null) {
                // Player's world position
                int playerWorldX = position.getX();
                int playerWorldY = position.getY();

                // Calculate the player's screen position relative to the camera
                int playerPositionX = playerWorldX - cameraX;
                int playerPositionY = playerWorldY - cameraY;

                // Scale the player image to be 5x smaller
                int scaledWidth = playerImage.getWidth() / 5;
                int scaledHeight = playerImage.getHeight() / 5;

                // Center the image on the player's screen position
                int playerDrawX = playerPositionX - scaledWidth / 2;
                int playerDrawY = playerPositionY - scaledHeight / 2;

                g2.drawImage(playerImage, playerDrawX, playerDrawY, scaledWidth, scaledHeight, null);
            }

            // Optionally draw the minimap and inventory
            minimapSystem.draw(g2, getWidth(), getHeight(), playerEntity);
            if (isInventoryOpen) {
                inventorySystem.drawInventory(g2, playerEntity, getWidth(), getHeight());
            }
        } else {
            g.drawString("Loading...", getWidth() / 2, getHeight() / 2);
        }
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

        gameTime += 1;

        if (isMultiplayer && server != null) {
            PositionComponent position = playerEntity.getComponent(PositionComponent.class);
            server.broadcastPlayerPosition(
                    playerEntity.getComponent(NameComponent.class).getName(),
                    position.getX(), position.getY()
            );
        }

        if (isMultiplayer && client != null) {
            client.sendPlayerPosition(playerEntity);
        }
    }

    public void saveGame(String saveFilePath) {
        this.saveFilePath = saveFilePath;
        System.out.println("Game saved to: " + saveFilePath);
    }
}
