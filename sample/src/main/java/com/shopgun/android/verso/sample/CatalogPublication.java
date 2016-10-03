package com.shopgun.android.verso.sample;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopgun.android.verso.VersoPageView;
import com.shopgun.android.verso.VersoPublication;
import com.shopgun.android.verso.VersoSpreadConfiguration;

public class CatalogPublication implements VersoPublication {

    public static final String TAG = CatalogPublication.class.getSimpleName();

    private CatalogSpreadConfiguration mConfiguration;

    public CatalogPublication(Context context) {
        mConfiguration = new CatalogSpreadConfiguration(context);
        mConfiguration.setOutro(true);
    }

    @NonNull
    @Override
    public View getPageView(ViewGroup container, int page) {
        try {
            CatalogPage catalogPage = mConfiguration.mPages.get(page-1);
            return new CatalogPageView(container.getContext(), catalogPage);
        } catch (Exception e) {
            return new Outro(container.getContext());
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
