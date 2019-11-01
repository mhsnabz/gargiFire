package com.rzn.gargi.helper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rzn.gargi.Log_Sign.LoginFragment;
import com.rzn.gargi.Log_Sign.RegisterFragment;

public class ViewPagerApapter extends FragmentPagerAdapter {
    public ViewPagerApapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0 :
                LoginFragment loginFragment = new LoginFragment();
                return loginFragment;
            case 1:
                RegisterFragment registerFragment = new
                        RegisterFragment();
                return  registerFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }
}
