package app;
import enums.Category;
import models.MenuItem;
import repository.MenuRepository;


public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to CafeSync!");
        MenuRepository menuRepo = new MenuRepository();
        MenuItem newItem = new MenuItem("Cappuccino", "M001", 3.50, "Espresso with steamed milk and foam", Category.BEVERAGE, "https://example.com/cappuccino.jpg", 5, 1);
        menuRepo.save(newItem);
        System.out.println(menuRepo.findById("M001").getItemName());

    }
    
}
