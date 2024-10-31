package src.game.ui.menus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionsMenu extends JFrame implements ActionListener {
    private JFrame parentFrame;
    private JButton backButton;
    private JSlider volumeSlider; // Example for options
    private JCheckBox fullscreenCheck; // Example for options

    public OptionsMenu(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setTitle("Options");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Components
        backButton = createMenuButton("Back");
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        fullscreenCheck = new JCheckBox("Fullscreen");

        // Panel setup
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Options"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(fullscreenCheck);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Volume"));
        panel.add(volumeSlider);
        panel.add(Box.createVerticalStrut(10));
        panel.add(backButton);

        add(panel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            parentFrame.setVisible(true);
            this.dispose();
        }
    }
}
