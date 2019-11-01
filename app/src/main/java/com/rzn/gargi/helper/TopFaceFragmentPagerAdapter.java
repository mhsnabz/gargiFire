package com.rzn.gargi.helper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rzn.gargi.topface.Man;
import com.rzn.gargi.topface.Woman;

public class TopFaceFragmentPagerAdapter extends FragmentPagerAdapter
{

    public TopFaceFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0 :
                Man man = new Man();
                return man;
            case 1:
                Woman woman = new
                        Woman();
                return  woman;
         /*   case 2:
                MyRates rates = new MyRates();
                    return rates;
            case 3:
                MyViews views = new MyViews();
                return views;*/
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}