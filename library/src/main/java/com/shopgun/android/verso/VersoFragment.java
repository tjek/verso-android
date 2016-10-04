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

import com.shopgun.android.utils.log.L;

import java.util.HashSet;
import java.util.Locale;

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
        void onVisiblePageIndexesChanged(int[] pages, int[] added, int[] removed);
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
        int mChangePosition = 0;
        int mScrollPosition = 0;
        HashSet<Integer> mPages = new HashSet<>();
        Rect mViewPagetHitRect = new Rect();
        Rect mFragmentHitRect = new Rect();
        int[] mPos = new int[2];
        float mLastOffset = 0;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            // First check if we have scrolled far enough that a new fragment is in the center of the ViewPager

            if (position < mScrollPosition ||
                    (position == mScrollPosition && positionOffset < mLastOffset)) {
                // Scrolling left
                int nextPos = mScrollPosition-1;
                if (nextPos >= 0) {
                    VersoPageViewFragment f = mVersoAdapter.getVersoFragment(null, nextPos);
                    updateFragmentHitRect(f);
                    if (mFragmentHitRect.centerX() >= mViewPagetHitRect.centerX()
                            || (position == 0 && positionOffsetPixels <= 0)) {
                        scrollTo(nextPos);
                    }
                }
            } else if (position > mScrollPosition ||
                    (position == mScrollPosition && positionOffset > mLastOffset)) {
                // Scrolling right
                int nextPos = mScrollPosition+1;
                VersoPageViewFragment f = mVersoAdapter.getVersoFragment(null, nextPos);
                updateFragmentHitRect(f);
                if (mFragmentHitRect.centerX() <= mViewPagetHitRect.centerX()
                        || (nextPos == mVersoAdapter.getCount()-1 && mFragmentHitRect.right <= mViewPagetHitRect.right)) {
                    scrollTo(nextPos);
                }
            } else {
                // initializer and bounce effect
                mVersoViewPager.getHitRect(mViewPagetHitRect);
                mVersoViewPager.getLocationOnScreen(mPos);
                mViewPagetHitRect.offsetTo(mPos[0], mPos[1]);
            }

            mLastOffset = positionOffset;

            // Now check if there is any change in visible and removed pages (from individual fragments)

            if (position == 0) {
                // First position
//                mPageChangeListener.onVisiblePageIndexesChanged();
            } else if (position == mVersoAdapter.getCount()) {
                // last position

            } else {
                // anywhere in between

            }

        }

        private void updateFragmentHitRect(VersoPageViewFragment f) {
            View v = f.getView();
            if (v != null) {
                v.getHitRect(mFragmentHitRect);
                v.getLocationOnScreen(mPos);
                mFragmentHitRect.offsetTo(mPos[0], mPos[1]);
            } else {
                mFragmentHitRect.set(0,0,0,0);
            }
        }

        private void printScrolled(String msg, int position, float positionOffset, int positionOffsetPixels) {
//            if (positionOffset < 0.10f || 0.90f < positionOffset ) {
                L.d(TAG, String.format(Locale.US, "scroll[ %s, pos:%s, offset:%.2f, offsetPx:%s ]", msg, position, positionOffset, positionOffsetPixels));
//            }
        }

        private void scrollTo(int position) {
            int prevPos = mScrollPosition;
            mScrollPosition = position;
            if (mPageChangeListener != null) {
                VersoSpreadConfiguration c = mVersoPublication.getConfiguration();
                int[] previousPages = c.getSpreadProperty(prevPos).getPages();
                int[] currentPages = c.getSpreadProperty(mScrollPosition).getPages();
                mPageChangeListener.onPagesScrolled(mScrollPosition, currentPages, prevPos, previousPages);
            }
        }

        @Override
        public void onPageSelected(int position) {
            moveTo(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mState = state;
            moveTo(mVersoViewPager.getCurrentItem());
        }

        private void moveTo(int position) {
            if (mState == ViewPager.SCROLL_STATE_IDLE && position != mChangePosition) {
                int prevPos = mChangePosition;
                mChangePosition = position;
                if (mPageChangeListener != null) {
                    VersoSpreadConfiguration c = mVersoPublication.getConfiguration();
                    int[] previousPages = c.getSpreadProperty(prevPos).getPages();
                    int[] currentPages = c.getSpreadProperty(mChangePosition).getPages();
                    mPageChangeListener.onPagesChanged(mChangePosition, currentPages, prevPos, previousPages);
                }
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
