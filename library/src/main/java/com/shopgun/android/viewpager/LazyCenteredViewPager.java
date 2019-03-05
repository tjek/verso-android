package com.shopgun.android.viewpager;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.util.AttributeSet;


/**
 * When the PagerAdapter is set on the ViewPager, the first items are always populated.
 * We'll try to prevent this behaviour.
 */
public class LazyCenteredViewPager extends CenteredViewPager {

    public static final String TAG = LazyCenteredViewPager.class.getSimpleName();

    boolean mCurrentItemSet = false;
    boolean mSetAdapterFlag = false;

    public LazyCenteredViewPager(Context context) {
        super(context);
    }

    public LazyCenteredViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        mSetAdapterFlag = true;
        super.setAdapter(adapter);
        mSetAdapterFlag = false;
    }

    @Override
    void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
        if(!mSetAdapterFlag) {
            mCurrentItemSet = true;
        }
        super.setCurrentItemInternal(item, smoothScroll, always, velocity);
    }

    @Override
    void populate() {
        if(mCurrentItemSet) {
            super.populate();
        }
    }

}
