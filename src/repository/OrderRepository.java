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
import models.User;
import models.OrderItem;

public class OrderRepository implements interfaces.CSVRepository<Order> {

    private static final String FILE_PATH = "data/orders.csv";
    private static final String HEADER = "orderID,user, orderType, customerName, customerContact, deliveryAddress";

    // CSV line → Order object
    private Order deserialize(String line) {
        String[] p = line.split(",");
        return new Order(p[0], p[1], deserializeOrderItems(p[2]), Double.parseDouble(p[3]), OrderStatus.valueOf(p[4]),
                OrderType.valueOf(p[5]));
    }

    // Order object → CSV line
    private String serialize(Order order) {
        return String.join(",",
                order.getOrderID(), order.getCustomerID(), serializeOrderItems(order.getOrderItems()),
                String.valueOf(order.getTotalAmount()), order.getOrderStatus().name(), order.getOrderType().name());
    }

    @Override
    public boolean save(Order order) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            if (new java.io.File(FILE_PATH).length() == 0) {
                writer.append(HEADER + "\n");
            }
            writer.append(serialize(order) + "\n");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Order> findAll() {
        File file = new File(FILE_PATH);

        try (Scanner scanner = new Scanner(file);) {
            List<Order> orders = new ArrayList<>();
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                orders.add(deserialize(line));
            }
            scanner.close();
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Order findById(String orderID) {
        List<Order> orders = findAll();
        for (Order o : orders) {
            if (o.getOrderID().equals(orderID)) {
                return o;
            }
        }
        System.out.println("Order not found.");
        return null;
    }

    @Override
    public boolean update(Order order) {
        List<Order> orders = findAll();
        for (Order o : orders) {
            if (o.getOrderID().equals(order.getOrderID())) {
                orders.remove(o);
                orders.add(order);
                rewriteAll(orders);
                return true;
            }
        }
        System.out.println("Order not found.");
        return false;
    }

    @Override
    public boolean delete(String orderID) {
        List<Order> orders = findAll();
        for (Order o : orders) {
            if (o.getOrderID().equals(orderID)) {
                orders.remove(o);
                rewriteAll(orders);
                return true;
            }
        }
        System.out.println("Order not found.");
        rewriteAll(orders);
        return false;
    }

    private void rewriteAll(List<Order> orders) {
        try (FileWriter writer = new FileWriter(FILE_PATH, false)) {
            writer.append(HEADER + "\n");
            for (Order o : orders) {
                writer.append(serialize(o) + "\n");
            }
        } catch (Exception e) {
            System.out.println("Error occurred while rewriting orders.");
            e.printStackTrace();
        }
    }
}
