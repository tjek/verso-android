package com.shopgun.android.verso;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.shopgun.android.verso.utils.FragmentStatelessPagerAdapter;

public class VersoAdapter<T extends View & VersoPageView> extends FragmentStatelessPagerAdapter {

    public static final String TAG = VersoAdapter.class.getSimpleName();

    private VersoPublication mPublication;
    private VersoPageViewFragment.OnZoomListener mOnZoomListener;
    private VersoPageViewFragment.OnPanListener mOnPanListener;
    private VersoPageViewFragment.OnTapListener mOnTapListener;
    private VersoPageViewFragment.OnDoubleTapListener mOnDoubleTapListener;
    private VersoPageViewFragment.OnLongTapListener mOnLongTapListener;

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
        fragment.setOnZoomListener(mOnZoomListener);
        fragment.setOnPanListener(mOnPanListener);
        fragment.setOnTapListener(mOnTapListener);
        fragment.setOnDoubleTapListener(mOnDoubleTapListener);
        fragment.setOnLongTapListener(mOnLongTapListener);
        return fragment;
    }

    @Override
    public int getCount() {
        return mPublication.getConfiguration().getSpreadCount();
    }

    @Override
    public float getPageWidth(int position) {
        return mPublication.getConfiguration().getSpreadProperty(position).getWidth();
    }

    public VersoPageViewFragment getVersoFragment(ViewGroup container, int position) {
        return (VersoPageViewFragment) instantiateItem(container, position);
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
