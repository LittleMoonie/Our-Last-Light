package src.game.entities;

import src.game.components.*;
import src.game.components.stats.*;
import src.game.constants.Config;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PlayerEntity extends Entity {
    private BufferedImage playerImage;

    public PlayerEntity(String playerName, int startX, int startY) {
        addComponent(new PlayerComponent(playerName));             // Player name
        addComponent(new PositionComponent(startX, startY));       // Initial position
        addComponent(new InputComponent());                        // Input handling component
        addComponent(new SpeedComponent(4, 8));                    // Speed component
        addComponent(new InventoryComponent());                    // Inventory component
        addComponent(new MinimapComponent());                      // Minimap visibility
        addComponent(new HealthComponent(100, 100));               // Max health of 100
        addComponent(new HungerComponent(100, 100));               // Max hunger of 100
        addComponent(new ThirstComponent(100, 100));               // Max thirst of 100
        addComponent(new StaminaComponent(100, 100));              // Max stamina of 100
        addComponent(new SanityComponent(100, 100));               // Max sanity of 100

        loadPlayerImage();                                         // Load the player image
    }

    private void loadPlayerImage() {
        try {
            playerImage = ImageIO.read(getClass().getResourceAsStream(Config.PLAYER_IMAGE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getPlayerImage() {
        return playerImage;
    }
}
