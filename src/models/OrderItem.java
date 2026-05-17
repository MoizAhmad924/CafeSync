package models;

public class OrderItem{
    private MenuItem menuItem;
    private int quantity;
    private double subTotal;

    public OrderItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        this.quantity = 1;
        this.subTotal = menuItem.getPrice() * quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
        this.subTotal = menuItem.getPrice() * quantity;
    }
    public void decrementQuantity() {
        if (quantity > 1) {
            this.quantity--;
            this.subTotal = menuItem.getPrice() * quantity;
        }
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getSubTotal() {
        return subTotal;
    }

}