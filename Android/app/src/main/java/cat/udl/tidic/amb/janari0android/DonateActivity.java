package cat.udl.tidic.amb.janari0android;

import static androidx.core.view.MenuItemCompat.collapseActionView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cat.udl.tidic.amb.janari0android.adapters.AddStockAdapter;
import cat.udl.tidic.amb.janari0android.adapters.SearchStockAdapter;

public class DonateActivity extends AppCompatActivity {

    private static final String TAG = "bakedbeans";
    Button donate;
    View rootView;
    ImageButton go_back;
    SearchView searchProducts;
    TextInputEditText description;
    RecyclerView recyclerView;
    SearchStockAdapter searchStockAdapter;
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
        rootView = findViewById(R.id.root);
        rootView.requestFocus();
        buildRecyclerView();
        getSearchData();
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonateActivity.this, MainActivity.class);
                startActivity(intent);
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
                    rootView.requestFocus();
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
                    recyclerView.setVisibility(View.INVISIBLE);
                    searchProducts.setQuery("", false);
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
                ProductSale productSale = new ProductSale(product,String.valueOf(description.getText()),0);
                // Saving in a place that user can access
                db.collection("users").document(user.getUid()).collection("productsSale").document(product.getName())
                        .set(productSale)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                Toast.makeText(DonateActivity.this, "Product successfully added", Toast.LENGTH_SHORT).show();
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
                db.collection("productsDonate")
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
            }
        });
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
                                products.add(document.toObject(Product.class));
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
            }
        });
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
