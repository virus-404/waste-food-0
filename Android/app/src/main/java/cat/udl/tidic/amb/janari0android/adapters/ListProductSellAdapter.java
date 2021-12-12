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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import cat.udl.tidic.amb.janari0android.Product;
import cat.udl.tidic.amb.janari0android.ProductSale;
import cat.udl.tidic.amb.janari0android.R;

public class ListProductSellAdapter extends RecyclerView.Adapter<ListProductSellAdapter.ListProductSellViewHolder>{

    Context context;
    ArrayList<ProductSale> products = new ArrayList<>() ;

    public ListProductSellAdapter(ArrayList<ProductSale> products, Context context){
        this.products = products;
        this.context = context;

    }
    @NonNull
    @Override
    public ListProductSellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_sell,parent,false);

        return new ListProductSellViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListProductSellAdapter.ListProductSellViewHolder holder, int position) {
        ProductSale product = products.get(position);
        if(product.getProduct().getPhotos().size()==0)
            holder.image.setImageResource(R.drawable.__2_burger_free_download_png);
        else {
            Glide.with(context)
                    .load(product.getProduct().getPhotos().get(0))
                    .into(holder.image);
        }
        holder.name.setText(product.getProduct().getName());
        DateFormat fmt = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        //context.getString(R.string.ExpDateListProd);
        holder.expDate.setText(context.getString(R.string.ExpDateListProd) +" "+fmt.format(product.getProduct().getExpirationDate()));
        holder.price.setText(context.getString(R.string.PriceListProd) +" "+product.getPrice() + "€");

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ListProductSellViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, expDate, price, description;
        public ListProductSellViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_product);
            name = itemView.findViewById(R.id.name_product);
            expDate = itemView.findViewById(R.id.expirationDate);
            price = itemView.findViewById(R.id.PriceProd);
        }


    }
}
