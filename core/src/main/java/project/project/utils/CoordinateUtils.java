package project.project.utils;

import com.badlogic.gdx.math.Vector2;
import project.project.Constants;

public class CoordinateUtils {

    public static Vector2 worldToTile(float worldX, float worldY) {
        float tileX = (worldY / Constants.TILE_HEIGHT + worldX / Constants.TILE_WIDTH) / 2f;
        float tileY = (worldY / Constants.TILE_HEIGHT - worldX / Constants.TILE_WIDTH) / 2f;
        return new Vector2(Math.round(tileX), Math.round(tileY));
    }

    public static Vector2 tileToWorld(float tileX, float tileY) {
        float worldX = (tileX - tileY) * (Constants.TILE_WIDTH / 2f);
        float worldY = (tileX + tileY) * (Constants.TILE_HEIGHT / 2f);
        return new Vector2(worldX, worldY);
    }
}
