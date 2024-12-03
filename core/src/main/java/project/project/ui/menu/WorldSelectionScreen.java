package project.project.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

public class WorldSelectionScreen implements Screen {
    private final Stage stage;
    private final Table table;
    private BitmapFont font;
    private Label.LabelStyle labelStyle;
    private TextButton.TextButtonStyle textButtonStyle;
    private final Consumer<String> onWorldSelected;

    public WorldSelectionScreen(Consumer<String> onWorldSelected) {
        this.onWorldSelected = onWorldSelected;
        this.stage = new Stage(new ScreenViewport());
        this.table = new Table();

        // Initialize font and styles
        font = new BitmapFont(); // Use the default LibGDX font
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        // Add title label
        Label title = new Label("Select or Create a World", labelStyle);
        table.add(title).padBottom(50).row();

        // List worlds in the saves folder
        File savesFolder = new File("saves");
        if (!savesFolder.exists()) {
            savesFolder.mkdirs();
        }

        String[] worlds = savesFolder.list((dir, name) -> new File(dir, name).isDirectory());
        if (worlds != null) {
            Arrays.sort(worlds);
            for (String world : worlds) {
                TextButton worldButton = new TextButton(world, textButtonStyle);
                worldButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                        onWorldSelected.accept(world);
                    }
                });
                table.add(worldButton).width(300).height(50).padBottom(20).row();
            }
        }

        // Add "Create New World" button
        TextButton newWorldButton = new TextButton("Create New World", textButtonStyle);
        newWorldButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                String newWorldName = "New_World_" + System.currentTimeMillis();
                onWorldSelected.accept(newWorldName);
            }
        });
        table.add(newWorldButton).width(300).height(50).padBottom(20).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
