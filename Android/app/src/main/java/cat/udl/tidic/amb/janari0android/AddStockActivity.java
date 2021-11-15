package cat.udl.tidic.amb.janari0android;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddStockActivity extends AppCompatActivity {

    ImageButton go_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        go_back = findViewById(R.id.goBackButton);

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddStockActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

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

        final Calendar myCalendar = Calendar.getInstance();

        EditText edittext= (EditText) findViewById(R.id.addExpirationDate);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);

                edittext.setText(sdf.format(myCalendar.getTime()));
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddStockActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}
