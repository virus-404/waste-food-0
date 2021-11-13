package cat.udl.tidic.amb.janari0android;

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

public class CustomAdapter extends ArrayAdapter<SetData> {

    List<SetData> setData;
    int resource;
    Context context;

    CustomAdapter(Context context, int resource, List<SetData> setData){
        super(context, resource, setData);
        this.context = context;
        this.resource = resource;
        this.setData = setData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(resource, null, false);

        ImageView imageViewProduct = view.findViewById(R.id.image_product);
        TextView textViewName = view.findViewById(R.id.name_product);
        TextView textViewDescription = view.findViewById(R.id.description_product);
        final SetData setDataView = setData.get(position);

        textViewName.setText(setDataView.getName_product());
        textViewDescription.setText(setDataView.getDescription_product());
        return view;
    }
}
