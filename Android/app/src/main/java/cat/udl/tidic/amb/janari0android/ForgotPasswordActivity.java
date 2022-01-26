package cat.udl.tidic.amb.janari0android;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "bakedbeans";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ImageButton goBack;
    Button sendEmail;
    TextInputEditText emailText;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        goBack = findViewById(R.id.goBackButton);
        sendEmail = findViewById(R.id.forgotPasswordButton);
        emailText = findViewById(R.id.emailEditText);
        sendEmail.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String email = Objects.requireNonNull(emailText.getText()).toString();
                if(isValidEmail(email)){
                    auth.fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                    if (isNewUser) {
                                        Log.e("TAG", "Is New User!");
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.UserWithprovidedemaildoesntexist), Toast.LENGTH_SHORT).show();
                                    } else {
                                        auth.sendPasswordResetEmail(email)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "Email sent.");
                                                        }
                                                    }
                                                });
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.EmailSent), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                }
                            });
                }
                else
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.NotValidEmail), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
