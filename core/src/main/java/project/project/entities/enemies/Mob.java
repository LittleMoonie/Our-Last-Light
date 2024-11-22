//package project.project.entities.enemies;
//
//import project.project.components.AttackComponent;
//import project.project.components.HealthComponent;
//
//public class Mob {
//    private HealthComponent healthComponent;
//    private AttackComponent attackComponent;
//
//    public Mob(int health, int damage) {
//        this.healthComponent = new HealthComponent(health);
//        this.attackComponent = new AttackComponent(damage);
//    }
//
//    public HealthComponent getHealthComponent() {
//        return healthComponent;
//    }
//
//    public AttackComponent getAttackComponent() {
//        return attackComponent;
//    }
//
//    public boolean isAlive() {
//        return healthComponent.isAlive();
//    }
//}

//
//// Mob.java
//package project.project.entities.enemies;
//import project.project.components.TextureComponent;
//import project.project.entities.Character;
//import project.project.components.AttackComponent;
//import project.project.components.HealthComponent;
//import project.project.entities.Player;
//import com.badlogic.gdx.math.Vector2;
//
//public class Mob extends Character {
//    private HealthComponent healthComponent;
//    private AttackComponent attackComponent;
//    private Vector2 position;
//
//    public Mob(int health, int damage) {
//        super("");
//        addComponent(new TextureComponent("player1.png", 50, 70));
//        addComponent(new HealthComponent(health));
//        addComponent(new AttackComponent(damage));
//
//        this.position = new Vector2(); // Initial position
//    }
//
//    public HealthComponent getHealthComponent() {
//        return healthComponent;
//    }
//
//    public AttackComponent getAttackComponent() {
//        return attackComponent;
//    }
//
//    public boolean isAlive() {
//        return healthComponent.isAlive();
//    }
//
//    public void update(float delta, Player player) {
//        // Move towards the player
//        Vector2 playerPosition = player.getWorldPosition();
//        Vector2 direction = playerPosition.cpy().sub(position).nor();
//        position.add(direction.scl(delta * 50)); // Adjust speed as needed
//
//        // Check for attack range and attack if in range
//        if (position.dst(playerPosition) < 10) { // Example attack range
//            player.getComponent(HealthComponent.class).takeDamage(attackComponent.getDamage());
//        }
//    }
//
//    public Vector2 getWorldPosition() {
//        return position;
//    }
//}
//
//
//
//// Mob.java
//package project.project.entities.enemies;
//
//import project.project.components.TextureComponent;
//import project.project.entities.Character;
//import project.project.components.AttackComponent;
//import project.project.components.HealthComponent;
//import project.project.entities.Player;
//import com.badlogic.gdx.math.Vector2;
//
//public class Mob extends Character {
//    private HealthComponent healthComponent;
//    private AttackComponent attackComponent;
//    private Vector2 position;
//
//    public Mob(int health, int damage) {
//        super("");
//        addComponent(new TextureComponent("player1.png", 50, 70));
//        addComponent(new HealthComponent(health));
//        addComponent(new AttackComponent(damage));
//
//        this.position = new Vector2(); // Initial position
//    }
//
//    public HealthComponent getHealthComponent() {
//        return healthComponent;
//    }
//
//    public AttackComponent getAttackComponent() {
//        return attackComponent;
//    }
//
//    public boolean isAlive() {
//        return healthComponent.isAlive();
//    }
//
//    public void update(float delta, Player player) {
//        // Move towards the player
//        Vector2 playerPosition = player.getWorldPosition();
//        Vector2 direction = playerPosition.cpy().sub(position).nor();
//        position.add(direction.scl(delta * 50)); // Adjust speed as needed
//
//        // Check for attack range and attack if in range
//        if (position.dst(playerPosition) < 10) { // Example attack range
//            player.getComponent(HealthComponent.class).takeDamage(attackComponent.getDamage());
//        }
//    }
//
//    public Vector2 getWorldPosition() {
//        return position;
//    }
//
//    public void setWorldPosition(float x, float y) {
//        position.set(x, y);
//    }
//}


//
//// Mob.java
//package project.project.entities.enemies;
//
//import project.project.components.TextureComponent;
//import project.project.entities.Character;
//
//import project.project.components.AttackComponent;
//import project.project.components.HealthComponent;
//import project.project.entities.Player;
//import com.badlogic.gdx.math.Vector2;
//
//public class Mob extends Character {
//    private HealthComponent healthComponent;
//    private AttackComponent attackComponent;
//    private Vector2 position;
//
//    public Mob(int health, int damage) {
//        super("");
//        TextureComponent textureComponent = new TextureComponent("player1.png", 50, 70);
//        this.healthComponent = new HealthComponent(health);
//        this.attackComponent = new AttackComponent(damage);
//        addComponent(textureComponent);
//        addComponent(this.healthComponent);
//        addComponent(this.attackComponent);
//
//        this.position = new Vector2(); // Initial position
//    }
//
//    public HealthComponent getHealthComponent() {
//        return healthComponent;
//    }
//
//    public AttackComponent getAttackComponent() {
//        return attackComponent;
//    }
//
//    public boolean isAlive() {
//        return healthComponent.isAlive();
//    }
//
//    public void update(float delta, Player player) {
//        // Move towards the player
//        Vector2 playerPosition = player.getWorldPosition();
//        Vector2 direction = playerPosition.cpy().sub(position).nor();
//        position.add(direction.scl(delta * 50)); // Adjust speed as needed
//
//        // Check for attack range and attack if in range
//        if (position.dst(playerPosition) < 10) { // Example attack range
//            player.getComponent(HealthComponent.class).takeDamage(attackComponent.getDamage());
//        }
//    }
//
//    public Vector2 getWorldPosition() {
//        return position;
//    }
//
//    public void setWorldPosition(float x, float y) {
//        position.set(x, y);
//    }
//}


//package project.project.entities.enemies;
//
//import com.badlogic.gdx.graphics.Texture;
//import project.project.components.TextureComponent;
//import project.project.components.PositionComponent;
//import project.project.entities.Character;
//import com.badlogic.gdx.math.Vector2;
//
//public class Mob extends Character {
//    private TextureComponent texture;
//    private PositionComponent position;
//
//    public Mob(Vector2 playerPosition) {
//        super("Mob");
//        this.texture = new TextureComponent("player2.png", 50 , 80); // Utilise la même image que le joueur
//
//        // Génère une position aléatoire autour du joueur
//        float offsetX = (float) (Math.random() * 200 - 100); // Entre -100 et +100
//        float offsetY = (float) (Math.random() * 200 - 100); // Entre -100 et +100
//
//        // Crée un nouveau PositionComponent avec un Vector2 (x, y)
//        Vector2 mobPosition = new Vector2(playerPosition.x + offsetX, playerPosition.y + offsetY);
//        this.position = new PositionComponent(mobPosition); // Position en tuiles
//
//    }
//
//    // Méthode pour obtenir la position du mob dans le monde
//    public Vector2 getWorldPosition() {
//        return this.position.worldPos; // Accède à la position du mob dans le monde
//    }
//}

package project.project.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import project.project.components.TextureComponent;
import project.project.components.PositionComponent;
import project.project.entities.Character;
import com.badlogic.gdx.math.Vector2;
//

public class Mob extends Character {
    private TextureComponent texture;
    private PositionComponent position;

    public Mob(Vector2 playerPosition) {
        super("Mob");
        this.texture = new TextureComponent("player2.png", 50, 80); // Utilise la même image que le joueur

        // Génère une position aléatoire autour du joueur
        float offsetX = (float) (Math.random() * 200 - 100); // Entre -100 et +100
        float offsetY = (float) (Math.random() * 200 - 100); // Entre -100 et +100

        // Vérifie si la position du joueur est correctement récupérée
        System.out.println("Player position: x = " + playerPosition.x + ", y = " + playerPosition.y);

        // Ajoute un petit décalage autour du joueur
        float mobPosX = playerPosition.x + offsetX;
        float mobPosY = playerPosition.y + offsetY;

        // Si nécessaire, tu peux effectuer une conversion ici avant de passer la position
        this.position = new PositionComponent(new Vector2(mobPosX, mobPosY));

        // Pour vérification
        System.out.println("Mob spawned at coordinates: (" + mobPosX + ", " + mobPosY + ")");
    }

    // Méthode pour obtenir la position du mob dans le monde
    public Vector2 getWorldPosition() {
        return this.position.worldPos; // Accède à la position du mob dans le monde
    }
}
