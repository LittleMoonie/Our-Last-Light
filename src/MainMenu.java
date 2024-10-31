package src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame implements ActionListener {
    private JButton singlePlayerButton;
    private JButton multiplayerButton;
    private JButton optionsButton;
    private JButton exitButton;

    public MainMenu() {
        setTitle("Main Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        singlePlayerButton = new JButton("Single Player");
        multiplayerButton = new JButton("Multiplayer");
        optionsButton = new JButton("Options");
        exitButton = new JButton("Exit");

        singlePlayerButton.addActionListener(this);
        multiplayerButton.addActionListener(this);
        optionsButton.addActionListener(this);
        exitButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(singlePlayerButton);
        panel.add(multiplayerButton);
        panel.add(optionsButton);
        panel.add(exitButton);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == singlePlayerButton) {
            SinglePlayerMenu spMenu = new SinglePlayerMenu(this);
            spMenu.setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == multiplayerButton) {
            MultiplayerMenu mpMenu = new MultiplayerMenu(this);
            mpMenu.setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == optionsButton) {
            OptionsMenu optionsMenu = new OptionsMenu(this);
            optionsMenu.setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }
}
