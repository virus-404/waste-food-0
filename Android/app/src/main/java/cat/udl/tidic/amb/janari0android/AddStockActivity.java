package cat.udl.tidic.amb.janari0android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cat.udl.tidic.amb.janari0android.adapters.AddStockAdapter;

public class AddStockActivity extends AppCompatActivity {

    private static final String TAG = "bakedbeans";
    private ImageButton go_back;
    private ImageView productPicture, productInfoDelete;
    private Button addPhoto,gallery,camera, addToStock;
    private TextInputEditText name, expirationDate;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView products;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> listProducts = new ArrayList<>();
    ArrayList<String> imageInfo = new ArrayList<>();
    AddStockAdapter addStockAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);
        go_back = findViewById(R.id.goBackButton);
        addPhoto = findViewById(R.id.addPhoto);
        gallery = findViewById(R.id.addGallery);
        camera = findViewById(R.id.addCamera);
        name = findViewById(R.id.addProductName);
        expirationDate = findViewById(R.id.addExpirationDate);
        addToStock = findViewById(R.id.addToStock);

        buildRecyclerView();

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddStockActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent intent = new Intent(Intent.ACTION_PICK);
                //set type
                intent.setType("image/*");
                galleryActivityResultLauncher.launch(intent);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivity(intent);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        addToStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> product = new HashMap<>();
                String nameProduct = String.valueOf(name.getText());
                product.put("name", nameProduct);
                try {
                    product.put("expirationDate",new SimpleDateFormat("dd/MM/yy").parse(String.valueOf(expirationDate.getText())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //product.put("expirationDate", String.valueOf(expirationDate.getText()));
                product.put("images", images);
                db.collection("products").document(nameProduct)
                        .set(product)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
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

        // Date picker for expiration date
        final Calendar myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);

                expirationDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        expirationDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddStockActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }
    public void buildRecyclerView() {
        products = findViewById(R.id.photosViewer);
        addStockAdapter = new AddStockAdapter(this,images,imageInfo, productInfoDelete);
        products.setLayoutManager(new LinearLayoutManager(this));
        products.setAdapter(addStockAdapter);

        addStockAdapter.setOnItemClickListener(new AddStockAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });

    }
    public void removeItem(int position) {
        images.remove(position);
        imageInfo.remove(position);
        addStockAdapter.notifyItemRemoved(position);
    }
    private void ToggleButtons(View v) {
        Button gallery = (Button) findViewById(R.id.addGallery);
        Button camera = (Button) findViewById(R.id.addCamera);
        if(gallery.getVisibility()==View.INVISIBLE) {
            gallery.setVisibility(View.VISIBLE);
            camera.setVisibility(View.VISIBLE);
        }
        else{
            gallery.setVisibility(View.INVISIBLE);
            camera.setVisibility(View.INVISIBLE);
        }
    }
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here we will handle the result of our intent
                    if (result.getResultCode() == Activity.RESULT_OK){
                        //image picked
                        //get uri of image
                        Intent data = result.getData();
                        Uri imageUri = data.getData();

                        images.add(imageUri.toString());
                        Toast.makeText(AddStockActivity.this, "Added", Toast.LENGTH_SHORT).show();
                        // Make picture visible to user
                        File f = new File(String.valueOf(imageUri));
                        imageInfo.add(f.getName());


                        products.setAdapter(addStockAdapter);
                        /*View productView = getLayoutInflater().inflate(R.layout.row_add_product_info,null,false);
                        products.addView(productView);
                        productPicture = new ImageView(AddStockActivity.this);
                        productPicture.setId(View.generateViewId());
                        productPicture.setLayoutParams(new ConstraintLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        productPicture.setImageURI(imageUri);
                        products.addView(productPicture);*/

                    }
                    else {
                        //cancelled
                        Toast.makeText(AddStockActivity.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
