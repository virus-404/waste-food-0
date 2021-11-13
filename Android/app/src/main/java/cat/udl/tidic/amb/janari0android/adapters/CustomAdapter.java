package cat.udl.tidic.amb.janari0android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cat.udl.tidic.amb.janari0android.R;
import cat.udl.tidic.amb.janari0android.SetDataListProducts;

public class CustomAdapter extends ArrayAdapter<SetDataListProducts> {

    List<SetDataListProducts> setDatumListProducts;
    int resource;
    Context context;

    public CustomAdapter(Context context, int resource, List<SetDataListProducts> setDatumListProducts){
        super(context, resource, setDatumListProducts);
        this.context = context;
        this.resource = resource;
        this.setDatumListProducts = setDatumListProducts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(resource, null, false);

        ImageView imageViewProduct = view.findViewById(R.id.image_product);
        TextView textViewName = view.findViewById(R.id.name_product);
        TextView textViewDescription = view.findViewById(R.id.description_product);
        final SetDataListProducts setDataListProductsView = setDatumListProducts.get(position);

        textViewName.setText(setDataListProductsView.getName_product());
        textViewDescription.setText(setDataListProductsView.getDescription_product());
        return view;
    }
}
