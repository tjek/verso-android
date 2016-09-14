package com.shopgun.android.verso.sample;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.shopgun.android.verso.VersoPublication;
import com.shopgun.android.verso.VersoSpreadConfiguration;

public class CatalogPublication implements VersoPublication {

    public static final String TAG = CatalogPublication.class.getSimpleName();
    
    private CatalogSpreadConfiguration mConfiguration;

    public CatalogPublication() {
        mConfiguration = new CatalogSpreadConfiguration();
    }

    @NonNull
    @Override
    public View getPageView(ViewGroup container, int page) {
        CatalogPage catalogPage = mConfiguration.mPages.get(page-1);
        CatalogPageView view = new CatalogPageView(container.getContext(), catalogPage);
        return view;
    }

    @NonNull
    @Override
    public VersoSpreadConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mConfiguration, flags);
    }

    protected CatalogPublication(Parcel in) {
        this.mConfiguration = in.readParcelable(CatalogSpreadConfiguration.class.getClassLoader());
    }

    public static final Creator<CatalogPublication> CREATOR = new Creator<CatalogPublication>() {
        @Override
        public CatalogPublication createFromParcel(Parcel source) {
            return new CatalogPublication(source);
        }

        @Override
        public CatalogPublication[] newArray(int size) {
            return new CatalogPublication[size];
        }
    };
}
