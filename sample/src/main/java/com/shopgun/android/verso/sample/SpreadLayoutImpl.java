package com.shopgun.android.verso.sample;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.shopgun.android.utils.TextUtils;
import com.shopgun.android.verso.VersoSpreadOverlay;

import java.util.Random;

public class SpreadLayoutImpl extends TextView implements VersoSpreadOverlay {

    int[] mPages;

    public SpreadLayoutImpl(Context context, int[] pages) {
        super(context);
        mPages = pages;
        setText("SpreadOverlay\nPages: " + TextUtils.join(",", mPages));
        Random r = new Random();
        setBackgroundColor(Color.argb(100, r.nextInt(255), r.nextInt(255), r.nextInt(255)));
    }

}