package repository;
import enums.Category;
import enums.OrderStatus;
import enums.OrderType;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.MenuItem;

public class MenuRepository implements interfaces.CSVRepository<MenuItem> {

    private static final String FILE_PATH = "data/menu_items.csv";
    private static final String HEADER = "itemName,menuItemID,price,description,category,imageUrl,preparationTime,servingSize";

    // CSV line → MenuItem object
    private MenuItem deserialize(String line) {
        String[] p = line.split(",");
        return new MenuItem(p[0], p[1], Double.parseDouble(p[2]), p[3], Category.valueOf(p[4]), p[5], Integer.parseInt(p[6]), Integer.parseInt(p[7]));
    }

    // MenuItem object → CSV line
    private String serialize(MenuItem m) {
        return String.join(",",
            m.getItemName(), m.getID(), String.valueOf(m.getPrice()), m.getDescription(), m.getCategory().name(), m.getImageUrl(), String.valueOf(m.getPreparationTime()), String.valueOf(m.getServingSize()));
    }

    @Override
    public boolean save(MenuItem menuItem) {
        try(FileWriter writer = new FileWriter(FILE_PATH, true)) {
            if(new java.io.File(FILE_PATH).length() == 0) {
                writer.append(HEADER + "\n");
            }
            writer.append(serialize(menuItem) + "\n");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<MenuItem> findAll() {
        File file = new File(FILE_PATH);
    
        try(Scanner scanner = new Scanner(file);) {
            List<MenuItem> menuItems = new ArrayList<>();
            if(scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                menuItems.add(deserialize(line));
            }
            scanner.close();
            return menuItems;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    @Override
    public MenuItem findById(String menuItemID) {
        List<MenuItem> menuItems = findAll();
        for(MenuItem m : menuItems) {
            if(m.getID().equals(menuItemID)) {
                return m;
            }
        }
        System.out.println("Menu item not found.");
        return null;
    }

    @Override
    public boolean update(MenuItem menuItem) {
        List<MenuItem> menuItems = findAll();
        for(MenuItem m : menuItems) {
            if(m.getID().equals(menuItem.getID())) {
                menuItems.remove(m);
                menuItems.add(menuItem);
                rewriteAll(menuItems);
                return true;
            }
        }
        System.out.println("Menu item not found.");
        return false;
    }

    @Override
    public boolean delete(String menuItemID) {
        List<MenuItem> menuItems = findAll();
        for(MenuItem m : menuItems) {
            if(m.getID().equals(menuItemID)) {
                menuItems.remove(m);
                rewriteAll(menuItems);
                return true;
            }
        }
        System.out.println("Menu item not found.");
        rewriteAll(menuItems);
        return false;
    }

    private void rewriteAll(List<MenuItem> menuItems) {
        try(FileWriter writer = new FileWriter(FILE_PATH, false)) {
            writer.append(HEADER + "\n");
            for(MenuItem m : menuItems) {
                writer.append(serialize(m) + "\n");
            }
        } catch (Exception e) {
            System.out.println("Error occurred while rewriting menu items.");
            e.printStackTrace();
        }
    }
}

