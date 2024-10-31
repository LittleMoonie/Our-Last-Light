package src.game.ui;

import javax.swing.*;
import java.awt.*;

public class LoadingScreen {
    private JFrame frame;
    private JProgressBar progressBar;

    public LoadingScreen() {
        frame = new JFrame("Loading...");
        frame.setUndecorated(true);
        frame.setSize(400, 100);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Loading game, please wait...");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        panel.add(label, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        frame.add(panel);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void close() {
        frame.dispose();
    }
}
