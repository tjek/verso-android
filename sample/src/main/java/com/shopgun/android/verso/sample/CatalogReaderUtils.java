package com.shopgun.android.verso.sample;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

public class CatalogReaderUtils {

    /**
     * Get the max available heap size
     *
     * @param c A context
     * @return the maximum available heap size for the device
     */
    public static int getMaxHeap(Context c) {
        ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return am.getLargeMemoryClass();
        } else {
            return am.getMemoryClass();
        }
    }

}
