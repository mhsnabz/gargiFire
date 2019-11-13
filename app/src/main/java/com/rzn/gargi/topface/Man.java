package com.rzn.gargi.topface;


import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.ModelUser;
import com.rzn.gargi.helper.UserInfo;
import com.rzn.gargi.topface.Adapters.ManAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Man extends Fragment {
    RecyclerView man;
    View rootView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionMenu menu;
    String currentUser;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FloatingActionButton orderByViews,mix,orderByTopFace;
    ProgressBar progres;
    Dialog dialog;
    ManAdapter manAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("MAN");
    public Man() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_man, container, false);
        menu=(FloatingActionMenu)rootView.findViewById(R.id.menu);

        orderByTopFace = (FloatingActionButton)rootView.findViewById(R.id.topface);
        orderByViews = (FloatingActionButton)rootView.findViewById(R.id.views);
        mix = (FloatingActionButton)rootView.findViewById(R.id.mix);
        orderByTopFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close(true);
                manAdapter.stopListening();

                setRecyclerViewTopFace(rootView,man);
                manAdapter.startListening();

            }
        });
        orderByViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manAdapter.stopListening();

                setRecyclerView(rootView,man);
                manAdapter.startListening();

            }
        });
        setRecyclerView(rootView,man);

        return rootView;
    }
   /* FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("MAN")
                        .document(id)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            UserInfo info = task.getResult().toObject(UserInfo.class);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String,String> img = new HashMap<>();
            img.put("bir",task.getResult().get("profileImage").toString());
            db.collection("images").document(id)
                    .set(img, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("taskResult", "onComplete: ");
                }
            });
        }
    });*/
    private void setRecyclerView(View rootView,RecyclerView man){
        Query query = ref.orderBy("click",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ModelUser> options;
        options = new FirestoreRecyclerOptions.Builder<ModelUser>()
                .setQuery(query,ModelUser.class)
                .build();
        manAdapter= new ManAdapter(options,getContext());
        man = rootView.findViewById(R.id.manUsers);
        man.setHasFixedSize(true);
        man.setLayoutManager(new LinearLayoutManager(getContext()));
        man.setAdapter(manAdapter);
    }
    private void setRecyclerViewTopFace(View rootView,RecyclerView man){
        Query query = ref.orderBy("rate",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ModelUser> options = new FirestoreRecyclerOptions.Builder<ModelUser>()
                .setQuery(query,ModelUser.class)
                .build();
        manAdapter= new ManAdapter(options,getContext());
        man = rootView.findViewById(R.id.manUsers);
        man.setHasFixedSize(true);
        man.setLayoutManager(new LinearLayoutManager(getContext()));
        man.setAdapter(manAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        manAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        manAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
        manAdapter.stopListening();

    }
}
