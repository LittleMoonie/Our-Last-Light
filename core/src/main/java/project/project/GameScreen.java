// core/src/main/java/project/project/GameScreen.java
package project.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private IsometricRenderer renderer;
    private Player player;
    private Random random;
    private MapGenerator mapGenerator;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
        this.random = new Random(); // Initialize Random
    }

    @Override
    public void show() {

        // Initialize MapGenerator without worrying about seed generation
        mapGenerator = new MapGenerator(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);

        // Pass the map generator to the renderer
        renderer = new IsometricRenderer(mapGenerator);

        // Set up the camera centered at (0, 0) in world coordinates
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = Constants.INITIAL_ZOOM; // Adjust zoom as needed for larger maps

        // Center the camera at (0, 0) world coordinates initially
        camera.position.set(0, 0, 0);
        camera.update();

        // Spawn the player at the center of the island
//        Vector2 spawnPosition = findIslandCenterLandTile();
        player = new Player(tileToWorld(0, 0));

        // Adjust the camera to center on the player
        camera.position.set(player.getWorldPosition().x, player.getWorldPosition().y, 0);
        camera.update();

        // Pre-load chunks around the spawn position within render distance
//        renderer.preloadChunks(player.getWorldPosition().x, player.getWorldPosition().y);
    }

//    private Vector2 findIslandCenterLandTile() {
//        byte[][] biomeMap = renderer.getBiomeMap();
//        int centerX = biomeMap.length / 2;
//        int centerY = biomeMap[0].length / 2;
//        int radius = Math.min(centerX, centerY);
//
//        for (int r = 0; r < radius; r++) {
//            for (int x = centerX - r; x <= centerX + r; x++) {
//                for (int y = centerY - r; y <= centerY + r; y++) {
//                    if (x >= 0 && y >= 0 && x < biomeMap.length && y < biomeMap[0].length &&
//                        isLandBiome(biomeMap[x][y])) {
//                        return tileToWorld(x - centerX, y - centerY);
//                    }
//                }
//            }
//        }
//
//        System.err.println("Error: Could not find a central land tile.");
//        return tileToWorld(0, 0); // Fallback
//    }


    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update player and handle input
        player.update(delta);
        handleInput(delta);

        // Make the camera follow the player
        camera.position.set(player.getWorldPosition().x, player.getWorldPosition().y, 0);
        camera.update();

        // Set the camera's projection matrix
        batch.setProjectionMatrix(camera.combined);

        // Begin drawing
        batch.begin();
        renderer.drawGround(batch);
        player.render(batch);
        batch.end();
    }

    private void handleInput(float delta) {
        // Zoom controls
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.zoom -= Constants.ZOOM_SPEED * delta * 60; // Adjust for frame rate
            camera.zoom = Math.max(Constants.MIN_ZOOM, camera.zoom);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            camera.zoom += Constants.ZOOM_SPEED * delta * 60; // Adjust for frame rate
            camera.zoom = Math.min(Constants.MAX_ZOOM, camera.zoom);
        }

        camera.update();
    }

    /**
     * Randomly searches the biome map for a land tile and returns its world coordinates.
     *
     * @return A Vector2 representing the world coordinates of a land tile, or null if none found.
     */
// core/src/main/java/project/project/GameScreen.java

    private Vector2 findRandomLandTile() {
        byte[][] biomeMap = renderer.getBiomeMap();
        int mapWidth = biomeMap.length;
        int mapHeight = biomeMap[0].length;

        int centerX = mapWidth / 2;
        int centerY = mapHeight / 2;

        int maxRadius = (int) (Math.min(centerX, centerY) * 0.95); // 95% of half the map size

        // Try up to a certain number of attempts to find a suitable land tile
        int maxAttempts = 1000; // Define an appropriate attempt limit
        for (int i = 0; i < maxAttempts; i++) {
            int x = centerX + (int)(random.nextGaussian() * maxRadius);
            int y = centerY + (int)(random.nextGaussian() * maxRadius);
            if (x >= 0 && y >= 0 && x < mapWidth && y < mapHeight && isLandBiome(biomeMap[x][y])) {
                Vector2 worldPos = tileToWorld(x - centerX, y - centerY);
                return worldPos;
            }
        }

        // If no land tile found within the max attempts, use center as fallback
        return tileToWorld(0, 0);
    }


    private Vector2 tileToWorld(int tileX, int tileY) {
        float worldX = (tileX - tileY) * (Constants.TILE_WIDTH / 2f);
        float worldY = (tileX + tileY) * (Constants.TILE_HEIGHT / 2f);
        return new Vector2(worldX, worldY);
    }

    private Vector2 worldToTile(float worldX, float worldY) {
        float tileX = (worldY / Constants.TILE_HEIGHT + worldX / Constants.TILE_WIDTH) / 2f;
        float tileY = (worldY / Constants.TILE_HEIGHT - worldX / Constants.TILE_WIDTH) / 2f;
        return new Vector2(tileX, tileY);
    }

    private void testCoordinateTransformations() {
        Vector2 originalTile = new Vector2(10, 15);
        Vector2 worldPos = tileToWorld((int) originalTile.x, (int) originalTile.y);
        Vector2 convertedTile = worldToTile(worldPos.x, worldPos.y);
    }

    /**
     * Checks if a given biome is considered land.
     *
     * @param biome The biome identifier.
     * @return True if it's a land biome, false otherwise.
     */
    private boolean isLandBiome(byte biome) {
        return biome != MapGenerator.Biome.WATER.ordinal();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        player.dispose();
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
