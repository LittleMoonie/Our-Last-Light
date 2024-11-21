package project.project.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import project.project.components.PositionComponent;
import project.project.components.TextureComponent;
import project.project.entities.Character;

import java.util.ArrayList;
import java.util.List;

public class RenderSystem {
    private SpriteBatch batch;
    private List<Character> entities;

    public RenderSystem(SpriteBatch batch) {
        this.batch = batch;
        this.entities = new ArrayList<>();
    }

    public void addEntity(Character entity) {
        if (entity.hasComponent(TextureComponent.class) && entity.hasComponent(PositionComponent.class)) {
            entities.add(entity);
        }
    }

    public void update(float delta) {
        for (Character entity : entities) {
            TextureComponent texture = entity.getComponent(TextureComponent.class);
            PositionComponent position = entity.getComponent(PositionComponent.class);

            if (texture != null && position != null) {
                // Ensure the entity's position is correct
                float x = position.worldPos.x - texture.width / 2f;
                float y = position.worldPos.y - texture.height / 2f;

                // Draw the texture with the specified dimensions
                batch.draw(texture.texture, x, y, texture.width, texture.height);
            }
        }
    }
}
