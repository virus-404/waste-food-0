package cat.udl.tidic.amb.janari0android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class ChangeNameActivity extends AppCompatActivity {
    private static final String TAG = "bakedbeans";
    TextInputEditText username;
    ImageButton goBack;
    Button confirm;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        username = findViewById(R.id.editTextUsername);
        confirm = findViewById(R.id.confirmButton);
        goBack = findViewById(R.id.goBackButton);
        if(user.getDisplayName()!=null)
            username.setText(user.getDisplayName());
        username.requestFocus();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query userNameQuery = db.collection("users").whereEqualTo("username", Objects.requireNonNull(username.getText()).toString());
                userNameQuery.limit(1).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    boolean isEmpty = task.getResult().isEmpty();
                                    if (isEmpty) {
                                        setDisplayName();
                                        finish();
                                    }
                                    else
                                        Toast.makeText(ChangeNameActivity.this, getResources().getString(R.string.UsernameAlreadyExists), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        goBack.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void setDisplayName()
    {
        String userName = String.valueOf(username.getText());
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(String.valueOf(username.getText()))
                .build();
        user.updateProfile(profileUpdates);

        User userDB = new User(userName);
        db.collection("users").document(user.getUid())
                .set(userDB)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(ChangeNameActivity.this, getResources().getString(R.string.Successfullychanged), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}
