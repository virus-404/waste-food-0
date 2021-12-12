package cat.udl.tidic.amb.janari0android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private ImageButton goBack;
    private TextInputEditText currentPassword, newPassword, reNewPassword;
    private Button confirmChangePassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        goBack = findViewById(R.id.goBackButton);
        currentPassword=findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassEditText);
        reNewPassword = findViewById(R.id.repPassEditText);
        confirmChangePassword = findViewById(R.id.changePasswordConfirmButton);
        currentPassword.requestFocus();
        showKeyboard();
        String pleaseFillAllFields = getString(R.string.pleaseFillAllFields);
        String passwordsDontMatch = getString(R.string.passwordsDontMatch);
        String passwordShouldBe = getString(R.string.passwordShouldBe);

        goBack.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                Intent intent = new Intent(ChangePasswordActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
        currentPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        newPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        reNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        confirmChangePassword.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String pas = String.valueOf(newPassword.getText());
                String pasR = String.valueOf(reNewPassword.getText());
                // Checking new password requirements
                if (pas.isEmpty() || pasR.isEmpty())
                    Toast.makeText(getApplicationContext(),
                            pleaseFillAllFields, Toast.LENGTH_LONG).show();
                else if (!pas.equals(pasR))
                    Toast.makeText(getApplicationContext(),
                            passwordsDontMatch, Toast.LENGTH_LONG).show();
                else if (pas.length() < 6)
                    Toast.makeText(getApplicationContext(),
                            passwordShouldBe, Toast.LENGTH_LONG).show();
                else
                    updatePassword();

                Intent intent = new Intent(ChangePasswordActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updatePassword() {
        String passwordUpdated = getString(R.string.passwordUpdated);
        String wrongPassword = getString(R.string.wrongPassword);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Verifying if current password is authenticated
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), String.valueOf(currentPassword.getText()));
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // if the user input matches the current password, update password to new password
                if(task.isSuccessful()){
                    user.updatePassword(String.valueOf(newPassword.getText()));
                    Toast.makeText(ChangePasswordActivity.this, passwordUpdated,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, wrongPassword,
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
