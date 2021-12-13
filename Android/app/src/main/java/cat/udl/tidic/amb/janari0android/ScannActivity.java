package cat.udl.tidic.amb.janari0android;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
//import cat.udl.tidic.amb.janari0android.adapters;

public class ScannActivity extends AppCompatActivity {
    private Button buttonScan;
    private ImageButton backButton;
    private TextView Resultado;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        buttonScan = findViewById(R.id.ScanBtn);
        Resultado = findViewById(R.id.texScan);
        backButton = findViewById(R.id.goBackButton);

        buttonScan.setOnClickListener(mOnClickListener);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScannActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null)
            if(result.getContents() != null){
                Resultado.setText(result.getContents());
            }else{
                Resultado.setText("error");
            }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ScanBtn:
                    IntentIntegrator integrador = new IntentIntegrator(ScannActivity.this);
                    integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                    integrador.setPrompt("lector");
                    integrador.setCameraId(0);
                    integrador.setBeepEnabled(true);
                    integrador.setBarcodeImageEnabled(true);
                    integrador.initiateScan();


                    break;
            }

        }
    };



}


