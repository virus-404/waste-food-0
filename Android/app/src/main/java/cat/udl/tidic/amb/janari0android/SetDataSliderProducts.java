package cat.udl.tidic.amb.janari0android;

import android.media.Image;

public class SetDataSliderProducts {

    private int image;
    String name_product, description_product;

    public String getName_product() {
        return name_product;
    }

    public String getDescription_product() {
        return description_product;
    }

    public int getImage() {
        return image;
    }

    SetDataSliderProducts(int image, String name_product, String description_product){
        this.image = image;
        this.name_product = name_product;
        this.description_product = description_product;
    }
}
