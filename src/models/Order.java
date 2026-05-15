package models;
import enums.OrderStatus;
import enums.OrderType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


class Order{
    private Date orderDate;
    private String orderID;
    private List<OrderItem> orderItems;
    private double totalPrice;
    private OrderType orderType;
    private User user;
    private String customerName;
    private String customerContact;
    private String deliveryAddress;
    private OrderStatus orderStatus;

    public Order(String orderID, User user, OrderType orderType, String customerName, String customerContact, String deliveryAddress) {
        this.orderDate = new Date();
        this.orderID = orderID;
        this.user = user;
        this.orderType = orderType;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.deliveryAddress = deliveryAddress;
        this.orderItems = new ArrayList<>();
        this.totalPrice = 0.0;
        this.orderStatus = OrderStatus.PENDING;
    }

    public void setOrderType(OrderType orderType) {
        if(orderType != null){
            this.orderType = orderType;
        } else {
            throw new IllegalArgumentException("Order type cannot be null.");
        }
    }
    public void setCustomerName(String customerName) {
        if(customerName != null && !customerName.trim().isEmpty()){
            this.customerName = customerName;
        } else {
            throw new IllegalArgumentException("Customer name cannot be null or empty.");
        }
    }
    public void setCustomerContact(String customerContact) {
        if(customerContact != null && !customerContact.trim().isEmpty()){
            this.customerContact = customerContact;
        } else {
            throw new IllegalArgumentException("Customer contact cannot be null or empty.");
        }
    }
    public void setDeliveryAddress(String deliveryAddress) {
        
        if(this.orderType == OrderType.DELIVERY){
            if(deliveryAddress != null && !deliveryAddress.trim().isEmpty()){
                this.deliveryAddress = deliveryAddress;
            } else {
                throw new IllegalArgumentException("Delivery address cannot be null or empty.");
            }
        }else{
            this.deliveryAddress = "";
        }
    }

    public void addOrderItem(MenuItem menuItem) {
        for (OrderItem item : orderItems) {
            if (item.getMenuItem().getID().equals(menuItem.getID())) {
                item.incrementQuantity();
                totalPrice += menuItem.getPrice();
                return;
            }
        }
        OrderItem newItem = new OrderItem(menuItem);
        orderItems.add(newItem);
        totalPrice += menuItem.getPrice();
    }
    public void removeOrderItem(int index) {
        if (index > 0 && index < orderItems.size()) {
            index--;
            OrderItem item = orderItems.get(index);
            totalPrice -= item.getSubTotal();
            orderItems.remove(index);
        }
    }
    public void decrementOrderItem(int index) {
        if (index > 0 && index < orderItems.size()) {
            index--;
            OrderItem item = orderItems.get(index);
            item.decrementQuantity();
            totalPrice -= item.getMenuItem().getPrice();
        }
    }
    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCELLED;
    }
    public void completeOrder() {
        this.orderStatus = OrderStatus.COMPLETED;
    }
    public void outForDelivery() {
        this.orderStatus = OrderStatus.OUT_FOR_DELIVERY;
    }
    public void prepareOrder() {
        this.orderStatus = OrderStatus.PREPARING;
    }

    public OrderType getOrderType() {return orderType;}
    public String getOrderID() {return orderID;}
    public List<OrderItem> getOrderItems() {return orderItems;}
    public double getTotalPrice() {return totalPrice;}
    public String getCustomerName() {return customerName;}
    public String getCustomerContact() {return customerContact;}
    public String getDeliveryAddress() {return deliveryAddress;}
    public OrderStatus getOrderStatus() {return orderStatus;}
    public Date getOrderDate() {return orderDate;}
    public String getUserName() {return user.getName();}

}
