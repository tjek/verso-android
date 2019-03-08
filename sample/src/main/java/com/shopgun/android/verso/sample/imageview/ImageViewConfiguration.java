package com.shopgun.android.verso.sample.imageview;

import android.content.res.Configuration;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.shopgun.android.utils.UnitUtils;
import com.shopgun.android.utils.enums.Orientation;
import com.shopgun.android.verso.VersoSpreadConfiguration;
import com.shopgun.android.verso.VersoSpreadProperty;
import com.shopgun.android.verso.sample.SpreadPropertyImpl;
import com.shopgun.android.verso.sample.VersoSampleApp;

import java.util.List;
import java.util.Random;

public class ImageViewConfiguration implements VersoSpreadConfiguration {

    public static final String TAG = ImageViewConfiguration.class.getSimpleName();

    List<CatalogPage> mPages;

    Orientation mOrientation;
    int mPageCount;
    float[] mWidth;

    public ImageViewConfiguration() {
        this(false);
    }

    public ImageViewConfiguration(boolean randomWidth) {
        mPages = CatalogPage.create();
        mOrientation = Orientation.fromContext(VersoSampleApp.getContext());
        mPageCount = mPages.size();
        mWidth = new float[mPageCount];
        Random r = new Random();
        for (int i = 0; i < mWidth.length; i++) {
            mWidth[i] = randomWidth ? 0.6f + (0.4f * r.nextFloat()) : 0.8f;
        }
    }

    @NonNull
    @Override
    public View getPageView(ViewGroup container, int page) {
        CatalogPage catalogPage = mPages.get(page);
        return new ImageViewPageView(container.getContext(), catalogPage);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mOrientation = Orientation.fromConfiguration(newConfig);
    }

    @Override
    public int getPageCount() {
        return mPageCount;
    }

    @Override
    public int getSpreadCount() {
        return mOrientation.isLandscape() ? (getPageCount()/2)+1 : getPageCount();
    }

    @Override
    public int getSpreadMargin() {
        return UnitUtils.dpToPx(20, VersoSampleApp.getContext());
    }

    @Override
    public VersoSpreadProperty getSpreadProperty(int spreadPosition) {
        int[] pages = positionToPages(spreadPosition);
        boolean narrow = spreadPosition == 0 || getSpreadCount()-1 == spreadPosition;
        float w = narrow ? 0.6f : mWidth[spreadPosition];
        return new SpreadPropertyImpl(pages, w, 1.0f, (narrow ? 1.0f : 4.0f));
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

    @Override
    public int getSpreadPositionFromPage(int page) {
        if (mOrientation.isLandscape() && page > 0) {
            page += page % 2;
            return page/2;
        }
        return page;
    }

    @Override
    public int[] getPagesFromSpreadPosition(int spreadPosition) {
        if (mOrientation.isLandscape() && spreadPosition > 0) {
            int page = spreadPosition * 2;
            return new int[]{ page, page+1 };
        }
        return new int[spreadPosition];
    }

    @Override
    public boolean hasData() {
        return true;
    }

    @Override
    public View getSpreadOverlay(ViewGroup container, int[] pages) {
        return null;
    }

}
