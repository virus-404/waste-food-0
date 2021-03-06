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

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cat.udl.tidic.amb.janari0android.adapters.AddStockAdapter;
import cat.udl.tidic.amb.janari0android.adapters.ListProductAdapter;
import cat.udl.tidic.amb.janari0android.adapters.ListProductSellAdapter;
import cat.udl.tidic.amb.janari0android.adapters.SearchStockAdapter;
import cat.udl.tidic.amb.janari0android.adapters.SliderAdapter;

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
        }if(page==5){
            titleProducts.setText(getResources().getString(R.string.nearbyProducts));
        }
        if(page==6){
            titleProducts.setText(getResources().getString(R.string.freeProducts));
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
                    intent.putExtra("id", productsSale.get(position).getProduct().getId());
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
        db.collection("products").document(String.valueOf(products.get(position).getId())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        document.getReference().delete();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        db.collection("productsSale").document(String.valueOf(products.get(position).getId())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        document.getReference().delete();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        db.collection("users").document(user.getUid()).collection("products").document(String.valueOf(products.get(position).getId())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        document.getReference().delete();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        db.collection("users").document(user.getUid()).collection("productsSale").document(String.valueOf(products.get(position).getId())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        document.getReference().delete();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        db.collection("productsSell").document(String.valueOf(products.get(position).getId())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        document.getReference().delete();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        db.collection("productsDonate").document(String.valueOf(products.get(position).getId())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        document.getReference().delete();
                        products.remove(position);
                        listProductAdapter.notifyItemRemoved(position);
                        Toast.makeText(ListProductsActivity.this,R.string.productDeleted,Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    private void getData(int page) {
        products = new ArrayList<>();
        if (page == 0 || page == 1 || page == 2 || page == 3) {
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
                                    if (page == 0)
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
        }else if(page == 5){ //nearby products
            productsSale.clear();
            // Get products nearby
            db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        String geohash = (String) document.get("geohash");
                        double lat = (double) document.get("lat",double.class);
                        double lon = (double) document.get("lon", double.class);
                        final GeoLocation center = new GeoLocation(lat, lon);
                        final double radiusInM = 1 * 1000;
                        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM);
                        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                        for (GeoQueryBounds b : bounds) {
                            Query q = db.collection("productsSale")
                                    .orderBy("geohash")
                                    .startAt(b.startHash)
                                    .endAt(b.endHash);
                            tasks.add(q.get());
                        }
                        // Collect all the query results together into a single list
                        Tasks.whenAllComplete(tasks)
                                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                                        List<DocumentSnapshot> matchingDocs = new ArrayList<>();
                                        for (Task<QuerySnapshot> task : tasks) {
                                            QuerySnapshot snap = task.getResult();
                                            for (DocumentSnapshot doc : snap.getDocuments()) {
                                                double lat = doc.getDouble("lat");
                                                double lon = doc.getDouble("lon");
                                                // We have to filter out a few false positives due to GeoHash
                                                // accuracy, but most will match
                                                GeoLocation docLocation = new GeoLocation(lat, lon);
                                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                                if (distanceInM <= radiusInM) {
                                                    matchingDocs.add(doc);
                                                }
                                            }
                                        }
                                        for(DocumentSnapshot doc : matchingDocs){
                                            productsSale.add(doc.toObject(ProductSale.class));
                                        }
                                        Log.d(TAG, String.valueOf(productsSale.size()));
                                        buildRecyclerView(true);
                                    }
                                });
                    }
                }
            });
        }else if(page == 6) {//free products
            productsSale.clear();
            // Get products free
            db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        String geohash = (String) document.get("geohash");
                        double lat = (double) document.get("lat",double.class);
                        double lon = (double) document.get("lon", double.class);
                        final GeoLocation center = new GeoLocation(lat, lon);
                        final double radiusInM = 1 * 1000;
                        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM);
                        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                        for (GeoQueryBounds b : bounds) {
                            Query q = db.collection("productsDonate")
                                    .orderBy("geohash")
                                    .startAt(b.startHash)
                                    .endAt(b.endHash);
                            tasks.add(q.get());
                        }
                        // Collect all the query results together into a single list
                        Tasks.whenAllComplete(tasks)
                                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                                        List<DocumentSnapshot> matchingDocs = new ArrayList<>();
                                        for (Task<QuerySnapshot> task : tasks) {
                                            QuerySnapshot snap = task.getResult();
                                            for (DocumentSnapshot doc : snap.getDocuments()) {
                                                double lat = doc.getDouble("lat");
                                                double lon = doc.getDouble("lon");
                                                // We have to filter out a few false positives due to GeoHash
                                                // accuracy, but most will match
                                                GeoLocation docLocation = new GeoLocation(lat, lon);
                                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                                if (distanceInM <= radiusInM) {
                                                    matchingDocs.add(doc);
                                                }
                                            }
                                        }
                                        for (DocumentSnapshot doc : matchingDocs) {
                                            productsSale.add(doc.toObject(ProductSale.class));
                                        }
                                        Log.d(TAG, String.valueOf(productsSale.size()));
                                        buildRecyclerView(true);
                                    }
                                });
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
