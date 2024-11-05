package src.game.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameData {
    public long gameTime;
    public long worldSeed;
    public Map<String, PlayerData> players = new HashMap<>();

    public static class PlayerData {
        public int playerX;
        public int playerY;
        public String playerName;
        public Map<String, Integer> inventory = new HashMap<>();
        public int health;
        public int hunger;
        public int thirst;
        public int stamina;
        public int sanity;

        // Serialize PlayerData to a String format
        public String serialize() {
            StringBuilder sb = new StringBuilder();
            sb.append(playerName).append(";")
                    .append(playerX).append(";")
                    .append(playerY).append(";")
                    .append(health).append(";")
                    .append(hunger).append(";")
                    .append(thirst).append(";")
                    .append(stamina).append(";")
                    .append(sanity).append(";");

            // Serialize inventory
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                sb.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
            }

            return sb.toString();
        }

        // Deserialize PlayerData from a String format
        public static PlayerData deserialize(String data) {
            String[] parts = data.split(";");
            PlayerData playerData = new PlayerData();
            playerData.playerName = parts[0];
            playerData.playerX = Integer.parseInt(parts[1]);
            playerData.playerY = Integer.parseInt(parts[2]);
            playerData.health = Integer.parseInt(parts[3]);
            playerData.hunger = Integer.parseInt(parts[4]);
            playerData.thirst = Integer.parseInt(parts[5]);
            playerData.stamina = Integer.parseInt(parts[6]);
            playerData.sanity = Integer.parseInt(parts[7]);

            // Deserialize inventory
            String[] inventoryItems = parts[8].split(",");
            for (String item : inventoryItems) {
                if (item.isEmpty()) continue;
                String[] itemParts = item.split(":");
                playerData.inventory.put(itemParts[0], Integer.parseInt(itemParts[1]));
            }

            return playerData;
        }
    }

    // Save GameData to file
    public void saveToFile(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(gameTime + "\n");
            writer.write(worldSeed + "\n");
            for (Map.Entry<String, PlayerData> entry : players.entrySet()) {
                writer.write(entry.getValue().serialize() + "\n");
            }
        }
    }

    // Load GameData from file
    public static GameData loadFromFile(String filePath) throws IOException {
        GameData gameData = new GameData();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            gameData.gameTime = Long.parseLong(reader.readLine());
            gameData.worldSeed = Long.parseLong(reader.readLine());

            String line;
            while ((line = reader.readLine()) != null) {
                PlayerData playerData = PlayerData.deserialize(line);
                gameData.players.put(playerData.playerName, playerData);
            }
        }
        return gameData;
    }
}
