package cat.udl.tidic.amb.janari0android.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import cat.udl.tidic.amb.janari0android.Product;
import cat.udl.tidic.amb.janari0android.R;

public class SearchStockAdapter extends RecyclerView.Adapter<SearchStockAdapter.SearchStockViewHolder>{

    Context context;
    ArrayList<Product> products;
    OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        listener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    // method for filtering our recyclerview items.
    public void filterList(ArrayList<Product> filterList) {
        products = filterList;
    }
    public SearchStockAdapter(Context ct, ArrayList<Product> products){
        context=ct;
        this.products=products;
    }
    @NonNull
    @Override
    public SearchStockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_product, parent, false);
        return new SearchStockViewHolder(view, listener);
    }
    @Override
    public void onBindViewHolder(@NonNull SearchStockViewHolder holder, int position) {
        Product product = products.get(position);
        if(product.getPhotos().size()==0)
            holder.image.setImageResource(R.drawable.__2_burger_free_download_png);
        else {
            Glide.with(context)
                    .load(product.getPhotos().get(0))
                    .into(holder.image);
        }
        holder.name.setText(product.getName());
    }
    @Override
    public int getItemCount() {
        return products.size();
    }
    public class SearchStockViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;
        public SearchStockViewHolder(@NonNull View itemView, final OnItemClickListener listener)
        {
            super(itemView);
            image = itemView.findViewById(R.id.rowProductImage);
            name = itemView.findViewById(R.id.rowProductName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
