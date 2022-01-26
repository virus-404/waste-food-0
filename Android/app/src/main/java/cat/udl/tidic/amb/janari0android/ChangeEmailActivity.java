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

public class ChangeEmailActivity extends AppCompatActivity {
    private static final String TAG = "bakedbeans";
    TextInputEditText email;
    ImageButton goBack;
    Button confirm;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        email = findViewById(R.id.editTextUsername);
        confirm = findViewById(R.id.confirmButton);
        goBack = findViewById(R.id.goBackButton);
        if(user.getDisplayName()!=null)
            email.setText(user.getEmail());
        email.requestFocus();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEmail(String.valueOf(email.getText())))
                    setEmail();
                else
                    Toast.makeText(ChangeEmailActivity.this, getResources().getString(R.string.EmailAddressNotV), Toast.LENGTH_SHORT).show();
            }
        });
        goBack.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    private void setEmail()
    {
        auth.fetchSignInMethodsForEmail(String.valueOf(email.getText()))
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                        if (isNewUser) {
                            Log.e("TAG", "Is New User!");
                            user.updateEmail(String.valueOf(email.getText()))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User email address updated.");
                                                Toast.makeText(ChangeEmailActivity.this, getResources().getString(R.string.EmailAddressUpdated), Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });
                        } else {
                            Log.e("TAG", "Is Old User!");
                            Toast.makeText(ChangeEmailActivity.this, getResources().getString(R.string.emaialreadyexists), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
