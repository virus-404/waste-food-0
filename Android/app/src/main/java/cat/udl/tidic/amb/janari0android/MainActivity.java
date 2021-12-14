package cat.udl.tidic.amb.janari0android;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;


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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
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
import com.google.type.DateTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cat.udl.tidic.amb.janari0android.adapters.SearchStockAdapter;
import cat.udl.tidic.amb.janari0android.adapters.SliderAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "bakedbeans";
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private Button mCaptureBtn;
    private ImageView gps;
    private FloatingActionButton open, give, add, sell;
    private Button list, profile, list2, list3, help;
    private boolean visibleFloatingButton = false;
    private ViewPager2 saleSection;
    private final Handler sliderHandler = new Handler();
    private final ArrayList<ProductSale> products = new ArrayList<>();
    private SliderAdapter sliderAdapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FusedLocationProviderClient fusedLocationProviderClient;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        // Check if user is signed in (non-null) and update UI accordingly.
        if (user == null) {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_main);

        open = findViewById(R.id.floatingButtonOpen);
        add = findViewById(R.id.floatingButtonAdd);
        give = findViewById(R.id.floatingButtonGift);
        sell = findViewById(R.id.floatingButtonSell);
        list = findViewById(R.id.numberItems);
        list2 = findViewById(R.id.numberItems2);
        list3 = findViewById(R.id.numberItems3);
        profile = findViewById(R.id.toolbarUserMenuButton);
        saleSection = findViewById(R.id.viewpager2_layout2);
        mCaptureBtn = findViewById(R.id.toolbarMenuButton);
        help = findViewById(R.id.toolbarHelpbottom);

        setOnClickListeners();

        // Get location of the user and show him the products nearby
        getLocation();
        showProductsInfo();

        // for the first time , do the tutorial
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstTime = prefs.getBoolean("firstTime", true);
        if (firstTime) {
            tarjetaPrueba2();
        }

        sliderAdapter = new SliderAdapter(products, saleSection, this);
        saleSection.setAdapter(sliderAdapter);
        saleSection.setClipToPadding(false);
        saleSection.setClipChildren(false);
        saleSection.setOffscreenPageLimit(3);
        saleSection.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        saleSection.setPageTransformer(compositePageTransformer);
        //Images slide every 3 seconds
        //The user still can slide images on his own
        saleSection.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000); //Slide duration
            }
        });
        sliderAdapter.setOnItemClickListener(new SliderAdapter.OnItemClickListener() {
            @Override
            public void onClickProduct(int position) {
                Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
                intent.putExtra("name", products.get(position).getProduct().getName());
                startActivity(intent);
            }
        });
    }

    private void setOnClickListeners() {
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFloatingButton();
            }
        });
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScannActivity.class));
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
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tarjetaPrueba2();
            }
        });
    }
    private void getLocation() {
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
                            Toast.makeText(MainActivity.this, addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
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
                                            getSliderData();
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
    private void getSliderData() {
        products.clear();
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
                    final double radiusInM = 30 * 1000;
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
                                        products.add(doc.toObject(ProductSale.class));
                                    }
                                    Log.d(TAG, String.valueOf(products.size()));
                                    saleSection.setAdapter(sliderAdapter);
                                }
                            });
                }
            }
        });
    }
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            saleSection.setCurrentItem(saleSection.getCurrentItem() + 1);
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
        getLocation();
        showProductsInfo();
    }
    private void tarjetaPrueba2() {
        give.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
        sell.setVisibility(View.VISIBLE);
        final TapTargetSequence sequence =new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.toolbarUserMenuButton), "Your Profile",
                                "here you can edit your account, see personal information and log out")
                                .outerCircleColor(R.color.colorPrimary900)
                                //.dimColor(R.color.colorPrimary700)
                                .outerCircleAlpha(0.95f)
                                .titleTextSize(25)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.colorDarkGrey)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .cancelable(false),
                        TapTarget.forView(findViewById(R.id.toolbarMenuButton), "QR Scan", "add products to your list just by scanning their barcode or QR")
                                .outerCircleColor(R.color.colorPrimary900)
                                //.dimColor(R.color.colorPrimary700)
                                .outerCircleAlpha(0.95f)
                                .titleTextSize(25)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.colorDarkGrey)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)
                                .tintTarget(true)
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
                                .tintTarget(true)
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
                    list2.setText(String.valueOf(num_products_all));
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
            visibleFloatingButton = false;
        } else {
            give.setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);
            sell.setVisibility(View.VISIBLE);
            visibleFloatingButton = true;
        }
    }
}