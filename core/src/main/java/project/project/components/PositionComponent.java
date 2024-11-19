package project.project.components;

import com.badlogic.gdx.math.Vector2;
import project.project.Constants;

public class PositionComponent implements Component {
    public Vector2 tilePos; // Position en tuiles
    public Vector2 worldPos; // Position dans le monde

    public PositionComponent(Vector2 startingTilePos) {
        this.tilePos = new Vector2(startingTilePos);
        this.worldPos = tileToWorld(tilePos); // Conversion initiale tuile -> monde
    }

    // Met à jour la position en tuiles et recalcule la position monde
    public void setTilePosition(float tileX, float tileY) {
        this.tilePos.set(tileX, tileY);
        this.worldPos = tileToWorld(tilePos); // Conversion automatique
    }

    // Met à jour la position dans le monde et recalcule la position en tuiles
    public void setWorldPosition(float worldX, float worldY) {
        this.worldPos.set(worldX, worldY);
        this.tilePos = worldToTile(worldPos); // Conversion automatique
    }

    // Conversion tuile -> monde
    private Vector2 tileToWorld(Vector2 tilePos) {
        float worldX = (tilePos.x - tilePos.y) * (Constants.TILE_WIDTH / 2f);
        float worldY = (tilePos.x + tilePos.y) * (Constants.TILE_HEIGHT / 2f);
        return new Vector2(worldX, worldY);
    }

    // Conversion monde -> tuile
    private Vector2 worldToTile(Vector2 worldPos) {
        float tileX = (worldPos.x / (Constants.TILE_WIDTH / 2f) +
            worldPos.y / (Constants.TILE_HEIGHT / 2f)) / 2f;
        float tileY = (worldPos.y / (Constants.TILE_HEIGHT / 2f) -
            worldPos.x / (Constants.TILE_WIDTH / 2f)) / 2f;
        return new Vector2(tileX, tileY);
    }
}
