package models;

import enums.OrderStatus;
import enums.OrderType;
import java.util.Date;
import java.util.List;


public class Order{
    private Date orderDate;
    private String orderID;
    private List<OrderItem> orderItems;
    private double totalPrice;
    private OrderType orderType;
    private String userName;
    private String customerName;
    private String customerContact;
    private String deliveryAddress;
    private OrderStatus orderStatus;

    public Order(String userName, OrderType orderType, String customerName, String customerContact, String deliveryAddress, List<OrderItem> orderItems) {
        setOrderDate();
        setOrderID();
        setUserName(userName);
        setOrderType(orderType);
        setCustomerName(customerName);
        setCustomerContact(customerContact);
        setDeliveryAddress(deliveryAddress);
        setOrderItems(orderItems);
        setTotalPrice();
        setOrderStatus();
    }

    public Order(String orderID, String orderDate, String userName, OrderType orderType, String customerName, String customerContact, String deliveryAddress, List<OrderItem> orderItems, double totalPrice, OrderStatus orderStatus) {
        setOrderDate(orderDate);
        setOrderID(orderID);
        setUserName(userName);
        setOrderType(orderType);
        setCustomerName(customerName);
        setCustomerContact(customerContact);
        setDeliveryAddress(deliveryAddress);
        setOrderItems(orderItems);
        setTotalPrice(totalPrice);
        setOrderStatus(orderStatus);
    }





    public void setUserName(String userName) {
        if(userName != null && !userName.trim().isEmpty()){
            this.userName = userName;
        } else {
            throw new IllegalArgumentException("User name cannot be null or empty.");
        }
    }

    private void setOrderStatus() {
        this.orderStatus = OrderStatus.PENDING;
    }
    private void setOrderStatus(OrderStatus orderStatus) {
        if(orderStatus != null){
            this.orderStatus = orderStatus;
        } else {
            throw new IllegalArgumentException("Order status cannot be null.");
        }
    }


    private  void setTotalPrice() {
        this.totalPrice = 0.0;
    }
    private void setTotalPrice(double totalPrice) {
        if(totalPrice >= 0){
            this.totalPrice = totalPrice;
        } else {
            throw new IllegalArgumentException("Total price cannot be negative.");
        }
    }


    private void setOrderDate() {
        this.orderDate = new Date();
    }
    private void setOrderDate(String orderDate) {
        if(orderDate != null){
            this.orderDate = new Date(orderDate);
        } else {
            throw new IllegalArgumentException("Order date cannot be null.");
        }
    }


    private void setOrderID(String orderID) {
        if(orderID != null && !orderID.trim().isEmpty()){
            this.orderID = orderID;
        } else {
            throw new IllegalArgumentException("Order ID missing or invalid.");
        }
    }
    private void setOrderID() {
        this.orderID = "ORD" + System.currentTimeMillis();
        }

    private void setOrderItems(List<OrderItem> orderItems) {
        if(orderItems != null){
            this.orderItems = orderItems;
        } else {
            throw new IllegalArgumentException("Order items cannot be null.");
        }
    }


    
    private void setOrderType(OrderType orderType) {
        if(orderType != null){
            this.orderType = orderType;
        } else {
            throw new IllegalArgumentException("Order type cannot be null.");
        }
    }
    private void setCustomerName(String customerName) {
        if(customerName != null && !customerName.trim().isEmpty()){
            this.customerName = customerName;
        } else {
            throw new IllegalArgumentException("Customer name cannot be null or empty.");
        }
    }
    private void setCustomerContact(String customerContact) {
        if(customerContact != null && !customerContact.trim().isEmpty()){
            this.customerContact = customerContact;
        } else {
            throw new IllegalArgumentException("Customer contact cannot be null or empty.");
        }
    }
    private void setDeliveryAddress(String deliveryAddress) {
        
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
    public String getUserName() {return userName;}

}
