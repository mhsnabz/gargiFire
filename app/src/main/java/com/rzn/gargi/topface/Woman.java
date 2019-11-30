package com.rzn.gargi.topface;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.ModelUser;
import com.rzn.gargi.profile.UserProfileActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.rzn.gargi.profile.ProfileActivity.ALLOWED_CHARACTERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class Woman extends Fragment {

    RecyclerView woman;
    View rootView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionMenu menu;
    String currentUser;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FloatingActionButton orderByViews,mix,orderByTopFace;
    ProgressBar progres;
    Dialog dialog;
    TextView discp;
    FirestorePagingAdapter<ModelUser , Woman.ViewHolder> adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("WOMAN");
    public Woman() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =inflater.inflate(R.layout.fragment_woman, container, false);
        menu=(FloatingActionMenu)rootView.findViewById(R.id.menu);
        discp =(TextView)rootView.findViewById(R.id.discp);
        dialog=new Dialog(getContext());
        woman = rootView.findViewById(R.id.manUsers);
        woman.setHasFixedSize(true);
        woman.setLayoutManager(new LinearLayoutManager(getContext()));
        orderByTopFace = (FloatingActionButton)rootView.findViewById(R.id.topface);
        orderByViews = (FloatingActionButton)rootView.findViewById(R.id.views);
        mix = (FloatingActionButton)rootView.findViewById(R.id.mix);
        orderByTopFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close(true);
                adapter.stopListening();
                discp.setText(R.string.en_cok_puan_alan_kullanicilar);
                Query query = ref.orderBy("rate",Query.Direction.DESCENDING);

                ListPagin(query);
                adapter.startListening();

            }
        });
        orderByViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.stopListening();
                discp.setText(R.string.en_populer);
                Query query = ref.orderBy("click",Query.Direction.DESCENDING);

                menu.close(true);
                ListPagin(query);
                adapter.startListening();

            }
        });
        discp.setText(R.string.en_populer);
        Query query = ref.orderBy("click",Query.Direction.DESCENDING);
        ListPagin(query);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
        adapter.stopListening();

    }

    private void ListPagin(Query query){
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();
        FirestorePagingOptions<ModelUser > options = new FirestorePagingOptions.Builder<ModelUser>()
                .setLifecycleOwner(getActivity())
                .setQuery(query,config,ModelUser.class).build();
        adapter = new FirestorePagingAdapter<ModelUser, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final ModelUser model) {
                holder.getName(model.getName());
                holder.setImage(model.getThumb_image());
                holder.getClick(model.getClick());
                holder.getAge(model.getAge());
                holder.getBurc(model.getBurc());
                holder.getCityName(model.getCityName());
                holder.calculateRate(model.getCount(),model.getTotalRate());
                int totalCount = getItemCount();
                holder.setNameSplit(model.getName());
                TextView tv = holder.view.findViewById(R.id.number);
                tv.setText(String.valueOf(((position+1)))+".");
                Log.d("userId", "onBindViewHolder: "+getItem(position).getId());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String userId = getItem(position).getId();

                        if (!userId.equals(auth.getUid())){

                            dialog.setContentView(R.layout.wait_dialog);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                            holder.setClik(model.getClick(),userId);
                            db.collection("allUser")
                                    .document(auth.getUid()).get().addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        Intent i = new Intent(getActivity(), UserProfileActivity.class);
                                        i.putExtra("userId",userId);
                                        i.putExtra("gender","WOMAN");
                                        String gender = task.getResult().getString("gender");
                                        i.putExtra("myGender",gender);
                                        dialog.dismiss();
                                        getActivity().startActivity(i);
                                    }
                                }
                            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Crashlytics.logException(e);
                                }
                            });

                        }

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.topface_single_layout, parent, false);


                return new Woman.ViewHolder(itemView);
            }
        };

        woman.setAdapter(adapter);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        View view ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;

        }

        TextView seen = (TextView)itemView.findViewById(R.id.seen);
        TextView name = (TextView)itemView.findViewById(R.id.name);

        public void getClick(long _click){
            if (_click<=9999999)
            {
                seen.setText(Long.toString(_click));
            }
            else if (_click>=1000000 && _click<=999999999)
                seen.setText(Long.toString(_click/1000000)+"M");
        }
        public void getName(String _name){
            name.setText(_name);
        }
        public void setNameSplit(String _name){

            String currentString =_name;
            String[] separated = currentString.split(" ");

            name.setText(separated[0]); // this will contain "Fruit"

        }

        public void getCityName(String cityName) {
            TextView location =(TextView)view.findViewById(R.id.userLocation);
            if (cityName!=null){
                location.setText(cityName);
            }

        }

        public void setImage(String _image){
            CircleImageView image = (CircleImageView)view.findViewById(R.id.profileImage);
            final ProgressBar loading = (ProgressBar)view.findViewById(R.id.loading);
            Log.d("image", "setImage: "+_image);
          if (!_image.isEmpty()&&_image!=null){
                Picasso.get().load(_image).config(Bitmap.Config.RGB_565).placeholder(R.drawable.upload_place_holder)
                        .into(image, new Callback() {
                            @Override
                            public void onSuccess() {
                                loading.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            }else {
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
        private  String getRandomString(final int sizeOfRandomString){
            final Random random=new Random();
            final StringBuilder sb=new StringBuilder(sizeOfRandomString);
            for(int i=0;i<sizeOfRandomString;++i)
                sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
            Log.d("randomStrign", "getRandomString: "+sb.toString());

            return sb.toString();
        }
        public void setClik(final long clik, final String userId){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Map<String,Object> map = new HashMap<>();
            map.put(getRandomString(10),1);
            map.put("time", FieldValue.serverTimestamp());
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref = db.collection("WOMAN")
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
            db.document("WOMAN"+"/"+userId)
                    .set(map,SetOptions.merge());
        }
    }

}
