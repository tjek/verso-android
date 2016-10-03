package com.shopgun.android.verso.sample;

import com.shopgun.android.verso.VersoSpreadProperty;

public class CatalogSpreadProperty implements VersoSpreadProperty {

    private final int[] mPages;
    private final float mWidth;
    private final float mMaxZoomScale;

    public CatalogSpreadProperty(int[] pages, float width, float maxZoomScale) {
        mPages = pages;
        mWidth = width;
        mMaxZoomScale = maxZoomScale;
    }

    @Override
    public int[] getPages() {
        return mPages;
    }

    @Override
    public float getWidth() {
        return mWidth;
    }

    @Override
    public float getMaxZoomScale() {
        return mMaxZoomScale;
    }

}
