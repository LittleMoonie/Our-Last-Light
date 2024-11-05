package src.game.systems;

import src.game.components.PositionComponent;
import src.game.components.InventoryComponent;
import src.game.components.InputComponent;
import src.game.entities.Entity;
import src.game.world.World;
import src.game.world.Tile;

public class ResourceCollectionSystem extends System {
    private World world;

    public ResourceCollectionSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        InputComponent input = entity.getComponent(InputComponent.class);
        InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
        PositionComponent position = entity.getComponent(PositionComponent.class);

        if (input == null || inventory == null || position == null) return;

        if (input.collecting) {
            Tile tile = world.getTileAt(position.x, position.y, true);
            if (tile != null && tile.type.equals("forest")) {
                inventory.addItem("Wood", 1);
                tile.setType("grass");
            }
            input.reset();
        }
    }
}
