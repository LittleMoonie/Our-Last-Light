// RenderSystem.java
package project.project.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import project.project.entities.Entity;
import project.project.components.PositionComponent;
import project.project.components.TextureComponent;
import project.project.entities.Character;


import java.util.ArrayList;
import java.util.List;

public class RenderSystem {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final List<Entity> entities;

    public RenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
        this.entities = new ArrayList<>();
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void update(float deltaTime, List<Entity> entities) {
//        batch.begin(); // Démarre le dessin

        for (Entity entity : entities) {
            if (entity instanceof Character) {
                Character character = (Character) entity;
                PositionComponent position = character.getComponent(PositionComponent.class);
                TextureComponent texture = character.getComponent(TextureComponent.class);

                if (position != null && texture != null) {
                    batch.draw(texture.getTexture(), position.worldPos.x, position.worldPos.y,
                        texture.getWidth(), texture.getHeight());
                    batch.draw(texture.getTexture(), position.worldPos.x, position.worldPos.y, texture.getWidth(), texture.getHeight());

                }
            }
        }
    }

//    private void renderEntity(Entity entity) {
//        if (entity instanceof Player) {
//            Player player = (Player) entity;
//            PositionComponent position = player.getComponent(PositionComponent.class);
//            TextureComponent texture = player.getComponent(TextureComponent.class);
//
//            if (position != null && texture != null) {
//                batch.draw(texture.getTexture(), position.worldPos.x, position.worldPos.y, texture.getWidth(), texture.getHeight());
//            }
//        }
//    }
}
