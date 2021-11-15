package cat.udl.tidic.amb.janari0android;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddStockActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        final Button addPhoto = findViewById(R.id.addPhoto);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
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
        });
    }
}
