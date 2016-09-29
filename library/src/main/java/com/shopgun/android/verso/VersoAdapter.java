package com.shopgun.android.verso;

import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.shopgun.android.verso.utils.FragmentStatelessPagerAdapter;
import com.shopgun.android.zoomlayout.ZoomLayout;

public class VersoAdapter<T extends View & VersoPageView> extends FragmentStatelessPagerAdapter {

    public static final String TAG = VersoAdapter.class.getSimpleName();

    VersoPublication mPublication;
    ZoomPanListener mSpreadCallback;

    public VersoAdapter(FragmentManager fragmentManager, VersoPublication publication) {
        this(fragmentManager, publication, null);
    }

    public VersoAdapter(FragmentManager fragmentManager, VersoPublication publication, ZoomPanListener callback) {
        super(fragmentManager);
        mPublication = publication;
        mSpreadCallback = callback;
    }

    @Override
    public Fragment createItem(int position) {
        return VersoPageViewFragment.newInstance(mPublication, position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        VersoPageViewFragment fragment = (VersoPageViewFragment) super.instantiateItem(container, position);
        VersoSpreadConfiguration config = mPublication.getConfiguration();
        final VersoSpreadProperty property = config.getSpreadProperty(position);
        final int[] pages = property.getPages();
        Callback callback = new Callback(position, pages);
        fragment.setOnZoomListener(callback);
        fragment.setOnPanListener(callback);
        return fragment;
    }

    @Override
    public int getCount() {
        return mPublication.getConfiguration().getSpreadCount();
    }

    public VersoFragment getVersoFragment(ViewGroup container, int position) {
        return (VersoFragment) instantiateItem(container, position);
    }

    interface ZoomPanListener {
        void onZoomBegin(int position, int[] pages, float scale);
        void onZoom(int position, int[] pages, float scale);
        void onZoomEnd(int position, int[] pages, float scale);
        void onPanBegin(int position, int[] pages, Rect viewRect);
        void onPan(int position, int[] pages, Rect viewRect);
        void onPanEnd(int position, int[] pages, Rect viewRect);
    }

    private class Callback implements ZoomLayout.OnPanListener, ZoomLayout.OnZoomListener {

        private final int mPosition;
        private final int[] mPages;
        private final Rect mDrawRect = new Rect();

        public Callback(int position, int[] pages) {
            mPosition = position;
            mPages = pages;
        }

        @Override
        public void onPanBegin(ZoomLayout view) {
            if (mSpreadCallback != null) {
                buildRect(view.getDrawRect());
                mSpreadCallback.onPanBegin(mPosition, mPages, mDrawRect);
            }
        }

        @Override
        public void onPan(ZoomLayout view) {
            if (mSpreadCallback != null) {
                buildRect(view.getDrawRect());
                mSpreadCallback.onPan(mPosition, mPages, mDrawRect);
            }
        }

        @Override
        public void onPanEnd(ZoomLayout view) {
            if (mSpreadCallback != null) {
                buildRect(view.getDrawRect());
                mSpreadCallback.onPanEnd(mPosition, mPages, mDrawRect);
            }
        }

        @Override
        public void onZoomBegin(ZoomLayout view, float scale) {
            if (mSpreadCallback != null) {
                mSpreadCallback.onZoomBegin(mPosition, mPages, scale);
            }
        }

        @Override
        public void onZoom(ZoomLayout view, float scale) {
            if (mSpreadCallback != null) {
                mSpreadCallback.onZoom(mPosition, mPages, scale);
            }
        }

        @Override
        public void onZoomEnd(ZoomLayout view, float scale) {
            if (mSpreadCallback != null) {
                mSpreadCallback.onZoomEnd(mPosition, mPages, scale);
            }
        }

        private void buildRect(RectF rectF) {
            mDrawRect.set(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
        }

    }

}
