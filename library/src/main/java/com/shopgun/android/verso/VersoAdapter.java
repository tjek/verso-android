package com.shopgun.android.verso;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.shopgun.android.verso.utils.FragmentStatelessPagerAdapter;

public class VersoAdapter<T extends View & VersoPageView> extends FragmentStatelessPagerAdapter {

    public static final String TAG = VersoAdapter.class.getSimpleName();

    VersoPublication mPublication;

    public VersoAdapter(FragmentManager fragmentManager, VersoPublication publication) {
        super(fragmentManager);
        mPublication = publication;
    }

    @Override
    public Fragment createItem(int position) {
        return VersoPageViewFragment.newInstance(mPublication, position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        VersoPageViewFragment fragment = (VersoPageViewFragment) super.instantiateItem(container, position);
        // here we can attach an interface if we need to
        return fragment;
    }

    @Override
    public int getCount() {
        return mPublication.getConfiguration().getSpreadCount();
    }

}
