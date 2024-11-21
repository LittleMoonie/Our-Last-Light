package project.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import project.project.Constants;
import project.project.components.TextureComponent;
import project.project.rendering.IsometricRenderer;
import project.project.map.MapGenerator;
import project.project.entities.Player;
import project.project.systems.MovementSystem;
import project.project.systems.ObjectPlacementSystem;
import project.project.systems.RenderSystem;
import project.project.ui.HUD;
import project.project.ui.InventoryUI;

public class GameScreen implements Screen {
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

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;

        // Initialize map generator and renderer
        this.mapGenerator = new MapGenerator(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        this.renderer = new IsometricRenderer(mapGenerator);

        // Initialize camera
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = Constants.INITIAL_ZOOM;
        this.camera.update();

        // Initialize player
        Vector2 initialTile = new Vector2(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f);
        this.player = new Player(initialTile);

        // Initialize HUD
        this.hud = new HUD(batch, player);

        // Initialize systems
        this.placementSystem = new ObjectPlacementSystem();
        this.movementSystem = new MovementSystem(placementSystem);

        // Initialize render system (fix for NullPointerException)
        this.renderSystem = new RenderSystem(batch);

        // Add the player to the render system
        renderSystem.addEntity(player);

        // Initialize stage
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Initialize the inventory UI
        this.inventoryUI = new InventoryUI(stage, player, placementSystem);
        inventoryUI.setVisible(false); // Start with the inventory hidden
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Redirect input to stage

        // Initialize the inventory UI
        inventoryUI = new InventoryUI(stage, player, placementSystem);
        inventoryUI.setVisible(false); // Start with the inventory hidden
    }

    @Override
    public void render(float delta) {
        // Ensure systems are initialized
        if (renderSystem == null) {
            throw new IllegalStateException("RenderSystem is not initialized!");
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);

        // Update camera
        centerCameraOnPlayer();

        // Render game world
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderer.drawGround(batch);
        renderSystem.update(delta); // Render entities
        batch.end();

        // Render HUD and UI
        hud.update();
        hud.render();

        stage.act(delta);
        stage.draw();
    }

    private void handleInput(float delta) {
        // Update player movement
        movementSystem.update(delta, player);

        // Toggle inventory with "E"
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            isInventoryOpen = !isInventoryOpen;
            inventoryUI.setVisible(isInventoryOpen); // Show or hide the inventory
        }

        // Handle camera zoom
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.zoom -= Constants.ZOOM_SPEED * delta * 60;
            camera.zoom = Math.max(Constants.MIN_ZOOM, camera.zoom);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            camera.zoom += Constants.ZOOM_SPEED * delta * 60;
            camera.zoom = Math.min(Constants.MAX_ZOOM, camera.zoom);
        }
        camera.update();
    }

    private void centerCameraOnPlayer() {
        Vector2 playerWorldPosition = player.getWorldPosition();
        camera.position.set(playerWorldPosition.x, playerWorldPosition.y, 0);
        camera.update();
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
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
