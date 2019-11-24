package com.rzn.gargi.chat;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.MsgListModel;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class OldMesseges extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    RecyclerView messeges_list;
    View rootView;
    RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<MsgListModel> list = new ArrayList<>();
    OldMesseges.msgListAdapter adapter;
    CollectionReference ref = db.collection("oldList");
    public OldMesseges() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_old_messeges, container, false);

        getList(messeges_list);
        return rootView;
    }
    private void getList(RecyclerView msg){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query ref = db
                .collection("oldList")
                .document(auth.getUid())
                .collection(auth.getUid()).orderBy("time",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<MsgListModel> options;
        options = new FirestoreRecyclerOptions.Builder<MsgListModel>()
                .setQuery(ref,MsgListModel.class)
                .build();

        adapter= new OldMesseges.msgListAdapter(options);
        messeges_list = rootView.findViewById(R.id.messeges_list);
        messeges_list.setHasFixedSize(true);

        messeges_list.setLayoutManager(new LinearLayoutManager(getContext()));
        messeges_list.setAdapter(adapter);
        messeges_list.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));


    }
    public class msgListAdapter extends FirestoreRecyclerAdapter<MsgListModel, OldMesseges.msgListAdapter.ViewHolder>
    {
        /**
         * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
         * FirestoreRecyclerOptions} for configuration options.
         *
         * @param options
         */
        public msgListAdapter(@NonNull FirestoreRecyclerOptions<MsgListModel> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull final OldMesseges.msgListAdapter.ViewHolder holder, int position, @NonNull final MsgListModel model) {
            Log.d("tag", "onBindViewHolder: "+model.getSenderUid());
            holder.getInfo(model.getSenderUid());
            holder.getBadgeCount(model.getSenderUid());
            //  holder.getTimeAgo(model.getTime());
            Log.d("time", "onBindViewHolder: "+model.getTime());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), OldOneToOneChat.class);
                    i.putExtra("userId",model.getSenderUid());
                    // i.putExtra("isOnline",model.isOnline());
                    i.putExtra("gender",getActivity().getIntent().getStringExtra("gender"));

                    holder.removeBadge(model.getSenderUid());

                    getActivity().startActivity(i);
                    getActivity().finish();
                }
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {           View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_mesenger_layout, parent, false);
            return new OldMesseges.msgListAdapter.ViewHolder(itemView);
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            View view;

            public ViewHolder(@NonNull View itemView) {

                super(itemView);
                view=itemView;
            }

            public void getInfo(String uid){
                final CircleImageView thumb_image = (CircleImageView)view.findViewById(R.id.profile_image);
                final TextView name =(TextView)view.findViewById(R.id.name);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("allUser")
                        .document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String image = documentSnapshot.getString("thumb_image");
                        String _name = documentSnapshot.getString("name");
                        if (!image.isEmpty()){
                            Picasso.get().load(image).config(Bitmap.Config.RGB_565).placeholder(R.drawable.upload_place_holder).resize(128,128)
                                    .memoryPolicy(MemoryPolicy.NO_STORE)
                                    .into(thumb_image);
                        }else thumb_image.setImageResource(R.drawable.upload_place_holder);

                        name.setText(_name);
                    }
                });

            }
            public void setTime(){
                TextView time = (TextView)view.findViewById(R.id.time);

            }
            public  void getTimeAgo(long time) {
                TextView timeAgo = (TextView)view.findViewById(R.id.time);

                int SECOND_MILLIS = 1000;
                int MINUTE_MILLIS = 60 * SECOND_MILLIS;
                int HOUR_MILLIS = 60 * MINUTE_MILLIS;
                final int DAY_MILLIS = 24 * HOUR_MILLIS;
                if (time < 1000000000000L) {
                    // if timestamp given in seconds, convert to millis
                    time *= 1000;
                }

                long now = Calendar.getInstance().getTimeInMillis();
                if (time > now || time <= 0) {
                    timeAgo.setText("");
                }

                // TODO: localize
                final long diff = now - time;
                if (diff < MINUTE_MILLIS) {
                    timeAgo.setText("şimdi");
                } else if (diff < 2 * MINUTE_MILLIS) {
                    timeAgo.setText("Bir Kaç Dk Önce");
                } else if (diff < 50 * MINUTE_MILLIS) {
                    String _time =String.valueOf(diff / MINUTE_MILLIS) + "Dk Önce";
                    timeAgo.setText(_time);
                } else if (diff < 90 * MINUTE_MILLIS) {
                    timeAgo.setText("Bir Saat Önce");
                } else if (diff < 24 * HOUR_MILLIS) {
                    String _time =String.valueOf(diff / HOUR_MILLIS) + "Saat Önce";
                    timeAgo.setText(_time);

                } else if (diff < 48 * HOUR_MILLIS) {
                    timeAgo.setText("Dün");
                } else {
                    String _time =String.valueOf(diff / DAY_MILLIS)+ "Gün Önce";
                    timeAgo.setText(_time);

                }
            }

            public void getBadgeCount(final String userId){
                final RelativeLayout relBadge=(RelativeLayout)itemView.findViewById(R.id.relBadge);



                db.collection("badgeCount")
                        .document(auth.getUid())
                        .addSnapshotListener(getActivity(), MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (documentSnapshot!=null){
                                    if (documentSnapshot.get(userId)!=null){
                                        relBadge.setVisibility(View.VISIBLE);

                                    }else {
                                        relBadge.setVisibility(View.GONE);

                                    }
                                }
                            }
                        });
            }
            public void removeBadge(final String userId){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String,Object> delete = new HashMap<>();
                delete.put(userId, FieldValue.delete());
                db.collection("badgeCount")
                        .document(auth.getUid())
                        .set(delete, SetOptions.merge());

            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
