package com.rzn.gargi.helper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.rzn.gargi.R;
import com.willy.ratingbar.ScaleRatingBar;

public class RatingDialog extends DialogFragment implements onOptionsListener {
    ScaleRatingBar ratingBar;
    TextView tv_rating;
    Double rate=0.0;
    Button onayla;
    String gender;
    String userId,currentUser;
    double oldRate,_totalRate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rating_dialog,container,false);
        if (getArguments() != null) {
            userId = getArguments().getString("userId","");
            gender = getArguments().getString("gender","");
            oldRate=getArguments().getLong("oldRate",  0);
            _totalRate=getArguments().getLong("totalRate",  0);
            Log.d("currentUser,UserId", "onCreateView: "+currentUser +"+" + userId + oldRate);
        }
        return view;    }

    @Override
    public void sendOption(float option) {

    }

    @Override
    public void sendData(String id) {

    }
}
