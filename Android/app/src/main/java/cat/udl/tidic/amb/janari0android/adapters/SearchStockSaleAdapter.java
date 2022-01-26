package cat.udl.tidic.amb.janari0android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cat.udl.tidic.amb.janari0android.ProductSale;
import cat.udl.tidic.amb.janari0android.R;

public class SearchStockSaleAdapter extends RecyclerView.Adapter<SearchStockSaleAdapter.SearchStockSaleViewHolder>{

    Context context;
    ArrayList<ProductSale> productsSale;
    OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        listener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    // method for filtering our recyclerview items.
    public void filterList(ArrayList<ProductSale> filterList) {
        productsSale = filterList;
    }
    public SearchStockSaleAdapter(Context ct, ArrayList<ProductSale> productsSale){
        context=ct;
        this.productsSale=productsSale;
    }
    @NonNull
    @Override
    public SearchStockSaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_product, parent, false);
        return new SearchStockSaleViewHolder(view, listener);
    }
    @Override
    public void onBindViewHolder(@NonNull SearchStockSaleViewHolder holder, int position) {
        ProductSale product = productsSale.get(position);
        if(product.getProduct().getPhotos().size()==0)
            holder.image.setImageResource(R.drawable.__2_burger_free_download_png);
        else {
            Glide.with(context)
                    .load(product.getProduct().getPhotos().get(0))
                    .into(holder.image);
        }
        holder.name.setText(product.getProduct().getName());
    }
    @Override
    public int getItemCount() {
        return productsSale.size();
    }
    public class SearchStockSaleViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;
        public SearchStockSaleViewHolder(@NonNull View itemView, final OnItemClickListener listener)
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
