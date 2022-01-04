package cat.udl.tidic.amb.janari0android;

import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Product {
    String id;
    Date expirationDate = new Date();
    List<String> photos = new ArrayList<String>();
    String name;
    public Product(String id, String name, List<String> photos, Date expirationDate){
        this.id = id;
        this.name = name;
        this.photos = photos;
        this.expirationDate = expirationDate;
    }
    public Product(){
        this.name = "bakedbeans";
    }
    public String getId() {return id;}
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
