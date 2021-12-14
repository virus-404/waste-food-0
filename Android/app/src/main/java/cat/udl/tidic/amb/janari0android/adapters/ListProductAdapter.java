package cat.udl.tidic.amb.janari0android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import cat.udl.tidic.amb.janari0android.Product;
import cat.udl.tidic.amb.janari0android.R;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ListProductViewHolder>{

    Context context;
    ArrayList<Product> products = new ArrayList<>() ;
    OnItemClickListener Listener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        Listener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }
    public ListProductAdapter(ArrayList<Product> products, Context context){
        this.products = products;
        this.context = context;
    }
    @NonNull
    @Override
    public ListProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product,parent,false);

        return new ListProductViewHolder(view, Listener);
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
        DateFormat fmt = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        holder.expDate.setText(context.getString(R.string.ExpDateListProd) + " " + fmt.format(product.getExpirationDate()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ListProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image, delete;
        TextView name, expDate;
        public ListProductViewHolder(@NonNull View itemView, final ListProductAdapter.OnItemClickListener listener) {
            super(itemView);
            image = itemView.findViewById(R.id.image_product);
            name = itemView.findViewById(R.id.name_product);
            expDate = itemView.findViewById(R.id.expirationDate);
            delete = itemView.findViewById(R.id.productInfoDelete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }


    }
}
