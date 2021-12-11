package cat.udl.tidic.amb.janari0android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "bakedbeans";
    private Button changePassword;
    private ImageButton goBackButton;
    private TextView textName,textEmail,textPhoneNumber;
    private CircleImageView profilePicture;
    Button changeName,changeEmail,changePhoneNumber;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        changePassword = findViewById(R.id.changePasswordButton);
        goBackButton = findViewById(R.id.goBackButton);
        profilePicture = findViewById(R.id.profilePicture);
        changeName = findViewById(R.id.changeName);
        changeEmail = findViewById(R.id.changeEmail);
        changePhoneNumber = findViewById(R.id.changePhoneNumber);
        textName = findViewById(R.id.username);
        textEmail = findViewById(R.id.email);
        textPhoneNumber = findViewById(R.id.phoneNumber);
        textName.setText(user.getDisplayName());
        textEmail.setText(user.getEmail());
        getPhoneNumber();
        if(user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(profilePicture);
        }
        else
            profilePicture.setImageResource(R.drawable.login_icon);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this, ChangePasswordActivity.class));
            }
        });
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this, ChangeNameActivity.class));
            }
        });
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this, ChangeEmailActivity.class));
            }
        });
        changePhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this, ChangePhoneNumberActivity.class));
            }
        });
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this, UserProfileActivity.class));
            }
        });
    }

    private void getPhoneNumber() {
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        textPhoneNumber.setText(document.get("phoneNumber",String.class));
                    } else {
                        Log.d(TAG, "No phone number");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
