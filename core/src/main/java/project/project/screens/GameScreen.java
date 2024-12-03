// GameScreen.java
package project.project.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import project.project.Constants;
import project.project.components.*;
import project.project.entities.*;
import project.project.entities.enemies.Mob;
import project.project.map.*;
import project.project.components.PositionComponent;
import project.project.components.TextureComponent;
import project.project.entities.Character;
import project.project.map.MapLoader;
import project.project.rendering.IsometricRenderer;
import project.project.systems.*;
import project.project.ui.*;
import project.project.utils.*;
import project.project.map.MapGenerator;
import project.project.entities.Player;
import project.project.systems.MovementSystem;
import project.project.systems.ObjectPlacementSystem;
import project.project.systems.RenderSystem;
import project.project.ui.HUD;
import project.project.ui.InventoryUI;
import project.project.ui.menu.WorldSelectionScreen;
import project.project.utils.CoordinateUtils;
import project.project.ui.InventoryUI;

import java.util.*;

import static project.project.Constants.*;
import java.io.*;
import java.util.Scanner;

import static project.project.Constants.MAP_HEIGHT;
import static project.project.Constants.MAP_WIDTH;

public class GameScreen implements Screen {
    private final String worldName;
    private final String username;

    private SpriteBatch batch;
    private OrthographicCamera camera; // World camera
    private IsometricRenderer renderer;
    private Player player;
    private BitmapFont font;
    private HUD hud;
    private MapGenerator mapGenerator;
    private MovementSystem movementSystem;
    private RenderSystem renderSystem;
    private InventoryUI inventoryUI;
    private Stage stage;
    private boolean isInventoryOpen = false; // Track inventory state
    private ObjectPlacementSystem placementSystem;
    private final MapLoader mapLoader;
    private MobSpawnSystem mobSpawnSystem;
//    private AttackSystem attackSystem; // Ajout de l'AttackSystem
    private ShapeRenderer shapeRenderer; // Ajout de ShapeRenderer pour dessiner les zones d'attaque
    private Rectangle attackRangeRectangle; // Rectangle pour la zone d'attaque
    private float attackRangeDuration = 0.2f; // Durée d'affichage du rectangle d'attaque
    private float attackRangeTimer = 0f; // Timer pour suivre la durée d'affichage du rectangle d'attaque


    private List<Mob> deadMobs = new ArrayList<>();


    public GameScreen(SpriteBatch batch, String worldName, String username) {
        this.batch = batch;
        shapeRenderer = new ShapeRenderer(); // Initialisation de ShapeRenderer
        this.mobSpawnSystem = new MobSpawnSystem();
        this.worldName = worldName;
        this.username = username;

        // Map generator and renderer
        MapGenerator mapGenerator = new MapGenerator(MAP_WIDTH, MAP_HEIGHT);
        this.mapLoader = new MapLoader(mapGenerator);
        this.renderer = new IsometricRenderer(mapGenerator, mapLoader);
        float centerX = MAP_WIDTH / 2f;
        float centerY = MAP_HEIGHT / 2f;

        // Convert isometric center to world coordinates
        Vector2 centerWorldPos = isoToWorld(centerX, centerY);
        player = new Player(new Vector2(centerX, centerY));
        player.setWorldPosition(centerWorldPos.x, centerWorldPos.y);

        // Camera setup
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = Constants.INITIAL_ZOOM;
        smoothCameraFollow();
        this.camera.update();

        // HUD and systems
        this.hud = new HUD(batch, player);

        // Pass the render system to the placement system
        this.renderSystem = new RenderSystem(batch, camera);
        this.placementSystem = new ObjectPlacementSystem(renderSystem);

        stage = new Stage(new ScreenViewport());
        // Add the player to the render system
        this.movementSystem = new MovementSystem();
        renderSystem.addEntity(player);

        // Initialize stage
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Initialize the inventory UI
        this.inventoryUI = new InventoryUI(stage, player, placementSystem);
        inventoryUI.setVisible(false); // Start with the inventory hidden
        // Font for debug UI
        this.font = new BitmapFont();

        Vector2 playerPosition = player.getWorldPosition();
        this.mobSpawnSystem.spawnMobs(playerPosition, 5);

        // Initialize game-specific components using worldName and username
        System.out.println("Game started for user: " + username + " in world: " + worldName);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Redirect input to stage

        // Initialize the inventory UI
        inventoryUI = new InventoryUI(stage, player, placementSystem);
        inventoryUI.setVisible(false);

        Vector2 playerPosition = player.getWorldPosition();

        // Ajouter les mobs au RenderSystem
        List<Mob> mobs = mobSpawnSystem.getMobs();
        for (Mob mob : mobs) {
            renderSystem.addEntity(mob);
            System.out.println("Mob added to RenderSystem: " + mob.getWorldPosition());
        }
    }

    @Override
    public void render(float delta) {
        // Ensure systems are initialized
        if (renderSystem == null) {
            throw new IllegalStateException("RenderSystem is not initialized!");
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle input
        handleInput(delta);

        // Update player position smoothly
        movementSystem.update(delta, player);

        // Interpolate camera to follow the player smoothly
        smoothCameraFollow();

        // Update chunks based on camera position
        Vector2 cameraCenter = new Vector2(camera.position.x, camera.position.y);
        mapLoader.update(cameraCenter.x, cameraCenter.y);

        // Render game world
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Calculate visible bounds based on camera zoom
        float scaledViewportWidth = camera.viewportWidth * camera.zoom;
        float scaledViewportHeight = camera.viewportHeight * camera.zoom;

        float tileWidth = Constants.TILE_WIDTH;
        float tileHeight = Constants.TILE_HEIGHT;

        float extendedWidth = scaledViewportWidth + tileWidth * 2;
        float extendedHeight = scaledViewportHeight + tileHeight * 2;

        Rectangle viewBounds = new Rectangle(
            camera.position.x - extendedWidth / 2,
            camera.position.y - extendedHeight / 2,
            extendedWidth,
            extendedHeight
        );

        // Placement mode logic
        if (placementSystem.isPlacingObject()) {
            Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());

            // Unproject the mouse position from screen coordinates to world coordinates
            Vector3 worldPos3D = camera.unproject(new Vector3(mousePos.x, mousePos.y, 0));
            Vector2 worldPos = new Vector2(worldPos3D.x, worldPos3D.y);

            placementSystem.updatePlacement(worldPos);

            // Confirm placement on left click
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                boolean placed = placementSystem.confirmPlacement(worldPos);
            }
        }

        // Draw ground and entities
        renderer.drawGround(batch, viewBounds);

        List<Entity> visibleEntities = new ArrayList<>();
        visibleEntities.add(player);


        List<Mob> mobsInView = new ArrayList<>();
        for (Mob mob : mobSpawnSystem.getMobs()) {
            if (isWithinCameraView(mob.getWorldPosition(), camera)) {
                mob.render(batch,shapeRenderer); // Dessine le mob avec ShapeRenderer
                mob.update(delta, player.getWorldPosition());
                mobsInView.add(mob);
                visibleEntities.add(mob);

                // Dessiner la vie du mob au-dessus du mob
                HealthComponent healthComponent = mob.getComponent(HealthComponent.class);
                if (healthComponent != null) {
                    float healthPercentage = (float) healthComponent.getCurrentHealth() / healthComponent.getMaxHealth();
                    String healthText = String.format("%.0f", (float) healthComponent.getCurrentHealth());
                    font.draw(batch, healthText, mob.getWorldPosition().x, mob.getWorldPosition().y + 32);
                }

                AttackCooldownComponent attackCooldown = mob.getComponent(AttackCooldownComponent.class);
//                if (attackCooldown != null && attackCooldown.cooldownTimer <= 0) {
//                    // Afficher le pistolet
//                    font.getData().setScale(2f);
//                    font.draw(batch, "piou piou",
//                        mob.getWorldPosition().x,
//                        mob.getWorldPosition().y + mob.getHitboxRectangle().height + 20
//                    );
//                    font.getData().setScale(1f); // Rétablir la taille de police par défaut
//                }
            }
        }
        // afficher les mobs de la liste mobsInView
//        System.out.println("Mobs in view: " + mobsInView.size());

        renderSystem.update(delta, visibleEntities);


//        attackSystem.update(delta, mobsInView, player);

        // Ajouter la vérification et la gestion des attaques des mobs
        checkMobAttacksOnPlayer(delta);
        batch.end();

        renderCollisions(player, mobsInView);

        mobSpawnSystem.updateMobs(delta);
        hud.update();
        hud.render();

        stage.act(delta);
        stage.draw();
    }
    private boolean isInAttackRange(Mob mob, Player player) {
        PositionComponent mobPosition = mob.getComponent(PositionComponent.class);
        PositionComponent playerPosition = player.getComponent(PositionComponent.class);
        AttackComponent mobAttack = mob.getComponent(AttackComponent.class);

        if (mobPosition != null && playerPosition != null && mobAttack != null) {
            float distance = mobPosition.worldPos.dst(playerPosition.worldPos);
            return distance <= mobAttack.getAttackRange();
        }
        return false;
    }

    public void applyDamageToPlayer(Player player, Mob mob) {
        // Récupérer le composant de santé du mob
        HealthComponent playerHealth = player.getComponent(HealthComponent.class);

        // Vérifier si le mob a un composant de santé
        if (playerHealth != null) {
            int damage = mob.getComponent(AttackComponent.class).getAttackDamage(); // Récupérer les dégâts du joueur

            playerHealth.takeDamage(damage);

            // Afficher un message dans la console pour debug
            System.out.println("Le mob " + mob.getId() + " a fait " + damage + " dégâts au player. Vie restante : " + playerHealth.currentHealth);

            // Si la santé du mob est inférieure ou égale à 0, le mob est tué
            if (playerHealth.currentHealth <= 0) {
                System.out.println( player.getName() + " est mort !");
            }
        }
    }


    private void checkMobAttacksOnPlayer(float delta) {
        List<Mob> mobs = mobSpawnSystem.getMobs();
        HealthComponent playerHealth = player.getComponent(HealthComponent.class);
        PositionComponent playerPosition = player.getComponent(PositionComponent.class);

        if (playerHealth == null || playerPosition == null) {
            return;
        }

        for (Mob mob : mobs) {
            // Mettre à jour l'effet d'attaque pour chaque mob
            mob.updateAttackEffect(delta);

            // Vérifier si le mob peut attaquer
            AttackComponent mobAttack = mob.getComponent(AttackComponent.class);
            AttackCooldownComponent attackCooldown = mob.getComponent(AttackCooldownComponent.class);

            if (mobAttack == null || attackCooldown == null) {
                continue;
            }

            // Réduire le temps de cooldown
            attackCooldown.cooldownTimer -= delta;

            // Calculer la distance entre le mob et le joueur
            float distance = mob.getPosition().dst(playerPosition.worldPos);

            // Vérifier si le joueur est dans la portée d'attaque et si le cooldown est écoulé
            if (distance <= mobAttack.getAttackRange() && attackCooldown.cooldownTimer <= 0) {
                // Attaquer le joueur
                int damage = mobAttack.getAttackDamage();
                playerHealth.takeDamage(damage);

                // Déclencher l'effet d'attaque du mob
                mob.startAttack();

                // Réinitialiser le cooldown
                attackCooldown.cooldownTimer = attackCooldown.cooldownDuration;

                System.out.println("Mob " + mob.getId() + " attaque le joueur et fait " + damage + " dégâts. Vie restante du joueur : " + playerHealth.currentHealth);

                // Vérifier si le joueur est mort
                if (playerHealth.currentHealth <= 0) {
                    System.out.println("Le joueur est mort !");
                    //
                }
            }
        }
    }

    private void smoothCameraFollow() {
        Vector2 playerWorldPosition = player.getWorldPosition();
        camera.position.set(playerWorldPosition.x, playerWorldPosition.y, 0);
        camera.update();
    }
    private boolean isWithinCameraView(Vector2 position, OrthographicCamera camera) {
        float startX = camera.position.x - (camera.viewportWidth * camera.zoom) / 2;
        float endX = camera.position.x + (camera.viewportWidth * camera.zoom) / 2;
        float startY = camera.position.y - (camera.viewportHeight * camera.zoom) / 2;
        float endY = camera.position.y + (camera.viewportHeight * camera.zoom) / 2;

        return position.x >= startX && position.x <= endX && position.y >= startY && position.y <= endY;
    }

    private void handleInput(float delta) {
        // Handle player movement
        movementSystem.update(delta, player);

        // Toggle inventory with "E"
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            isInventoryOpen = !isInventoryOpen;
            inventoryUI.setVisible(isInventoryOpen); // Show or hide the inventory
        }

        // Handle camera zoom
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.zoom -= Constants.ZOOM_SPEED * delta * 10;
            camera.zoom = Math.max(Constants.MIN_ZOOM, camera.zoom);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            camera.zoom += Constants.ZOOM_SPEED * delta * 10;
            camera.zoom = Math.min(Constants.MAX_ZOOM, camera.zoom);
        }

        // Gestion de l'attaque avec la touche espace
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            System.out.println("Attaque détectée !");
            List<Mob> mobs = mobSpawnSystem.getMobs();
            List<Mob> deadMobs = new ArrayList<>();

            for (Mob mob : mobs) {
                // Récupérer la position du mob
                Vector2 mobPosition = mob.getPosition();
                if (mobPosition != null) {
                    // Calculer la distance entre le centre du cercle d'attaque du joueur et la position du mob
                    PositionComponent playerPosition = player.getComponent(PositionComponent.class);
                    HitboxComponent playerHitbox = player.getComponent(HitboxComponent.class);
                    AttackComponent playerAttack = player.getComponent(AttackComponent.class);

                    if (playerPosition != null && playerHitbox != null && playerAttack != null) {
                        float playerCenterX = playerPosition.worldPos.x + playerHitbox.width / 2;
                        float playerCenterY = playerPosition.worldPos.y + playerHitbox.height / 2;

                        // Calculer la distance entre le centre du cercle d'attaque et la position du mob
                        double distance = Math.sqrt(Math.pow(playerCenterX - mobPosition.x, 2) +
                            Math.pow(playerCenterY - mobPosition.y, 2));

                        // Vérifier si le mob est dans le cercle d'attaque du joueur
                        if (distance <= playerAttack.getAttackRange()) {
                            // Appliquer les dégâts si le mob est dans la portée d'attaque
                            System.out.println("Mob " + mob.getId() + " est dans la range d'attaque de Player et il lui reste " + mob.getComponent(HealthComponent.class).getCurrentHealth() + " de vie");
                            applyDamage(mob);

                            // Vérifier si le mob est mort et l'ajouter à la liste des mobs morts
                            HealthComponent mobHealth = mob.getComponent(HealthComponent.class);
                            if (mobHealth != null && mobHealth.getCurrentHealth() <= 0) {
                                System.out.println("Le mob " + mob.getId() + " est mort !");
                                deadMobs.add(mob);
                            }
                        }
                    } else {
                        System.out.println("Un des composants manquants pour le mob " + mob.getId());
                    }
                } else {
                    System.out.println("Position non trouvée pour le mob " + mob.getId());
                }
            }
        }

        camera.update();
    }
    private void renderCollisions(Player player, List<Mob> mobs) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Commencez à dessiner des formes en mode "Ligne" pour la hitbox
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Récupère la position du joueur
        PositionComponent playerPosition = player.getComponent(PositionComponent.class);
        HitboxComponent playerHitbox = player.getComponent(HitboxComponent.class);
        AttackComponent playerAttack = player.getComponent(AttackComponent.class);

        if (playerPosition != null && playerHitbox != null && playerAttack != null) {


            // Dessiner la hitbox du joueur
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(
                playerPosition.worldPos.x, playerPosition.worldPos.y,
                playerHitbox.width, playerHitbox.height
            );

            shapeRenderer.setColor(1, 0.647f, 0, 0.5f); // Orange avec une transparence (alpha = 0.5f)

            // Dessiner le cercle rempli pour la zone d'attaque
            shapeRenderer.circle(playerPosition.worldPos.x + playerHitbox.width / 2,
                playerPosition.worldPos.y + playerHitbox.height / 2,
                playerAttack.getAttackRange());

        } else {
            if (playerPosition == null) {
                System.out.println("Player PositionComponent not found");
            }
            if (playerHitbox == null) {
                System.out.println("Player HitboxComponent not found");
            }
        }

        // Itération sur les mobs
        for (Mob mob : mobs) {
            // Utilise la méthode getHitboxRectangle() pour récupérer la hitbox correcte du mob
            Rectangle mobHitbox = mob.getHitboxRectangle();
            AttackComponent mobAttack = mob.getComponent(AttackComponent.class);
            HealthComponent mobHealth = mob.getComponent(HealthComponent.class);

            if (mobHitbox != null && mobAttack != null) {
                shapeRenderer.setColor(Color.BLUE);
                shapeRenderer.rect(
                    mobHitbox.x, mobHitbox.y, // Utilisation des coordonnées de la hitbox
                    mobHitbox.width, mobHitbox.height
                );

                // Dessiner le cercle rempli pour la zone
                shapeRenderer.setColor(1, 0.647f, 0, 0.5f);
                shapeRenderer.circle(mobHitbox.x + mobHitbox.width / 2,
                    mobHitbox.y + mobHitbox.height / 2,
                    mobAttack.getAttackRange());

                // Calculer la distance entre le joueur et le mob
                double distance = Math.sqrt(Math.pow(playerPosition.worldPos.x - mobHitbox.x, 2) +
                    Math.pow(playerPosition.worldPos.y - mobHitbox.y, 2));

                // Vérifier si le mob est dans la portée d'attaque du joueur
                if (distance <= playerAttack.getAttackRange()) {
                    // Afficher le message indiquant que le mob est dans la portée d'attaque
//                    System.out.println("Mob " + mob.getId() + " est dans la range d'attaque de " + player.getName()+" et il lui reste "+mobHealth.currentHealth+" de vie");
                }
            } else {
                System.out.println("Mob Hitbox is null");
            }
        }

        // Terminer le dessin pour les mobs
        shapeRenderer.end();
    }

    private void applyDamage(Mob mob) {
        // Récupérer le composant de santé du mob
        HealthComponent mobHealth = mob.getComponent(HealthComponent.class);

        // Vérifier si le mob a un composant de santé
        if (mobHealth != null) {
            int damage = player.getComponent(AttackComponent.class).getAttackDamage(); // Récupérer les dégâts du joueur

            mobHealth.takeDamage(damage);

            // Afficher un message dans la console pour debug
            System.out.println("Le mob " + mob.getId() + " a reçu " + damage + " dégâts. Vie restante : " + mobHealth.currentHealth);

            // Si la santé du mob est inférieure ou égale à 0, le mob est tué
            if (mobHealth.currentHealth <= 0) {
                System.out.println("Le mob " + mob.getId() + " est mort !");
                mobDie(mob); // Appeler une méthode pour gérer la mort du mob
            }
        }
    }

    private void mobDie(Mob mob) {
        deadMobs.add(mob); // Ajouter le mob à la liste des morts
    }





    private Vector2 isoToWorld(float tileX, float tileY) {
        // Correct conversion from isometric to world coordinates
        return CoordinateUtils.tileToWorld(tileX, tileY);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        // Update the stage viewport
        stage.getViewport().update(width, height, true);

        // Resize UI elements
        inventoryUI.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.getComponent(TextureComponent.class).dispose();
        hud.dispose();
        renderer.dispose();
        font.dispose();
        stage.dispose(); // Dispose the stage
        mapLoader.dispose();
        shapeRenderer.dispose(); // Dispose de ShapeRenderer
//        attackSystem.dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

}
