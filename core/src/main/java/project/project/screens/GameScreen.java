package project.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import project.project.Constants;
import project.project.components.TextureComponent;
import project.project.map.Biome;
import project.project.rendering.IsometricRenderer;
import project.project.map.MapGenerator;
import project.project.entities.Player;
import project.project.systems.MovementSystem;
import project.project.systems.RenderSystem;
import project.project.ui.HUD;

import static project.project.utils.CoordinateUtils.worldToTile;

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
        this.movementSystem = new MovementSystem();
        this.renderSystem = new RenderSystem(batch);
        renderSystem.addEntity(player);

        // Initialize font for debug UI
        this.font = new BitmapFont();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);

        // Update the camera to follow the player
        centerCameraOnPlayer();

        // Render the game world
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderer.drawGround(batch);
        renderSystem.update(delta); // Render entities
        batch.end();

        // Render the HUD
        hud.update();
        hud.render();
    }

    private void handleInput(float delta) {
        // Update player movement
        movementSystem.update(delta, player);

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
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.getComponent(TextureComponent.class).dispose();
        hud.dispose();
        renderer.dispose();
        font.dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void show() {
        // Optional: Debug information or initialization logic if needed
    }
}
