package com.rzn.gargi.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.ybq.android.spinkit.SpinKitView;
import com.rzn.gargi.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class sliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> images;


    public sliderAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==(RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater =(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView profile_image =(ImageView)view.findViewById(R.id.profile_image);
        if (images.size()==0){
            profile_image.setImageResource(R.drawable.upload_place_holder);
        }
        final SpinKitView loading = (SpinKitView)view.findViewById(R.id.spinkit_loding);
        Log.d("instantiateItem", "instantiateItem: " + images.get(position));
        String img = String.valueOf(images.get(position));
        Picasso.get().load(images.get(position)).config(Bitmap.Config.RGB_565).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.upload_place_holder)
                .into(profile_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
