package src.game.data;

import src.game.components.InventoryComponent;
import src.game.components.PlayerComponent;
import src.game.components.PositionComponent;
import src.game.components.stats.*;
import src.game.entities.Entity;
import src.main.GamePanel;

import java.io.IOException;

public class SaveHandler {

    public static void saveGame(GamePanel gamePanel, String saveFilePath) {
        GameData gameData = new GameData();
        gameData.gameTime = gamePanel.getGameTime();
        gameData.worldSeed = gamePanel.getWorld().getWorldSeed();

        for (Entity playerEntity : gamePanel.getPlayerEntities()) {
            GameData.PlayerData playerData = new GameData.PlayerData();

            PositionComponent position = playerEntity.getComponent(PositionComponent.class);
            InventoryComponent inventory = playerEntity.getComponent(InventoryComponent.class);
            HealthComponent health = playerEntity.getComponent(HealthComponent.class);
            HungerComponent hunger = playerEntity.getComponent(HungerComponent.class);
            ThirstComponent thirst = playerEntity.getComponent(ThirstComponent.class);
            StaminaComponent stamina = playerEntity.getComponent(StaminaComponent.class);
            SanityComponent sanity = playerEntity.getComponent(SanityComponent.class);

            playerData.playerX = position.x;
            playerData.playerY = position.y;
            playerData.playerName = playerEntity.getComponent(PlayerComponent.class).name; // Assuming a PlayerComponent with a name
            playerData.inventory = inventory.getItems();
            if (health != null) playerData.health = health.getHealth();
            if (hunger != null) playerData.hunger = hunger.getHunger();
            if (thirst != null) playerData.thirst = thirst.getThirst();
            if (stamina != null) playerData.stamina = stamina.getStamina();
            if (sanity != null) playerData.sanity = sanity.getSanity();

            gameData.players.put(playerData.playerName, playerData);
        }

        try {
            gameData.saveToFile(saveFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGame(GamePanel gamePanel, String saveFilePath) {
        try {
            GameData gameData = GameData.loadFromFile(saveFilePath);
            gamePanel.initializeFromData(gameData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
