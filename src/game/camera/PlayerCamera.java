package src.game.camera;

import src.game.components.PositionComponent;
import src.game.entities.Entity;

import java.awt.*;

public class PlayerCamera {
    private int offsetX; // Camera offset in X direction
    private int offsetY; // Camera offset in Y direction

    public PlayerCamera() {
        this.offsetX = 0;
        this.offsetY = 0;
    }

    public void update(Entity playerEntity, Dimension screenSize) {
        PositionComponent position = playerEntity.getComponent(PositionComponent.class);
        if (position != null) {
            // Center the camera on the player's position
            offsetX = position.getX() - (screenSize.width / 2);
            offsetY = position.getY() - (screenSize.height / 2);
        }
    }

    public Point getCameraPosition() {
        return new Point(offsetX, offsetY);
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }
}
