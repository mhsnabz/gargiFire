package com.rzn.gargi.topface;


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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rzn.gargi.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyRates extends Fragment {
    View rootView;
    TextView myRates;
    RecyclerView list;
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
        public void onBindViewHolder(@NonNull MyRates.ViewHolder holder, int i) {
            holder.loadUsers(userId.get(i));
            holder.getRate(userId.get(i));
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
                     name.setText(task.getResult().getString("name"));


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
    }

}
