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

    // Inside ObjectPlacementSystem class
    private String buildingToPlace = null; // Tracks the building currently being placed

    /**
     * Starts the placement of a building after crafting.
     *
     * @param buildingName Name of the building to place.
     */
    public void startPlacingBuilding(String buildingName) {
        this.buildingToPlace = buildingName; // Store the building name
        System.out.println("Started placing building: " + buildingName);
        // Optionally, you can enable a placement preview here if desired.
    }

    /**
     * Handles placement logic when the player confirms placement.
     *
     * @param worldPosition The world position where the building will be placed.
     * @return true if placement was successful, false otherwise.
     */
    public boolean confirmPlacement(Vector2 worldPosition) {
        if (buildingToPlace == null) {
            return false; // Nothing to place
        }

        Vector2 snappedPosition = snapToTile(worldPosition);

        // Example: Create and place the building entity
        project.project.entities.Character buildingEntity = new project.project.entities.Character(buildingToPlace);
        buildingEntity.addComponent(new PlacementComponent(true, 1, 1));
        buildingEntity.addComponent(new HitboxComponent(Constants.TILE_WIDTH, Constants.TILE_HEIGHT));

        boolean placed = placeObject(null, buildingEntity, snappedPosition, false); // Null player for simplicity
        if (placed) {
            System.out.println("Building placed successfully: " + buildingToPlace);
            buildingToPlace = null; // Clear placement state
            return true;
        } else {
            System.out.println("Failed to place building: " + buildingToPlace);
            return false;
        }
    }

    /**
     * Snaps a position to the nearest grid tile.
     *
     * @param position The raw position.
     * @return Snapped position.
     */
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
