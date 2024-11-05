// src/game/systems/InputSystem.java
package src.game.systems;

import src.game.components.InputComponent;
import src.game.entities.Entity;
import java.awt.event.KeyEvent;

public class InputSystem extends System {

    @Override
    public void update(Entity entity, float deltaTime) {
        InputComponent input = entity.getComponent(InputComponent.class);
        if (input == null) return;

        // Reset single-action flags that only need to be true for one update cycle
        input.collecting = false;
        input.showInventory = false;
    }

    public void keyPressed(Entity entity, KeyEvent e) {
        InputComponent input = entity.getComponent(InputComponent.class);
        if (input == null) return;

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_Z) input.up = true;
        if (code == KeyEvent.VK_S) input.down = true;
        if (code == KeyEvent.VK_Q) input.left = true;
        if (code == KeyEvent.VK_D) input.right = true;
        if (code == KeyEvent.VK_SHIFT) input.sprint = true;

        // Set flags for actions triggered by single key presses
        if (code == KeyEvent.VK_F) input.collecting = true;
        if (code == KeyEvent.VK_E) input.showInventory = true;
        if (code == KeyEvent.VK_M) input.showMinimap = true;
    }

    public void keyReleased(Entity entity, KeyEvent e) {
        InputComponent input = entity.getComponent(InputComponent.class);
        if (input == null) return;

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_Z) input.up = false;
        if (code == KeyEvent.VK_S) input.down = false;
        if (code == KeyEvent.VK_Q) input.left = false;
        if (code == KeyEvent.VK_D) input.right = false;
        if (code == KeyEvent.VK_SHIFT) input.sprint = false;
    }
}
