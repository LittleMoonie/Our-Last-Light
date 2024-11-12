package project.project.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import project.project.GameScreen;
import project.project.Isometric;

public class DesktopLauncher {

    public static void main (String[] arg) {

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        new Lwjgl3Application(new Isometric(), config);

        config.setWindowedMode(GameScreen.MAP_WIDTH, GameScreen.MAP_HEIGHT);
    }
}
