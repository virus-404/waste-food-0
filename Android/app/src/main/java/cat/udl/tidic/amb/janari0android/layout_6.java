package cat.udl.tidic.amb.janari0android;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import cat.udl.tidic.amb.janari0android.adapters.CustomAdapter;
import cat.udl.tidic.amb.janari0android.adapters.SliderAdapter;

public class layout_6 extends AppCompatActivity {

    private ListView listView;
    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_6);

        //Products list
        //
        listView = findViewById(R.id.list_view);
        List<SetDataListProducts> setDatumListProducts;
        setDatumListProducts = new ArrayList<>();

        //We will have to take this values from the API
        //By now i put them manually
        setDatumListProducts.add(new SetDataListProducts(getString(R.string.productName),getString(R.string.productDescription)));
        setDatumListProducts.add(new SetDataListProducts("Cheese","OK"));
        setDatumListProducts.add(new SetDataListProducts("Bread","Nice"));
        setDatumListProducts.add(new SetDataListProducts("Burguer","Good"));

        CustomAdapter customAdapter = new CustomAdapter(this, R.layout.activity_list_products, setDatumListProducts);
        listView.setAdapter(customAdapter);

        //Slider de imatges
        //
        viewPager2 = findViewById(R.id.viewPagerImageSlider);

        //We pass images list, we will have to take them from the API
        //By now i put them manually
        List<SetDataSliderProducts> sliderItems = new ArrayList<>();
 /*       sliderItems.add(new SetDataSliderProducts(R.drawable.__2_burger_free_download_png, "Hamburguesa con queso", "Burger rebuena"));
        sliderItems.add(new SetDataSliderProducts(R.drawable.__2_burger_png_file, "Lasañita rica", "En buen estado"));
*/

        viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2));

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        //Images slides every 3 seconds
        // Altouht it, the user still can slide images at his own
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000); //Slide duration
            }
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}
