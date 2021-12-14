package cat.udl.tidic.amb.janari0android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class AddStockProductNameActivity extends AppCompatActivity {
    private static final String TAG = "bakedbeans";
    private ImageButton go_back;
    private TextInputEditText name;
    private Button next;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_stock_product_name);
        go_back = findViewById(R.id.goBackButton);
        name = findViewById(R.id.addProductName);
        next = findViewById(R.id.next);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddStockProductNameActivity.this, MainActivity.class);
                startActivity(intent);
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
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("users").document(user.getUid()).collection("products").whereEqualTo("name", Objects.requireNonNull(name.getText()).toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                                        String user = documentSnapshot.getString("username");
                                        Product product = documentSnapshot.toObject(Product.class);
                                        assert product != null;
                                        if(product.getName().equals(Objects.requireNonNull(name.getText()).toString())){
                                            Log.d(TAG, "Name exists");
                                            Toast.makeText(AddStockProductNameActivity.this, "Product name already exists", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                if(Objects.requireNonNull(task.getResult()).size() == 0 ){
                                    Log.d(TAG, "Name does not Exist");
                                    if(Objects.requireNonNull(name.getText()).toString().isEmpty())
                                        Toast.makeText(getApplicationContext(),"Name field can not be empty", Toast.LENGTH_SHORT).show();
                                    else if(name.getText().toString().length() < 3)
                                        Toast.makeText(getApplicationContext(),"Name must be longer than two letters", Toast.LENGTH_SHORT).show();
                                    else {
                                        Intent intent = new Intent(AddStockProductNameActivity.this, AddStockActivity.class);
                                        intent.putExtra("name", name.getText().toString());
                                        startActivity(intent);
                                    }
                                }
                            }
                });
            }
        });
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
