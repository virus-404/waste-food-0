package cat.udl.tidic.amb.janari0android;


import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.Typeface;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.widget.SearchView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cat.udl.tidic.amb.janari0android.adapters.SliderAdapter;
import io.grpc.Context;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "bakedbeans";
    private FirebaseAuth auth = FirebaseAuth.getInstance();;

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    private Button mCaptureBtn;

    private ImageView mImageView;
    SearchView search;

    private FloatingActionButton open, give, add, sell;

    private Button list, profile, list2, list3, help;

    private boolean visibleFloatingButton = false;
    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();
    private ArrayList<ProductSale> products = new ArrayList<>();
    private List<ProductSale> sliderItems = new ArrayList<>();
    private SliderAdapter sliderAdapter;
    Uri image_uri;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();





    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        // Check if user is signed in (non-null) and update UI accordingly.

        auth = FirebaseAuth.getInstance();
 
        if (auth.getCurrentUser() == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            // boolean emailVerified = user.isEmailVerified();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }

        setContentView(R.layout.activity_main);

        open = findViewById(R.id.floatingButtonOpen);
        add = findViewById(R.id.floatingButtonAdd);
        give = findViewById(R.id.floatingButtonGift);
        sell = findViewById(R.id.floatingButtonSell);
        list = findViewById(R.id.numberItems);
        list2 = findViewById(R.id.numberItems2);
        list3 = findViewById(R.id.numberItems3);
        profile = findViewById(R.id.toolbarUserMenuButton);
        viewPager2 = findViewById(R.id.viewpager2_layout2);
        mCaptureBtn = findViewById(R.id.toolbarMenuButton);
        search = findViewById(R.id.searchView);
        help = findViewById(R.id.toolbarHelpbottom);






        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScannActivity.class);
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddStockProductNameActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(MainActivity.this, DonateActivity.class);
                startActivity(intent);
            }
        });
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SellActivity.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tarjetaPrueba2();
            }
        });

        // for the firs time , do the tutorial
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        boolean firstTime = prefs.getBoolean("firstTime",true);
        if (firstTime){
           //onPause();
            tarjetaPrueba2();
        }


        getSliderData();
        List<ProductSale> sliderItems = products;
        sliderAdapter = new SliderAdapter(sliderItems, viewPager2, this);
        viewPager2.setAdapter(sliderAdapter);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        //Images slides every 3 seconds
        // Altouht it, the user still can slide images at his own
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000); //Slide duration
            }
        });

        DateTime timenow = DateTime.getDefaultInstance();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);

        db.collection("users").document(user.getUid()).collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int num_products_toexpire = 0;
                int num_products_expired = 0;
                int num_products_all = 0;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG,document.getId() + " => " + document.getData());
                        Product p = document.toObject(Product.class);
                        Calendar cprod = Calendar.getInstance();
                        cprod.setTime(p.expirationDate);
                        if (cprod.compareTo(c) <= 0 && cprod.compareTo(Calendar.getInstance()) > 0){
                            num_products_toexpire ++;
                        }else if(cprod.compareTo(Calendar.getInstance()) <= 0){
                            num_products_expired ++;
                        }else {
                            num_products_all++;
                        }
                    }
                    //QuerySnapshot t = ;
                    //int number_products = task.getResult().getDocumentChanges().size();
                    list.setText(String.valueOf(num_products_toexpire));
                    list3.setText(String.valueOf(num_products_expired));
                    list2.setText(String.valueOf(num_products_all+num_products_toexpire));

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });



    }

    private void getSliderData() {
        db.collection("productsSale")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                products.add(document.toObject(ProductSale.class));
                            }
                            sliderItems = products;
                            viewPager2.setAdapter(sliderAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
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
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                        //TapTarget.forView(findViewById(R.id.searchView), "You", "Up"),
                        TapTarget.forView(findViewById(R.id.searchView), "Search tool", "look for any product we have for you")
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
                                .targetRadius(100)
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



}