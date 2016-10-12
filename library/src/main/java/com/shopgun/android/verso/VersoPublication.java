package com.shopgun.android.verso;

import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

public interface VersoPublication extends Parcelable {

    /**
     * Get the {@link View} representing the current page.
     * @param page a page
     * @return a View
     */
    @NonNull View getPageView(ViewGroup container, int page);

    @NonNull VersoSpreadConfiguration getConfiguration();

    void onConfigurationChanged(Configuration newConfig);

    View getSpreadOverlay(ViewGroup container, int[] pages);

}
