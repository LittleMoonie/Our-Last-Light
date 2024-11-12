package project.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen {
    public static final int MAP_WIDTH = 100; // Adjust as needed
    public static final int MAP_HEIGHT = 100; // Adjust as needed

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
        // Initialize camera with appropriate viewport size
        camera = new OrthographicCamera(MAP_WIDTH * IsometricRenderer.TILE_WIDTH / 2f, MAP_HEIGHT * IsometricRenderer.TILE_HEIGHT / 2f);
        camera.position.set(0, 0, 0); // Initialize at (0, 0)
        camera.update();

        // Initialize the renderer with map dimensions
        renderer = new IsometricRenderer(MAP_WIDTH, MAP_HEIGHT);

        // Count land tiles
        byte[][] biomeMap = renderer.getBiomeMap();
        int landCount = 0;
        for (int x = 0; x < biomeMap.length; x++) {
            for (int y = 0; y < biomeMap[0].length; y++) {
                byte biome = biomeMap[x][y];
                if (isLandBiome(biome)) {
                    landCount++;
                }
            }
        }
        System.out.println("Number of land tiles: " + landCount);

        // Find a land tile to spawn the player
        Vector2 startingTilePos = findLandTile();
        if (startingTilePos != null) {
            System.out.println("Player starting tile position: " + startingTilePos);
            player = new Player(startingTilePos);
        } else {
            System.out.println("No land tile found. Spawning at default position.");
            player = new Player(new Vector2(0, 0));
        }
    }


    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update player and handle input
        player.update(delta);
        handleInput();

        // Make the camera follow the player
        camera.position.set(player.getWorldPosition().x, player.getWorldPosition().y, 0);
        camera.update();

        // Set the camera's projection matrix
        batch.setProjectionMatrix(camera.combined);

        // Begin drawing
        batch.begin();
        renderer.drawGround(batch, player.getWorldPosition().x, player.getWorldPosition().y);
        player.render(batch);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.zoom -= 0.002f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            camera.zoom += 0.002f;
        }

        // Remove manual camera movement since the camera follows the player
    }

    /**
     * Searches the biome map for a land tile and returns its tile coordinates.
     *
     * @return A Vector2 representing the tile coordinates of a land tile, or null if none found.
     */
    private Vector2 findLandTile() {
        byte[][] biomeMap = renderer.getBiomeMap();
        int mapWidth = biomeMap.length;
        int mapHeight = biomeMap[0].length;

        int centerX = mapWidth / 2;
        int centerY = mapHeight / 2;

        int maxRadius = Math.max(centerX, centerY);

        for (int radius = 0; radius < maxRadius; radius++) {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int y = centerY - radius; y <= centerY + radius; y++) {
                    if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight) {
                        byte biome = biomeMap[x][y];
                        if (isLandBiome(biome)) {
                            return new Vector2(x, y);
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Checks if a given biome is considered land.
     *
     * @param biome The biome identifier.
     * @return True if it's a land biome, false otherwise.
     */
    private boolean isLandBiome(byte biome) {
        return biome != MapGenerator.Biome.OCEAN.ordinal() && biome != MapGenerator.Biome.BEACH.ordinal();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        player.dispose();
    }

    @Override
    public void resize(int width, int height) {
        // Optional: Adjust the viewport or camera if needed
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
