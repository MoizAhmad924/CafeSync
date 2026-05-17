package app;
import enums.Category;
import gui.LoginScreen;
import javax.swing.*;
import models.MenuItem;
import repository.MenuRepository;


public class Main {
    public static void main(String[] args) {
        MenuItem item1 = new MenuItem("Shawarma","M002",280.0, "Chicken Shawarma wrapped in our inhouse pita",Category.MAIN_COURSE, "data/MenuImgs/M002.jpg",8,1);
        MenuRepository menuRepo = new MenuRepository();
        menuRepo.save(item1);
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}    
