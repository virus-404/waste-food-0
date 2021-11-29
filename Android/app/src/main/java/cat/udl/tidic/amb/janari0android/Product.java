package cat.udl.tidic.amb.janari0android;

import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Product {
    Date expirationDate = new Date();
    List<String> photos = new ArrayList<String>();
    String name;
    public Product(String name, List<String> photos, Date expirationDate){
        this.name = name;
        this.photos = photos;
        this.expirationDate = expirationDate;
    }
    public Product(){
        this.name = "bakedbeans";
    }
    public Date getExpirationDate() {
        return expirationDate;
    }
    public List<String> getPhotos() {
        return photos;
    }
    public String getName() {
        return name;
    }
}
