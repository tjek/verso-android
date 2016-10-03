package com.shopgun.android.verso;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.CenteredViewPager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VersoFragment extends Fragment {

    public static final String TAG = VersoFragment.class.getSimpleName();

    public static final String PUBLICATION = "publication";

    public static VersoFragment newInstance(VersoPublication publication) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(PUBLICATION, publication);
        VersoFragment fragment = new VersoFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    VersoPublication mVersoPublication;
    VersoViewPager mVersoViewPager;
    VersoAdapter mVersoAdapter;

    OnPageChangeListener mPageChangeListener;
    OnZoomListener mZoomListener;
    OnPanListener mPanListener;
    OnTapListener mTapListener;
    OnDoubleTapListener mDoubleTapListener;
    OnLongTapListener mLongTapListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVersoPublication = getArguments().getParcelable(PUBLICATION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVersoViewPager = (VersoViewPager) inflater.inflate(R.layout.verso_fragment, container, false);
        mVersoAdapter = new VersoAdapter(getChildFragmentManager(), mVersoPublication);
        Dispatcher dispatcher = new Dispatcher();
        mVersoAdapter.setOnTapListener(dispatcher);
        mVersoAdapter.setOnDoubleTapListener(dispatcher);
        mVersoAdapter.setOnLongTapListener(dispatcher);
        mVersoAdapter.setOnZoomListener(dispatcher);
        mVersoAdapter.setOnPanListener(dispatcher);
        mVersoViewPager.setOnPageChangeListener(new PageChangeDispatcher());
        mVersoViewPager.setAdapter(mVersoAdapter);
        mVersoViewPager.setPageMargin(mVersoPublication.getConfiguration().getSpreadMargin());
        return mVersoViewPager;
    }

    private String pageScrollStateToString(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING: return "SCROLL_STATE_DRAGGING";
            case ViewPager.SCROLL_STATE_IDLE: return "SCROLL_STATE_IDLE";
            case ViewPager.SCROLL_STATE_SETTLING: return "SCROLL_STATE_SETTLING";
            default: return "unknown";
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener pageChangeListener) {
        mPageChangeListener = pageChangeListener;
    }

    public void setOnPanListener(OnPanListener panListener) {
        mPanListener = panListener;
    }

    public void setOnZoomListener(OnZoomListener zoomListener) {
        mZoomListener = zoomListener;
    }

    public void setOnTapListener(OnTapListener tapListener) {
        mTapListener = tapListener;
    }

    public void setOnDoubleTapListener(OnDoubleTapListener doubleTapListener) {
        mDoubleTapListener = doubleTapListener;
    }

    public void setOnLongTapListener(OnLongTapListener longTapListener) {
        mLongTapListener = longTapListener;
    }

    public interface OnPageChangeListener {
        void onPagesScrolled(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages);
        void onPagesChanged(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages);
        void onVisiblePageIndexesChanged(int[] pages, int[] removedPages);
    }

    public interface OnZoomListener {
        void onZoomBegin(int[] pages, float scale);
        void onZoom(int[] pages, float scale);
        void onZoomEnd(int[] pages, float scale);
    }

    public interface OnPanListener {
        void onPanBegin(int[] pages, Rect viewRect);
        void onPan(int[] pages, Rect viewRect);
        void onPanEnd(int[] pages, Rect viewRect);
    }

    public interface OnTapListener {
        boolean onContentTap(int[] pages, float absX, float absY, float relX, float relY);
        boolean onViewTap(int[] pages, float absX, float absY);
    }

    public interface OnDoubleTapListener {
        boolean onContentDoubleTap(int[] pages, float absX, float absY, float relX, float relY);
        boolean onViewDoubleTap(int[] pages, float absX, float absY);
    }

    public interface OnLongTapListener {
        void onContentLongTap(int[] pages, float absX, float absY, float relX, float relY);
        void onViewLongTap(int[] pages, float absX, float absY);
    }

    private class PageChangeDispatcher implements CenteredViewPager.OnPageChangeListener {

        int mState = ViewPager.SCROLL_STATE_IDLE;
        int mChange = 0;
        int mScroll = 0;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

//            if (positionOffset < 0.02f || 0.98f < positionOffset ) {
//                L.d(TAG, String.format(Locale.US, "onPageScrolled[ pos:%s, offset:%.2f, offsetPx:%s, mScroll:%s, %s]", position, positionOffset, positionOffsetPixels, mScroll, pageScrollStateToString(mState)));
//            }

            if (mScroll != position) {
                if (mScroll < position) {
                    // dragging right
                    callScroll(position, mScroll);
                } else if (position+1 != mScroll) {
                    // dragging left, but not at first page
                    callScroll(position+1, mScroll);
                } else if (positionOffsetPixels == 0) {
                    // fling left and first page
                    callScroll(position, mScroll);
                }
            }

//            if (position == 0) {
//
//            } else if (position == mVersoAdapter.getCount()) {
//
//            } else {
//
//            }

            // TODO determine new and removed views in each fragment

        }

        @Override
        public void onPageSelected(int position) {
            onChange(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mState = state;
            callMove(mVersoViewPager.getCurrentItem(), mChange);
        }

        private void onChange(int position) {
            callMove(position, mChange);
        }

        private void callMove(int currentPos, int prevPos) {
            if (mState == ViewPager.SCROLL_STATE_IDLE && currentPos != prevPos) {
                mChange = currentPos;
                if (mPageChangeListener != null) {
                    VersoSpreadConfiguration c = mVersoPublication.getConfiguration();
                    int[] previousPages = c.getSpreadProperty(prevPos).getPages();
                    int[] currentPages = c.getSpreadProperty(currentPos).getPages();
                    mPageChangeListener.onPagesChanged(currentPos, currentPages, prevPos, previousPages);
                }
            }
        }

        private void callScroll(int currentPos, int prevPos) {
            mScroll = currentPos;
            if (mPageChangeListener != null) {
                VersoSpreadConfiguration c = mVersoPublication.getConfiguration();
                int[] previousPages = c.getSpreadProperty(prevPos).getPages();
                int[] currentPages = c.getSpreadProperty(currentPos).getPages();
                mPageChangeListener.onPagesScrolled(currentPos, currentPages, prevPos, previousPages);
            }
        }

    }

    private class Dispatcher implements
            VersoPageViewFragment.OnTapListener,
            VersoPageViewFragment.OnDoubleTapListener,
            VersoPageViewFragment.OnLongTapListener,
            VersoPageViewFragment.OnZoomListener,
            VersoPageViewFragment.OnPanListener {

        @Override
        public boolean onContentTap(VersoPageViewFragment fragment, int position, int[] pages, float absX, float absY, float relX, float relY) {
            return mTapListener != null && mTapListener.onContentTap(pages, absX, absY, relX, relY);
        }

        @Override
        public boolean onViewTap(VersoPageViewFragment fragment, int position, int[] pages, float absX, float absY, float relX, float relY) {
            return mTapListener != null && mTapListener.onViewTap(pages, absX, absY);
        }

        @Override
        public boolean onContentDoubleTap(VersoPageViewFragment fragment, int position, int[] pages, float absX, float absY, float relX, float relY) {
            return mDoubleTapListener != null && mDoubleTapListener.onContentDoubleTap(pages, absX, absY, relX, relY);
        }

        @Override
        public boolean onViewDoubleTap(VersoPageViewFragment fragment, int position, int[] pages, float absX, float absY, float relX, float relY) {
            return mDoubleTapListener != null && mDoubleTapListener.onViewDoubleTap(pages, absX, absY);
        }

        @Override
        public void onContentLongTap(VersoPageViewFragment fragment, int position, int[] pages, float absX, float absY, float relX, float relY) {
            if (mLongTapListener != null) mLongTapListener.onContentLongTap(pages, absX, absY, relX, relY);
        }

        @Override
        public void onViewLongTap(VersoPageViewFragment fragment, int position, int[] pages, float absX, float absY, float relX, float relY) {
            if (mLongTapListener != null) mLongTapListener.onViewLongTap(pages, absX, absY);
        }

        @Override
        public void onZoomBegin(VersoPageViewFragment fragment, int position, int[] pages, float scale, Rect viewRect) {
            if (mZoomListener != null) mZoomListener.onZoomBegin(pages, scale);
        }

        @Override
        public void onZoom(VersoPageViewFragment fragment, int position, int[] pages, float scale, Rect viewRect) {
            if (mZoomListener != null) mZoomListener.onZoom(pages, scale);
        }

        @Override
        public void onZoomEnd(VersoPageViewFragment fragment, int position, int[] pages, float scale, Rect viewRect) {
            if (mZoomListener != null) mZoomListener.onZoomEnd(pages, scale);
        }

        @Override
        public void onPanBegin(VersoPageViewFragment fragment, int position, int[] pages, Rect viewRect) {
            if (mPanListener != null) mPanListener.onPanBegin(pages, viewRect);
        }

        @Override
        public void onPan(VersoPageViewFragment fragment, int position, int[] pages, Rect viewRect) {
            if (mPanListener != null) mPanListener.onPan(pages, viewRect);
        }

        @Override
        public void onPanEnd(VersoPageViewFragment fragment, int position, int[] pages, Rect viewRect) {
            if (mPanListener != null) mPanListener.onPanEnd(pages, viewRect);
        }

    }

}
