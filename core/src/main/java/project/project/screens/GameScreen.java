package project.project.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import project.project.Constants;
import project.project.components.*;
import project.project.entities.*;
import project.project.entities.enemies.Mob;
import project.project.map.*;
import project.project.rendering.IsometricRenderer;
import project.project.systems.*;
import project.project.ui.*;
import project.project.utils.*;

import java.util.*;

import static project.project.Constants.*;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private IsometricRenderer renderer;
    private Player player;
    private BitmapFont font;
    private HUD hud;
    private MapGenerator mapGenerator;
    private MovementSystem movementSystem;
    private RenderSystem renderSystem;
    private InventoryUI inventoryUI;
    private Stage stage;
    private boolean isInventoryOpen = false;
    private ObjectPlacementSystem placementSystem;
    private final MapLoader mapLoader;
    private MobSpawnSystem mobSpawnSystem;
    private AttackSystem attackSystem; // Ajout de l'AttackSystem
    private ShapeRenderer shapeRenderer; // Ajout de ShapeRenderer pour dessiner les zones d'attaque
    private Rectangle attackRangeRectangle; // Rectangle pour la zone d'attaque
    private float attackRangeDuration = 0.2f; // Durée d'affichage du rectangle d'attaque
    private float attackRangeTimer = 0f; // Timer pour suivre la durée d'affichage du rectangle d'attaque

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
        shapeRenderer = new ShapeRenderer(); // Initialisation de ShapeRenderer
        mapGenerator = new MapGenerator(MAP_WIDTH, MAP_HEIGHT);
        mobSpawnSystem = new MobSpawnSystem();
        mapLoader = new MapLoader(mapGenerator);
        renderer = new IsometricRenderer(mapGenerator, mapLoader);

        Vector2 centerWorldPos = isoToWorld(MAP_WIDTH / 2f, MAP_HEIGHT / 2f);
        player = new Player(new Vector2(MAP_WIDTH / 2f, MAP_HEIGHT / 2f));
        player.setWorldPosition(centerWorldPos.x, centerWorldPos.y);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = Constants.INITIAL_ZOOM;
        smoothCameraFollow();
        camera.update();

        hud = new HUD(batch, player);

        placementSystem = new ObjectPlacementSystem();
        movementSystem = new MovementSystem();
        renderSystem = new RenderSystem(batch, camera);
        attackSystem = new AttackSystem(); // Initialisation de l'AttackSystem

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        inventoryUI = new InventoryUI(stage, player, placementSystem);
        inventoryUI.setVisible(false);

        font = new BitmapFont();

        Vector2 playerPosition = player.getWorldPosition();
        mobSpawnSystem.spawnMobs(playerPosition, 15);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);

        movementSystem.update(delta, player);
        smoothCameraFollow();

        Vector2 cameraCenter = new Vector2(camera.position.x, camera.position.y);
        mapLoader.update(cameraCenter.x, cameraCenter.y);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float extendedWidth = camera.viewportWidth * camera.zoom + Constants.TILE_WIDTH * 2;
        float extendedHeight = camera.viewportHeight * camera.zoom + Constants.TILE_HEIGHT * 2;
        Rectangle viewBounds = new Rectangle(
            camera.position.x - extendedWidth / 2,
            camera.position.y - extendedHeight / 2,
            extendedWidth,
            extendedHeight
        );

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
            }
        }
        // afficher les mobs de la liste mobsInView
        System.out.println("Mobs in view: " + mobsInView.size());

        if (isAttackKeyPressed()) {
            player.attack(mobsInView); // Attaque les entités proches.
            shapeRenderer.end();
        }

        renderSystem.update(delta, visibleEntities);

        batch.end();

        renderCollisions(player, mobsInView);

        mobSpawnSystem.updateMobs(delta);
        hud.update();
        hud.render();
        stage.act(delta);
        stage.draw();
    }

    private boolean isWithinCameraView(Vector2 position, OrthographicCamera camera) {
        float startX = camera.position.x - (camera.viewportWidth * camera.zoom) / 2;
        float endX = camera.position.x + (camera.viewportWidth * camera.zoom) / 2;
        float startY = camera.position.y - (camera.viewportHeight * camera.zoom) / 2;
        float endY = camera.position.y + (camera.viewportHeight * camera.zoom) / 2;

        return position.x >= startX && position.x <= endX && position.y >= startY && position.y <= endY;
    }

    private void smoothCameraFollow() {
        Vector2 playerWorldPosition = player.getWorldPosition();
        camera.position.set(playerWorldPosition.x, playerWorldPosition.y, 0);
        camera.update();

    }

    private void handleInput(float delta) {
        movementSystem.update(delta, player);

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            isInventoryOpen = !isInventoryOpen;
            inventoryUI.setVisible(isInventoryOpen);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.zoom -= Constants.ZOOM_SPEED * delta * 10;
            camera.zoom = Math.max(Constants.MIN_ZOOM, camera.zoom);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            camera.zoom += Constants.ZOOM_SPEED * delta * 10;
            camera.zoom = Math.min(Constants.MAX_ZOOM, camera.zoom);
        }

        // Gestion des attaques au clic de la souris
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 worldPos = camera.unproject(new Vector3(mousePos.x, mousePos.y, 0)); // Utilisation de Vector3

            // Créer un grand rectangle autour de la position de la souris
            float rectangleSize = 2.0f; // Taille du rectangle d'attaque
            attackRangeRectangle = new Rectangle(
                worldPos.x - rectangleSize / 2,
                worldPos.y - rectangleSize / 2,
                rectangleSize,
                rectangleSize
            );

            // Réinitialiser le timer pour afficher le rectangle
            attackRangeTimer = 0f;

            // Vérifier si un Mob est dans la zone d'attaque
            List<Mob> mobs = mobSpawnSystem.getMobs();
            List<Entity> entities = new ArrayList<>(); // Define entities
            entities.add(player);
            entities.addAll(mobs);

            for (Mob mob : mobs) {
                PositionComponent mobPosition = mob.getComponent(PositionComponent.class);
                if (mobPosition != null && attackRangeRectangle.contains(mobPosition.worldPos)) {
                    attackSystem.handleAttack(player, entities);
                    break;
                }
            }
        }

        camera.update();
    }

    private boolean isAttackKeyPressed() {
        // barre d'espace pour attaque et le mettre a false pour ne pas attaquer en continue
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
    }

    private void drawAttackRanges(float delta) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dessiner la zone d'attaque du Player en rouge si un rectangle est défini
        if (attackRangeRectangle != null) {
            attackRangeTimer += delta;
            if (attackRangeTimer < attackRangeDuration) {
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.rect(attackRangeRectangle.x, attackRangeRectangle.y, attackRangeRectangle.width, attackRangeRectangle.height);
            } else {
                attackRangeRectangle = null; // Réinitialiser le rectangle après la durée
            }
        }

        shapeRenderer.end();
    }


    private void renderCollisions(Player player, List<Mob> mobs) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Récupère la position du joueur
        PositionComponent playerPosition = player.getComponent(PositionComponent.class);
        HitboxComponent playerHitbox = player.getComponent(HitboxComponent.class);
        if (playerPosition != null && playerHitbox != null) {
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(
                playerPosition.worldPos.x, playerPosition.worldPos.y,
                playerHitbox.width, playerHitbox.height
            );
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

            if (mobHitbox != null) {
                shapeRenderer.setColor(Color.BLUE);
                shapeRenderer.rect(
                    mobHitbox.x, mobHitbox.y, // Utilisation des coordonnées de la hitbox
                    mobHitbox.width, mobHitbox.height
                );
            } else {
                System.out.println("Mob Hitbox is null");
            }
        }
        shapeRenderer.end();
    }


    private Vector2 isoToWorld(float tileX, float tileY) {
        return CoordinateUtils.tileToWorld(tileX, tileY);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        stage.getViewport().update(width, height, true);

        inventoryUI.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.getComponent(TextureComponent.class).dispose();
        hud.dispose();
        renderer.dispose();
        font.dispose();
        stage.dispose();
        mapLoader.dispose();
        shapeRenderer.dispose(); // Dispose de ShapeRenderer
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
