package cat.udl.tidic.amb.janari0android;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

import cat.udl.tidic.amb.janari0android.adapters.AddStockAdapter;
import cat.udl.tidic.amb.janari0android.adapters.ListProductAdapter;
import cat.udl.tidic.amb.janari0android.adapters.ListProductSellAdapter;
import cat.udl.tidic.amb.janari0android.adapters.SearchStockAdapter;

public class ListProductsActivity extends AppCompatActivity {

    private static final String TAG = "bakedbeans";
    ArrayList<Product> products = new ArrayList<>() ;
    ArrayList<ProductSale> productsSale = new ArrayList<>() ;
    RecyclerView recyclerView;
    ListProductAdapter listProductAdapter;
    ListProductSellAdapter listProductSellAdapter;
    TextView title, titleProducts;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageButton goBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_products);
        title = findViewById(R.id.title);
        titleProducts = findViewById(R.id.titleProducts);
        Intent myIntent = getIntent(); // gets the previously created intent
        int page = 0;
        page = myIntent.getIntExtra("Page", 0);
        getData(page);
        goBack = findViewById(R.id.goBackButton);
        if(page==1)
            titleProducts.setText(getResources().getString(R.string.aboutToExpire));
        if(page==2)
            titleProducts.setText(getResources().getString(R.string.freshProducts));
        if(page==3)
            titleProducts.setText(getResources().getString(R.string.expiredProducts));
        if(page==4) {
            title.setText(getResources().getString(R.string.articles));
            titleProducts.setText(getResources().getString(R.string.allArticles));
        }
        goBack.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void buildRecyclerView(Boolean onSale) {
        if(onSale){
            recyclerView = findViewById(R.id.list_productRecycler);
            listProductSellAdapter = new ListProductSellAdapter(productsSale, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(listProductSellAdapter);
            listProductSellAdapter.setOnItemClickListener(new ListProductSellAdapter.OnItemClickListener() {
                @Override
                public void onClickProduct(int position) {
                    Intent intent = new Intent(ListProductsActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("name", productsSale.get(position).getProduct().getName());
                    startActivity(intent);
                }
            });
        }
        else {
            recyclerView = findViewById(R.id.list_productRecycler);
            listProductAdapter = new ListProductAdapter(products, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(listProductAdapter);
            listProductAdapter.setOnItemClickListener(new ListProductAdapter.OnItemClickListener() {
                @Override
                public void onDeleteClick(int position) {
                    removeItem(position);
                }
            });
        }
    }
    public void removeItem(int position) {
        Query qProducts = db.collection("products").whereEqualTo("name", products.get(position).getName());
        Query qProductsUser = db.collection("users").document(user.getUid()).collection("products").whereEqualTo("name",products.get(position).getName());
        qProducts.limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isEmpty = task.getResult().isEmpty();
                            if (isEmpty) {
                                Toast.makeText(ListProductsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                doc.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });
                            }
                        }
                    }
                });
        qProductsUser.limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isEmpty = task.getResult().isEmpty();
                            if (isEmpty) {
                                Toast.makeText(ListProductsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                doc.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        products.remove(position);
                                        listProductAdapter.notifyItemRemoved(position);
                                        Toast.makeText(ListProductsActivity.this,R.string.productDeleted,Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });
                            }
                        }
                    }
                });

    }
    private void getData(int page) {
        products = new ArrayList<>();
        if(page!=4) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 7);
            db.collection("users").document(user.getUid()).collection("products")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    //products.add(document.toObject(Product.class));

                                    Product p = document.toObject(Product.class);
                                    Calendar cprod = Calendar.getInstance();
                                    cprod.setTime(p.expirationDate);
                                    if(page == 0)
                                        products.add(p);
                                    else if (page == 1) {//to expire
                                        if (cprod.compareTo(c) <= 0 && cprod.compareTo(Calendar.getInstance()) > 0) {
                                            products.add(p);
                                        }
                                    } else if (page == 2) {//all
                                        if (cprod.compareTo(Calendar.getInstance()) > 0) {
                                            products.add(p);
                                        }
                                    } else if (page == 3) {//expired
                                        if (cprod.compareTo(Calendar.getInstance()) <= 0) {
                                            products.add(p);
                                        }
                                    }
                                }
                                buildRecyclerView(false);
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }else{

            db.collection("users").document(user.getUid()).collection("productsSale")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    ProductSale p = document.toObject(ProductSale.class);
                                    productsSale.add(p);
                                }
                                buildRecyclerView(true);
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }
    }
}
