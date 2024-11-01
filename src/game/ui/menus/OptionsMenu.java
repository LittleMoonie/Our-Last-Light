package src.game.ui.menus;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionsMenu extends JPanel implements ActionListener {
    private JButton backButton;
    private MainMenu parentMenu;

    public OptionsMenu(MainMenu parent) {
        this.parentMenu = parent;

        backButton = new JButton("Back");
        backButton.addActionListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(backButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Use the parent menu method to switch back to the main menu
        parentMenu.showMainMenu();
    }
}
