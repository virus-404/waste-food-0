package cat.udl.tidic.amb.janari0android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import cat.udl.tidic.amb.janari0android.adapters.SearchStockAdapter;

public class SellActivity extends AppCompatActivity {

    private static final String TAG = "bakedbeans";
    ImageButton goBack;
    TextInputEditText description,inputPrice;

    SearchView searchProducts;
    RecyclerView recyclerView;
    SearchStockAdapter searchStockAdapter;
    ArrayList<Product> products = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        goBack = findViewById(R.id.goBackButton);
        description = findViewById(R.id.descriptionSell);
        inputPrice = findViewById(R.id.inputPrice);
        searchProducts = (SearchView)findViewById(R.id.SellSearchV);
        getSearchData();
        buildRecyclerView();

        goBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellActivity.this, MainActivity.class);
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
            }
        });

        searchProducts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);

                return false;
            }
        });

        inputPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
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
    }
    public void buildRecyclerView() {
        recyclerView = findViewById(R.id.recycle_sell);
        searchStockAdapter = new SearchStockAdapter(this, products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchStockAdapter);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
