package project.project.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import project.project.Constants;
import project.project.map.Biome;
import project.project.map.MapGenerator;
import project.project.map.MapLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IsometricRenderer {
    private final byte[][] biomeMap;
    private final int[][] tileVariants;
    private final List<MapGenerator.ResourcePlacement> resources;
    private final MapLoader mapLoader;
    private Map<String, BiomeResources> biomeResources;

    // Taille d’un chunk
    private static final int CHUNK_SIZE = 16; // Exemple : 16x16 tuiles

    public IsometricRenderer(MapGenerator mapGenerator, MapLoader mapLoader) {
        this.biomeMap = mapGenerator.getBiomeMap();
        this.tileVariants = mapGenerator.getTileVariants();
        this.resources = mapGenerator.getResources();
        this.mapLoader = mapLoader;

        loadBiomeConfig();
    }

    private void loadBiomeConfig() {
        biomeResources = new HashMap<>();
        Json json = new Json();
        JsonValue root = json.fromJson(null, Gdx.files.internal("biome_config.json"));

        for (JsonValue biomeEntry : root) {
            String biomeName = biomeEntry.name();
            BiomeResources resources = new BiomeResources();

            Texture tileTexture = new Texture(Gdx.files.internal("256x192 Tiles.png"));
            for (JsonValue tileData : biomeEntry.get("tiles")) {
                int x = tileData.getInt("x");
                int y = tileData.getInt("y");
                int width = tileData.getInt("width");
                int height = tileData.getInt("height");
                resources.tiles.add(new TextureRegion(tileTexture, x, y, width, height));
            }

            // Load trees and objects
            if (biomeEntry.has("trees")) {
                Texture treeTexture = new Texture(Gdx.files.internal("256x512 Trees.png"));
                for (JsonValue treeData : biomeEntry.get("trees")) {
                    int x = treeData.getInt("x");
                    int y = treeData.getInt("y");
                    int width = treeData.getInt("width");
                    int height = treeData.getInt("height");
                    resources.trees.add(new TextureRegion(treeTexture, x, y, width, height));
                }
            }

            if (biomeEntry.has("objects")) {
                Texture objectTexture = new Texture(Gdx.files.internal("256x256 Objects.png"));
                for (JsonValue objectData : biomeEntry.get("objects")) {
                    int x = objectData.getInt("x");
                    int y = objectData.getInt("y");
                    int width = objectData.getInt("width");
                    int height = objectData.getInt("height");
                    resources.objects.add(new TextureRegion(objectTexture, x, y, width, height));
                }
            }

            biomeResources.put(biomeName, resources);
        }
    }

public void drawGround(SpriteBatch batch, Rectangle viewBounds) {
    for (int row = biomeMap.length - 1; row >= 0; row--) {
        for (int col = biomeMap[0].length - 1; col >= 0; col--) {
            float x = (col - row) * (Constants.TILE_WIDTH / 2f);
            float y = (col + row) * (Constants.TILE_HEIGHT / 3f);

            // Vérifier si la tile est dans les limites de la vue
            if (viewBounds.contains(x, y)) {
                String biomeName = Biome.values()[biomeMap[row][col]].name();
                BiomeResources resources = biomeResources.get(biomeName);

                if (resources != null && !resources.tiles.isEmpty()) {
                    TextureRegion tileTexture = resources.tiles.get(tileVariants[row][col] % resources.tiles.size());
                    batch.draw(tileTexture, x, y, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
                }
            }
        }
    }
    drawPreplacedResources(batch, viewBounds);
}


    private void drawChunk(SpriteBatch batch, int chunkRow, int chunkCol) {
        int startRow = chunkRow * CHUNK_SIZE;
        int endRow = startRow + CHUNK_SIZE;

        int startCol = chunkCol * CHUNK_SIZE;
        int endCol = startCol + CHUNK_SIZE;

        // Limiter les indices pour éviter les dépassements
        endRow = Math.min(endRow, biomeMap.length);
        endCol = Math.min(endCol, biomeMap[0].length);

        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                float x = (col - row) * (Constants.TILE_WIDTH / 2f);
                float y = (col + row) * (Constants.TILE_HEIGHT / 3f);

                String biomeName = Biome.values()[biomeMap[row][col]].name();
                BiomeResources resources = biomeResources.get(biomeName);

                if (resources != null && !resources.tiles.isEmpty()) {
                    TextureRegion tileTexture = resources.tiles.get(tileVariants[row][col] % resources.tiles.size());
                    batch.draw(tileTexture, x, y, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
                }
            }
        }
    }

    private void drawPreplacedResources(SpriteBatch batch, Rectangle viewBounds) {
        for (MapGenerator.ResourcePlacement resource : resources) {
            float x = (resource.x - resource.y) * (Constants.TILE_WIDTH / 2f);
            float y = (resource.x + resource.y) * (Constants.TILE_HEIGHT / 3f);

            if (viewBounds.contains(x, y)) {
                String biomeName = Biome.values()[biomeMap[resource.x][resource.y]].name();
                BiomeResources biomeResources = this.biomeResources.get(biomeName);

                TextureRegion resourceTexture = null;
                if (resource.type == MapGenerator.ResourceType.TREE && !biomeResources.trees.isEmpty()) {
                    resourceTexture = biomeResources.trees.get(0);
                } else if (resource.type == MapGenerator.ResourceType.ROCK && !biomeResources.objects.isEmpty()) {
                    resourceTexture = biomeResources.objects.get(0);
                } else if (resource.type == MapGenerator.ResourceType.BUSH && !biomeResources.objects.isEmpty()) {
                    resourceTexture = biomeResources.objects.get(0);
                }

                if (resourceTexture != null) {
                    float heightMultiplier = (resource.type == MapGenerator.ResourceType.TREE) ? 2f : 1f;
                    batch.draw(resourceTexture, x, y, Constants.TILE_WIDTH, Constants.TILE_HEIGHT * heightMultiplier);
                }
            }
        }
    }


    public void dispose() {
        for (BiomeResources resources : biomeResources.values()) {
            for (TextureRegion region : resources.tiles) {
                region.getTexture().dispose();
            }
            for (TextureRegion region : resources.trees) {
                region.getTexture().dispose();
            }
            for (TextureRegion region : resources.objects) {
                region.getTexture().dispose();
            }
        }
    }

    private static class BiomeResources {
        List<TextureRegion> tiles = new ArrayList<>();
        List<TextureRegion> trees = new ArrayList<>();
        List<TextureRegion> objects = new ArrayList<>();
    }
}
