package com.shopgun.android.verso;

public class VersoSpreadProperty {

    final int[] mPages;
    final float mWidth;
    final float mMaxZoomScale;

    public VersoSpreadProperty(int[] pages) {
        this(pages, 1f, 1f);
    }

    public VersoSpreadProperty(int[] pages, float width, float maxZoomScale) {
        mPages = pages;
        mWidth = width;
        mMaxZoomScale = maxZoomScale;
    }

    public int[] getPages() {
        return mPages;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getMaxZoomScale() {
        return mMaxZoomScale;
    }

}
