package com.shopgun.android.verso.sample.imageview;

import android.content.Context;
import android.content.res.Configuration;

import com.shopgun.android.utils.UnitUtils;
import com.shopgun.android.utils.enums.Orientation;
import com.shopgun.android.verso.VersoSpreadConfiguration;
import com.shopgun.android.verso.VersoSpreadProperty;
import com.shopgun.android.verso.sample.SpreadPropertyImpl;
import com.shopgun.android.verso.sample.VersoSampleApp;

import java.util.Random;

public class ImageViewSpreadConfiguration implements VersoSpreadConfiguration {

    public static final String TAG = ImageViewSpreadConfiguration.class.getSimpleName();

    Orientation mOrientation;
    int mPageCount;
    float[] mWidth;

    public ImageViewSpreadConfiguration(int pageCount, Context context) {
        this(pageCount, Orientation.fromContext(context), false);
    }

    public ImageViewSpreadConfiguration(int pageCount, Configuration configuration) {
        this(pageCount, Orientation.fromConfiguration(configuration), false);
    }

    public ImageViewSpreadConfiguration(int pageCount, Orientation orientation, boolean randomWidth) {
        mOrientation = orientation;
        mPageCount = pageCount;
        mWidth = new float[pageCount];
        Random r = new Random();
        for (int i = 0; i < mWidth.length; i++) {
            mWidth[i] = randomWidth ? 0.6f + (0.4f * r.nextFloat()) : 0.8f;
        }
    }

    @Override
    public int getPageCount() {
        return mPageCount;
    }

    @Override
    public VersoSpreadProperty getSpreadProperty(int spreadPosition) {
        int[] pages = positionToPages(spreadPosition);
        boolean narrow = spreadPosition == 0 || getSpreadCount()-1 == spreadPosition;
        float w = narrow ? 0.6f : mWidth[spreadPosition];
        return new SpreadPropertyImpl(pages, w, 4.0f);
    }

    @Override
    public int getSpreadPositionFromPage(int page) {
        if (mOrientation.isLandscape() && page > 0) {
            page += page % 2;
            return page/2;
        }
        return page;
    }

    @Override
    public int getSpreadCount() {
        return mOrientation.isLandscape() ? (getPageCount()/2)+1 : getPageCount();
    }

    @Override
    public int getSpreadMargin() {
        return UnitUtils.dpToPx(20, VersoSampleApp.getContext());
    }

    private int[] positionToPages(int position) {

        // default is offset by one
        int page = position;
        if (mOrientation.isLandscape() && position > 0) {
            page = (position * 2) - 1;
        }

        if (mOrientation.isPortrait() || page == 0 || page >= mPageCount-1) {
            // first, last, and everything in portrait is single-page
            return new int[]{page};
        } else {
            // Anything else is double page
            return new int[]{page, (page + 1)};
        }

    }

}
