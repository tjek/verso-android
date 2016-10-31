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

        int containerWidth = 0;
        int containerHeight = 0;

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
                    containerWidth += child.getMeasuredWidth();
                    if (containerHeight < child.getMeasuredHeight()) {
                        containerHeight = child.getMeasuredHeight();
                    }
                }
            }
        }
        setMeasuredDimension(containerWidth, containerHeight);
    }

}
