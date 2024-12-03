package project.project.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import project.project.components.*;
import project.project.entities.Character;
//import project.project.systems.AttackSystem;

import java.util.List;

public class Mob extends Character {
    private Vector2 position; // Position actuelle
    private Vector2 targetPosition; // Position cible
    private static final float SIZE = 50; // Taille du mob
    private float health;
    private final Texture mobTexture;
    private Rectangle hitboxRectangle;

    // Ajoutez ces nouveaux champs
    private boolean isAttacking = false;
    private float attackEffectTimer = 0f;
    private static final float ATTACK_EFFECT_DURATION = 0.5f;

    public Mob(Vector2 mobPosition) {
        super("Mob");
        addComponent(new HealthComponent(300));
        this.position = mobPosition;
        this.targetPosition = null; // Pas de cible initialement
        addComponent(new AttackComponent(5, 50)); // Dégâts et vitesse d'attaque
        addComponent(new MovementComponent(101)); // Vitesse de mouvement
        addComponent(new HitboxComponent(50, 50));

        addComponent(new AttackCooldownComponent(1.0f)); // Cooldown de 1 seconde entre chaque attaque
        // Charger la texture
        mobTexture = new Texture("mob.png");

        // Créer une hitbox autour du mob
        hitboxRectangle = new Rectangle(
            mobPosition.x - SIZE / 2,
            mobPosition.y - SIZE / 2,
            SIZE,
            SIZE
        );

        System.out.println("Mob spawned at coordinates: " + mobPosition);
    }

    /**
     * Définit une position cible aléatoire autour du joueur.
     *
     * @param playerPosition Position du joueur.
     * @param radius         Rayon du cercle autour du joueur.
     */
    public void setRandomTargetAroundPlayer(Vector2 playerPosition, float radius) {
        float angle = (float) (Math.random() * 2 * Math.PI); // Angle aléatoire en radians
        float offsetX = (float) Math.cos(angle) * radius;
        float offsetY = (float) Math.sin(angle) * radius;

        this.targetPosition = new Vector2(playerPosition.x + offsetX, playerPosition.y + offsetY);
        System.out.println("Mob target position set to: " + targetPosition);
    }

    public Vector2 getTargetPosition() {
        return targetPosition;
    }

    /**
     * Met à jour la position du mob, le déplaçant vers sa cible.
     *
     * @param delta         Temps écoulé entre deux frames.
     * @param playerPosition Position du joueur (au cas où une nouvelle cible est nécessaire).
     */
    public void update(float delta, Vector2 playerPosition) {
        // Si aucune cible n'est définie ou si la cible est atteinte, en choisir une nouvelle
        if (targetPosition == null || position.dst(targetPosition) < 2f) { // 5f est une marge d'arrêt
            setRandomTargetAroundPlayer(playerPosition, 300); // Rayon fixe (100) autour du joueur
        }

        // Déplacement vers la position cible
        Vector2 direction = targetPosition.cpy().sub(position).nor(); // Direction normalisée
        MovementComponent movementComponent = getComponent(MovementComponent.class);
        position.add(direction.scl(delta * movementComponent.speed)); // Mouvement

        // Met à jour la hitbox
        hitboxRectangle.setPosition(position.x - SIZE / 2, position.y - SIZE / 2);



    }
    // Méthode pour déclencher l'attaque
    public void startAttack() {
        isAttacking = true;
        attackEffectTimer = ATTACK_EFFECT_DURATION;
    }

    // Méthode à appeler dans le update
    public void updateAttackEffect(float delta) {
        if (isAttacking) {
            attackEffectTimer -= delta;
            if (attackEffectTimer <= 0) {
                isAttacking = false;
            }
        }
    }
    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getHitboxRectangle() {
        return hitboxRectangle;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // Dessine la texture du mob
        batch.draw(mobTexture, position.x - SIZE / 2, position.y - SIZE / 2, SIZE, SIZE);
    }

    public boolean isAlive() {
        HealthComponent healthComponent = getComponent(HealthComponent.class);
        return healthComponent != null && healthComponent.getCurrentHealth() > 0;
    }

    public void takeDamage(float damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
        System.out.println("Mob took damage. Remaining health: " + this.health);
    }

    public void dispose() {
        mobTexture.dispose(); // Libère la mémoire
    }


    public float getPosX() {
        return position.x;
    }
    public float getPosY() {
        return position.y;
    }
    public Vector2 getWorldPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public static float getSpeed(Mob mob) {
        // Récupère le composant MovementComponent du mob
        MovementComponent movementComponent = mob.getComponent(MovementComponent.class);

        // Vérifie si le composant est trouvé
        if (movementComponent != null) {
            return movementComponent.speed; // Retourne la vitesse du mob
        } else {
            System.out.println("MovementComponent not found for the mob.");
            return 0; // Si le composant n'est pas trouvé, retourne 0
        }
    }

}
