package com.shopgun.android.verso;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import com.shopgun.android.zoomlayout.ZoomLayout;

public class VersoSpreadLayout extends ZoomLayout {
    
    public static final String TAG = VersoSpreadLayout.class.getSimpleName();

    public VersoSpreadLayout(Context context) {
        this(context, null);
    }

    public VersoSpreadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VersoSpreadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VersoSpreadLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
