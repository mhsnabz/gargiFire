package com.rzn.gargi.helper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rzn.gargi.chat.Messege;
import com.rzn.gargi.chat.OldMesseges;

public class ChatFragmentAdapter extends FragmentPagerAdapter {
    public ChatFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0 :
                Messege messege = new Messege();
                return messege;
            case 1:
                OldMesseges oldMesseges = new
                        OldMesseges();
                return  oldMesseges;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
