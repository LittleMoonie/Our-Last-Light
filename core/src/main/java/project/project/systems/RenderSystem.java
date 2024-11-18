//package project.project.systems;
//
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import project.project.components.PositionComponent;
//import project.project.components.TextureComponent;
//import project.project.entities.Character;
//
//public class RenderSystem {
//    private SpriteBatch batch;
//
//    public RenderSystem(SpriteBatch batch) {
//        this.batch = batch;
//    }
//
//    public void update(float delta, Character player) {
//        TextureComponent texture = player.getComponent(TextureComponent.class);
//        PositionComponent position = player.getComponent(PositionComponent.class);
//
//        // Assurez-vous que la position du joueur est correcte
//        float x = position.worldPos.x - texture.texture.getWidth() / 2f;
//        float y = position.worldPos.y - texture.texture.getHeight() / 2f;
//
//        // Spécifiez les dimensions souhaitées pour l'image du joueur
//        float width = 48;  // Largeur souhaitée
//        float height = 48; // Hauteur souhaitée
//
//        batch.begin();
//        batch.draw(texture.texture, x, y, width, height);
//        batch.end();
//    }
//}

// RenderSystem.java
package project.project.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import project.project.components.PositionComponent;
import project.project.components.TextureComponent;
import project.project.entities.Character;

public class RenderSystem {
    private SpriteBatch batch;

    public RenderSystem(SpriteBatch batch) {
        this.batch = batch;
    }

    public void update(float delta, Character player) {
        TextureComponent texture = player.getComponent(TextureComponent.class);
        PositionComponent position = player.getComponent(PositionComponent.class);

        // Ensure the player's position is correct
        float x = position.worldPos.x - texture.texture.getWidth() / 2f;
        float y = position.worldPos.y - texture.texture.getHeight() / 2f;

        // Specify the desired dimensions for the player's image
        float width = 48;  // Desired width
        float height = 48; // Desired height

        batch.draw(texture.texture, x, y, width, height);
    }
}
