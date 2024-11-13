package project.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IsometricRenderer {
    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 192;

    private Map<MapGenerator.Biome, ArrayList<TextureRegion>> biomeTileMap;
    private Texture tileSheet;
    private byte[][] biomeMap;  // Biome map from MapGenerator
    private int[][] tileVariants;  // Precomputed tile variants for consistency

    public IsometricRenderer(MapGenerator mapGenerator) {
        this.biomeMap = mapGenerator.getBiomeMap();
        this.tileVariants = mapGenerator.getTileVariants();

        loadTileConfig();
    }

    private void loadTileConfig() {
        Json json = new Json();
        JsonValue root = json.fromJson(null, Gdx.files.internal("tile_config.json"));

        tileSheet = new Texture(Gdx.files.internal("256x192 Tiles.png"));
        biomeTileMap = new HashMap<>();

        for (JsonValue biomeEntry : root) {
            String biomeName = biomeEntry.name();
            ArrayList<TextureRegion> tileVariantsList = new ArrayList<>();

            for (JsonValue tileData : biomeEntry) {
                int x = tileData.getInt("x");
                int y = tileData.getInt("y");
                int width = tileData.getInt("width");
                int height = tileData.getInt("height");

                TextureRegion region = new TextureRegion(tileSheet, x, y, width, height);
                tileVariantsList.add(region);
            }

            try {
                MapGenerator.Biome biomeType = MapGenerator.Biome.valueOf(biomeName);
                biomeTileMap.put(biomeType, tileVariantsList);
            } catch (IllegalArgumentException e) {
                System.err.println("Biome type not recognized: " + biomeName);
            }
        }
    }

    public void drawGround(SpriteBatch batch) {
        for (int row = biomeMap.length - 1; row >= 0; row--) {
            for (int col = biomeMap[0].length - 1; col >= 0; col--) {
                float x = (col - row) * (TILE_WIDTH / 2f);
                float y = (col + row) * (TILE_HEIGHT / 3f);

                MapGenerator.Biome biome = MapGenerator.Biome.values()[biomeMap[row][col]];
                int variantIndex = tileVariants[row][col];

                ArrayList<TextureRegion> tileVariantsList = biomeTileMap.get(biome);
                if (variantIndex >= tileVariantsList.size()) {
                    variantIndex = 0;  // Fallback to the first variant if out of bounds
                }

                TextureRegion tileTexture = tileVariantsList.get(variantIndex);
                batch.draw(tileTexture, x, y, TILE_WIDTH, TILE_HEIGHT);
            }
        }
    }

    public void dispose() {
        tileSheet.dispose();
    }
    public byte[][] getBiomeMap() {
        return biomeMap;
    }
}
