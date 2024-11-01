package src.game.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import src.main.GamePanel;

import java.io.File;
import java.io.IOException;

public class SaveHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void saveGame(GamePanel gamePanel, String saveFilePath) {
        GameData gameData = new GameData();
        gameData.playerX = gamePanel.getPlayer().x;
        gameData.playerY = gamePanel.getPlayer().y;
        gameData.playerName = gamePanel.getPlayer().name;
        gameData.gameTime = gamePanel.getGameTime();
        gameData.worldSeed = gamePanel.getWorld().getWorldSeed();
        gameData.inventory = gamePanel.getPlayer().inventory.getItems(); // Save the inventory

        try {
            objectMapper.writeValue(new File(saveFilePath), gameData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGame(GamePanel gamePanel, String saveFilePath) {
        try {
            GameData gameData = objectMapper.readValue(new File(saveFilePath), GameData.class);
            gamePanel.initializeFromData(gameData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
