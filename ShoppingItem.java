/**
 * Objects of this class stores information on a given item from a store.
 *
 * @author Chiara Lim
 **/
public class ShoppingItem {
    String brand;
    String item;
    double size;
    double price;
    String brand_item;

    /**
     * ShoppingItem constructor. This object stores information regarding a item's information.
     * This is populated by the data from the csv files.
     * @param brand
     * @param item
     * @param size
     * @param price
     */
    public ShoppingItem(String brand, String item, double size, double price){
        this.brand = brand;
        this.item = item;
        this.size = size;
        this.price = price;
        this.brand_item = brand + " " + item;
    }

}