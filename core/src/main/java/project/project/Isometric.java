package project.project;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import project.project.screens.GameScreen;
import project.project.ui.menu.MainMenuScreen;
import project.project.ui.menu.WorldSelectionScreen;

import java.io.*;
import java.util.Scanner;

public class Isometric extends Game {
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Start with the MainMenuScreen
        setScreen(new MainMenuScreen(
            this::showWorldSelectionScreen,  // Action for "Single Player"
            this::exitGame                  // Action for "Exit"
        ));
    }

    private void showWorldSelectionScreen() {
        setScreen(new WorldSelectionScreen(this::startGame));
    }

    private void startGame(String selectedWorld) {
        String username = loadGameWorld(selectedWorld);
        setScreen(new GameScreen(batch, selectedWorld, username));
    }

    private String loadGameWorld(String selectedWorld) {
        File savesFolder = new File("saves");
        File worldFolder = new File(savesFolder, selectedWorld);

        if (!worldFolder.exists()) {
            System.out.println("World does not exist. Creating...");
            if (!worldFolder.mkdirs()) {
                throw new RuntimeException("Failed to create world directory: " + worldFolder.getPath());
            }
        }

        // Manage username
        File usernameFile = new File(worldFolder, "username.txt");
        String username;

        if (usernameFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(usernameFile))) {
                username = reader.readLine();
                System.out.println("Loaded username: " + username);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load username file: " + e.getMessage(), e);
            }
        } else {
            username = askForUsername();
            if (username == null || username.isEmpty()) {
                throw new RuntimeException("No username provided. Cannot create world.");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(usernameFile))) {
                writer.write(username);
                System.out.println("Saved username: " + username);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save username file: " + e.getMessage(), e);
            }
        }

        // Manage world data
        File worldDataFile = new File(worldFolder, "world.dat");
        if (worldDataFile.exists()) {
            System.out.println("Loading existing world data...");
            loadWorldData(worldDataFile);
        } else {
            System.out.println("Generating new world...");
            generateNewWorld(worldDataFile);
        }

        return username;
    }

    private void loadWorldData(File worldDataFile) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(worldDataFile))) {
            Object worldData = inputStream.readObject(); // Replace with your actual deserialization logic
            System.out.println("World data loaded successfully: " + worldData);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load world data: " + e.getMessage(), e);
        }
    }

    private void generateNewWorld(File worldDataFile) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(worldDataFile))) {
            String initialWorldData = "This is your new world!"; // Replace with your world generation logic
            outputStream.writeObject(initialWorldData);
            System.out.println("New world generated and saved.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save new world data: " + e.getMessage(), e);
        }
    }

    private String askForUsername() {
        System.out.print("Enter a username: ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().trim();
    }

    private void exitGame() {
        Gdx.app.exit();
    }

    @Override
    public void render() {
        super.render(); // Calls render() on the active screen
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
