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
import com.shopgun.android.zoomlayout.ZoomLayout;
import com.shopgun.android.zoomlayout.ZoomOnDoubleTapListener;

import java.util.Arrays;
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
    private OverlaySizer mOverlaySizer;

    // Input data
    private VersoSpreadConfiguration mVersoSpreadConfiguration;
    private VersoSpreadProperty mProperty;
    protected int mPosition;
    protected int[] mPages;

    // listeners
    private OnZoomListener mOnZoomListener;
    private OnPanListener mOnPanListener;
    private OnTouchListener mOnTouchListener;
    private OnTapListener mOnTapListener;
    private OnDoubleTapListener mOnDoubleTapListener;
    private OnLongTapListener mOnLongTapListener;
    private OnLoadCompleteListener mOnLoadCompleteListener;

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
        mZoomLayout.addOnZoomListener(new ZoomDispatcher());
        mZoomLayout.addOnPanListener(new PanDispatcher());
        mZoomLayout.addOnTouchListener(new TouchDispatcher());
        mZoomLayout.addOnTapListener(new TapDispatcher());
        mZoomLayout.addOnDoubleTapListener(new DoubleTapDispatcher());
        mZoomLayout.addOnLongTapListener(new LongTapDispatcher());

        boolean zoom = !NumberUtils.isEqual(mProperty.getMaxZoomScale(), mProperty.getMinZoomScale());
        mZoomLayout.setAllowZoom(zoom);
        mZoomLayout.setMinScale(mProperty.getMinZoomScale());
        mZoomLayout.setMaxScale(mProperty.getMaxZoomScale());

        mZoomLayout.setZoomDuration(180);

        mPageContainer = (VersoHorizontalLayout) mZoomLayout.findViewById(R.id.verso_pages_container);
        mSpreadOverlay = mVersoSpreadConfiguration.getSpreadOverlay(mZoomLayout, mPages);
        if (mSpreadOverlay != null) {
            mZoomLayout.addView(mSpreadOverlay);
        }

        return mZoomLayout;

    }

    @Override
    public void onStart() {
        super.onStart();
        addVersoPageviews();
        mOverlaySizer = new OverlaySizer();
        mPageContainer.addOnLayoutChangeListener(mOverlaySizer);
    }

    private void addVersoPageviews() {
        for (int page : mPages) {
            View view = mVersoSpreadConfiguration.getPageView(mPageContainer, page);
            try {
                ((VersoPageView)view).setOnLoadCompleteListener(mOnLoadCompleteListener);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("The view must implement VersoPageView", e);
            }
            mPageContainer.addView(view);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mPageContainer.removeOnLayoutChangeListener(mOverlaySizer);
        mOverlaySizer = null;
        mPageContainer.removeAllViews();
    }

    public ZoomLayout getZoomLayout() {
        return mZoomLayout;
    }

    private class OverlaySizer implements View.OnLayoutChangeListener {

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            boolean changed = left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom;
            if (changed && mSpreadOverlay != null) {
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

    public int getSpreadPosition() {
        return mPosition;
    }

    public int[] getPages() {
        return Arrays.copyOf(mPages, mPages.length);
    }

    public void setVersoSpreadConfiguration(VersoSpreadConfiguration configuration) {
        mVersoSpreadConfiguration = configuration;
    }

    public void setOnTouchlistener(OnTouchListener touchListener) {
        mOnTouchListener = touchListener;
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

    public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
        mOnLoadCompleteListener = listener;
    }

    public interface OnTouchListener {
        boolean onTouch(int action, VersoTapInfo info);
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
        void onZoomBegin(VersoZoomPanInfo info);
        void onZoom(VersoZoomPanInfo info);
        void onZoomEnd(VersoZoomPanInfo info);
    }

    public interface OnPanListener {
        void onPanBegin(VersoZoomPanInfo info);
        void onPan(VersoZoomPanInfo info);
        void onPanEnd(VersoZoomPanInfo info);
    }

    public interface OnLoadCompleteListener {
        void onPageLoadComplete(boolean success, VersoPageView versoPageView);
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

    public boolean isScaled() {
        return mZoomLayout.isScaled();
    }

    public boolean isScaling() {
        return mZoomLayout.isScaling();
    }

    public boolean isTranslating() {
        return mZoomLayout.isTranslating();
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

    protected void dispatchPageVisibilityChange(int[] added, int[] removed) {
        if (!isAdded()) {
            // If scrolling is too fast, the MessageQueue (or something related) can't keep up, and we crash...
            // TODO: 01/12/16 Find a fix for this
            return;
        }
        for (int i = 0; i < mPageContainer.getChildCount(); i++) {
            View v = mPageContainer.getChildAt(i);
            int page = mPages[i];
            if (v instanceof VersoPageView) {
                VersoPageView pv = (VersoPageView) v;
                for (int a : added) if (a == page) pv.onVisible();
                for (int r : removed) if (r == page) pv.onInvisible();
            }
        }
    }

    private class TouchDispatcher implements ZoomLayout.OnTouchListener {

        @Override
        public boolean onTouch(ZoomLayout view, int action, ZoomLayout.TapInfo info) {
            return mOnTouchListener != null && mOnTouchListener.onTouch(action, new VersoTapInfo(info, VersoPageViewFragment.this));
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

    private Rect getZoomLayoutRect(ZoomLayout zl) {
        RectF r = zl.getDrawRect();
        return new Rect(Math.round(r.left), Math.round(r.top), Math.round(r.right), Math.round(r.bottom));
    }

    private class ZoomDispatcher implements ZoomLayout.OnZoomListener {

        @Override
        public void onZoomBegin(ZoomLayout view, float scale) {
            if (mOnZoomListener != null) {
                mOnZoomListener.onZoomBegin(new VersoZoomPanInfo(VersoPageViewFragment.this, scale, getZoomLayoutRect(view)));
            }
        }

        @Override
        public void onZoom(ZoomLayout view, float scale) {
            dispatchZoom(scale);
            if (mOnZoomListener != null) {
                mOnZoomListener.onZoom(new VersoZoomPanInfo(VersoPageViewFragment.this, scale, getZoomLayoutRect(view)));
            }
        }

        @Override
        public void onZoomEnd(ZoomLayout view, float scale) {
            if (mOnZoomListener != null) {
                mOnZoomListener.onZoomEnd(new VersoZoomPanInfo(VersoPageViewFragment.this, scale, getZoomLayoutRect(view)));
            }
        }

    }

    private class PanDispatcher implements ZoomLayout.OnPanListener {

        @Override
        public void onPanBegin(ZoomLayout view) {
            if (mOnPanListener != null) {
                mOnPanListener.onPanBegin(new VersoZoomPanInfo(VersoPageViewFragment.this, view.getScale(), getZoomLayoutRect(view)));
            }
        }

        @Override
        public void onPan(ZoomLayout view) {
            if (mOnPanListener != null) {
                mOnPanListener.onPan(new VersoZoomPanInfo(VersoPageViewFragment.this, view.getScale(), getZoomLayoutRect(view)));
            }
        }

        @Override
        public void onPanEnd(ZoomLayout view) {
            if (mOnPanListener != null) {
                mOnPanListener.onPanEnd(new VersoZoomPanInfo(VersoPageViewFragment.this, view.getScale(), getZoomLayoutRect(view)));
            }
        }

    }

}
