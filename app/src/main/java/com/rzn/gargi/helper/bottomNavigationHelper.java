package com.rzn.gargi.helper;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rzn.gargi.R;
import com.rzn.gargi.chat.ChatActivity;
import com.rzn.gargi.home.HomeActivity;
import com.rzn.gargi.profile.ProfileActivity;
import com.rzn.gargi.topface.TopFaceActivity;

public class bottomNavigationHelper {
    public static void enableNavigation(final Context context , BottomNavigationView view, final String gender )
    {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                        Intent home = new Intent(context, HomeActivity.class);
                       home.putExtra("gender",gender);
                        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(home);

                        break;

                    case R.id.topface:
                        Intent topFace = new Intent(context, TopFaceActivity.class);
                      topFace.putExtra("gender",gender);
                        topFace.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        topFace.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(topFace);

                        break;
                    case R.id.chat:
                        Intent chat = new Intent(context, ChatActivity.class);
                       chat.putExtra("gender",gender);
                        chat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(chat);

                        break;
                    case R.id.profile:
                        Intent profile = new Intent(context, ProfileActivity.class);
                        profile.putExtra("gender",gender);
                        profile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        profile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(profile);
                        break;

                }
                return false;
            }
        });
    }
}