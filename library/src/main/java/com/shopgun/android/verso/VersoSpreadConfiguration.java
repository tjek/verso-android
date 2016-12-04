package com.shopgun.android.verso;

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

public interface VersoSpreadConfiguration {

    /**
     * Get the {@link View} representing the current page.
     * @param page a page
     * @return a View
     */
    @NonNull View getPageView(ViewGroup container, int page);

    View getSpreadOverlay(ViewGroup container, int[] pages);

    void onConfigurationChanged(Configuration newConfig);

    int getPageCount();

    int getSpreadCount();

    int getSpreadMargin();

    VersoSpreadProperty getSpreadProperty(int spreadPosition);

    int getSpreadPositionFromPage(int page);

    int[] getPagesFromSpreadPosition(int spreadPosition);

    boolean hasData();

}
