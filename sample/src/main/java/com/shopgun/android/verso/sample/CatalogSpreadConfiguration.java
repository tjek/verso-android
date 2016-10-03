package com.shopgun.android.verso.sample;

import android.os.Parcel;

import com.shopgun.android.utils.enums.Orientation;
import com.shopgun.android.verso.VersoSpreadProperty;
import com.shopgun.android.verso.utils.PagedConfiguration;

import java.util.List;

public class CatalogSpreadConfiguration extends PagedConfiguration {

    public static final String TAG = CatalogSpreadConfiguration.class.getSimpleName();

    List<VersoSpreadProperty> mSpreadProperties;
    List<CatalogPage> mPages;

    public CatalogSpreadConfiguration() {
        super(Orientation.LANDSCAPE, false, false);
        mPages = CatalogPage.create();
        for (int i = 0; i < getSpreadCount(); i++) {
            
        }
    }

    @Override
    public int getPageCount() {
        return mPages.size();
    }

    @Override
    public VersoSpreadProperty getSpreadProperty(int spreadPosition) {
        boolean last = getSpreadCount()-1 == spreadPosition;

        int[] pages = positionToPages(spreadPosition, getPageCount());
        float w = last ? 0.8f : 1f;
//        float w = 0.8f;
//        float w = 0.5f + 0.5f * (spreadPosition/getSpreadCount());
        return new VersoSpreadProperty(pages, w, 4.0f);
    }

    @Override
    public int getSpreadCount() {
        int pageCount = getPageCount();
        int count = getOrientation().isLandscape() ? (pageCount/2)+1 : pageCount;
        if (hasIntro()) {
            count++;
        }
        if (hasOutro()) {
            count ++;
        }
        return count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    protected CatalogSpreadConfiguration(Parcel in) {
    }

    public static final Creator<CatalogSpreadConfiguration> CREATOR = new Creator<CatalogSpreadConfiguration>() {
        @Override
        public CatalogSpreadConfiguration createFromParcel(Parcel source) {
            return new CatalogSpreadConfiguration(source);
        }

        @Override
        public CatalogSpreadConfiguration[] newArray(int size) {
            return new CatalogSpreadConfiguration[size];
        }
    };
}
