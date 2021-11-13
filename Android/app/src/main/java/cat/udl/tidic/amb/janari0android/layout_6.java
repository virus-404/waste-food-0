package cat.udl.tidic.amb.janari0android;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class layout_6 extends AppCompatActivity {

    private ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_6);

        listView = findViewById(R.id.list_view);
        List<SetData> setData;
        setData = new ArrayList<>();
        setData.add(new SetData(getString(R.string.product_name),getString(R.string.produc_description)));

        CustomAdapter customAdapter = new CustomAdapter(this, R.layout.list_products, setData);
        listView.setAdapter(customAdapter);
    }
}
