package com.shopgun.android.verso;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    ViewPager.OnPageChangeListener mViewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        final String TAG = ViewPager.OnPageChangeListener.class.getSimpleName();
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
                    callMove(position, mScroll, false);
                    mScroll = position;
                } else if (position+1 != mScroll) {
                    // dragging left, but not at first page
                    callMove(position+1, mScroll, false);
                    mScroll = position+1;
                } else if (positionOffsetPixels == 0) {
                    // fling left and first page
                    callMove(position, mScroll, false);
                    mScroll = position;
                }
            }

            // TODO determine new and removed views in each fragment

        }

        @Override
        public void onPageSelected(int position) {
            onChange(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mState = state;
            onChange(mVersoViewPager.getCurrentItem());
        }

        private void onChange(int position) {
            if (mState == ViewPager.SCROLL_STATE_IDLE && position != mChange) {
                callMove(position, mChange, true);
                mChange = position;
            }
        }

        private void callMove(int currentPos, int prevPos, boolean changed) {
            if (mPageChangeListener != null) {
                VersoSpreadConfiguration c = mVersoPublication.getConfiguration();
                int[] previousPages = c.getSpreadProperty(prevPos).getPages();
                int[] currentPages = c.getSpreadProperty(currentPos).getPages();
                if (changed) {
                    mPageChangeListener.onPagesChanged(currentPos, currentPages, prevPos, previousPages);
                } else {
                    mPageChangeListener.onPagesScrolled(currentPos, currentPages, prevPos, previousPages);
                }
            }
        }

    };

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
        mVersoViewPager.addOnPageChangeListener(mViewPagerPageChangeListener);
        mVersoViewPager.setAdapter(mVersoAdapter);
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

    public OnPageChangeListener getOnPageChangeListener() {
        return mPageChangeListener;
    }

    public void setOnPageChangeListener(OnPageChangeListener pageChangeListener) {
        mPageChangeListener = pageChangeListener;
    }

    public OnPanListener getOnPanListener() {
        return mPanListener;
    }

    public void setOnPanListener(OnPanListener panListener) {
        mPanListener = panListener;
    }

    public OnZoomListener getOnZoomListener() {
        return mZoomListener;
    }

    public void setOnZoomListener(OnZoomListener zoomListener) {
        mZoomListener = zoomListener;
    }

    public interface OnPageChangeListener {
        void onPagesScrolled(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages);
        void onPagesChanged(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages);
        void onVisiblePageIndexesChanged(int[] pages, int[] removedPages);
    }

    public interface OnZoomListener {
        void onBeginZoom(int[] pages, float scale);
        void onZoom(int[] pages, float scale);
        void onEndZoom(int[] pages, float scale);
    }

    public interface OnPanListener {
        void onBeginPan(int[] pages, Rect viewRect);
        void onPan(int[] pages, Rect viewRect);
        void onEndPan(int[] pages, Rect viewRect);
    }

}
