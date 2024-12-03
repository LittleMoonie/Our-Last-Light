package project.project.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private Stage stage;
    private Table table;
    private BitmapFont font;
    private Label.LabelStyle labelStyle;
    private TextButton.TextButtonStyle textButtonStyle;

    private final Runnable onNewSinglePlayer;
    private final Runnable onExitGame;

    public MainMenuScreen(Runnable onNewSinglePlayer, Runnable onExitGame) {
        this.onNewSinglePlayer = onNewSinglePlayer;
        this.onExitGame = onExitGame;

        // Create the stage
        stage = new Stage(new ScreenViewport());

        // Create a default font
        font = new BitmapFont(); // Use default LibGDX font

        // Define LabelStyle
        labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        // Define TextButtonStyle
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.up = new TextButton.TextButtonStyle().up; // Optional background or texture
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label title = new Label("Our Last Light", labelStyle);
        table.add(title).padBottom(50).row();

        TextButton singlePlayerButton = new TextButton("Single Player", textButtonStyle);
        singlePlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                onNewSinglePlayer.run();
            }
        });
        table.add(singlePlayerButton).width(300).height(50).padBottom(20).row();

        TextButton exitButton = new TextButton("Exit", textButtonStyle);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                onExitGame.run();
            }
        });
        table.add(exitButton).width(300).height(50).padBottom(20).row();
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
