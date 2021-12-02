package cat.udl.tidic.amb.janari0android;

import java.util.Date;
import java.util.List;

public class ProductSale {
    Product product;
    String description = "Default description";
    float price = 0;
    public ProductSale(Product product, String description, float price) {
        this.product = product;
        this.description = description;
        this.price=price;
    }
    public ProductSale(){}
    public String getDescription() {
        return description;
    }
    public Product getProduct() {
        return product;
    }
    public float getPrice() {
        return price;
    }

}
