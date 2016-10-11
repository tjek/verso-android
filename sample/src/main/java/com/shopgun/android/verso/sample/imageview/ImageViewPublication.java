package com.shopgun.android.verso.sample.imageview;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopgun.android.verso.VersoPageView;
import com.shopgun.android.verso.VersoPublication;
import com.shopgun.android.verso.VersoSpreadConfiguration;
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
        if (catalogPage.page == -1) {
            return new Outro(container.getContext());
        } else {
            return new ImageViewPageView(container.getContext(), catalogPage);
        }
    }

    class Outro extends TextView implements VersoPageView {

        public Outro(Context context) {
            super(context);
            setText("My Outro");
        }

        @Override
        public boolean onZoom(float scale) {
            return false;
        }

        @Override
        public void setOnCompletionListener() {

        }

        @Override
        public OnLoadCompletionListener getOnLoadCompleteListener() {
            return null;
        }

        @Override
        public void onVisible() {

        }

        @Override
        public void onInvisible() {

        }


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
