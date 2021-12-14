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
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.util.List;

import cat.udl.tidic.amb.janari0android.ProductSale;
import cat.udl.tidic.amb.janari0android.R;

public class SliderAdapter extends RecyclerView.Adapter <SliderAdapter.SliderViewHolder>{

    private List<ProductSale> sliderItems;
    private ViewPager2 viewPager2;
    private Context context;
    public SliderAdapter(List<ProductSale> sliderItems, ViewPager2 viewPager2, Context context) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
        this.context = context;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.row_new_slide_product,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        // if there is no image, put a default one
        if(sliderItems.get(position).getProduct().getPhotos().size()==0)
            holder.imageView.setImageResource(R.drawable.__2_burger_free_download_png);
        else {
            // Loading image with firebase
            Glide.with(context)
                    .load(sliderItems.get(position).getProduct().getPhotos().get(0))
                    .fitCenter()
                    .into(holder.imageView);
        }
        holder.setName(sliderItems.get(position));
        holder.setDescription(sliderItems.get(position));
        if (position == sliderItems.size() - 2){
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }


    class SliderViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView product_name;
        private TextView product_description;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_product);
            product_name = itemView.findViewById(R.id.name_product);
            product_description = itemView.findViewById(R.id.description_product);
        }

        void setName(ProductSale setDataSliderProducts){
            product_name.setText(setDataSliderProducts.getProduct().getName());
        }
        void setDescription(ProductSale setDataSliderProducts){
            product_description.setText(setDataSliderProducts.getDescription());
        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };
}
