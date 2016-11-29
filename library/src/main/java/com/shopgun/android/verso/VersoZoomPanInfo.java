package com.shopgun.android.verso;

import android.graphics.Rect;

import com.shopgun.android.utils.TextUtils;

import java.util.Arrays;
import java.util.Locale;

public class VersoZoomPanInfo {

    private static final String STRING_FORMAT = "VersoZoomPanInfo[ position:%s, pages:%s, scale:%.2f, viewRect:%s ]";

    private final VersoPageViewFragment mFragment;
    private final int mPosition;
    private final int[] mPages;
    private final float mScale;
    private final Rect mViewRect;

    protected VersoZoomPanInfo(VersoPageViewFragment fragment, float scale, Rect viewRect) {
        mFragment = fragment;
        mPosition = mFragment.mPosition;
        mPages = Arrays.copyOf(mFragment.mPages, mFragment.mPages.length);
        mScale = scale;
        mViewRect = viewRect;
    }

    public VersoPageViewFragment getFragment() {
        return mFragment;
    }

    public int getPosition() {
        return mPosition;
    }

    public int[] getPages() {
        return mPages;
    }

    public float getScale() {
        return mScale;
    }

    public Rect getViewRect() {
        return mViewRect;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, STRING_FORMAT, mPosition, TextUtils.join(",", mPages), mScale, mViewRect.toString());
    }
}
