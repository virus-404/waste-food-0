package cat.udl.tidic.amb.janari0android;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProductSale {
    String id;
    Product product;
    String description = "Default description";
    String price;
    String geohash;
    String phoneNumber;
    double lat,lon;
    public ProductSale(String id, Product product, String description, String price, String geohash, double lat, double lon, String phoneNumber) {
        this.id = id;
        this.product = product;
        this.description = description;
        this.price=price;
        this.geohash = geohash;
        this.lat = lat;
        this.lon = lon;
        this.phoneNumber=phoneNumber;
    }
    public ProductSale(String id, Product product, String description, String price, String geohash, double lat, double lon) {
        this.id = id;
        this.product = product;
        this.description = description;
        this.price=price;
        this.geohash = geohash;
        this.lat = lat;
        this.lon = lon;
    }
    public ProductSale(Product product, String description, String price) {
        this.product = product;
        this.description = description;
        this.price=price;
    }
    public ProductSale(){}
    public String getId() { return id; }
    public void setGeohash(String geohash) { this.geohash = geohash; }
    public void setLat(double lat) { this.lat = lat;}
    public void setLon(double lon) { this.lon = lon;}
    public String getGeohash() {return geohash;}
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public String getPhoneNumber(){return phoneNumber;}
    public String getDescription() {
        return description;
    }
    public Product getProduct() {
        return product;
    }
    public String getPrice() {
        return price;
    }

}
