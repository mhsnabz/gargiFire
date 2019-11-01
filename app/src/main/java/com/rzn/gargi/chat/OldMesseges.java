package com.rzn.gargi.chat;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rzn.gargi.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OldMesseges extends Fragment {


    public OldMesseges() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_old_messeges, container, false);
    }

}
