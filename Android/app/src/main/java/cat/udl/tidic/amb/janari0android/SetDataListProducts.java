package cat.udl.tidic.amb.janari0android;

public class SetDataListProducts {

    String name_product, description_product;

    SetDataListProducts(String name_product, String description_product){
        this.name_product = name_product;
        this.description_product = description_product;
    }

    public String getName_product() {
        return name_product;
    }

    public String getDescription_product() {
        return description_product;
    }
}
