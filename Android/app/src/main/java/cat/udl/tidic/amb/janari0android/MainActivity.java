package cat.udl.tidic.amb.janari0android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cat.udl.tidic.amb.janari0android.adapters.SliderAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "bakedbeans";
    private FirebaseAuth auth;

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    //Button mCaptureBtn;
    private ImageButton mCaptureBtn;
    private ImageView mImageView;
    private FloatingActionButton open, give, add, sell;
    private Button list, profile;
    private boolean visibleFloatingButton = false;
    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();

    Uri image_uri;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
        profile = findViewById(R.id.toolbarUserMenuButton);
        viewPager2 = findViewById(R.id.viewpager2_layout2);


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
        //mImageView = findViewById(R.id.image_view);
        //mCaptureBtn = findViewById(R.id.capture_image_btn);

        //button click
       /*
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if system os is >= marshmallow, request runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED){
                        //permission not enabled, request it
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup to request permissions
                        requestPermissions(permission, PERMISSION_CODE);
                    }
                    else {
                        //permission already granted
                        openCamera();
                    }
                }
                else {
                    //system os < marshmallow
                    openCamera();
                }
            }
        });
        */


        //Slider de imatges
        //viewPager2 = findViewById(R.id.viewpager2_layout2);

        //We pass images list, we will have to take them from the API
        //By now i put them manually
        List<SetDataSliderProducts> sliderItems = new ArrayList<>();
        /*sliderItems.add(new SetDataSliderProducts(R.drawable.__2_burger_free_download_png, "Hamburguesa con queso", "Burger rebuena"));
        sliderItems.add(new SetDataSliderProducts(R.drawable.__2_burger_png_file, "Lasañita rica", "En buen estado"));
        sliderItems.add(new SetDataSliderProducts(R.drawable.dollar, "Dolarsito", "Money pa todos"));*/


        viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2));

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
                int num_products = 0;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG,document.getId() + " => " + document.getData());
                        Product p = document.toObject(Product.class);
                        Calendar cprod = Calendar.getInstance();
                        cprod.setTime(p.expirationDate);
                        if (cprod.compareTo(c) <= 0){
                            num_products ++;
                        }
                    }
                    //QuerySnapshot t = ;
                    //int number_products = task.getResult().getDocumentChanges().size();
                    list.setText(String.valueOf(num_products));
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

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is called, when user presses Allow or Deny from Permission Request Popup
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //permission from popup was granted
                    openCamera();
                } else {
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //called when image was captured from camera

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //set the image captured to our ImageView
            mImageView.setImageURI(image_uri);
        }
    }
}