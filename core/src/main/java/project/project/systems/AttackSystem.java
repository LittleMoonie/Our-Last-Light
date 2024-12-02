////// AttackSystem.java
////package project.project.systems;
////
////import project.project.components.*;
////import project.project.entities.Player;
////import project.project.entities.enemies.Mob;
////import project.project.utils.InputHandler;
////
////import java.util.List;
////
////
////public class AttackSystem {
////
////    // Méthode qui gère l'attaque du joueur
////    public void handlePlayerAttack(Player player, List<Mob> mobs) {
////        AttackComponent playerAttack = player.getComponent(AttackComponent.class);
////        PositionComponent playerPosition = player.getComponent(PositionComponent.class);
////
////        // Si le composant d'attaque existe et la position du joueur est valide
////        if (playerAttack != null && playerPosition != null) {
////            // Vérifier si l'espace est pressé pour attaquer
////            if (InputHandler.isSpacePressed()) {
////                attack(player, mobs, playerAttack, playerPosition);
////            }
////        }
////    }
////
////    // Méthode qui effectue l'attaque
////    private void attack(Player player, List<Mob> mobs, AttackComponent playerAttack, PositionComponent playerPosition) {
////        for (Mob mob : mobs) {
////            PositionComponent mobPosition = mob.getComponent(PositionComponent.class);
////
////            // Vérifier si le mob existe et si sa position est valide
////            if (mobPosition != null) {
////                // Calculer la distance entre le joueur et le mob
////                float distance = calculateDistance(playerPosition, mobPosition);
////
////                // Vérifier si le mob est dans la portée d'attaque du joueur
////                if (distance <= playerAttack.getAttackRange()) {
////                    // Appliquer des dégâts au mob
////                    mob.takeDamage(playerAttack.getAttackDamage());
////                    System.out.println("Mob " + mob.getId() + " a été attaqué par " + player.getName() +
////                        ". Vie restante: " + mob.getComponent(HealthComponent.class).getCurrentHealth());
////                }
////            }
////        }
////    }
////
////    // Méthode pour calculer la distance entre deux positions
////    private float calculateDistance(PositionComponent position1, PositionComponent position2) {
////        float dx = position1.getWorldPosition().x - position2.getWorldPosition().x;
////        float dy = position1.getWorldPosition().y - position2.getWorldPosition().y;
////        return (float) Math.sqrt(dx * dx + dy * dy);
////    }
////}
//
////
////package project.project.systems;
////
////import com.badlogic.gdx.graphics.g2d.BitmapFont;
////import com.badlogic.gdx.graphics.g2d.SpriteBatch;
////import com.badlogic.gdx.math.Vector2;
////import project.project.components.*;
////import project.project.entities.*;
////import project.project.entities.enemies.Mob;
////
////import java.util.List;
////
////public class AttackSystem {
////    private BitmapFont font;
////
////    public AttackSystem() {
////        font = new BitmapFont();
////    }
////
////    public void update(float delta, Player player, List<Mob> mobs, List<Mob> deadMobs, SpriteBatch batch) {
////        PositionComponent playerPosition = player.getComponent(PositionComponent.class);
////        AttackComponent playerAttack = player.getComponent(AttackComponent.class);
////
////        // Vérifier si le joueur attaque
////        if (playerAttack != null && playerAttack.isAttacking()) {
////            for (Mob mob : mobs) {
////                if (isInRange(player, mob)) {
////                    applyDamage(mob, playerAttack.getAttackDamage());
////                    if (isDead(mob)) {
////                        deadMobs.add(mob);
////                    }
////                }
////            }
////            playerAttack.setAttacking(false); // Réinitialiser l'état d'attaque
////        }
////
////        // Mobs attaquent le joueur
////        for (Mob mob : mobs) {
////            AttackComponent mobAttack = mob.getComponent(AttackComponent.class);
////            if (mobAttack != null && isInRange(mob, player)) {
////                // Afficher 🔫 au-dessus du mob
////                renderAttackVisual(mob, batch);
////                applyDamage(player, mobAttack.getAttackDamage());
////            }
////        }
////    }
////
////    private boolean isInRange(Entity attacker, Entity target) {
////        PositionComponent attackerPosition = attacker.getComponent(PositionComponent.class);
////        PositionComponent targetPosition = target.getComponent(PositionComponent.class);
////        AttackComponent attackerAttack = attacker.getComponent(AttackComponent.class);
////
////        if (attackerPosition != null && targetPosition != null && attackerAttack != null) {
////            float distance = attackerPosition.worldPos.dst(targetPosition.worldPos);
////            return distance <= attackerAttack.getAttackRange();
////        }
////        return false;
////    }
////
////    private void applyDamage(Entity entity, int damage) {
////        HealthComponent health = entity.getComponent(HealthComponent.class);
////        if (health != null) {
////            health.setHealth(health.getCurrentHealth() - damage);
////        }
////    }
////
////    private boolean isDead(Entity entity) {
////        HealthComponent health = entity.getComponent(HealthComponent.class);
////        return health != null && health.getCurrentHealth() <= 0;
////    }
////
////    private void renderAttackVisual(Mob mob, SpriteBatch batch) {
////        PositionComponent mobPosition = mob.getComponent(PositionComponent.class);
////        if (mobPosition != null) {
////            font.draw(batch, "🔫", mobPosition.worldPos.x, mobPosition.worldPos.y + 50);
////        }
////    }
////}
//
//
//package project.project.systems;
//
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.math.Vector2;
//import project.project.components.*;
//import project.project.entities.enemies.Mob;
//import project.project.entities.Player;
//
//import java.util.List;
//
//public class AttackSystem {
//    private final SpriteBatch batch;
//    private final BitmapFont font;
//
//    public AttackSystem() {
//        this.batch = new SpriteBatch();
//        this.font = new BitmapFont();
//        this.font.setColor(Color.RED);
//    }
//
////    public void update(float delta, List<Mob> mobs, Player player) {
////        PositionComponent playerPosition = player.getComponent(PositionComponent.class);
////        HitboxComponent playerHitbox = player.getComponent(HitboxComponent.class);
////        HealthComponent playerHealth = player.getComponent(HealthComponent.class);
////
////        if (playerPosition == null || playerHitbox == null || playerHealth == null) {
////            System.out.println("Player is missing essential components for mob attacks.");
////            return;
////        }
////
////        float playerCenterX = playerPosition.worldPos.x + playerHitbox.width / 2;
////        float playerCenterY = playerPosition.worldPos.y + playerHitbox.height / 2;
////
////        for (Mob mob : mobs) {
////            PositionComponent mobPosition = mob.getComponent(PositionComponent.class);
////            AttackComponent mobAttack = mob.getComponent(AttackComponent.class);
////            AttackCooldownComponent mobCooldown = mob.getComponent(AttackCooldownComponent.class);
////
////            if (mobPosition != null && mobAttack != null && mobCooldown != null) {
////                // Mettre à jour le cooldown
////                mobCooldown.update(delta);
////
////                // Calculer la distance entre le mob et le joueur
////                double distance = Math.sqrt(Math.pow(playerCenterX - mobPosition.worldPos.x, 2) +
////                    Math.pow(playerCenterY - mobPosition.worldPos.y, 2));
////
////                // Vérifier si le joueur est dans la portée d'attaque
////                if (distance <= mobAttack.getAttackRange()) {
////                    // Vérifier si le cooldown est terminé
////                    if (mobCooldown.isReady()) {
////                        // Appliquer des dégâts au joueur
////                        System.out.println("Mob " + mob.getId() + " attaque le joueur pour " + mobAttack.getAttackDamage() + " dégâts !");
////                        playerHealth.setHealth(playerHealth.getCurrentHealth() - mobAttack.getAttackDamage());
////
////                        // Réinitialiser le cooldown
////                        mobCooldown.resetCooldown();
////
////                        // Afficher l'effet visuel d'attaque
////                        showAttackEffect(mobPosition);
////                    }
////                }
////            }
////        }
////    }
//
//package project.project.systems;
//
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.math.Vector2;
//import project.project.components.AttackComponent;
//import project.project.components.AttackCooldownComponent;
//import project.project.components.HealthComponent;
//import project.project.components.HitboxComponent;
//import project.project.entities.Player;
//import project.project.entities.enemies.Mob;
//
//import java.util.List;
//
//    public class AttackSystem {
//        private final SpriteBatch batch;
//        private final BitmapFont font;
//
//        public AttackSystem(SpriteBatch batch) {
//            this.batch = batch;
//            this.font = new BitmapFont(); // Police pour afficher les effets d'attaque
//        }
//
//        public void update(float delta, List<Mob> mobs, Player player) {
//            HitboxComponent playerHitbox = player.getComponent(HitboxComponent.class);
//            HealthComponent playerHealth = player.getComponent(HealthComponent.class);
//
//            if (playerHitbox == null || playerHealth == null) {
//                System.out.println("Player is missing hitbox or health component.");
//                return;
//            }
//
//            for (Mob mob : mobs) {
//                AttackComponent attackComponent = mob.getComponent(AttackComponent.class);
//                AttackCooldownComponent cooldownComponent = mob.getComponent(AttackCooldownComponent.class);
//                double distance = Math.sqrt(Math.pow(mob.worldPos.x - mobHitbox.x, 2) +
//                    Math.pow(mob.worldPos.y - mobHitbox.y, 2));
//
//                if (attackComponent != null && cooldownComponent != null) {
//                    cooldownComponent.update(delta);
//
//                    // Vérifier si le joueur est dans la portée d'attaque
//                    Rectangle mobHitbox = mob.getHitboxRectangle();
//                    if (mobHitbox.overlaps(playerHitbox)) {
//                        System.out.println("Mob " + mob.getId() + " est à portée du joueur.");
//                        if (cooldownComponent.isReady()) {
//                            // Attaque le joueur
//                            System.out.println("Mob " + mob.getId() + " attaque !");
//                            playerHealth.setHealth(playerHealth.getCurrentHealth() - attackComponent.getAttackDamage());
//                            cooldownComponent.resetCooldown();
//
//                            // Affiche l'effet d'attaque
//                            showAttackEffect(mob.getPosition());
//                        } else {
//                            System.out.println("Mob " + mob.getId() + " est en cooldown.");
//                        }
//                    }
//                }
//            }
//        }
//
//        private void showAttackEffect(Vector2 mobPosition) {
//            // Affiche un effet visuel "Attaque !" au-dessus du mob
//            batch.begin();
//            font.draw(batch, "Attaque !", mobPosition.x - 10, mobPosition.y + 60); // Position ajustée
//            batch.end();
//        }
//    }
//
//
//
//    private void showAttackEffect(PositionComponent mobPosition) {
//        batch.begin();
//        font.draw(batch, "Attaque !", mobPosition.worldPos.x, mobPosition.worldPos.y + 50); // 50 pixels au-dessus du mob
//        batch.end();
//    }
//
//    public void dispose() {
//        batch.dispose();
//        font.dispose();
//    }
//}
