package com.shopgun.android.verso;

import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.shopgun.android.utils.NumberUtils;
import com.shopgun.android.zoomlayout.ZoomLayout;
import com.shopgun.android.zoomlayout.ZoomOnDoubleTapListener;

import java.util.HashSet;

public class VersoPageViewFragment extends Fragment {

    public static final String TAG = VersoPageViewFragment.class.getSimpleName();

    private static final String KEY_POSITION = "position";

    public static VersoPageViewFragment newInstance(int position) {
        Bundle arguments = new Bundle();
        arguments.putInt(KEY_POSITION, position);
        VersoPageViewFragment fragment = new VersoPageViewFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    // Views
    private ZoomLayout mZoomLayout;
    private VersoHorizontalLayout mPageContainer;
    private View mSpreadOverlay;

    // Input data
    private VersoSpreadConfiguration mVersoSpreadConfiguration;
    private VersoSpreadProperty mProperty;
    private int mPosition;
    private int[] mPages;

    // listeners
    private OnZoomListener mOnZoomListener;
    private OnPanListener mOnPanListener;
    private OnTapListener mOnTapListener;
    private OnDoubleTapListener mOnDoubleTapListener;
    private OnLongTapListener mOnLongTapListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(KEY_POSITION);
            mProperty = mVersoSpreadConfiguration.getSpreadProperty(mPosition);
            mPages = mProperty.getPages();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mZoomLayout = (ZoomLayout) inflater.inflate(R.layout.verso_page_layout, container, false);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            // scale operations on large bitmaps are horrible slow
            // for some reason, this works. LAYER_TYPE_SOFTWARE works too...
            mZoomLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        mZoomLayout.setOnZoomListener(new ZoomDispatcher());
        mZoomLayout.setOnPanListener(new PanDispatcher());
        mZoomLayout.setOnTapListener(new TapDispatcher());
        mZoomLayout.setOnDoubleTapListener(new DoubleTapDispatcher());
        mZoomLayout.setOnLongTapListener(new LongTapDispatcher());

        boolean zoom = !NumberUtils.isEqual(mProperty.getMaxZoomScale(), mProperty.getMinZoomScale());
        mZoomLayout.setAllowZoom(zoom);
        mZoomLayout.setMinScale(mProperty.getMinZoomScale());
        mZoomLayout.setMaxScale(mProperty.getMaxZoomScale());

        mPageContainer = (VersoHorizontalLayout) mZoomLayout.findViewById(R.id.verso_pages_container);
        for (int page : mPages) {
            View view = mVersoSpreadConfiguration.getPageView(mPageContainer, page);
            if (!(view instanceof VersoPageView)) {
                throw new IllegalArgumentException("The view must implement VersoPageView");
            }
            VersoPageView vpv = (VersoPageView) view;
            mPageContainer.addView(view);
        }

        mSpreadOverlay = mVersoSpreadConfiguration.getSpreadOverlay(mZoomLayout, mPages);
        if (mSpreadOverlay != null) {
            mZoomLayout.addView(mSpreadOverlay);
        }

        return mZoomLayout;

    }

    @Override
    public void onResume() {
        super.onResume();
        mPageContainer.getViewTreeObserver().addOnGlobalLayoutListener(new OverlaySizer());
    }

    class OverlaySizer implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            if (mSpreadOverlay != null) {
                Rect r = getChildPosition();
                if (r.left != mSpreadOverlay.getLeft() ||
                        r.top != mSpreadOverlay.getTop() ||
                        r.right != mSpreadOverlay.getRight() ||
                        r.bottom != mSpreadOverlay.getBottom()) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSpreadOverlay.getLayoutParams();
                    lp.width = r.width();
                    lp.height = r.height();
                    lp.gravity = Gravity.CENTER;
                    mSpreadOverlay.setLayoutParams(lp);
                }
            }
        }

    }

    private Rect getChildPosition() {
        Rect rect = new Rect();
        int childCount = mPageContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = mPageContainer.getChildAt(i);
            if (i == 0) {
                // First item, just set the rect
                rect.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            } else {
                if (rect.left > child.getLeft()) {
                    rect.left = child.getLeft();
                }
                if (rect.top > child.getTop()) {
                    rect.top = child.getTop();
                }
                if (rect.right < child.getRight()) {
                    rect.right = child.getRight();
                }
                if (rect.bottom < child.getBottom()) {
                    rect.bottom = child.getBottom();
                }
            }
        }
        return rect;
    }

    public void setVersoSpreadConfiguration(VersoSpreadConfiguration configuration) {
        mVersoSpreadConfiguration = configuration;
    }

    public void setOnTapListener(OnTapListener tapListener) {
        mOnTapListener = tapListener;
    }

    public void setOnDoubleTapListener(OnDoubleTapListener doubleTapListener) {
        mOnDoubleTapListener = doubleTapListener;
    }

    public void setOnLongTapListener(OnLongTapListener longTapListener) {
        mOnLongTapListener = longTapListener;
    }

    public void setOnZoomListener(OnZoomListener listener) {
        mOnZoomListener = listener;
    }

    public void setOnPanListener(OnPanListener listener) {
        mOnPanListener = listener;
    }

    public interface OnTapListener {
        boolean onContentTap(VersoPageViewFragment fragment, int position, int[] pages, float absX, float absY, float relX, float relY);
        boolean onViewTap(VersoPageViewFragment fragment, int position, int[] pages, float absX, float absY, float relX, float relY);
    }

    public interface OnDoubleTapListener {
        boolean onContentDoubleTap(VersoPageViewFragment fragment, int position, int[] pages, float absX, float absY, float relX, float relY);
        boolean onViewDoubleTap(VersoPageViewFragment fragment, int position, int[] pages, float absX, float absY, float relX, float relY);
    }

    public interface OnLongTapListener {
        void onContentLongTap(VersoPageViewFragment fragment, int position, int[] pages, float absX, float absY, float relX, float relY);
        void onViewLongTap(VersoPageViewFragment fragment, int position, int[] pages, float absX, float absY, float relX, float relY);
    }

    public interface OnZoomListener {
        void onZoomBegin(VersoPageViewFragment fragment, int position, int[] pages, float scale, Rect viewRect);
        void onZoom(VersoPageViewFragment fragment, int position, int[] pages, float scale, Rect viewRect);
        void onZoomEnd(VersoPageViewFragment fragment, int position, int[] pages, float scale, Rect viewRect);
    }

    public interface OnPanListener {
        void onPanBegin(VersoPageViewFragment fragment, int position, int[] pages, Rect viewRect);
        void onPan(VersoPageViewFragment fragment, int position, int[] pages, Rect viewRect);
        void onPanEnd(VersoPageViewFragment fragment, int position, int[] pages, Rect viewRect);
    }

    private final Rect mDrawRect = new Rect();
    private void updateRect() {
        RectF r = mZoomLayout.getDrawRect();
        mDrawRect.set(Math.round(r.left), Math.round(r.top), Math.round(r.right), Math.round(r.bottom));
    }

    private class TapDispatcher implements ZoomLayout.OnTapListener {

        @Override
        public boolean onContentTap(ZoomLayout view, float absX, float absY, float relX, float relY) {
            return mOnTapListener != null && mOnTapListener.onContentTap(VersoPageViewFragment.this, mPosition, mPages, absX, absY, relX, relY);
        }

        @Override
        public boolean onViewTap(ZoomLayout view, float absX, float absY, float relX, float relY) {
            return mOnTapListener != null && mOnTapListener.onViewTap(VersoPageViewFragment.this, mPosition, mPages, absX, absY, relX, relY);
        }
    }

    public void dispatchZoom(float scale) {
        int count = mPageContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            final View v = mPageContainer.getChildAt(i);
            if (v instanceof VersoPageView) {
                ((VersoPageView)v).onZoom(scale);
            }
        }
    }

    public void getVisiblePages(Rect bounds, HashSet<Integer> result) {
        if (mZoomLayout == null) {
            return;
        }
        Rect mHitBounds = new Rect();
        int[] pos = new int[2];
        int count = mPageContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            final View v = mPageContainer.getChildAt(i);
            v.getHitRect(mHitBounds);
            v.getLocationOnScreen(pos);
            mHitBounds.offsetTo(pos[0], pos[1]);
            if (Rect.intersects(bounds, mHitBounds)) {
                result.add(mPages[i]);
            }
        }
    }

    private class DoubleTapDispatcher implements ZoomLayout.OnDoubleTapListener {

        ZoomLayout.OnDoubleTapListener mZoomDoubleTapListener = new ZoomOnDoubleTapListener(false);

        @Override
        public boolean onContentDoubleTap(ZoomLayout view, float absX, float absY, float relX, float relY) {
            boolean consumed = mOnDoubleTapListener != null && mOnDoubleTapListener.onContentDoubleTap(VersoPageViewFragment.this, mPosition, mPages, absX, absY, relX, relY);
            return !consumed && mZoomDoubleTapListener.onContentDoubleTap(view, absX, absY, relX, relY);
        }

        @Override
        public boolean onViewDoubleTap(ZoomLayout view, float absX, float absY, float relX, float relY) {
            boolean consumed = mOnDoubleTapListener != null && mOnDoubleTapListener.onViewDoubleTap(VersoPageViewFragment.this, mPosition, mPages, absX, absY, relX, relY);
            return !consumed && mZoomDoubleTapListener.onViewDoubleTap(view, absX, absY, relX, relY);
        }

    }

    private class LongTapDispatcher implements ZoomLayout.OnLongTapListener {

        @Override
        public void onContentLongTap(ZoomLayout view, float absX, float absY, float relX, float relY) {
            if (mOnLongTapListener != null) mOnLongTapListener.onContentLongTap(VersoPageViewFragment.this, mPosition, mPages, absX, absY, relX, relY);
        }

        @Override
        public void onViewLongTap(ZoomLayout view, float absX, float absY, float relX, float relY) {
            if (mOnLongTapListener != null) mOnLongTapListener.onViewLongTap(VersoPageViewFragment.this, mPosition, mPages, absX, absY, relX, relY);
        }
    }

    private class ZoomDispatcher implements ZoomLayout.OnZoomListener {

        @Override
        public void onZoomBegin(ZoomLayout view, float scale) {
            if (mOnZoomListener != null) {
                updateRect();
                mOnZoomListener.onZoomBegin(VersoPageViewFragment.this, mPosition, mPages, scale, mDrawRect);
            }
        }

        @Override
        public void onZoom(ZoomLayout view, float scale) {
            dispatchZoom(scale);
            if (mOnZoomListener != null) {
                updateRect();
                mOnZoomListener.onZoom(VersoPageViewFragment.this, mPosition, mPages, scale, mDrawRect);
            }
        }

        @Override
        public void onZoomEnd(ZoomLayout view, float scale) {
            if (mOnZoomListener != null) {
                updateRect();
                mOnZoomListener.onZoomEnd(VersoPageViewFragment.this, mPosition, mPages, scale, mDrawRect);
            }
        }

    }

    private class PanDispatcher implements ZoomLayout.OnPanListener {

        @Override
        public void onPanBegin(ZoomLayout view) {
            if (mOnPanListener != null) {
                updateRect();
                mOnPanListener.onPanBegin(VersoPageViewFragment.this, mPosition, mPages, mDrawRect);
            }
        }

        @Override
        public void onPan(ZoomLayout view) {
            if (mOnPanListener != null) {
                updateRect();
                mOnPanListener.onPan(VersoPageViewFragment.this, mPosition, mPages, mDrawRect);
            }
        }

        @Override
        public void onPanEnd(ZoomLayout view) {
            if (mOnPanListener != null) {
                updateRect();
                mOnPanListener.onPanEnd(VersoPageViewFragment.this, mPosition, mPages, mDrawRect);
            }
        }

    }
}
