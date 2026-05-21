package models;

public class OrderItem{
    private final MenuItem menuItem;
    private int quantity;
    private double subTotal;

    public OrderItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        this.quantity = 1;
        this.subTotal = menuItem.getPrice() * quantity;
    }
    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.subTotal = menuItem.getPrice() * quantity;
    }
    public void setQuantity(int Quantity){
        if(Quantity >= 0){
            this.quantity = Quantity;
        }
    }

    public void incrementQuantity() {
        this.quantity++;
        this.subTotal = menuItem.getPrice() * quantity;
    }
    public void incrementQuantity(int quantity) {
        this.quantity += quantity;
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