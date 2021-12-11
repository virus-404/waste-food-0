package cat.udl.tidic.amb.janari0android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private Button changePassword;
    private ImageButton goBackButton;
    private TextView textName,textEmail,textPhoneNumber;
    private CircleImageView profilePicture;
    Button changeName,changeEmail,changePhoneNumber;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this, ChangeNameActivity.class));
            }
        });
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this, UserProfileActivity.class));
            }
        });
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this, ChangeNameActivity.class));
            }
        });
    }
}
