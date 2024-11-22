package project.project.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import project.project.entities.Character;

public class Mob extends Character {
    private Vector2 position;
    private static final float SIZE = 50; // Width and height of the mob's texture
    private float health;
    private final Texture mobTexture;

    public Mob(Vector2 mobPosition) {
        super("Mob");
        this.health = 100; // Default health value
        this.position = mobPosition;

        // Load the texture for the mob (replace "mob.png" with your actual texture path)
        mobTexture = new Texture("player2.png");

        System.out.println("Mob spawned at coordinates: " + mobPosition);
    }

    public Vector2 getWorldPosition() {
        return position;
    }

    public void render(SpriteBatch batch) {
        // Draw the mob texture at its position
        batch.draw(mobTexture, position.x - SIZE / 2, position.y - SIZE / 2, SIZE, SIZE);
    }

    public void update(float delta, Vector2 playerPosition) {
        // Move towards the player
        Vector2 direction = playerPosition.cpy().sub(position).nor();
        position.add(direction.scl(delta * 50)); // Adjust speed as needed
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(float damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
        System.out.println("Mob took damage. Remaining health: " + this.health);
    }

    public void dispose() {
        mobTexture.dispose(); // Dispose of the texture to avoid memory leaks
    }
}
