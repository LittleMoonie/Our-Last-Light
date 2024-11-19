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
import project.project.map.Biome;
import project.project.rendering.IsometricRenderer;
import project.project.map.MapGenerator;
import project.project.entities.Player;

import static project.project.utils.CoordinateUtils.worldToTile;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private SpriteBatch uiBatch; // Separate batch for UI
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera; // Separate camera for UI
    private IsometricRenderer renderer;
    private Player player;
    private BitmapFont font;

    private MapGenerator mapGenerator;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
        this.uiBatch = new SpriteBatch(); // Initialize UI batch
        this.mapGenerator = new MapGenerator(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        this.renderer = new IsometricRenderer(mapGenerator);

        // World camera for following the player
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = Constants.INITIAL_ZOOM;
        this.camera.position.set(0, 0, 0);
        this.camera.update();

        // UI camera (orthographic and static)
        this.uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.uiCamera.position.set(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 0);
        this.uiCamera.update();

        this.player = new Player(new Vector2(0, 0));
        this.font = new BitmapFont(); // Default font for UI text
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.update(delta);
        handleInput(delta);

        // Update the world camera to follow the player
        camera.position.set(player.getWorldPosition().x, player.getWorldPosition().y, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Draw world (map and player)
        batch.begin();
        renderer.drawGround(batch);
        player.render(batch);
        batch.end();

        // Draw UI (player coordinates and biome) using separate UI camera and batch
        uiBatch.setProjectionMatrix(uiCamera.combined);
        uiBatch.begin();
        displayPlayerInfo(uiBatch); // Display player info at the top left
        uiBatch.end();
    }

    private void displayPlayerInfo(SpriteBatch uiBatch) {
        Vector2 tilePos = worldToTile(player.getWorldPosition().x, player.getWorldPosition().y);
        int tileX = Math.round(tilePos.x);
        int tileY = Math.round(tilePos.y);

        // Clamp tileX and tileY to the bounds of the biomeMap
        tileX = Math.max(0, Math.min(tileX, mapGenerator.getBiomeMap().length - 1));
        tileY = Math.max(0, Math.min(tileY, mapGenerator.getBiomeMap()[0].length - 1));

        // Retrieve the biome using clamped coordinates
        byte biomeByte = mapGenerator.getBiomeMap()[tileX][tileY];
        Biome currentBiome = Biome.values()[biomeByte];

        String infoText = String.format("Position: (%d, %d) - Biome: %s", tileX, tileY, currentBiome.name());
        font.draw(uiBatch, infoText, 10, Gdx.graphics.getHeight() - 10); // Draw at top left
    }

    private void handleInput(float delta) {
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

    @Override
    public void dispose() {
        renderer.dispose();
        player.dispose();
        font.dispose();
        uiBatch.dispose(); // Dispose of UI batch
    }

    @Override
    public void resize(int width, int height) {
        // Update both world and UI cameras on resize
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        uiCamera.viewportWidth = width;
        uiCamera.viewportHeight = height;
        uiCamera.position.set(width / 2f, height / 2f, 0);
        uiCamera.update();
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void show() { }
}
