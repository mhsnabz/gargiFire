package com.rzn.gargi.topface;


import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.ModelUser;
import com.rzn.gargi.topface.Adapters.ManAdapter;
import com.rzn.gargi.topface.Adapters.WomanAdapter;

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
    WomanAdapter womanAdapter;
    TextView discp;

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

        orderByTopFace = (FloatingActionButton)rootView.findViewById(R.id.topface);
        orderByViews = (FloatingActionButton)rootView.findViewById(R.id.views);
        mix = (FloatingActionButton)rootView.findViewById(R.id.mix);
        orderByTopFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close(true);
                womanAdapter.stopListening();
                discp.setText(R.string.en_cok_puan_alan_kullanicilar);

                setRecyclerViewTopFace(rootView,woman);
                womanAdapter.startListening();

            }
        });
        orderByViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                womanAdapter.stopListening();
                discp.setText(R.string.en_populer);
                menu.close(true);
                setRecyclerView(rootView,woman);
                womanAdapter.startListening();

            }
        });
        discp.setText(R.string.en_populer);

        setRecyclerView(rootView,woman);
        return rootView;
    }

    private void setRecyclerView(View rootView, RecyclerView woman) {
        Query query = ref.orderBy("click",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ModelUser> options;
        options = new FirestoreRecyclerOptions.Builder<ModelUser>()
                .setQuery(query,ModelUser.class)
                .build();
        womanAdapter= new WomanAdapter(options,getContext());
        woman = rootView.findViewById(R.id.manUsers);
        woman.setHasFixedSize(true);
        woman.setLayoutManager(new LinearLayoutManager(getContext()));
        woman.setAdapter(womanAdapter);
    }

    private void setRecyclerViewTopFace(View rootView, RecyclerView woman) {
        Query query = ref.orderBy("rate",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ModelUser> options;
        options = new FirestoreRecyclerOptions.Builder<ModelUser>()
                .setQuery(query,ModelUser.class)
                .build();
        womanAdapter= new WomanAdapter(options,getContext());
        woman = rootView.findViewById(R.id.manUsers);
        woman.setHasFixedSize(true);
        woman.setLayoutManager(new LinearLayoutManager(getContext()));
        woman.setAdapter(womanAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        womanAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        womanAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
        womanAdapter.stopListening();

    }
}
