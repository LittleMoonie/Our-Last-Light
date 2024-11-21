package project.project.systems;

import com.badlogic.gdx.math.Vector2;
import project.project.Constants;
import project.project.components.HitboxComponent;
import project.project.components.PlacementComponent;
import project.project.components.PositionComponent;
import project.project.entities.Character;

public class PlacementSystem {
    public boolean placeObject(Character placer, Character objectToPlace, Vector2 tilePosition, boolean rotate) {
        PositionComponent placerPos = placer.getComponent(PositionComponent.class);
        PlacementComponent placement = objectToPlace.getComponent(PlacementComponent.class);

        if (placement == null || !placement.isPlaceable) {
            return false; // Cannot place this object
        }

        PositionComponent objPosition = new PositionComponent(tilePosition);
        if (rotate) {
            // Implement rotation logic here if needed
        }

        HitboxComponent hitbox = objectToPlace.getComponent(HitboxComponent.class);
        hitbox.width = Constants.TILE_WIDTH;
        hitbox.height = Constants.TILE_HEIGHT;

        objectToPlace.addComponent(objPosition);
        return true; // Placement successful
    }
}
