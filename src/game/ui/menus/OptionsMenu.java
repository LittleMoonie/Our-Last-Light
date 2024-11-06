//package src.game.ui.menus;
//
//import src.game.constants.Config;
//
//import javax.swing.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class OptionsMenu extends JPanel implements ActionListener {
//    private JButton backButton;
//    private MainMenu parentMenu;
//
//    public OptionsMenu(MainMenu parent) {
//        this.parentMenu = parent;
//
//        backButton = new JButton("Back");
//        backButton.setFont(Config.MAIN_FONT);
//        backButton.setBackground(Config.BUTTON_BACKGROUND_COLOR);
//        backButton.setForeground(Config.BUTTON_TEXT_COLOR);
//        backButton.addActionListener(this);
//
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        add(backButton);
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        System.out.println("Button clicked: " + e.getActionCommand());
//        if (e.getSource() == backButton) {
//            parentMenu.showMainMenu();
//        }
//    }
//}
