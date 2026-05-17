package app;
import enums.OrderType;
import java.util.List;
import models.MenuItem;
import models.Order;
import models.OrderItem;
import models.User;
import repository.MenuRepository;
import repository.OrderRepository;
import repository.UserRepository;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to CafeSync!");
        OrderRepository orderRepo = new OrderRepository();
        MenuRepository menuRepo = new MenuRepository();
        UserRepository userRepo = new UserRepository();
        User user1 = userRepo.findById("Taiba1");
        MenuItem menuItem1 = menuRepo.findById("M001");
        OrderItem item1 = new OrderItem(menuItem1, 2);
        List<OrderItem> orderItems = List.of(item1);

        Order order1 = new Order(user1.getUsername(), OrderType.TAKEAWAY, "Moiz", "1234567890", "", orderItems);
        if(orderRepo.save(order1))
        {
            System.out.println("Order saved successfully!");
        }
    }
    
}
