package com.shopgun.android.verso;

import android.content.Context;
import android.support.v4.view.CenteredViewPager;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;

public class VersoViewPager extends CenteredViewPager {

    public static final String TAG = VersoViewPager.class.getSimpleName();

    public VersoViewPager(Context context) {
        super(context);
    }

    public VersoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (adapter != null && !(adapter instanceof VersoAdapter)) {
            throw new UnsupportedOperationException("The adapter must be an instance of VersoAdapter.");
        }
        super.setAdapter(adapter);
    }

}
