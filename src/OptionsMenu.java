package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OptionsMenu extends JFrame {
    private JFrame parentFrame;
    private JButton backButton;


    public OptionsMenu(JFrame parentFrame) {
        this.parentFrame = parentFrame;

        setTitle("Options");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        // Create components
        backButton = new JButton("Back");
        backButton.addActionListener((ActionListener) this);

        // Layout
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Add options components here (e.g., sliders, checkboxes)

        panel.add(backButton, BorderLayout.SOUTH);
        add(panel);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            parentFrame.setVisible(true);
            this.dispose();
        }
    }
}
