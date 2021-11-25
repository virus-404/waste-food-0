package cat.udl.tidic.amb.janari0android;

import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Product {
    SimpleDateFormat ExpirationDate;
    List<String> Photos = new ArrayList<String>();
    String Name;
    public Product( String name, List<String> photos, SimpleDateFormat expirationDate){
        this.Name = name;
        this.Photos = photos;
        this.ExpirationDate = expirationDate;
    }



}
