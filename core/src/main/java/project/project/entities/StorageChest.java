package project.project.entities;

import com.badlogic.gdx.math.Vector2;
import project.project.components.InventoryComponent;
import project.project.components.PositionComponent;

public class StorageChest extends Character {
    public StorageChest(Vector2 position) {
        super("StorageChest");
        addComponent(new PositionComponent(position));
        addComponent(new InventoryComponent(6, 9)); // 6x9 inventory grid
    }
}
