package models;
import enums.Category;

public class MenuItem{
    private String itemName;
    private String menuItemID;
    private double price;
    private String description;
    private Category category;
    private String imageUrl;
    private boolean isAvailable;
    private int preparationTime;
    private int servingSize;
    public MenuItem(String itemName, double price, String description, Category category, String imageUrl, int preparationTime, int servingSize) {
        setItemName(itemName);
        setMenuItemID();
        setPrice(price);
        setDescription(description);
        setCategory(category);
        setImageUrl(imageUrl);
        setIsAvailable();
        setPreparationTime(preparationTime);
        setServingSize(servingSize);
    }
    public MenuItem(String itemName,String menuItemID, double price, String description, Category category, String imageUrl, int preparationTime, int servingSize) {
        setItemName(itemName);
        setMenuItemID(menuItemID);
        setPrice(price);
        setDescription(description);
        setCategory(category);
        setImageUrl(imageUrl);
        setIsAvailable();
        setPreparationTime(preparationTime);
        setServingSize(servingSize);
    }

    
    public String getItemName() {return itemName;}
    public String getID() {return menuItemID;}
    public double getPrice() {return price;}
    public String getDescription() {return description;}
    public Category getCategory() {return category;}
    public String getImageUrl() {return imageUrl;}
    public boolean getIsAvailable() {return isAvailable;}
    public int getPreparationTime() {return preparationTime;}
    public int getServingSize() {return servingSize;}
    


    public void setItemName(String itemName){
        if(itemName != null && !itemName.trim().isEmpty()){
            this.itemName = itemName;
        } else {
            throw new IllegalArgumentException("Item name cannot be null or empty.");
        }
    }

    public void setIsAvailable() {this.isAvailable = true;}
    public void setIsUnavailable() {this.isAvailable = false;}

    public void setPrice(double price) {
        if(price < 0.0){
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }

    public void setDescription(String description) {
        if(description != null && !description.trim().isEmpty()){
            this.description = description;
        } else {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
    }

    public void setCategory(Category category) {
        if(category != null){
            this.category = category;
        } else {
            throw new IllegalArgumentException("Category cannot be null.");
        }
    }

    public void setImageUrl(String imageUrl) {
        if(imageUrl != null && !imageUrl.trim().isEmpty()){
            this.imageUrl = imageUrl;
        } else {
            throw new IllegalArgumentException("Image URL cannot be null or empty.");
        }
    }
    public void setPreparationTime(int preparationTime) {
        if(preparationTime < 0){
            throw new IllegalArgumentException("Preparation time cannot be negative.");
        }
        this.preparationTime = preparationTime;
    }
    public void setServingSize(int servingSize) {
        if(servingSize < 0){
            throw new IllegalArgumentException("Serving size cannot be negative.");
        }
        this.servingSize = servingSize;
    }

    private void setMenuItemID() {
        this.menuItemID = "MI" + System.currentTimeMillis();
    }
    private void setMenuItemID(String menuItemID) {
        if(menuItemID != null && !menuItemID.trim().isEmpty()){
            this.menuItemID = menuItemID;
        } else {
            throw new IllegalArgumentException("Menu Item ID cannot be null or empty.");
        }
    }


}