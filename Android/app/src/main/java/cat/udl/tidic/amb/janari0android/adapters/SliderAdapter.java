package cat.udl.tidic.amb.janari0android.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import cat.udl.tidic.amb.janari0android.R;
import cat.udl.tidic.amb.janari0android.SetDataSliderProducts;

public class SliderAdapter extends RecyclerView.Adapter <SliderAdapter.SliderViewHolder>{

    private List<SetDataSliderProducts> sliderItems;
    private ViewPager2 viewPager2;

    public SliderAdapter(List<SetDataSliderProducts> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.new_slide_product,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(sliderItems.get(position));
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

        void setImage(SetDataSliderProducts setDataSliderProducts){
            imageView.setImageURI(Uri.parse(setDataSliderProducts.getImage()));
        }

        void setName(SetDataSliderProducts setDataSliderProducts){
            product_name.setText(setDataSliderProducts.getName_product());
        }
        void setDescription(SetDataSliderProducts setDataSliderProducts){
            product_description.setText(setDataSliderProducts.getDescription_product());
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
