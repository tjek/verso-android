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
import android.widget.FrameLayout;

import com.shopgun.android.utils.NumberUtils;
import com.shopgun.android.utils.log.L;
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
    protected int mPosition;
    protected int[] mPages;

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

    OverlaySizer mOverlaySizer;

    @Override
    public void onStart() {
        super.onStart();
        mOverlaySizer = new OverlaySizer();
        mPageContainer.addOnLayoutChangeListener(mOverlaySizer);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPageContainer.removeOnLayoutChangeListener(mOverlaySizer);
        mOverlaySizer = null;
    }

    class OverlaySizer implements View.OnLayoutChangeListener {

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            boolean changed = left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom;
            if (changed) {
                L.d(TAG, "onLayoutChange");
                Rect r = getChildPosition();
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSpreadOverlay.getLayoutParams();
                lp.width = r.width();
                lp.height = r.height();
                lp.gravity = Gravity.CENTER;
                mSpreadOverlay.setLayoutParams(lp);

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

    public View getSpreadOverlay() {
        return mSpreadOverlay;
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
        boolean onTap(VersoTapInfo info);
    }

    public interface OnDoubleTapListener {
        boolean onDoubleTap(VersoTapInfo info);
    }

    public interface OnLongTapListener {
        void onLongTap(VersoTapInfo info);
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

    private class TapDispatcher implements ZoomLayout.OnTapListener {

        @Override
        public boolean onTap(ZoomLayout view, ZoomLayout.TapInfo info) {
            return mOnTapListener != null && mOnTapListener.onTap(new VersoTapInfo(info, VersoPageViewFragment.this));
        }

    }

    private class DoubleTapDispatcher implements ZoomLayout.OnDoubleTapListener {

        ZoomLayout.OnDoubleTapListener mZoomDoubleTapListener = new ZoomOnDoubleTapListener(false);

        @Override
        public boolean onDoubleTap(ZoomLayout view, ZoomLayout.TapInfo info) {
            boolean consumed = mOnDoubleTapListener != null && mOnDoubleTapListener.onDoubleTap(new VersoTapInfo(info, VersoPageViewFragment.this));
            return !consumed && mZoomDoubleTapListener.onDoubleTap(view, info);
        }

    }

    private class LongTapDispatcher implements ZoomLayout.OnLongTapListener {

        @Override
        public void onLongTap(ZoomLayout view, ZoomLayout.TapInfo info) {
            if (mOnLongTapListener != null) mOnLongTapListener.onLongTap(new VersoTapInfo(info, VersoPageViewFragment.this));
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
