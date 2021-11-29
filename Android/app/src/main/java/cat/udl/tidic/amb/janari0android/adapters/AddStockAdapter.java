package cat.udl.tidic.amb.janari0android.adapters;
import android.content.Context;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import cat.udl.tidic.amb.janari0android.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AddStockAdapter extends RecyclerView.Adapter<AddStockAdapter.AddStockViewHolder>{

    Context context;
    ArrayList<String> productImages = new ArrayList<>();
    ArrayList<String> productImageInfo = new ArrayList<>();
    ImageView deleteImage;
    OnItemClickListener Listener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        Listener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }
    public AddStockAdapter(Context ct, ArrayList<String> images, ArrayList<String> productimginfo, ImageView deleteImg){
        context=ct;
        productImages = images;
        productImageInfo = productimginfo;
        deleteImage = deleteImg;
    }
    @NonNull
    @Override
    public AddStockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_add_stock,parent,false);
        return new AddStockViewHolder(view, Listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AddStockViewHolder holder, int position) {
        Glide.with(context)
                .load(productImages.get(position))
                .into(holder.image);
        holder.imageInfo.setText(productImageInfo.get(position));
        holder.delete = deleteImage;
    }

    @Override
    public int getItemCount() {
        return productImages.size();
    }

    public class AddStockViewHolder extends RecyclerView.ViewHolder{

        ImageView image, delete;
        TextView imageInfo;
        public AddStockViewHolder(@NonNull View itemView, final OnItemClickListener listener)
        {
            super(itemView);
            image = itemView.findViewById(R.id.productInfoImage);
            imageInfo = itemView.findViewById(R.id.productInfoImageName);
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
