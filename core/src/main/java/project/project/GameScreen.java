////// GameScreen.java
package project.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import project.project.components.TextureComponent;
import project.project.entities.Player;
import project.project.systems.MovementSystem;
import project.project.systems.RenderSystem;

public class GameScreen implements Screen {
    public static final int MAP_WIDTH = 100; // Adjust as needed
    public static final int MAP_HEIGHT = 100; // Adjust as needed
    private MovementSystem movementSystem;
    private RenderSystem renderSystem;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private IsometricRenderer renderer;
    private Player player;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
        show(); // Initialize game components
    }

    @Override
    public void show() {
        // Center of the map dimensions in isometric coordinates
        float centerX = MAP_WIDTH / 2f;
        float centerY = MAP_HEIGHT / 2f;

        // Convert isometric center to world coordinates
        Vector2 centerWorldPos = isoToWorld(centerX, centerY);

        // Place the player at the center
        player = new Player(new Vector2(centerX, centerY));
        player.setWorldPosition(centerWorldPos.x, centerWorldPos.y);

        // Initialize the camera to follow the player from the start
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        centerCameraOnPlayer(); // Ensure camera starts centered on the player
        camera.update();

        // Initialize the renderer
        renderer = new IsometricRenderer(MAP_WIDTH, MAP_HEIGHT);

        // Systems
        movementSystem = new MovementSystem();
        renderSystem = new RenderSystem(batch);

        // Debugging
        System.out.println("Player position (iso): x = " + centerX + ", y = " + centerY);
        System.out.println("Player position (world): x = " + centerWorldPos.x + ", y = " + centerWorldPos.y);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update systems
        movementSystem.update(delta, player);

        // Make the camera follow the player
        if (player.getWorldPosition() != null) {
            centerCameraOnPlayer();
            camera.update();
        }

        // Set the camera's projection matrix
        batch.setProjectionMatrix(camera.combined);

        // Draw the ground
        batch.begin();
        renderer.drawGround(batch, player.getWorldPosition().x, player.getWorldPosition().y); // Draw the map
        renderSystem.update(delta, player); // Draw the player
        handleInput();
        batch.end(); // Ensure to end the batch here
    }

    private Vector2 isoToWorld(float tileX, float tileY) {
        // Correct conversion from isometric to world coordinates
        float worldX = (tileX - tileY) * (IsometricRenderer.TILE_WIDTH / 2f);
        float worldY = (tileX + tileY) * (IsometricRenderer.TILE_HEIGHT / 2f);
        return new Vector2(worldX, worldY);
    }
    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.zoom -= 0.002f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            camera.zoom += 0.002f;
        }

        // Center the camera on the player
        if (player.getWorldPosition() != null) {
            camera.position.set(player.getWorldPosition().x, player.getWorldPosition().y, 0);
            camera.update();
        }
    }

    private void centerCameraOnPlayer() {
        Vector2 playerWorldPosition = player.getWorldPosition();
        camera.position.set(playerWorldPosition.x, playerWorldPosition.y, 0);
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.getComponent(TextureComponent.class).dispose();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void pause() {
        // Handle game pause
    }

    @Override
    public void resume() {
        // Handle game resume
    }

    @Override
    public void hide() {
        // Called when the screen is no longer the current screen
    }
}
