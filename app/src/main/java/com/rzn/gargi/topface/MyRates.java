package com.rzn.gargi.topface;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.rzn.gargi.R;
import com.rzn.gargi.profile.UserProfileActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.rzn.gargi.profile.ProfileActivity.ALLOWED_CHARACTERS;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyRates extends Fragment {
    View rootView;
    TextView myRates;
    RecyclerView list;
    Dialog dialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<String> users;
    VotersAdapter adapter;
    LinearLayoutManager  layoutManager;
    static String admin ="ml20r64rnmXBpPHNpO8tbSW5Y8v1";

    public MyRates() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_rates, container, false);
        users=new ArrayList<>();
        myRates=(TextView)rootView.findViewById(R.id.ratesCount);
        layoutManager = new LinearLayoutManager(getContext());
        list=(RecyclerView)rootView.findViewById(R.id.list);
        list.setHasFixedSize(true);
        list.setLayoutManager(layoutManager);
        adapter=new VotersAdapter(users);
        list.setAdapter(adapter);
        dialog=new Dialog(getContext());
        getMyVoterId();
        return rootView;
    }

    private void getMyRateCount(){
        db.collection("rate")
                .document(auth.getUid())
                .collection(auth.getUid()).get().addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult()!=null){
                        long size = task.getResult().getDocuments().size();
                        Log.d("rateSize", "onComplete: "+size);
                        myRates.setText(size+" " +getResources().getString(R.string.kullanici_sana_oy_verdi));
                    }
                }
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Crashlytics.logException(e);
            }
        });
    }
    private void getMyVoterId()
    {


        db.collection("rate")
                .document(auth.getUid())
                .collection(auth.getUid())
                .get().addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot dc : task.getResult().getDocuments()){
                        users.add(dc.getId());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            Crashlytics.logException(e);
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        getMyRateCount();

    }

    private class VotersAdapter extends RecyclerView.Adapter<MyRates.ViewHolder>{

        ArrayList<String> userId;

        public VotersAdapter(ArrayList<String> userId)
        {

            this.userId = userId;
        }

        @NonNull
        @Override
        public MyRates.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_rate, parent, false);
            return new MyRates.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyRates.ViewHolder holder, final int i) {
            holder.loadUsers(userId.get(i));
            holder.getRate(userId.get(i));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        dialog.setContentView(R.layout.wait_dialog);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        holder.setClick(userId.get(i));
                    }

            });
        }

        @Override
        public int getItemCount() {
            return userId.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        TextView name =(TextView)itemView.findViewById(R.id.name);
        TextView ratesText =(TextView)itemView.findViewById(R.id.ratesText);
        CircleImageView image =(CircleImageView)itemView.findViewById(R.id.profileImage);
        ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.loading);
        public void loadUsers(final String id){
            db.collection("allUser")
                    .document(id)
                    .get().addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                 if (task.isSuccessful()){
                     if (task.getResult().getString("thumb_image")!=null&&!task.getResult().getString("thumb_image").isEmpty()){
                         Picasso.get().load(task.getResult().getString("thumb_image"))

                                 .into(image, new Callback() {
                                     @Override
                                     public void onSuccess() {
                                         progressBar.setVisibility(View.GONE);
                                     }

                                     @Override
                                     public void onError(Exception e) {

                                     }
                                 });
                     }else progressBar.setVisibility(View.GONE);
                     if (task.getResult().getString("name")!=null){
                         String currentString =task.getResult().getString("name");
                         String[] separated = currentString.split(" ");

                         name.setText(separated[0]);
                     }


                 }
                }
            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Crashlytics.logException(e);
                }
            });
        }
        public void getRate(String id){
            db.collection("rate")
                    .document(auth.getUid())
                    .collection(auth.getUid())
                    .document(id).get().addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        long rate = task.getResult().getLong("rate");


                        ratesText.setText(getResources().getString(R.string.size)+" "+rate+" "+getResources().getString(R.string.puan_verdi));
                    }
                }
            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                        Crashlytics.logException(e);
                 }
            });
        }
        public void setClick(final String userId)
        {
            db.collection("allUser")
                    .document(userId)
                    .get().addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult()!=null){
                            final String gender = task.getResult().getString("gender");
                            db.collection(gender)
                                    .document(userId)
                                    .get().addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult()!=null){
                                            long click = task.getResult().getLong("click");
                                            _setClik(click,userId,gender);
                                        }
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
                }
            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Crashlytics.logException(e);
                }
            });
        }
    }

    public void _setClik(final long clik, final String userId, final String gender){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Map<String,Object> map = new HashMap<>();
        map.put(getRandomString(10),1);
        map.put("time", FieldValue.serverTimestamp());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection(gender)
                .document(userId)
                .collection("view")
                .document(auth.getUid());
        ref.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ubdateClik(clik,userId,gender);
                }
            }
        });

    }
    private  String getRandomString(final int sizeOfRandomString){
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        Log.d("randomStrign", "getRandomString: "+sb.toString());

        return sb.toString();
    }
    private void ubdateClik(long click, final String userId, final String gender){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> map = new HashMap<>();
        map.put("click",click+1);
        db.document(gender+"/"+userId)
                .set(map, SetOptions.merge()).addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent i = new Intent(getActivity(), UserProfileActivity.class);
                i.putExtra("userId",userId);
                i.putExtra("gender",gender);

               // i.putExtra("myGender",getActivity().getIntent().getStringExtra("gender"));
                dialog.dismiss();
                getActivity().startActivity(i);
            }
        });
    }
}
