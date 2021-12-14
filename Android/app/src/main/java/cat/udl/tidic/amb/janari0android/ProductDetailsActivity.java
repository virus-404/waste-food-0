package cat.udl.tidic.amb.janari0android;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class ProductDetailsActivity extends AppCompatActivity {
    private static final String TAG = "bakedbeans";
    ImageButton goBack;
    ImageView productImage;
    TextView nameView, priceView, descriptionView;
    ProductSale productSale = new ProductSale();
    CarouselView carouselView;
    String currency = "€";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Bundle extras = getIntent().getExtras();
        String name = "";
        if(extras!=null) {
            name = extras.getString("name");
        }
        goBack = findViewById(R.id.goBackButton);
        nameView = findViewById(R.id.nameView);
        priceView = findViewById(R.id.priceView);
        descriptionView = findViewById(R.id.descriptionView);
        getProduct(name);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Glide.with(ProductDetailsActivity.this)
                    .load(productSale.getProduct().getPhotos().get(position))
                    .into(imageView);
        }
    };
    private void getProduct(String name) {
        db.collection("users").document(user.getUid()).collection("productsSale").document(name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        productSale = document.toObject(ProductSale.class);
                        carouselView = findViewById(R.id.carouselView);
                        carouselView.setImageListener(imageListener);
                        carouselView.setPageCount(productSale.getProduct().getPhotos().size());
                        nameView.setText(productSale.getProduct().getName());
                        if(productSale.getPrice() == (int) productSale.getPrice())
                            priceView.setText(currency + String.valueOf((int) productSale.getPrice()));
                        else
                            priceView.setText(currency + String.valueOf(productSale.getPrice()));
                        descriptionView.setText(productSale.getDescription());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
