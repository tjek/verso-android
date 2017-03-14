package com.shopgun.android.verso;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.shopgun.android.verso.utils.FragmentStatelessPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class VersoAdapter extends FragmentStatelessPagerAdapter {

    public static final String TAG = VersoAdapter.class.getSimpleName();

    private VersoSpreadConfiguration mConfiguration;
    private VersoPageViewFragment.OnZoomListener mOnZoomListener;
    private VersoPageViewFragment.OnPanListener mOnPanListener;
    private VersoPageViewFragment.OnTouchListener mOnTouchListener;
    private VersoPageViewFragment.OnTapListener mOnTapListener;
    private VersoPageViewFragment.OnDoubleTapListener mOnDoubleTapListener;
    private VersoPageViewFragment.OnLongTapListener mOnLongTapListener;

    public VersoAdapter(FragmentManager fragmentManager, VersoSpreadConfiguration configuration) {
        super(fragmentManager);
        mConfiguration = configuration;
    }

    @Override
    public Fragment createItem(int position) {
        VersoPageViewFragment f = VersoPageViewFragment.newInstance(position);
        f.setVersoSpreadConfiguration(mConfiguration);
        return f;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        VersoPageViewFragment fragment = (VersoPageViewFragment) super.instantiateItem(container, position);
        fragment.setOnZoomListener(mOnZoomListener);
        fragment.setOnPanListener(mOnPanListener);
        fragment.setOnTouchlistener(mOnTouchListener);
        fragment.setOnTapListener(mOnTapListener);
        fragment.setOnDoubleTapListener(mOnDoubleTapListener);
        fragment.setOnLongTapListener(mOnLongTapListener);
        return fragment;
    }

    @Override
    public int getCount() {
        return mConfiguration.getSpreadCount();
    }

    @Override
    public float getPageWidth(int position) {
        return mConfiguration.getSpreadProperty(position).getWidth();
    }

    public VersoPageViewFragment getVersoFragment(ViewGroup container, int position) {
        return (VersoPageViewFragment) getItem(position);
    }

    public List<VersoPageViewFragment> getVersoFragments() {
        ArrayList<VersoPageViewFragment> list = new ArrayList<>();
        for (Fragment f : getFragments()) {
            if (f != null) {
                list.add((VersoPageViewFragment)f);
            }
        }
        return list;
    }

    @Override
    public Fragment[] getFragments() {
        return super.getFragments();
    }

    public void setOnTouchListener(VersoPageViewFragment.OnTouchListener listener) {
        mOnTouchListener = listener;
    }

    public void setOnTapListener(VersoPageViewFragment.OnTapListener listener) {
        mOnTapListener = listener;
    }

    public void setOnDoubleTapListener(VersoPageViewFragment.OnDoubleTapListener listener) {
        mOnDoubleTapListener = listener;
    }

    public void setOnLongTapListener(VersoPageViewFragment.OnLongTapListener listener) {
        mOnLongTapListener = listener;
    }

    public void setOnZoomListener(VersoPageViewFragment.OnZoomListener listener) {
        mOnZoomListener = listener;
    }

    public void setOnPanListener(VersoPageViewFragment.OnPanListener listener) {
        mOnPanListener = listener;
    }

}
