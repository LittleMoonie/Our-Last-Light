package src.game.ui.menus;

import src.main.GamePanel;
import src.game.constants.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame implements ActionListener {
    private JButton startGameButton;

    public MainMenu() {
        setTitle("Main Menu");
        setSize(Config.FRAME_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main menu layout
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Config.MAIN_BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Our Last Light", SwingConstants.CENTER);
        titleLabel.setFont(Config.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        startGameButton = new JButton("Start Game");
        startGameButton.setFont(Config.MAIN_FONT);
        startGameButton.addActionListener(this);
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(titleLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(startGameButton);
        menuPanel.add(Box.createVerticalGlue());

        setContentPane(menuPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startGameButton) {
            // When Start Game is clicked, transition to GamePanel
            launchGamePanel();
        }
    }


    private void launchGamePanel() {
        // Replace MainMenu with GamePanel
        getContentPane().removeAll();
        GamePanel gamePanel = new GamePanel();
        setContentPane(gamePanel);
        revalidate();
        repaint();
    }
}
