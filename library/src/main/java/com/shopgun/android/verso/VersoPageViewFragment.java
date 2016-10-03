package com.shopgun.android.verso;

import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shopgun.android.verso.utils.VersoLog;
import com.shopgun.android.zoomlayout.ZoomLayout;
import com.shopgun.android.zoomlayout.ZoomOnDoubleTapListener;

public class VersoPageViewFragment extends Fragment {

    public static final String TAG = VersoPageViewFragment.class.getSimpleName();
    
    private static final String VERSO_PUBLICATION_KEY = "verso_publication";
    private static final String VERSO_POSITION_KEY = "verso_position";

    public static VersoPageViewFragment newInstance(VersoPublication publication, int position) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(VERSO_PUBLICATION_KEY, publication);
        arguments.putInt(VERSO_POSITION_KEY, position);
        VersoPageViewFragment fragment = new VersoPageViewFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    // Views
    private ZoomLayout mZoomLayout;
    private LinearLayout mPageContainer;

    // Input data
    private VersoPublication mVersoPublication;
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
            mVersoPublication = getArguments().getParcelable(VERSO_PUBLICATION_KEY);
            mPosition = getArguments().getInt(VERSO_POSITION_KEY);
            VersoSpreadConfiguration config = mVersoPublication.getConfiguration();
            VersoSpreadProperty property = config.getSpreadProperty(mPosition);
            mPages = property.getPages();
            if (mPages.length == 0) {
                VersoLog.d(TAG, "There are no pages in the current spread, at position " + mPosition + ". No content will be displayed");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mZoomLayout = (ZoomLayout) inflater.inflate(R.layout.verso_page_layout, container, false);
        mZoomLayout.setOnZoomListener(new ZoomDispatcher());
        mZoomLayout.setOnPanListener(new PanDispatcher());
        mZoomLayout.setOnTapListener(new TapDispatcher());
        mZoomLayout.setOnDoubleTapListener(new DoubleTapDispatcher());
        mZoomLayout.setOnLongTapListener(new LongTapDispatcher());
        mPageContainer = (LinearLayout) mZoomLayout.findViewById(R.id.verso_pages_container);

        for (int page : mPages) {
            View view = mVersoPublication.getPageView(mPageContainer, page);
            if (!(view instanceof VersoPageView)) {
                throw new IllegalArgumentException("The view must implement VersoPageView");
            }
            mPageContainer.addView(view);
        }
        return mZoomLayout;

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
            int count = mPageContainer.getChildCount();
            for (int i = 0; i < count; i++) {
                final View v = mPageContainer.getChildAt(i);
                if (v instanceof VersoPageView) {
                    ((VersoPageView)v).onZoom(scale);
                }
            }
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
