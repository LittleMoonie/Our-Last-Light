// SaveHandler.java
package src.game.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import src.main.GamePanel;
import src.game.constants.Config;

import java.io.File;
import java.io.IOException;

public class SaveHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void saveGame(GamePanel gamePanel, String saveFilePath) {
        String worldFolderPath = getWorldFolderPath(gamePanel);
        new File(worldFolderPath).mkdirs();  // Create world folder if it doesn't exist

        GameData gameData = new GameData();
        gameData.playerX = gamePanel.getPlayer().x;
        gameData.playerY = gamePanel.getPlayer().y;
        gameData.playerName = gamePanel.getPlayer().name;
        gameData.gameTime = gamePanel.getGameTime();
        gameData.worldSeed = gamePanel.getWorld().getWorldSeed();
        gameData.inventory = gamePanel.getPlayer().inventory.getItems();

        try {
            objectMapper.writeValue(new File(worldFolderPath + "/player_data.json"), gameData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gamePanel.getWorld().saveWorldData();  // Save world data in the same folder
    }

    public static void loadGame(GamePanel gamePanel, String saveFilePath) {
        String worldFolderPath = getWorldFolderPath(gamePanel);

        try {
            GameData gameData = objectMapper.readValue(new File(worldFolderPath + "/player_data.json"), GameData.class);
            gamePanel.initializeFromData(gameData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getWorldFolderPath(GamePanel gamePanel) {
        return Config.SAVE_DIRECTORY + "/" + gamePanel.getWorld().getWorldName();
    }
}
