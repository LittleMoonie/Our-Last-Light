package project.project.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import project.project.components.HealthComponent;
import project.project.components.TextureComponent;
import project.project.entities.Player;

public class HUD {
    private Stage stage;
    private Player player;
    private TextureComponent heartTexture;
    private TextureComponent halfHeartTexture;
    private Table table;

    public HUD(SpriteBatch spriteBatch, Player player) {
        this.player = player;
        stage = new Stage(new ScreenViewport(), spriteBatch);

        heartTexture = new TextureComponent("heart.png", 40, 40); // Full heart texture
        halfHeartTexture = new TextureComponent("heart_midlife.png", 40, 40); // Half heart texture

        table = new Table();
        table.top().left();
        table.setFillParent(true);

        updateHearts();

        stage.addActor(table);
    }

    private void updateHearts() {
        table.clear();
        int health = player.getComponent(HealthComponent.class).getCurrentHealth();
        int numFullHearts = health / 20;
        int remainingHealth = health % 20;

        for (int i = 0; i < numFullHearts; i++) {
            Image heartImage = new Image(new TextureRegionDrawable(heartTexture.texture));
            heartImage.setSize(heartTexture.width, heartTexture.height); // Resize the image
            table.add(heartImage).size(heartTexture.width, heartTexture.height).padTop(10).padRight(5);
        }

        if (remainingHealth > 0) {
            Image halfHeartImage = new Image(new TextureRegionDrawable(halfHeartTexture.texture));
            halfHeartImage.setSize(halfHeartTexture.width, halfHeartTexture.height); // Resize the image
            table.add(halfHeartImage).size(halfHeartTexture.width, halfHeartTexture.height).padTop(10).padRight(5);
        }
    }

    public void update() {
        updateHearts();
    }

    public void render() {
        stage.act();
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        heartTexture.dispose();
        halfHeartTexture.dispose();
    }
}
