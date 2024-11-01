package src.game.data;

import java.util.Map;

public class GameData {
    public int playerX;
    public int playerY;
    public String playerName;
    public long gameTime;
    public long worldSeed;
    public Map<String, Integer> inventory;
    public String worldFilePath; // Path to the saved world data
}
