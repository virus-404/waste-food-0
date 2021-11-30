package cat.udl.tidic.amb.janari0android.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cat.udl.tidic.amb.janari0android.Product;
import cat.udl.tidic.amb.janari0android.R;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ListProductViewHolder>{

    Context context;
    ArrayList<Product> products = new ArrayList<>() ;

    public ListProductAdapter(ArrayList<Product> products, Context context){
        this.products = products;
        this.context = context;

    }
    @NonNull
    @Override
    public ListProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_list_products,parent,false);

        return new ListProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListProductAdapter.ListProductViewHolder holder, int position) {
        Product product = products.get(position);
        if(product.getPhotos().size()==0)
            holder.image.setImageResource(R.drawable.__2_burger_free_download_png);
        else {
            Glide.with(context)
                    .load(product.getPhotos().get(0))
                    .into(holder.image);
        }
        holder.name.setText(product.getName());
        holder.expDate.setText(product.getExpirationDate().toString());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ListProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, expDate;
        public ListProductViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_product);
            name = itemView.findViewById(R.id.name_product);
            expDate = itemView.findViewById(R.id.expirationDate);
        }


    }
}
