package project.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import project.project.Constants;
import project.project.components.TextureComponent;
import project.project.entities.Entity;
import project.project.map.MapLoader;
import project.project.rendering.IsometricRenderer;
import project.project.map.MapGenerator;
import project.project.entities.Player;
import project.project.entities.enemies.Mob;
import project.project.systems.MobSpawnSystem;
import project.project.systems.MovementSystem;
import project.project.systems.ObjectPlacementSystem;
import project.project.systems.RenderSystem;
import project.project.ui.HUD;
import project.project.ui.InventoryUI;
import project.project.utils.CoordinateUtils;

import java.util.ArrayList;
import java.util.List;

import static project.project.Constants.MAP_HEIGHT;
import static project.project.Constants.MAP_WIDTH;

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

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
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

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        inventoryUI = new InventoryUI(stage, player, placementSystem);
        inventoryUI.setVisible(false);

        font = new BitmapFont();

        Vector2 playerPosition = player.getWorldPosition();
        mobSpawnSystem.spawnMob(playerPosition);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        inventoryUI = new InventoryUI(stage, player, placementSystem);
        inventoryUI.setVisible(false);

        Vector2 playerPosition = player.getWorldPosition();

        // Spawn mobs and add them to the render system
        mobSpawnSystem.spawnMob(playerPosition);
        List<Mob> mobs = mobSpawnSystem.getMobs();
        for (Mob mob : mobs) {
            renderSystem.addEntity(mob); // Add each mob to the RenderSystem
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

        List<Entity> entities = new ArrayList<>();
        entities.add(player);

        for (Mob mob : mobSpawnSystem.getMobs()) {
            if (isWithinCameraView(mob.getWorldPosition(), camera)) {
            System.out.println("Mob position: " + mob.getWorldPosition());
            System.out.println("Player position: " + player.getWorldPosition());

                mob.render(batch);
                mob.update(delta, player.getWorldPosition());
            } else {
                System.out.println("Mob out of view: " + mob.getWorldPosition());
            }
        }

        renderSystem.update(delta, entities);

        batch.end();

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

        float lerp = 0.05f;
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
        camera.update();
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
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
