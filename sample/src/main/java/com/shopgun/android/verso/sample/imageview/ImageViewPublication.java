package com.shopgun.android.verso.sample.imageview;

import android.content.res.Configuration;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.shopgun.android.verso.VersoPublication;
import com.shopgun.android.verso.VersoSpreadConfiguration;
import com.shopgun.android.verso.sample.SpreadLayoutImpl;
import com.shopgun.android.verso.sample.VersoSampleApp;

import java.util.List;

public class ImageViewPublication implements VersoPublication {

    public static final String TAG = ImageViewPublication.class.getSimpleName();

    List<CatalogPage> mPages;
    private ImageViewSpreadConfiguration mConfiguration;

    public ImageViewPublication() {
        mPages = CatalogPage.create();
        mConfiguration = new ImageViewSpreadConfiguration(mPages.size(), VersoSampleApp.getContext());
    }

    @NonNull
    @Override
    public View getPageView(ViewGroup container, int page) {
        CatalogPage catalogPage = mPages.get(page);
        return new ImageViewPageView(container.getContext(), catalogPage);
    }

    @NonNull
    @Override
    public VersoSpreadConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mConfiguration = new ImageViewSpreadConfiguration(mPages.size(), newConfig);
    }

    @Override
    public View getSpreadOverlay(ViewGroup container, int[] pages) {
        return new SpreadLayoutImpl(container.getContext(), pages);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    protected ImageViewPublication(Parcel in) {
        this();
    }

    public static final Creator<ImageViewPublication> CREATOR = new Creator<ImageViewPublication>() {
        @Override
        public ImageViewPublication createFromParcel(Parcel source) {
            return new ImageViewPublication(source);
        }

        @Override
        public ImageViewPublication[] newArray(int size) {
            return new ImageViewPublication[size];
        }
    };
}
