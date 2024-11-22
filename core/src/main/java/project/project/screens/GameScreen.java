//// GameScreen.java
//package project.project.screens;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.scenes.scene2d.Stage;
//import com.badlogic.gdx.utils.viewport.ScreenViewport;
//import project.project.Constants;
//import project.project.components.TextureComponent;
//import project.project.map.MapLoader;
//import project.project.rendering.IsometricRenderer;
//import project.project.map.MapGenerator;
//import project.project.entities.Player;
//import project.project.systems.MovementSystem;
//import project.project.systems.ObjectPlacementSystem;
//import project.project.systems.RenderSystem;
//import project.project.ui.HUD;
//import project.project.ui.InventoryUI;
//import project.project.utils.CoordinateUtils;
//import project.project.ui.InventoryUI;
//
//import static project.project.Constants.MAP_HEIGHT;
//import static project.project.Constants.MAP_WIDTH;
//
//public class GameScreen implements Screen {
//    private SpriteBatch batch;
//    private OrthographicCamera camera; // World camera
//    private IsometricRenderer renderer;
//    private Player player;
//    private BitmapFont font;
//    private HUD hud;
//    private MapGenerator mapGenerator;
//    private MovementSystem movementSystem;
//    private RenderSystem renderSystem;
//    private InventoryUI inventoryUI;
//    private Stage stage;
//    private boolean isInventoryOpen = false; // Track inventory state
//    private ObjectPlacementSystem placementSystem;
//    private final MapLoader mapLoader;
//
//
//    public GameScreen(SpriteBatch batch) {
//        this.batch = batch;
//
//
//        // Map generator and renderer
//        MapGenerator mapGenerator = new MapGenerator(MAP_WIDTH, MAP_HEIGHT);
//        this.mapLoader = new MapLoader(mapGenerator);
//        this.renderer = new IsometricRenderer(mapGenerator, mapLoader);
//        float centerX = MAP_WIDTH / 2f;
//        float centerY = MAP_HEIGHT / 2f;
//
//        // Convert isometric center to world coordinates
//        Vector2 centerWorldPos = isoToWorld(centerX, centerY);
//        player = new Player(new Vector2(centerX, centerY));
//        player.setWorldPosition(centerWorldPos.x, centerWorldPos.y);
//
//        // Camera setup
//        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        this.camera.zoom = Constants.INITIAL_ZOOM;
//        smoothCameraFollow();
//        this.camera.update();
//
//        // HUD and systems
//        this.hud = new HUD(batch, player);
//
//        // Initialize systems
//        this.placementSystem = new ObjectPlacementSystem();
//
//        // Add the player to the render system
//        this.movementSystem = new MovementSystem();
//        this.renderSystem = new RenderSystem(batch, camera);
//        renderSystem.addEntity(player);
//
//        // Initialize stage
//        this.stage = new Stage(new ScreenViewport());
//        Gdx.input.setInputProcessor(stage);
//
//        // Initialize the inventory UI
//        this.inventoryUI = new InventoryUI(stage, player, placementSystem);
//        inventoryUI.setVisible(false); // Start with the inventory hidden
//        // Font for debug UI
//        this.font = new BitmapFont();
//
//        int totalChunks = mapLoader.getTotalChunks();
//        System.out.println("Total number of chunks: " + totalChunks);
//    }
//
//    @Override
//    public void show() {
//        stage = new Stage(new ScreenViewport());
//        Gdx.input.setInputProcessor(stage); // Redirect input to stage
//
//        // Initialize the inventory UI
//        inventoryUI = new InventoryUI(stage, player, placementSystem);
//        inventoryUI.setVisible(false); // Start with the inventory hidden
//    }
//
//    @Override
//    public void render(float delta) {
//        // Ensure systems are initialized
//        if (renderSystem == null) {
//            throw new IllegalStateException("RenderSystem is not initialized!");
//        }
//
//        Gdx.gl.glClearColor(0, 0, 0, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        // Handle input
//        handleInput(delta);
//
//        // Update player position smoothly
//        movementSystem.update(delta, player);
//
//        // Interpolate camera to follow the player smoothly
//        smoothCameraFollow();
//
//        // Update chunks based on camera position
//        Vector2 cameraCenter = new Vector2(camera.position.x, camera.position.y);
//        mapLoader.update(cameraCenter.x, cameraCenter.y);
//
//        // Render game world
//        batch.setProjectionMatrix(camera.combined);
//        batch.begin();
//
//
//        // Calculer les limites visibles en fonction du zoom de la caméra
//        float scaledViewportWidth = camera.viewportWidth * camera.zoom;
//        float scaledViewportHeight = camera.viewportHeight * camera.zoom;
//
//        // Ajouter deux tuiles de marge de chaque côté
//        float tileWidth = Constants.TILE_WIDTH;   // Largeur d'une tuile
//        float tileHeight = Constants.TILE_HEIGHT; // Hauteur d'une tuile
//
//        // Calculer les dimensions étendues
//        float extendedWidth = scaledViewportWidth + tileWidth * 2;
//        float extendedHeight = scaledViewportHeight + tileHeight * 2;
//
//        // Définir les limites visibles étendues comme un rectangle
//        Rectangle viewBounds = new Rectangle(
//            camera.position.x - extendedWidth / 2,
//            camera.position.y - extendedHeight / 2,
//            extendedWidth,
//            extendedHeight
//        );
//
//        // Appeler la méthode drawGround avec les paramètres corrects
//        renderer.drawGround(batch, viewBounds);
//        renderSystem.update(delta); // Ensure this does not call batch.begin() again
//        batch.end();
//
//        // Render HUD and UI
//        hud.update();
//        hud.render();
//
//        stage.act(delta);
//        stage.draw();
//    }
//
//    private void smoothCameraFollow() {
//        Vector2 playerWorldPosition = player.getWorldPosition();
//
//        // Interpoler la position de la caméra pour un suivi fluide
//        float lerp = 0.05f; // Valeur d'interpolation (0 = pas de mouvement, 1 = mouvement instantané)
//        camera.position.set(playerWorldPosition.x, playerWorldPosition.y, 0);
//        camera.update();
//    }
//
//    private void handleInput(float delta) {
//        // Handle player movement
//        movementSystem.update(delta, player);
//
//        // Toggle inventory with "E"
//        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
//            isInventoryOpen = !isInventoryOpen;
//            inventoryUI.setVisible(isInventoryOpen); // Show or hide the inventory
//        }
//
//        // Handle camera zoom
//        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
//            camera.zoom -= Constants.ZOOM_SPEED * delta * 10;
//            camera.zoom = Math.max(Constants.MIN_ZOOM, camera.zoom);
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
//            camera.zoom += Constants.ZOOM_SPEED * delta * 10;
//            camera.zoom = Math.min(Constants.MAX_ZOOM, camera.zoom);
//        }
//        camera.update();
//    }
//
//    private Vector2 isoToWorld(float tileX, float tileY) {
//        // Correct conversion from isometric to world coordinates
//        return CoordinateUtils.tileToWorld(tileX, tileY);
//    }
//
//    @Override
//    public void resize(int width, int height) {
//        camera.viewportWidth = width;
//        camera.viewportHeight = height;
//        camera.update();
//
//        // Update the stage viewport
//        stage.getViewport().update(width, height, true);
//
//        // Resize UI elements
//        inventoryUI.resize(width, height);
//    }
//
//    @Override
//    public void dispose() {
//        batch.dispose();
//        player.getComponent(TextureComponent.class).dispose();
//        hud.dispose();
//        renderer.dispose();
//        font.dispose();
//        stage.dispose(); // Dispose the stage
//        mapLoader.dispose();
//    }
//
//    @Override
//    public void pause() {}
//
//    @Override
//    public void resume() {}
//
//    @Override
//    public void hide() {}
//
//}

//
//// GameScreen.java
//package project.project.screens;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.scenes.scene2d.Stage;
//import com.badlogic.gdx.utils.viewport.ScreenViewport;
//import project.project.Constants;
//import project.project.components.TextureComponent;
//import project.project.map.MapLoader;
//import project.project.rendering.IsometricRenderer;
//import project.project.map.MapGenerator;
//import project.project.entities.Player;
//import project.project.entities.enemies.Mob;
//import project.project.systems.MobSpawnSystem;
//import project.project.systems.MovementSystem;
//import project.project.systems.ObjectPlacementSystem;
//import project.project.systems.RenderSystem;
//import project.project.ui.HUD;
//import project.project.ui.InventoryUI;
//import project.project.utils.CoordinateUtils;
//
//import static project.project.Constants.MAP_HEIGHT;
//import static project.project.Constants.MAP_WIDTH;
//
//public class GameScreen implements Screen {
//    private SpriteBatch batch;
//    private OrthographicCamera camera; // World camera
//    private IsometricRenderer renderer;
//    private Player player;
//    private BitmapFont font;
//    private HUD hud;
//    private MapGenerator mapGenerator;
//    private MovementSystem movementSystem;
//    private RenderSystem renderSystem;
//    private InventoryUI inventoryUI;
//    private Stage stage;
//    private boolean isInventoryOpen = false; // Track inventory state
//    private ObjectPlacementSystem placementSystem;
//    private final MapLoader mapLoader;
//    private MobSpawnSystem mobSpawnSystem; // Add MobSpawnSystem
//
//
//    public GameScreen(SpriteBatch batch) {
//        this.batch = batch;
//
//        // Map generator and renderer
//        MapGenerator mapGenerator = new MapGenerator(MAP_WIDTH, MAP_HEIGHT);
//        this.mapLoader = new MapLoader(mapGenerator);
//        this.renderer = new IsometricRenderer(mapGenerator, mapLoader);
//        float centerX = MAP_WIDTH / 2f;
//        float centerY = MAP_HEIGHT / 2f;
//
//        // Convert isometric center to world coordinates
//        Vector2 centerWorldPos = isoToWorld(centerX, centerY);
//        player = new Player(new Vector2(centerX, centerY));
//        player.setWorldPosition(centerWorldPos.x, centerWorldPos.y);
//
//        // Camera setup
//        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        this.camera.zoom = Constants.INITIAL_ZOOM;
//        smoothCameraFollow();
//        this.camera.update();
//
//        // HUD and systems
//        this.hud = new HUD(batch, player);
//
//        // Initialize systems
//        this.placementSystem = new ObjectPlacementSystem();
//        this.movementSystem = new MovementSystem();
//        this.renderSystem = new RenderSystem(batch, camera);
//        renderSystem.addEntity(player);
//
//        // Initialize MobSpawnSystem
//        this.mobSpawnSystem = new MobSpawnSystem();
//
//        // Initialize stage
//        this.stage = new Stage(new ScreenViewport());
//        Gdx.input.setInputProcessor(stage);
//
//        // Initialize the inventory UI
//        this.inventoryUI = new InventoryUI(stage, player, placementSystem);
//        inventoryUI.setVisible(false); // Start with the inventory hidden
//        // Font for debug UI
//        this.font = new BitmapFont();
//
//        int totalChunks = mapLoader.getTotalChunks();
//        System.out.println("Total number of chunks: " + totalChunks);
//    }
//
//    @Override
//    public void show() {
//        stage = new Stage(new ScreenViewport());
//        Gdx.input.setInputProcessor(stage); // Redirect input to stage
//
//        // Initialize the inventory UI
//        inventoryUI = new InventoryUI(stage, player, placementSystem);
//        inventoryUI.setVisible(false); // Start with the inventory hidden
//
//        // Spawn a mob and add it to the render system
//        mobSpawnSystem.spawnMob(player);
//        for (Mob mob : mobSpawnSystem.getMobs()) {
//            renderSystem.addEntity(mob);
//        }
//    }
//
//    @Override
//    public void render(float delta) {
//        // Ensure systems are initialized
//        if (renderSystem == null) {
//            throw new IllegalStateException("RenderSystem is not initialized!");
//        }
//
//        Gdx.gl.glClearColor(0, 0, 0, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        // Handle input
//        handleInput(delta);
//
//        // Update player position smoothly
//        movementSystem.update(delta, player);
//
//        // Interpolate camera to follow the player smoothly
//        smoothCameraFollow();
//
//        // Update chunks based on camera position
//        Vector2 cameraCenter = new Vector2(camera.position.x, camera.position.y);
//        mapLoader.update(cameraCenter.x, cameraCenter.y);
//
//        // Update mob spawn system
//        mobSpawnSystem.update(delta, player);
//
//        // Render game world
//        batch.setProjectionMatrix(camera.combined);
//        batch.begin();
//
//        // Calculer les limites visibles en fonction du zoom de la caméra
//        float scaledViewportWidth = camera.viewportWidth * camera.zoom;
//        float scaledViewportHeight = camera.viewportHeight * camera.zoom;
//
//        // Ajouter deux tuiles de marge de chaque côté
//        float tileWidth = Constants.TILE_WIDTH;   // Largeur d'une tuile
//        float tileHeight = Constants.TILE_HEIGHT; // Hauteur d'une tuile
//
//        // Calculer les dimensions étendues
//        float extendedWidth = scaledViewportWidth + tileWidth * 2;
//        float extendedHeight = scaledViewportHeight + tileHeight * 2;
//
//        // Définir les limites visibles étendues comme un rectangle
//        Rectangle viewBounds = new Rectangle(
//            camera.position.x - extendedWidth / 2,
//            camera.position.y - extendedHeight / 2,
//            extendedWidth,
//            extendedHeight
//        );
//
//        // Appeler la méthode drawGround avec les paramètres corrects
//        renderer.drawGround(batch, viewBounds);
//        renderSystem.update(delta); // Ensure this does not call batch.begin() again
//        batch.end();
//
//        // Render HUD and UI
//        hud.update();
//        hud.render();
//
//        stage.act(delta);
//        stage.draw();
//    }
//
//    private void smoothCameraFollow() {
//        Vector2 playerWorldPosition = player.getWorldPosition();
//
//        // Interpoler la position de la caméra pour un suivi fluide
//        float lerp = 0.05f; // Valeur d'interpolation (0 = pas de mouvement, 1 = mouvement instantané)
//        camera.position.set(playerWorldPosition.x, playerWorldPosition.y, 0);
//        camera.update();
//    }
//
//    private void handleInput(float delta) {
//        // Handle player movement
//        movementSystem.update(delta, player);
//
//        // Toggle inventory with "E"
//        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
//            isInventoryOpen = !isInventoryOpen;
//            inventoryUI.setVisible(isInventoryOpen); // Show or hide the inventory
//        }
//
//        // Handle camera zoom
//        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
//            camera.zoom -= Constants.ZOOM_SPEED * delta * 10;
//            camera.zoom = Math.max(Constants.MIN_ZOOM, camera.zoom);
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
//            camera.zoom += Constants.ZOOM_SPEED * delta * 10;
//            camera.zoom = Math.min(Constants.MAX_ZOOM, camera.zoom);
//        }
//        camera.update();
//    }
//
//    private Vector2 isoToWorld(float tileX, float tileY) {
//        // Correct conversion from isometric to world coordinates
//        return CoordinateUtils.tileToWorld(tileX, tileY);
//    }
//
//    @Override
//    public void resize(int width, int height) {
//        camera.viewportWidth = width;
//        camera.viewportHeight = height;
//        camera.update();
//
//        // Update the stage viewport
//        stage.getViewport().update(width, height, true);
//
//        // Resize UI elements
//        inventoryUI.resize(width, height);
//    }
//
//    @Override
//    public void dispose() {
//        batch.dispose();
//        player.getComponent(TextureComponent.class).dispose();
//        hud.dispose();
//        renderer.dispose();
//        font.dispose();
//        stage.dispose(); // Dispose the stage
//        mapLoader.dispose();
//    }
//
//    @Override
//    public void pause() {}
//
//    @Override
//    public void resume() {}
//
//    @Override
//    public void hide() {}
//
//}

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
    private MobSpawnSystem mobSpawnSystem; // Add MobSpawnSystem


//    public GameScreen(SpriteBatch batch) {
//        this.batch = batch;
//
//        // Map generator and renderer
//        MapGenerator mapGenerator = new MapGenerator(MAP_WIDTH, MAP_HEIGHT);
//        this.mapLoader = new MapLoader(mapGenerator);
//        this.renderer = new IsometricRenderer(mapGenerator, mapLoader);
//        float centerX = MAP_WIDTH / 2f;
//        float centerY = MAP_HEIGHT / 2f;
//
//        // Convert isometric center to world coordinates
//        Vector2 centerWorldPos = isoToWorld(centerX, centerY);
//        player = new Player(new Vector2(centerX, centerY));
//        player.setWorldPosition(centerWorldPos.x, centerWorldPos.y);
//
//        // Camera setup
//        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        this.camera.zoom = Constants.INITIAL_ZOOM;
//        smoothCameraFollow();
//        this.camera.update();
//
//        // HUD and systems
//        this.hud = new HUD(batch, player);
//
//        // Initialize systems
//        this.placementSystem = new ObjectPlacementSystem();
//        this.movementSystem = new MovementSystem();
//        this.renderSystem = new RenderSystem(batch, camera);
//        renderSystem.addEntity(player);
//
//        // Initialize MobSpawnSystem
//        this.mobSpawnSystem = new MobSpawnSystem();
//
//        // Initialize stage
//        this.stage = new Stage(new ScreenViewport());
//        Gdx.input.setInputProcessor(stage);
//
//        // Initialize the inventory UI
//        this.inventoryUI = new InventoryUI(stage, player, placementSystem);
//        inventoryUI.setVisible(false); // Start with the inventory hidden
//        // Font for debug UI
//        this.font = new BitmapFont();
//
//        int totalChunks = mapLoader.getTotalChunks();
//        System.out.println("Total number of chunks: " + totalChunks);
//    }

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
        mapGenerator = new MapGenerator(MAP_WIDTH, MAP_HEIGHT);
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
        mobSpawnSystem = new MobSpawnSystem();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        inventoryUI = new InventoryUI(stage, player, placementSystem);
        inventoryUI.setVisible(false);

        font = new BitmapFont();

        // Spawn mobs and add them to render system
        mobSpawnSystem.spawnMob(player.getPosition());
        List<Mob> mobs = mobSpawnSystem.getMobs();
        for (Mob mob : mobs) {
            renderSystem.addEntity(mob);
        }
    }

//    @Override
//    public void show() {
//        stage = new Stage(new ScreenViewport());
//        Gdx.input.setInputProcessor(stage); // Redirect input to stage
//
//        // Initialize the inventory UI
//        inventoryUI = new InventoryUI(stage, player, placementSystem);
//        inventoryUI.setVisible(false); // Start with the inventory hidden
//
//        // Spawn a single mob and add it to the render system
//        mobSpawnSystem.spawnMob(player);
//        Mob mob = mobSpawnSystem.getMobs().get(0); // Get the first mob
//        renderSystem.addEntity(mob);
//
//        // Print the mob's coordinates to the terminal
//        Vector2 mobPosition = mob.getWorldPosition();
//        System.out.println("Mob spawned at coordinates: (" + mobPosition.x + ", " + mobPosition.y + ")");
//    }


    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Redirect input to stage

        // Initialize the inventory UI
        inventoryUI = new InventoryUI(stage, player, placementSystem);
        inventoryUI.setVisible(false); // Start with the inventory hidden

        // Get the position of the player as a Vector2
        Vector2 playerPosition = player.getWorldPosition();  // Récupère la position du joueur

        // Spawn a single mob at the player's position
        mobSpawnSystem.spawnMob(playerPosition);  // Passe la position du joueur à spawnMob

        Mob mob = mobSpawnSystem.getMobs().get(0); // Get the first mob
        renderSystem.addEntity(mob);
        player.getId(); // Récupère l'ID du joueur
        mob.getId(); // Récupère l'ID du mob
        System.out.println("Player ID: " + player.getId()); // Affiche l'ID du joueur
        System.out.println("Mob ID: " + mob.getId()); // Affiche l'ID du mob

        // Print the mob's coordinates to the terminal
        Vector2 mobPosition = mob.getWorldPosition();
//        System.out.println("Mob spawned at coordinates: (" + mobPosition.x + ", " + mobPosition.y + ")");
    }

//    @Override
//    public void render(float delta) {
//        // Clear the screen
//        Gdx.gl.glClearColor(0, 0, 0, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        // Handle player input
//        handleInput(delta);
//
//        // Update player movement and camera
//        movementSystem.update(delta, player);
//        smoothCameraFollow();
//
//        // Update chunks based on camera position
//        Vector2 cameraCenter = new Vector2(camera.position.x, camera.position.y);
//        mapLoader.update(cameraCenter.x, cameraCenter.y);
//
//        // Render everything
//        batch.setProjectionMatrix(camera.combined);
//        batch.begin();
//
//        // Calculate extended view bounds
//        float extendedWidth = camera.viewportWidth * camera.zoom + Constants.TILE_WIDTH * 2;
//        float extendedHeight = camera.viewportHeight * camera.zoom + Constants.TILE_HEIGHT * 2;
//        Rectangle viewBounds = new Rectangle(
//            camera.position.x - extendedWidth / 2,
//            camera.position.y - extendedHeight / 2,
//            extendedWidth,
//            extendedHeight
//        );
//
//        // Render ground and entities
//        renderer.drawGround(batch, viewBounds);
//
//        List<Entity> entities = new ArrayList<>();
//        entities.add(player);
//        entities.addAll(mobSpawnSystem.getMobs());
//        renderSystem.update(delta, entities);
//
//        batch.end();
//
//        // Render HUD and stage
//        hud.update();
//        hud.render();
//        stage.act(delta);
//        stage.draw();
//    }
    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle player input
        handleInput(delta);

        // Update player movement and camera
        movementSystem.update(delta, player);
        smoothCameraFollow();

        // Update chunks based on camera position
        Vector2 cameraCenter = new Vector2(camera.position.x, camera.position.y);
        mapLoader.update(cameraCenter.x, cameraCenter.y);

        // Set the projection matrix for the batch
        batch.setProjectionMatrix(camera.combined);

        // Start the batch for drawing
        batch.begin();

        // Calculate extended view bounds
        float extendedWidth = camera.viewportWidth * camera.zoom + Constants.TILE_WIDTH * 2;
        float extendedHeight = camera.viewportHeight * camera.zoom + Constants.TILE_HEIGHT * 2;
        Rectangle viewBounds = new Rectangle(
            camera.position.x - extendedWidth / 2,
            camera.position.y - extendedHeight / 2,
            extendedWidth,
            extendedHeight
        );

        // Render ground and entities
        renderer.drawGround(batch, viewBounds);

        // Prepare entities to render
        List<Entity> entities = new ArrayList<>();
        entities.add(player);

        // Génère un mob autour du joueur (une seule fois pour cet exemple)
        Mob mob = new Mob(player.getPosition()); // Crée le mob autour du joueur
        entities.add(mob);

        // Ajoute tous les mobs générés par le MobSpawnSystem
        entities.addAll(mobSpawnSystem.getMobs());

        // Update render system to draw entities
        renderSystem.update(delta, entities); // Cette méthode dessine toutes les entités avec le batch

        // End the batch after rendering
        batch.end();

        printPlayerPosition();

        // Render HUD and stage (outside of the batch)
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

    private void printPlayerPosition() {
        Vector2 playerWorldPosition = player.getWorldPosition();
        System.out.println("Player position: x = " + playerWorldPosition.x + ", y = " + playerWorldPosition.y);
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

}
