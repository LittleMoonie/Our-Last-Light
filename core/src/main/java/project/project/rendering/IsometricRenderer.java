package project.project.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import project.project.Constants;
import project.project.map.Biome;
import project.project.map.MapGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IsometricRenderer {
    private byte[][] biomeMap;
    private int[][] tileVariants;
    private List<MapGenerator.ResourcePlacement> resources;

    private Map<String, BiomeResources> biomeResources;

    public IsometricRenderer(MapGenerator mapGenerator) {
        this.biomeMap = mapGenerator.getBiomeMap();
        this.tileVariants = mapGenerator.getTileVariants();
        this.resources = mapGenerator.getResources();

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

    public void drawGround(SpriteBatch batch) {
        for (int row = biomeMap.length - 1; row >= 0; row--) {
            for (int col = biomeMap[0].length - 1; col >= 0; col--) {
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
        drawPreplacedResources(batch);
    }

    private void drawPreplacedResources(SpriteBatch batch) {
        for (MapGenerator.ResourcePlacement resource : resources) {
            float x = (resource.x - resource.y) * (Constants.TILE_WIDTH / 2f);
            float y = (resource.x + resource.y) * (Constants.TILE_HEIGHT / 3f);

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
