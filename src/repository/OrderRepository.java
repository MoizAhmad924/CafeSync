package repository;

import enums.OrderStatus;
import enums.OrderType;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.MenuItem;
import models.Order;
import models.OrderItem;

public class OrderRepository implements interfaces.CSVRepository<Order> {

    private static final String FILE_PATH = "data/orders.csv";
    private static final String HEADER = "orderID, Date, userName, orderType, customerName, customerContact, deliveryAddress, orderItems, totalPrice, orderStatus";

    // CSV line → Order object
    private Order deserialize(String line) {
        String[] p = line.split(",");
        return new Order(p[0], p[1], p[2], OrderType.valueOf(p[3]), p[4], p[5], p[6], deserializeOrderItems(p[7]), Double.parseDouble(p[8]), OrderStatus.valueOf(p[9]));
    }
    // OrderItems string → List<OrderItem>
    private List<OrderItem> deserializeOrderItems(String orderItemsStr) {
        List<OrderItem> orderItems = new ArrayList<>();
        String[] items = orderItemsStr.split(";");
        for(String item : items) {
            String[] parts = item.split(":");
            String menuItemID = parts[0];
            int quantity = Integer.parseInt(parts[1]);
            MenuItem menuItem = new MenuRepository().findById(menuItemID);
            if(menuItem != null) {
                OrderItem orderItem = new OrderItem(menuItem, quantity);
                orderItems.add(orderItem);
            }
        }
        return orderItems;
    }





    // Order object → CSV line
    private String serialize(Order order) {
        return String.join(",",
            order.getOrderID(),order.getOrderDate().toString(), order.getUserName(), order.getOrderType().name(), order.getCustomerName(), order.getCustomerContact(), order.getDeliveryAddress(), serializeOrderItems(order.getOrderItems()), String.valueOf(order.getTotalPrice()), order.getOrderStatus().name());
    }
    // List<OrderItem> → OrderItems string
    private String serializeOrderItems(List<OrderItem> orderItems) {
        StringBuilder sb = new StringBuilder();
        for(OrderItem oi : orderItems) {
            MenuItem mi = oi.getMenuItem();
            sb.append(mi.getID()).append(":").append(oi.getQuantity()).append(";");
        }
        return sb.toString();
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

    public List<Order> findOrdersInRange(LocalDate start, LocalDate end) {
        List<Order> allOrders = findAll();
        List<Order> ordersInRange = new ArrayList<>();
        for (Order o : allOrders) {
            if (!o.getOrderDate().isBefore(start) && !o.getOrderDate().isAfter(end)) {
                ordersInRange.add(o);
            }
        }
        return ordersInRange;
        
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
