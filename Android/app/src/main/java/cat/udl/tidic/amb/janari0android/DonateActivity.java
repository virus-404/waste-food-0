package cat.udl.tidic.amb.janari0android;

import static androidx.core.view.MenuItemCompat.collapseActionView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import cat.udl.tidic.amb.janari0android.adapters.AddStockAdapter;
import cat.udl.tidic.amb.janari0android.adapters.SearchStockAdapter;

public class DonateActivity extends AppCompatActivity {

    private static final String TAG = "bakedbeans";
    Button donate;
    TextView nameProduct, expirationDate;
    ImageView imageProduct;
    ImageButton go_back;
    SearchView searchProducts;
    TextInputEditText description;
    RecyclerView recyclerView;
    SearchStockAdapter searchStockAdapter;
    CardView product_details;
    ArrayList<Product> products = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Product product = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        go_back = findViewById(R.id.goBackButton);
        description = findViewById(R.id.descriptionDonate);
        searchProducts = findViewById(R.id.searchProductsDonate);
        donate = findViewById(R.id.addProductDonate);
        nameProduct = findViewById(R.id.name_product);
        expirationDate = findViewById(R.id.expirationDate);
        imageProduct = findViewById(R.id.image_product);
        product_details = findViewById(R.id.product_list);
        buildRecyclerView();
        getSearchData();
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        searchProducts.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchProducts.clearFocus();
                    searchProducts.setIconified(true);
                    hideKeyboard(v);
                }
            }
        });
        searchProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchProducts.setIconified(false);
                recyclerView.setAdapter(searchStockAdapter);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        searchProducts.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean queryTextFocused) {
                if(!queryTextFocused) {
                    recyclerView.setVisibility(View.GONE);
                    searchProducts.setQuery("", false);
                    searchProducts.clearFocus();
                    searchProducts.setIconified(true);
                    hideKeyboard(v);
                }
                else{
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        searchProducts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts.clearFocus();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DonateActivity.this);
                if (ActivityCompat.checkSelfPermission(DonateActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(DonateActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DonateActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if(location!=null) {
                            Geocoder geocoder = new Geocoder(DonateActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                Address address = addresses.get(0);
                                String geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(address.getLatitude(), address.getLongitude()));
                                double lat = address.getLatitude();
                                double lon = address.getLongitude();
                                db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            ProductSale productSale = null;
                                            if (document.exists()) {
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                if(document.get("phoneNumber") != null)
                                                    productSale = new ProductSale(UUID.randomUUID().toString(), product, String.valueOf(description.getText()), "Free", geohash, lat, lon, document.get("phoneNumber",String.class));
                                                else
                                                    productSale = new ProductSale(UUID.randomUUID().toString(), product, String.valueOf(description.getText()), "Free", geohash, lat, lon);
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                            // Saving in a place that user can access
                                            assert productSale != null;
                                            db.collection("users").document(user.getUid()).collection("productsSale").document(String.valueOf(product.getId()))
                                                    .set(productSale)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                                            Toast.makeText(DonateActivity.this, getResources().getString(R.string.ProductSuccessfull), Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error writing document", e);
                                                        }
                                                    });
                                            // Saving in a place where products on sale can be randomly selected from any user
                                            db.collection("productsSale").document(String.valueOf(product.getId())).set(productSale);
                                            // Distinguishing a donated product from the one with a price
                                            db.collection("productsDonate").document(String.valueOf(product.getId())).set(productSale);
                                            showProductDetails(productSale);
                                            finish();
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }
    private void showProductDetails(ProductSale productSale) {
        Intent intent = new Intent(DonateActivity.this, ProductDetailsActivity.class);
        intent.putExtra("id", productSale.getProduct().getId());
        startActivity(intent);
    }
    private void getSearchData() {
        db.collection("users").document(user.getUid()).collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Product product = document.toObject(Product.class);
                                // Check if product is already on sale
                                Query userNameQuery = db.collection("users").document(user.getUid()).collection("productsSale").whereEqualTo("product", product);
                                userNameQuery.limit(1).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    boolean isEmpty = task.getResult().isEmpty();
                                                    if (isEmpty) {
                                                        products.add(product);
                                                    }
                                                }
                                                else
                                                    products.add(product);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void filter(String text) {
        ArrayList<Product> filteredlist = new ArrayList<>();
        for (Product item : products) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        searchStockAdapter.filterList(filteredlist);
        recyclerView.setAdapter(searchStockAdapter);
    }
    public void buildRecyclerView() {
        recyclerView = findViewById(R.id.searchProductsView);
        searchStockAdapter = new SearchStockAdapter(this, products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchStockAdapter);
        searchStockAdapter.setOnItemClickListener(new SearchStockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                product = products.get(position);
                if(product.getPhotos().size() != 0)
                    Glide.with(DonateActivity.this)
                            .load(product.getPhotos().get(0))
                            .placeholder(R.drawable.__2_burger_free_download_png)
                            .into(imageProduct);
                nameProduct.setText(product.getName().toString());
                DateFormat fmt = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                expirationDate.setText("Expiration date: " + fmt.format(product.getExpirationDate()));
                product_details.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                searchProducts.clearFocus();
                searchProducts.setIconified(true);
            }
        });
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
