package com.shopgun.android.verso;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class VersoHorizontalLayout extends LinearLayout {
    
    public static final String TAG = VersoHorizontalLayout.class.getSimpleName();

    private int mChildExpectedCount = 2;

    public VersoHorizontalLayout(Context context) {
        super(context);
    }

    public VersoHorizontalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VersoHorizontalLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        final int width = r - l;
//        int curLeft = getPaddingLeft();
//        int childWidthTotal = 0;
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            if (child.getVisibility() != View.GONE) {
//                childWidthTotal += child.getWidth();
//            }
//        }
//
//        if (childWidthTotal < width) {
//            curLeft += (width - childWidthTotal) / 2;
//        }
//
//        VersoLog.d(TAG, String.format("width:%s, childWidth:%s, left:%s", width, childWidthTotal, curLeft));
//
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            if (child.getVisibility() != GONE) {
//                child.layout(curLeft, getPaddingTop(), curLeft + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
//                curLeft += child.getMeasuredWidth();
//            }
//        }
    }

    private int getVisibleChildCount() {
        int c = getChildCount();
        for (int i = 0; i < c; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                c--;
            }
        }
        return c;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        final int childCount = getChildCount();
        if (childCount > 0) {
            final int childWidth = width / getVisibleChildCount();
            final int childHeight = height;

            final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST);
            final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST);

            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() != View.GONE) {
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                }
            }
        }

        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

}
