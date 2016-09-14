package com.shopgun.android.verso.sample;

import android.os.Parcel;

import com.shopgun.android.utils.enums.Orientation;
import com.shopgun.android.verso.VersoSpreadProperty;
import com.shopgun.android.verso.utils.PagedConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CatalogSpreadConfiguration extends PagedConfiguration {

    public static final String TAG = CatalogSpreadConfiguration.class.getSimpleName();

    List<CatalogPage> mPages;

    public CatalogSpreadConfiguration() {
        mPages = CatalogPage.create();
        setOrientation(Orientation.LANDSCAPE);
    }

    @Override
    public int getPageCount() {
        return mPages.size();
    }

    @Override
    public VersoSpreadProperty getSpreadProperty(int spreadPosition) {
        int[] pages = positionToPages(spreadPosition, getPageCount());
        return new VersoSpreadProperty(pages);
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
        dest.writeList(this.mPages);
    }

    protected CatalogSpreadConfiguration(Parcel in) {
        this.mPages = new ArrayList<CatalogPage>();
        in.readList(this.mPages, CatalogPage.class.getClassLoader());
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
