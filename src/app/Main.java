package app;

import enums.Category;
import gui.LoginScreen;
import javax.swing.*;
import models.MenuItem;
import repository.MenuRepository;

public class Main {
    public static void main(String[] args) {
        MenuItem item1 = new MenuItem("Pizza", "M003", 700.0, "Cheesy Chicken Pizza",
                Category.MAIN_COURSE, "data/MenuImgs/M003.jpg", 15, 1);
        MenuRepository menuRepo = new MenuRepository();
        if (menuRepo.findById(item1.getID()) == null) {
            menuRepo.save(item1);
        }
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
