package project.project.systems;

import com.badlogic.gdx.math.Vector2;
import project.project.Constants;
import project.project.components.HitboxComponent;
import project.project.components.PlacementComponent;
import project.project.components.PositionComponent;
import project.project.entities.Character;

import java.util.HashSet;
import java.util.Set;

public class ObjectPlacementSystem {

    private final Set<PlacedObject> placedObjects = new HashSet<>();

    public boolean placeObject(Character placer, Character objectToPlace, Vector2 tilePosition, boolean rotate) {
        PlacementComponent placement = objectToPlace.getComponent(PlacementComponent.class);

        if (placement == null || !placement.isPlaceable) {
            return false; // Object is not placeable
        }

        Vector2 snappedPosition = snapToTile(tilePosition);

        if (isOccupied(snappedPosition, placement.tileWidth, placement.tileHeight)) {
            return false; // Tile is already occupied
        }

        if (rotate) {
            rotateObject(objectToPlace, placement);
        }

        PositionComponent positionComponent = new PositionComponent(snappedPosition);
        objectToPlace.addComponent(positionComponent);

        HitboxComponent hitbox = objectToPlace.getComponent(HitboxComponent.class);
        if (hitbox != null) {
            placedObjects.add(new PlacedObject(snappedPosition, placement.tileWidth, placement.tileHeight));
        }

        return true;
    }

    public Vector2 snapToTile(Vector2 position) {
        float x = Math.round(position.x / Constants.TILE_WIDTH) * Constants.TILE_WIDTH;
        float y = Math.round(position.y / Constants.TILE_HEIGHT) * Constants.TILE_HEIGHT;
        return new Vector2(x, y);
    }

    private void rotateObject(Character object, PlacementComponent placement) {
        int temp = placement.tileWidth;
        placement.tileWidth = placement.tileHeight;
        placement.tileHeight = temp;

        HitboxComponent hitbox = object.getComponent(HitboxComponent.class);
        if (hitbox != null) {
            float tempWidth = hitbox.width;
            hitbox.width = hitbox.height;
            hitbox.height = tempWidth;
        }
    }

    private boolean isOccupied(Vector2 position, int width, int height) {
        for (PlacedObject placed : placedObjects) {
            if (placed.overlaps(position, width, height)) {
                return true;
            }
        }
        return false;
    }

    public boolean canMoveTo(Vector2 position) {
        for (PlacedObject placed : placedObjects) {
            if (placed.contains(position)) {
                return false; // Position is blocked
            }
        }
        return true;
    }

    private static class PlacedObject {
        private final Vector2 position;
        private final int width;
        private final int height;

        public PlacedObject(Vector2 position, int width, int height) {
            this.position = position;
            this.width = width;
            this.height = height;
        }

        public boolean overlaps(Vector2 other, int otherWidth, int otherHeight) {
            return !(other.x + otherWidth <= position.x || other.x >= position.x + width
                || other.y + otherHeight <= position.y || other.y >= position.y + height);
        }

        public boolean contains(Vector2 point) {
            return point.x >= position.x && point.x < position.x + width &&
                point.y >= position.y && point.y < position.y + height;
        }
    }
}
