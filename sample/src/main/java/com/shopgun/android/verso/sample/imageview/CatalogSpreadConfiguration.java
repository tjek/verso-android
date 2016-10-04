package com.shopgun.android.verso.sample.imageview;

import com.shopgun.android.utils.UnitUtils;
import com.shopgun.android.utils.enums.Orientation;
import com.shopgun.android.verso.VersoSpreadProperty;
import com.shopgun.android.verso.sample.SpreadPropertyImpl;
import com.shopgun.android.verso.sample.VersoSampleApp;
import com.shopgun.android.verso.utils.PagedConfiguration;

import java.util.List;
import java.util.Random;

public class CatalogSpreadConfiguration extends PagedConfiguration {

    public static final String TAG = CatalogSpreadConfiguration.class.getSimpleName();

    List<VersoSpreadProperty> mSpreadProperties;
    List<CatalogPage> mPages;
    float[] mWidth;

    public CatalogSpreadConfiguration() {
        super(Orientation.LANDSCAPE, false, false);
        mPages = CatalogPage.create();
        mWidth = new float[mPages.size()];
        Random r = new Random();
        for (int i = 0; i < mWidth.length; i++) {
            mWidth[i] = 0.6f + (0.4f * r.nextFloat());
        }
    }

    @Override
    public int getPageCount() {
        return mPages.size();
    }

    @Override
    public VersoSpreadProperty getSpreadProperty(int spreadPosition) {
        int[] pages = positionToPages(spreadPosition, getPageCount());
        boolean narrow = spreadPosition == 0 || getSpreadCount()-1 == spreadPosition;
        float w = narrow ? 0.6f : mWidth[spreadPosition];
        return new SpreadPropertyImpl(pages, w, 4.0f);
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
    public int getSpreadMargin() {
        return UnitUtils.dpToPx(20, VersoSampleApp.context);
    }

}
