package project.project.systems;

import com.badlogic.gdx.math.Vector2;
import project.project.Constants;
import project.project.components.HitboxComponent;
import project.project.components.PlacementComponent;
import project.project.components.TextureComponent;
import project.project.entities.Character;
import project.project.components.PositionComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectPlacementSystem {
    private Character previewObject; // Object being placed
    private boolean isPlacing = false;
    private final List<Character> placedObjects = new ArrayList<>(); // Tracks all placed objects
    private Map<String, Character> predefinedObjects = new HashMap<>();

    public ObjectPlacementSystem(RenderSystem renderSystem) {
        // Initialize predefined objects
        this.renderSystem = renderSystem;

        Character campfire = new Character("Campfire");
        campfire.addComponent(new PositionComponent(new Vector2(0, 0))); // Ensure PositionComponent is initialized
        campfire.addComponent(new PlacementComponent(true, 1, 1));
        campfire.addComponent(new HitboxComponent(32, 32));
        campfire.addComponent(new TextureComponent("campfire.png", Constants.TILE_WIDTH, Constants.TILE_HEIGHT));
        predefinedObjects.put("campfire", campfire);

        Character chest = new Character("Chest");
        chest.addComponent(new PositionComponent(new Vector2(0, 0))); // Ensure PositionComponent is initialized
        chest.addComponent(new PlacementComponent(true, 1, 1));
        chest.addComponent(new HitboxComponent(32, 32));
        chest.addComponent(new TextureComponent("chest.png", Constants.TILE_WIDTH, Constants.TILE_HEIGHT));
        predefinedObjects.put("chest", chest);
    }

    public boolean isPlacingObject() {
        return isPlacing;
    }

    public Character clonePredefinedObject(String key) {
        Character original = predefinedObjects.get(key);
        if (original == null) return null;

        Character clone = new Character(original.getName());
        clone.addComponent(new PositionComponent(new Vector2(0, 0))); // Ensure PositionComponent is initialized
        clone.addComponent(original.getComponent(PlacementComponent.class));
        clone.addComponent(original.getComponent(HitboxComponent.class));
        clone.addComponent(original.getComponent(TextureComponent.class));

        return clone;
    }


    /**
     * Start placing an object in the world.
     *
     * @param object The object to be placed.
     * @return true if placement starts successfully, false otherwise.
     */
    public boolean startPlacingObject(Character object) {
        if (isPlacing) {
            return false;
        }

        // Clone to ensure the preview object has a PositionComponent
        Character objectToPlace = clonePredefinedObject(object.getName());
        if (objectToPlace == null) {
            System.err.println("Error: No predefined object found for " + object.getName());
            return false;
        }

        this.previewObject = objectToPlace;
        this.isPlacing = true;

        PositionComponent position = previewObject.getComponent(PositionComponent.class);
        if (position == null) {
            return false;
        }
        return true;
    }

    /**
     * Updates the preview position of the object to the specified world position.
     *
     * @param worldPos The world position where the object should be previewed.
     */
    public void updatePlacement(Vector2 worldPos) {
        if (!isPlacing || previewObject == null) return;

        // Snap the object to the grid (assuming grid snapping logic)
        Vector2 snappedPosition = snapToGrid(worldPos);

        // Update the position of the preview object
        PositionComponent position = previewObject.getComponent(PositionComponent.class);
        if (position != null) {
            position.setWorldPosition(snappedPosition.x, snappedPosition.y);
        }
    }

    /**
     * Confirm placement of the object at the current preview position.
     *
     * @param worldPos The world position where the object should be placed.
     * @return true if placement is successful, false otherwise.
     */
    public boolean confirmPlacement(Vector2 worldPos) {
        if (!isPlacing || previewObject == null) {
            return false;
        }

        PositionComponent position = previewObject.getComponent(PositionComponent.class);
        if (position == null) {
            return false;
        }

        // Snap the object to the grid (optional)
        Vector2 snappedPosition = snapToGrid(worldPos);

        // Place the object in the game world
        placeObject(previewObject, snappedPosition);

        // Reset placement state
        previewObject = null;
        isPlacing = false;
        return true;
    }

    /**
     * Places the object in the game world.
     *
     * @param object The object to place.
     * @param position The world position to place the object.
     */
    private final RenderSystem renderSystem; // Injected or initialized elsewhere

    public void placeObject(Character object, Vector2 position) {
        PositionComponent positionComponent = object.getComponent(PositionComponent.class);

        if (positionComponent == null) {
            System.err.println("Error: Object lacks PositionComponent at placement.");
            return;
        }

        positionComponent.setWorldPosition(position.x, position.y);

        // Add to the list of placed objects
        placedObjects.add(object);

        // Notify the RenderSystem
        if (renderSystem != null) {
            renderSystem.addEntity(object);
        }
    }

    /**
     * Snaps a world position to the grid.
     *
     * @param worldPos The original world position.
     * @return The snapped position.
     */
    private Vector2 snapToGrid(Vector2 worldPos) {
        float tileSize = 32; // Adjust tile size as necessary
        float snappedX = Math.round(worldPos.x / tileSize) * tileSize;
        float snappedY = Math.round(worldPos.y / tileSize) * tileSize;
        return new Vector2(snappedX, snappedY);
    }

    /**
     * Get the list of placed objects.
     *
     * @return List of placed objects.
     */
    public List<Character> getPlacedObjects() {
        return new ArrayList<>(placedObjects); // Return a copy to avoid external modification
    }

    /**
     * Returns the preview object currently being placed.
     *
     * @return The preview object, or null if no object is being placed.
     */
    public Character getPreviewObject() {
        return previewObject;
    }
}
