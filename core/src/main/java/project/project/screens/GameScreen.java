// GameScreen.java
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
import project.project.map.MapLoader;
import project.project.rendering.IsometricRenderer;
import project.project.map.MapGenerator;
import project.project.entities.Player;
import project.project.systems.MovementSystem;
import project.project.systems.RenderSystem;
import project.project.ui.HUD;
import project.project.utils.CoordinateUtils;
import project.project.ui.InventoryUI;

import static project.project.Constants.MAP_HEIGHT;
import static project.project.Constants.MAP_WIDTH;

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
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final IsometricRenderer renderer;
    private final Player player;
    private final BitmapFont font;
    private final HUD hud;
    private final MapLoader mapLoader;
    private final MovementSystem movementSystem;
    private final RenderSystem renderSystem;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;


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
        this.movementSystem = new MovementSystem();
        this.renderSystem = new RenderSystem(batch, camera);
        renderSystem.addEntity(player);

        // Font for debug UI
        this.font = new BitmapFont();

        int totalChunks = mapLoader.getTotalChunks();
        System.out.println("Total number of chunks: " + totalChunks);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Redirect input to stage

        // Initialize the inventory UI
        inventoryUI = new InventoryUI(stage, player);
        inventoryUI.setVisible(false); // Start with the inventory hidden
    }

    public void render(float delta) {
        // Ensure the hotbar and inventory are correctly positioned
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle input
        handleInput(delta);

        // Update camera
        centerCameraOnPlayer();




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


        // Calculer les limites visibles en fonction du zoom de la caméra
        float scaledViewportWidth = camera.viewportWidth * camera.zoom;
        float scaledViewportHeight = camera.viewportHeight * camera.zoom;

        // Ajouter deux tuiles de marge de chaque côté
        float tileWidth = Constants.TILE_WIDTH;   // Largeur d'une tuile
        float tileHeight = Constants.TILE_HEIGHT; // Hauteur d'une tuile

        // Calculer les dimensions étendues
        float extendedWidth = scaledViewportWidth + tileWidth * 2;
        float extendedHeight = scaledViewportHeight + tileHeight * 2;

        // Définir les limites visibles étendues comme un rectangle
        Rectangle viewBounds = new Rectangle(
            camera.position.x - extendedWidth / 2,
            camera.position.y - extendedHeight / 2,
            extendedWidth,
            extendedHeight
        );

        // Appeler la méthode drawGround avec les paramètres corrects
        renderer.drawGround(batch, viewBounds);
        renderSystem.update(delta); // Ensure this does not call batch.begin() again
        batch.end();

        // Render HUD and Inventory
        hud.update();
        hud.render();

        stage.act(delta);
        stage.draw();
    }

    private void smoothCameraFollow() {
        Vector2 playerWorldPosition = player.getWorldPosition();

        // Interpoler la position de la caméra pour un suivi fluide
        float lerp = 0.05f; // Valeur d'interpolation (0 = pas de mouvement, 1 = mouvement instantané)
        camera.position.set(playerWorldPosition.x, playerWorldPosition.y, 0);
        camera.update();
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
        camera.update();
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
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void show() {}
        // Optional: Debug information or initialization logic if needed
    }
}
