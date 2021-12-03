package cat.udl.tidic.amb.janari0android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.sql.Array;
import java.text.DateFormat;
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
    private TextInputEditText expirationDate;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private RecyclerView products;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ArrayList<String> images = new ArrayList<>();
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
        expirationDate = findViewById(R.id.addExpirationDate);
        addToStock = findViewById(R.id.addToStock);

        buildRecyclerView();

        Bundle extras = getIntent().getExtras();
        String name = "";
        if(extras!=null) {
            name = extras.getString("name");
        }
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
        String finalName = name;
        addToStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = null;
                try {
                    product = new Product(finalName, images, new SimpleDateFormat("dd MMM yyyy").parse(String.valueOf(expirationDate.getText())));
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(AddStockActivity.this, "Error adding product", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.collection("users").document(user.getUid()).collection("products").document(finalName)
                        .set(product)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                Toast.makeText(AddStockActivity.this, "Product successfully added", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                db.collection("products").add(product)
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
        // Date picker for expiration date
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
            private void updateLabel() {
                DateFormat fmt = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                expirationDate.setText(fmt.format(myCalendar.getTime()));
            }
        };
        expirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddStockActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        assert data != null;
                        Uri imageUri = data.getData();

                        StorageReference storageRef = storage.getReference();
                        StorageReference imageRef;
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        assert user != null;
                        imageRef = storageRef.child("images/" + user.getUid() + "/" +  imageUri.getLastPathSegment());
                        Bitmap bitmap = uriToBitmap(imageUri);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        assert bitmap != null;
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                        byte[] imageData = baos.toByteArray();

                        UploadTask uploadTask = imageRef.putBytes(imageData);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(AddStockActivity.this, "Unsuccessful upload", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> urlTask = imageRef.getDownloadUrl();
                                urlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downloadUri = task.getResult();
                                            images.add(String.valueOf(downloadUri));
                                            File f = new File(String.valueOf(imageUri));
                                            imageInfo.add(f.getName());
                                            products.setAdapter(addStockAdapter);
                                            Toast.makeText(AddStockActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(AddStockActivity.this, "Something's wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });

                    }
                    else {
                        Log.d(TAG, "Cancelled");
                    }
                }
            }
    );
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
