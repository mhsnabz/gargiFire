package com.rzn.gargi.topface.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.ModelUser;
import com.rzn.gargi.helper.Rate;
import com.rzn.gargi.profile.UserProfileActivity;
import com.rzn.gargi.topface.TopFaceActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManAdapter extends FirestoreRecyclerAdapter<ModelUser,ManAdapter.ViewHoder > {
    Dialog dialog;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Context context;
    public ManAdapter(@NonNull FirestoreRecyclerOptions<ModelUser> options, Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHoder holder, final int position, @NonNull final ModelUser model) {
        holder.getName(model.getName());
        holder.setImage(model.getThumb_image());
        holder.getClick(model.getClick());
        holder.getAge(model.getAge());
        holder.getBurc(model.getBurc());
        holder.getCityName(model.getLocation());
      //  holder.getCityName(model.getLat(),model.getLongLat());
        holder.calculateRate(model.getCount(),model.getTotalRate());
        int totalCount = getItemCount();
        TextView tv = holder.view.findViewById(R.id.number);
        tv.setText(String.valueOf(((position+1)))+".");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = getItem(position).getId();

                if (!userId.equals(auth.getUid())){
                    dialog.setContentView(R.layout.wait_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    holder.setClik(model.getClick(),userId);
                    Intent i = new Intent(context, UserProfileActivity.class);
                    i.putExtra("userId",userId);
                    i.putExtra("gender","MAN");
                    dialog.dismiss();
                    context.startActivity(i);


                }

            }
        });

    }

    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topface_single_layout, parent, false);
        dialog = new Dialog( parent.getContext());

        return new ManAdapter.ViewHoder(itemView);
    }

    public class ViewHoder extends RecyclerView.ViewHolder {
        View view ;
        public ViewHoder(@NonNull View itemView) {
            super(itemView);
            view =itemView;

        }
        public void getClick(long _click){
            TextView seen = (TextView)view.findViewById(R.id.seen);
            if (_click<=9999999)
            {
                seen.setText(Long.toString(_click));
            }
            else if (_click>=1000000 && _click<=999999999)
                seen.setText(Long.toString(_click/1000000)+"M");
        }
        public void getName(String _name){
            TextView name = (TextView)view.findViewById(R.id.name);
            name.setText(_name);
        }
        public void getCityName(GeoPoint _location) {
            TextView location = view.findViewById(R.id.locaiton);

            if (_location!=null){
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(_location.getLatitude() ,  _location.getLongitude(), 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses!=null){
                        String cityName = addresses.get(0).getAdminArea();
                        String city2 = addresses.get(0).getSubAdminArea();
                        String code = addresses.get(0).getCountryCode();

                        if (city2==null && cityName ==null && code == null){
                            // location.setVisibility(View.INVISIBLE);

                            location.setText(R.string.konum_bilgisi_yok );
                        }
                        else
                            location.setText(city2+"/"+cityName+"/"+code);
                    }else location.setText(R.string.konum_bilgisi_yok );




            }else location.setText(R.string.konum_bilgisi_yok );



        }
        public void setImage(String _image){
            CircleImageView image = (CircleImageView)view.findViewById(R.id.profileImage);
            final ProgressBar loading = (ProgressBar)view.findViewById(R.id.loading);

            if (!_image.isEmpty()&&_image!=null){
                Picasso.get().load(_image).config(Bitmap.Config.RGB_565)
                        .into(image, new Callback() {
                            @Override
                            public void onSuccess() {
                                loading.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            }
            else {
                image.setImageResource(R.drawable.upload_place_holder);
                loading.setVisibility(View.GONE);
            }
        }
        public void getAge(long age){
            TextView _age = (TextView)view.findViewById(R.id.age);

            Calendar currentDate = Calendar.getInstance();
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
            Long time= currentDate.getTime().getTime() / 1000 - age / 1000;
            int years = Math.round(time) / 31536000;
            int months = Math.round(time - years * 31536000) / 2628000;
            _age.setText(String.valueOf(years));
        }
        public void getBurc(String _burc){
            ImageView burc =(ImageView) view.findViewById(R.id.horoscope);
            if (_burc.equals("balik"))
                burc.setImageResource(R.drawable.balik);
            else if (_burc.equals("kova"))
                burc.setImageResource(R.drawable.kova);
            else if (_burc.equals("koc"))
                burc.setImageResource(R.drawable.koc);
            else if (_burc.equals("boga"))
                burc.setImageResource(R.drawable.boga);
            else if (_burc.equals("ikizler"))
                burc.setImageResource(R.drawable.ikizler);
            else if (_burc.equals("yengec"))
                burc.setImageResource(R.drawable.yengec);
            else if (_burc.equals("aslan"))
                burc.setImageResource(R.drawable.aslan);
            else if (_burc.equals("basak"))
                burc.setImageResource(R.drawable.aslan);
            else if (_burc.equals("terazi"))
                burc.setImageResource(R.drawable.terazi);
            else if (_burc.equals("akrep"))
                burc.setImageResource(R.drawable.akrep);
            else if (_burc.equals("yay"))
                burc.setImageResource(R.drawable.yay);
            else if (_burc.equals("oglak"))
                burc.setImageResource(R.drawable.oglak);
            else {
                if (_burc.isEmpty())
                    burc.setVisibility(View.GONE);
            }
        }
        private void calculateRate(long count , long totalRate){
            TextView point =(TextView)view.findViewById(R.id.point);

            double rating = (double) totalRate/count;
            rating=Math.round(rating*100.0)/100.0;
            point.setText(String.valueOf(rating));

        }

        public void setClik(final long clik, final String userId){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Map<String,Object> map = new HashMap<>();
            map.put("clik",1);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref = db.collection("MAN")
                    .document(userId)
                    .collection("view")
                    .document(auth.getUid());
            ref.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ubdateClik(clik,userId);
                }
                }
            });

        }
        private void ubdateClik(long click,String userId){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String,Object> map = new HashMap<>();
            map.put("click",click+1);
            db.document("MAN"+"/"+userId)
                    .set(map,SetOptions.merge());
        }
    }
}
