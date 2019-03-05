package com.shopgun.android.verso;

import android.content.Context;
import com.shopgun.android.viewpager.LazyCenteredViewPager;
import androidx.viewpager.widget.PagerAdapter;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class VersoViewPager extends LazyCenteredViewPager {

    public static final String TAG = VersoViewPager.class.getSimpleName();

    private boolean mPagingEnabled = true;

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

    public VersoAdapter getVersoAdapter() {
        return (VersoAdapter) getAdapter();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            // Bug in MotionEvent.getX(int) and getY(int) where
            // pointerIndex is out of range - ignore any exceptions
        } catch (ArrayIndexOutOfBoundsException e) {
            // Don't know why this is being thrown
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            // Bug in MotionEvent.getX(int) and getY(int) where
            // pointerIndex is out of range - ignore any exceptions
        } catch (ArrayIndexOutOfBoundsException e) {
            // Don't know why this is being thrown
        }
        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        mPagingEnabled = enabled;
    }

    @Override
    public void setCurrentItem(int item) {
        if (mPagingEnabled) {
            super.setCurrentItem(item);
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (mPagingEnabled) {
            super.setCurrentItem(item, smoothScroll);
        }
    }

}
