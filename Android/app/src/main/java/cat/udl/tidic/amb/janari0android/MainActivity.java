package cat.udl.tidic.amb.janari0android;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cat.udl.tidic.amb.janari0android.adapters.SearchStockSaleAdapter;
import cat.udl.tidic.amb.janari0android.adapters.SliderAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "bakedbeans";
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private Button mCaptureBtn, seeAll, seeAllFree;
    private ImageView gps;
    private FloatingActionButton open, give, add, sell;
    private Button list, list2, list3;
    private boolean visibleFloatingButton = false;
    private RecyclerView nearbyProducts, freeProducts;
    private final Handler sliderHandler = new Handler();
    private final ArrayList<ProductSale> productsNearby = new ArrayList<>();
    private final ArrayList<ProductSale> productsFree = new ArrayList<>();
    private SliderAdapter sliderAdapterNearby;
    private SliderAdapter sliderAdapterFree;
    private SearchStockSaleAdapter searchStockSaleAdapter;
    private AdView mAdView;
    private RecyclerView searchProductsRecycler;
    private ArrayList<ProductSale> productsSale = new ArrayList<>();
    private Toolbar mainToolbar;
    private View logoView, profile, help;
    private TextView addProductText, donateText, sellText;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FusedLocationProviderClient fusedLocationProviderClient;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if user is signed in (non-null) and update UI accordingly.
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
            user.reload();
        }
        setContentView(R.layout.activity_main);
        seeAllFree = findViewById(R.id.seeAllFree);
        seeAll = findViewById(R.id.seeAll);
        open = findViewById(R.id.floatingButtonOpen);
        add = findViewById(R.id.floatingButtonAdd);
        give = findViewById(R.id.floatingButtonGift);
        sell = findViewById(R.id.floatingButtonSell);
        list = findViewById(R.id.numberItems);
        list2 = findViewById(R.id.numberItems2);
        list3 = findViewById(R.id.numberItems3);
        addProductText = findViewById(R.id.addStockText);
        donateText = findViewById(R.id.donateText);
        sellText = findViewById(R.id.sellText);
        mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        logoView = mainToolbar.getChildAt(0);
        nearbyProducts = findViewById(R.id.nearbyProductsView);
        freeProducts = findViewById(R.id.freeProductsView);
        help = findViewById(R.id.toolbarHelpbottom);
        profile = findViewById(R.id.toolbarUserMenuButton);
        searchProductsRecycler = findViewById(R.id.searchProductsView);
        setOnClickListeners();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Get location of the user and show him the products nearby
        getSearchData();
        getLocation();
        showProductsInfo();
        sliderAdapterNearby = new SliderAdapter(productsNearby, this);
        sliderAdapterNearby.setOnItemClickListener(new SliderAdapter.OnItemClickListener() {
            @Override
            public void onClickProduct(int position) {
                Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
                intent.putExtra("id", productsNearby.get(position).getProduct().getId());
                startActivity(intent);
            }
        });
        Log.w(TAG, "OnCreate");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.w(TAG, "OnCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchProductsViewMain);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchView.clearFocus();
                    searchView.setIconified(true);
                    hideKeyboard(v);
                }
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
                searchProductsRecycler.setAdapter(searchStockSaleAdapter);
                searchProductsRecycler.setVisibility(View.VISIBLE);
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean queryTextFocused) {
                if(!queryTextFocused) {
                    searchProductsRecycler.setVisibility(View.GONE);
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                    searchView.setIconified(true);
                    hideKeyboard(v);
                }
                else{
                    searchProductsRecycler.setVisibility(View.VISIBLE);
                }
            }
        });
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
/*        // for the first time , do the tutorial
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstTime = prefs.getBoolean("firstTime", true);
        if (firstTime) {
            tarjetaPrueba2();
        }*/
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                help = findViewById(R.id.toolbarHelpbottom);
                profile = findViewById(R.id.toolbarUserMenuButton);
                // SOME OF YOUR TASK AFTER GETTING VIEW REFERENCE
                Bundle extras = getIntent().getExtras();
                if(help == null)
                    Log.w(TAG, "Extras: ");

                if(extras!=null) {
                    tarjetaPrueba2();
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbarHelpbottom:
                tarjetaPrueba2();
                return true;

            case R.id.toolbarUserMenuButton:
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    private void getSearchData() {
        db.collection("productsSale")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                ProductSale product = document.toObject(ProductSale.class);
                                productsSale.add(product);
                            }
                            buildSearchProducts();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void buildSearchProducts() {
        searchStockSaleAdapter = new SearchStockSaleAdapter(MainActivity.this, productsSale);
        searchProductsRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchProductsRecycler.setAdapter(searchStockSaleAdapter);
        searchStockSaleAdapter.setOnItemClickListener(new SearchStockSaleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
                intent.putExtra("id", productsSale.get(position).getProduct().getId());
                startActivity(intent);
            }
        });
    }

    private void setOnClickListeners() {
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScannActivity.class));
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFloatingButton();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddStockProductNameActivity.class));
            }
        });
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListProductsActivity.class);
                intent.putExtra("Page", 1);
                startActivity(intent);
            }
        });
        list2.setOnClickListener(new View.OnClickListener() {//all
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListProductsActivity.class);
                intent.putExtra("Page", 2);
                startActivity(intent);
            }
        });
        list3.setOnClickListener(new View.OnClickListener() {//expired
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListProductsActivity.class);
                intent.putExtra("Page", 3);
                startActivity(intent);
            }
        });
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListProductsActivity.class);
                intent.putExtra("Page", 5);
                startActivity(intent);
            }
        });
        seeAllFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListProductsActivity.class);
                intent.putExtra("Page", 6);
                startActivity(intent);
            }
        });
        give.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DonateActivity.class));
            }
        });
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SellActivity.class));
            }
        });
    }
    private void getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Checking permissions, requesting if not given
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Find location of the user
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Address address = addresses.get(0);
                            String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(address.getLatitude(), address.getLongitude()));
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("geohash", hash);
                            updates.put("lat", address.getLatitude());
                            updates.put("lon", address.getLongitude());
                            // Update location and load slide data
                            db.collection("users").document(user.getUid()).update(updates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "Address successfully updated");
                                            getSliderData(1);
                                            getSliderDataFree(10);
                                        }
                                    });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void getSliderDataFree(int kilometers) {
        // Get products that are free
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    String geohash = (String) document.get("geohash");
                    double lat = (double) document.get("lat",double.class);
                    double lon = (double) document.get("lon", double.class);
                    final GeoLocation center = new GeoLocation(lat, lon);
                    final double radiusInM = kilometers * 1000;
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
                                    productsFree.clear();
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
                                        productsFree.add(doc.toObject(ProductSale.class));
                                    }
                                    //Log.d(TAG, productsFree.size() + "Free");
                                    sliderAdapterFree = new SliderAdapter(productsFree, MainActivity.this);
                                    freeProducts.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
                                    freeProducts.setAdapter(sliderAdapterFree);
                                    sliderAdapterFree.setOnItemClickListener(new SliderAdapter.OnItemClickListener() {
                                        @Override
                                        public void onClickProduct(int position) {
                                            Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("id", productsFree.get(position).getProduct().getId());
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                }
            }
        });


    }

    private void getSliderData(int kilometers) {
        productsNearby.clear();
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
                    final double radiusInM = kilometers * 1000;
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
                                    productsNearby.clear();
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
                                        productsNearby.add(doc.toObject(ProductSale.class));
                                    }
                                    //Log.d(TAG, String.valueOf(productsNearby.size()));
                                    sliderAdapterNearby = new SliderAdapter(productsNearby, MainActivity.this);
                                    nearbyProducts.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
                                    nearbyProducts.setAdapter(sliderAdapterNearby);
                                    sliderAdapterNearby.setOnItemClickListener(new SliderAdapter.OnItemClickListener() {
                                        @Override
                                        public void onClickProduct(int position) {
                                            Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("id", productsNearby.get(position).getProduct().getId());
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                }
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
        showProductsInfo();
    }
    private void filter(String text) {
        ArrayList<ProductSale> filteredlist = new ArrayList<>();
        for (ProductSale item : productsSale) {
            if (item.getProduct().getName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        searchStockSaleAdapter.filterList(filteredlist);
        searchProductsRecycler.setAdapter(searchStockSaleAdapter);
    }
    private void tarjetaPrueba2() {
        give.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
        sell.setVisibility(View.VISIBLE);
        final TapTargetSequence sequence =new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(profile, "Your Profile",
                                "here you can edit your account, see personal information and log out")
                                .outerCircleColor(R.color.colorPrimary900)
                                //.dimColor(R.color.colorPrimary700)
                                .outerCircleAlpha(0.95f)
                                .titleTextSize(25)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.colorDarkGrey)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)
                                .tintTarget(false)
                                .transparentTarget(false)
                                .cancelable(false),
                        TapTarget.forView(logoView, "QR Scan", "add products to your list just by scanning their barcode or QR")
                                .outerCircleColor(R.color.colorPrimary900)
                                //.dimColor(R.color.colorPrimary700)
                                .outerCircleAlpha(0.95f)
                                .titleTextSize(25)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.colorDarkGrey)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)
                                .tintTarget(false)
                                .transparentTarget(false)
                                .targetRadius(50)
                                .cancelable(false),
                        TapTarget.forView(findViewById(R.id.numberItems), "Products to Expire", "If you click here, a list of your products that are about to expire will appear")
                                .outerCircleColor(R.color.colorPrimary900)
                                //.dimColor(R.color.colorPrimary700)
                                .outerCircleAlpha(0.95f)
                                .titleTextSize(25)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.colorDarkGrey)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)
                                .tintTarget(false)
                                .transparentTarget(false)
                                .targetRadius(80)
                                .cancelable(false),
                                //.icon(gift)
                        TapTarget.forView(open, "More options", "click here and there are more options to do with your products")
                                .outerCircleColor(R.color.colorPrimary900)
                                //.dimColor(R.color.colorPrimary700)
                                .outerCircleAlpha(0.95f)
                                .titleTextSize(25)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.colorDarkGrey)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)
                                .tintTarget(false)
                                .transparentTarget(false)
                                .targetRadius(50)
                                .cancelable(false),
                        TapTarget.forView(add, "Add products", "1. add products to your own catalog")
                                .outerCircleColor(R.color.colorPrimary900)
                                //.dimColor(R.color.colorPrimary700)
                                .outerCircleAlpha(0.95f)
                                .titleTextSize(25)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.colorDarkGrey)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)
                                .tintTarget(false)
                                .transparentTarget(false)
                                .targetRadius(35)
                                .cancelable(false),
                        TapTarget.forView(give, "Donate products", "2. click here and there are more options to do with your products")
                                .outerCircleColor(R.color.colorPrimary900)
                                //.dimColor(R.color.colorPrimary700)
                                .outerCircleAlpha(0.95f)
                                .titleTextSize(25)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.colorDarkGrey)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)
                                .tintTarget(false)
                                .transparentTarget(false)
                                .targetRadius(35)
                                .cancelable(false),
                        TapTarget.forView(sell, "Sell Products", "3. sell your products in our huge virtual store")
                                .outerCircleColor(R.color.colorPrimary900)
                                //.dimColor(R.color.colorPrimary700)
                                .outerCircleAlpha(0.95f)
                                .titleTextSize(25)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.colorDarkGrey)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)
                                .tintTarget(false)
                                .transparentTarget(false)
                                .targetRadius(35)
                                .cancelable(false),
                        TapTarget.forView(help, "Help button",
                                "If you want to see this tutorial again, you can always click here and it will appear again")
                                .outerCircleColor(R.color.colorPrimary900)
                                //.dimColor(R.color.colorPrimary700)
                                .outerCircleAlpha(0.95f)
                                .titleTextSize(25)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.colorDarkGrey)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)
                                .tintTarget(false)
                                .transparentTarget(false)
                                .targetRadius(35)
                                .cancelable(false)
                                )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        give.setVisibility(View.INVISIBLE);
                        add.setVisibility(View.INVISIBLE);
                        sell.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        // Perform action for the current target
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                });
        sequence.start();
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstTime",false);
        editor.apply();


    }
    private void showProductsInfo() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);
        db.collection("users").document(user.getUid()).collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int num_products_toexpire = 0, num_products_expired = 0, num_products_all = 0;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Product p = document.toObject(Product.class);
                        Calendar cprod = Calendar.getInstance();
                        cprod.setTime(p.expirationDate);
                        if (cprod.compareTo(c) <= 0 && cprod.compareTo(Calendar.getInstance()) > 0) {
                            num_products_toexpire++;
                        } else if (cprod.compareTo(Calendar.getInstance()) <= 0) {
                            num_products_expired++;
                        } else
                            num_products_all++;
                    }
                    list.setText(String.valueOf(num_products_toexpire));
                    list3.setText(String.valueOf(num_products_expired));
                    list2.setText(String.valueOf(num_products_all + num_products_toexpire));
                } else
                    Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
    private void toggleFloatingButton() {
        if (visibleFloatingButton) {
            give.setVisibility(View.INVISIBLE);
            add.setVisibility(View.INVISIBLE);
            sell.setVisibility(View.INVISIBLE);
            addProductText.setVisibility(View.INVISIBLE);
            donateText.setVisibility(View.INVISIBLE);
            sellText.setVisibility(View.INVISIBLE);
            visibleFloatingButton = false;
        } else {
            give.setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);
            sell.setVisibility(View.VISIBLE);
            addProductText.setVisibility(View.VISIBLE);
            donateText.setVisibility(View.VISIBLE);
            sellText.setVisibility(View.VISIBLE);
            visibleFloatingButton = true;
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}