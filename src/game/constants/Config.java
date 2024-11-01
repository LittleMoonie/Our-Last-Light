// src/game/constants/Config.java

package src.game.constants;

import java.awt.*;

public class Config {
    // Paths
    public static final String PLAYER_IMAGE_PATH = "/resources/player.png";
    public static final String TILE_IMAGE_PATH = "/resources/";
    public static final String SAVE_DIRECTORY = "saves/";
    public static final String LOADING_IMAGE_PATH = "/resources/loading.png";

    // Game Dimensions
    public static final int TILE_SIZE = GameConstants.TILE_SIZE;
    public static final Dimension FRAME_SIZE = new Dimension(800, 600);

    // UI Configurations
    public static final Color MAIN_BACKGROUND_COLOR = new Color(20, 20, 20);
    public static final Color BUTTON_BACKGROUND_COLOR = new Color(80, 80, 80);
    public static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    public static final int BUTTON_WIDTH = 200;
    public static final int BUTTON_HEIGHT = 50;
    public static final Font MAIN_FONT = new Font("Arial", Font.PLAIN, 18);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 36);
    public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 28);

    // Game Messages
    public static final String LOADING_GAME_MESSAGE = "Loading game, please wait...";
    public static final String NAME_ALREADY_TAKEN_MESSAGE = "Name already taken. Please choose a different name.";
    public static final String ENTER_PLAYER_NAME_PROMPT = "Enter your player name:";
    public static final String ENTER_WORLD_NAME_PROMPT = "Enter a name for your new world:";
    public static final String WORLD_EXISTS_MESSAGE = "A world with this name already exists. Please choose a different name.";
    public static final String CREATE_WORLD_ERROR_MESSAGE = "An error occurred while creating the world.";

    // Other Constants
    public static final long MINIMAP_UPDATE_INTERVAL = 500L;
    public static final int MAX_SPAWN_ATTEMPTS = 1000;
    public static final int PLAYER_SIZE = 28;
    public static final double SIMPLEX_NOISE_SCALE_BASE = 0.005;
    public static final double SIMPLEX_NOISE_SCALE_DETAIL = 0.05;
    public static final double NOISE_THRESHOLD_GRASS = 0.3;
    public static final double NOISE_THRESHOLD_FOREST = 0.1;
    public static final double NOISE_THRESHOLD_SAND = -0.05;
    public static final double OBSTACLE_PROBABILITY_FOREST = 0.1;
    public static final int SERVER_PORT = 5000;
    public static final int DISCOVERY_PORT = 5001;
}
