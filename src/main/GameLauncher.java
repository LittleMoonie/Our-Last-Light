package src.main;

import src.game.ui.menus.MainMenu;

import javax.swing.*;

public class GameLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
    }
}
