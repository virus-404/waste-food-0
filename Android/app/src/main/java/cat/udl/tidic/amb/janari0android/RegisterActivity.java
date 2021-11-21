package cat.udl.tidic.amb.janari0android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    protected EditText username, email, password, passwordRep;
    protected Button register;
    protected ImageButton goBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        auth = FirebaseAuth.getInstance();
        goBack = findViewById(R.id.goBackButton);
        username = findViewById(R.id.usernameEditText);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        passwordRep = findViewById(R.id.repeatPasswordEditText);
        register = findViewById(R.id.registerFormButton);

        register.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                String us = username.getText().toString();
                String em = email.getText().toString();
                String pas = password.getText().toString();
                String pasR = passwordRep.getText().toString();

                if (us.isEmpty() || em.isEmpty() || pas.isEmpty() || pasR.isEmpty()){
                    Toast.makeText(getApplicationContext(),
                            "Please fill all the fields", Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(em))
                    Toast.makeText(getApplicationContext(),
                            "Invalid mail", Toast.LENGTH_LONG).show();
                else if (isStringBlank(us))
                    Toast.makeText(getApplicationContext(),
                            "Invalid username", Toast.LENGTH_LONG).show();
                else if (!pas.equals(pasR))
                    Toast.makeText(getApplicationContext(),
                            "Passwords doesn't match", Toast.LENGTH_LONG).show();
                else if (pas.length() < 5)
                    Toast.makeText(getApplicationContext(),
                            "Password should be at least 6 characters", Toast.LENGTH_LONG).show();
                else
                    createAccount(em, pas);
            }
        });
        goBack.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        passwordRep.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isStringBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void createAccount(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            setDisplayName();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });


    }
    private void setDisplayName()
    {
        FirebaseUser user = auth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(String.valueOf(username.getText()))
                .build();
        user.updateProfile(profileUpdates);
    }
    private void updateUI(FirebaseUser user) {
        if (user != null){
            Intent intent = new Intent(this , LoginActivity.class);
            startActivity(intent);
        }

    }
}
