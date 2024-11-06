//package src.game.ui.menus;
//
//import src.main.GamePanel;
//import src.game.constants.Config;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class SinglePlayerMenu extends JPanel implements ActionListener {
//    private JButton createNewWorldButton;
//    private JButton backButton;
//    private MainMenu parentMenu;
//
//    public SinglePlayerMenu(MainMenu parent) {
//        this.parentMenu = parent;
//
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        setBackground(Config.MAIN_BACKGROUND_COLOR);
//
//        JLabel titleLabel = new JLabel("World Options", SwingConstants.CENTER);
//        titleLabel.setFont(Config.LABEL_FONT);
//        titleLabel.setForeground(Color.WHITE);
//        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        createNewWorldButton = createMenuButton("Create New World");
//        backButton = createMenuButton("Back");
//
//        add(Box.createVerticalGlue());
//        add(titleLabel);
//        add(Box.createRigidArea(new Dimension(0, 15)));
//        add(createNewWorldButton);
//        add(Box.createRigidArea(new Dimension(0, 10)));
//        add(backButton);
//        add(Box.createVerticalGlue());
//    }
//
//    private JButton createMenuButton(String text) {
//        JButton button = new JButton(text);
//        button.setPreferredSize(new Dimension(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT));
//        button.setMaximumSize(new Dimension(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT));
//        button.setAlignmentX(Component.CENTER_ALIGNMENT);
//        button.setFont(Config.MAIN_FONT);
//        button.setBackground(Config.BUTTON_BACKGROUND_COLOR);
//        button.setForeground(Config.BUTTON_TEXT_COLOR);
//        button.setFocusPainted(false);
//        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
//        button.addActionListener(this);
//        return button;
//    }
//
//    private void createAndDisplayNewWorld() {
//        // Generate a new world with a random seed
//        GamePanel gamePanel = new GamePanel();
//
//        // Set GamePanel as the content pane and start displaying the world
//        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
//        mainFrame.setContentPane(gamePanel);
//        mainFrame.revalidate();
//        mainFrame.repaint();
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() == createNewWorldButton) {
//            createAndDisplayNewWorld();
//        } else if (e.getSource() == backButton) {
//            parentMenu.showMainMenu();
//        }
//    }
//}
