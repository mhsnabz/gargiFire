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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.ModelUser;
import com.rzn.gargi.topface.Adapters.ManAdapter;

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
        setRecyclerView(rootView,man);
        return rootView;
    }
    private void setRecyclerView(View rootView,RecyclerView man){
        Query query = ref.orderBy("click",Query.Direction.DESCENDING);
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
}
