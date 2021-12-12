package cat.udl.tidic.amb.janari0android;

import static androidx.core.view.MenuItemCompat.collapseActionView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cat.udl.tidic.amb.janari0android.adapters.AddStockAdapter;
import cat.udl.tidic.amb.janari0android.adapters.SearchStockAdapter;

public class SellActivity extends AppCompatActivity {

    private static final String TAG = "bakedbeans";
    Button sell;
    TextView nameProduct, expirationDate;
    ImageView imageProduct;
    ImageButton go_back;
    SearchView searchProducts;
    TextInputEditText description, inputPrice;
    RecyclerView recyclerView;
    SearchStockAdapter searchStockAdapter;
    CardView product_details;
    ArrayList<Product> products = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Product product = null;
    String expirationDateString = "Expiration date: ";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        go_back = findViewById(R.id.goBackButton);
        description = findViewById(R.id.descriptionSell);
        searchProducts = findViewById(R.id.searchProductsSell);
        sell = findViewById(R.id.sellProduct);
        nameProduct = findViewById(R.id.name_product);
        expirationDate = findViewById(R.id.expirationDate);
        imageProduct = findViewById(R.id.image_product);
        product_details = findViewById(R.id.product_list);
        inputPrice = findViewById(R.id.inputPrice);
        buildRecyclerView();
        getSearchData();


        inputPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
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
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputPrice.getText().toString().isEmpty())
                    inputPrice.setText("0");
                ProductSale productSale = new ProductSale(product,String.valueOf(description.getText()),Float.parseFloat(inputPrice.getText().toString()));
                // Saving in a place that user can access
                db.collection("users").document(user.getUid()).collection("productsSale").document(product.getName())
                        .set(productSale)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                Toast.makeText(SellActivity.this, "Product successfully added", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                // Saving in a place where products on sale can be randomly selected from any user
                db.collection("productsSale")
                        .add(productSale)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                // Distinguishing a donated product from the one with a price
                db.collection("productsSell")
                        .add(productSale)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                showProductDetails(productSale);
                finish();
            }
        });
    }



    private void showProductDetails(ProductSale productSale) {
        Intent intent = new Intent(SellActivity.this, ProductDetailsActivity.class);
        intent.putExtra("name", productSale.getProduct().getName());
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
                    Glide.with(SellActivity.this)
                        .load(product.getPhotos().get(0))
                        .placeholder(R.drawable.__2_burger_free_download_png)
                        .into(imageProduct);
                nameProduct.setText(product.getName().toString());
                DateFormat fmt = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                expirationDate.setText(expirationDateString + fmt.format(product.getExpirationDate()));
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
