package com.shopgun.android.verso;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.CenteredViewPager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.shopgun.android.utils.log.L;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

    HashSet<Integer> mCurrentVisiblePages = new HashSet<>();
    Rect mViewPagerHitRect = new Rect();
    int[] mOutLocation = new int[2];

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
        VersoOnLayoutChanged changedListener = new VersoOnLayoutChanged();
        mVersoViewPager.addOnLayoutChangeListener(changedListener);
        mVersoViewPager.getViewTreeObserver().addOnPreDrawListener(changedListener);
        mVersoViewPager.setOnPageChangeListener(new PageChangeDispatcher());
        mVersoViewPager.setAdapter(mVersoAdapter);
        mVersoViewPager.setPageMargin(mVersoPublication.getConfiguration().getSpreadMargin());
        return mVersoViewPager;
    }

    private class VersoOnLayoutChanged implements View.OnLayoutChangeListener, ViewTreeObserver.OnPreDrawListener {

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                updateHitRect(mVersoViewPager, mViewPagerHitRect, mOutLocation);
                updateVisiblePages();
            }
        }

        @Override
        public boolean onPreDraw() {
            updateVisiblePages();
            if (!mCurrentVisiblePages.isEmpty()) {
                if (mPageChangeListener != null) {
                    VersoSpreadConfiguration c = mVersoPublication.getConfiguration();
                    int position = mVersoViewPager.getCurrentItem();
                    int[] pages = c.getSpreadProperty(position).getPages();
                    mPageChangeListener.onPagesChanged(position, pages, position, pages);
                }
                mVersoViewPager.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
            return false;
        }

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
        Rect mFragmentHitRect = new Rect();
        float mLastOffset = 0;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            printScrolled("scroll", position, positionOffset, positionOffsetPixels);
            // First check if we need to update scroll position
            updateScrollPosition(position, positionOffset, positionOffsetPixels);
            // Then check if there is any change in visible and removed pages (from individual fragments)
            updateVisiblePages();
        }

        /**
         * Determine if we have scrolled far enough that a new fragment is in the center of the ViewPager.
         * If so, scroll position will be updated, and callbacks will be triggered.
         *
         * @param position             Position index of the first page currently being displayed.
         *                             Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
         * @param positionOffsetPixels Value in pixels indicating the offset from position.
         */
        private void updateScrollPosition(int position, float positionOffset, int positionOffsetPixels) {
            if (position < mScrollPosition ||
                    (position == mScrollPosition && positionOffset < mLastOffset)) {
                // Scrolling left
                int nextPos = mScrollPosition-1;
                if (nextPos >= 0) {
                    VersoPageViewFragment f = mVersoAdapter.getVersoFragment(mVersoViewPager, nextPos);
                    updateHitRect(f.getView(), mFragmentHitRect, mOutLocation);
                    if (mFragmentHitRect.centerX() >= mViewPagerHitRect.centerX()
                            || (position == 0 && positionOffsetPixels <= 0)) {
                        scrollTo(nextPos);
                    }
                }
            } else if (position > mScrollPosition ||
                    (position == mScrollPosition && positionOffset > mLastOffset)) {
                // Scrolling right
                int nextPos = mScrollPosition+1;
                VersoPageViewFragment f = mVersoAdapter.getVersoFragment(mVersoViewPager, nextPos);
                updateHitRect(f.getView(), mFragmentHitRect, mOutLocation);
                if (mFragmentHitRect.centerX() <= mViewPagerHitRect.centerX()
                        || (nextPos == mVersoAdapter.getCount()-1 && mFragmentHitRect.right <= mViewPagerHitRect.right)) {
                    scrollTo(nextPos);
                }
            }
            mLastOffset = positionOffset;
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
            if (mState == ViewPager.SCROLL_STATE_IDLE && mChangePosition != position ) {
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

    /**
     * Determine if there is any change to the currently visible VersoPageView's in the ViewPager.
     * If so,  callbacks will be triggered.
     */
    @SuppressWarnings("unchecked")
    private void updateVisiblePages() {
        List<VersoPageViewFragment> fragments = mVersoAdapter.getVersoFragments();
        HashSet<Integer> currentPages = new HashSet<>();
        for (VersoPageViewFragment f : fragments) {
            f.getVisiblePages(mViewPagerHitRect, currentPages);
        }
        Collection<Integer> added = diff(currentPages, mCurrentVisiblePages);
        Collection<Integer> removed = diff(mCurrentVisiblePages, currentPages);
        if (!added.isEmpty() || !removed.isEmpty()) {
            // There is new state in visible pages, we need to update
            mCurrentVisiblePages.clear();
            mCurrentVisiblePages.addAll(currentPages);
            if (mPageChangeListener != null) {
                mPageChangeListener.onVisiblePageIndexesChanged(
                        getVisiblePages(),
                        collectionToArray(added),
                        collectionToArray(removed));
            }
        }
    }

    private static void updateHitRect(View view, Rect rect, int[] out) {
        if (view != null) {
            view.getHitRect(rect);
            view.getLocationOnScreen(out);
            rect.offsetTo(out[0], out[1]);
        } else {
            rect.set(0,0,0,0);
        }
    }

    /**
     * Takes a {@link Collection} of {@link Integer}, sorts it in ascending order and converts it to an array.
     * @param collection A {@link Collection} to convert
     * @return An array
     */
    private static int[] sortCollection(Collection<Integer> collection) {
        List<Integer> ints = new ArrayList<>();
        for (Integer i : collection) {
            if (i != null) {
                ints.add(i);
            }
        }
        Collections.sort(ints);
        return collectionToArray(ints);
    }

    /**
     * Takes a {@link Collection} of {@link Integer} and converts it to an array.
     * @param collection A {@link Collection} to convert
     * @return An array
     */
    private static int[] collectionToArray(Collection<Integer> collection) {
        int[] tmp = new int[collection.size()];
        int i = 0;
        for (Iterator<Integer> it = collection.iterator(); it.hasNext(); i++) {
            tmp[i] = it.next();
        }
        return tmp;
    }

    /**
     * Finds the diff between two {@link Collection}'s, so elements found in {@code lhs} but not in
     * {@code rhs} will be added to the result set.
     * @param lhs {@link Collection} to find new elements in
     * @param rhs {@link Collection} diff against
     * @return A list containing the result of the diff
     */
    private static <T> Collection<T> diff(Collection<T> lhs, Collection<T> rhs) {
        Collection<T> result = new ArrayList<>();
        for (T t : lhs) {
            if (!rhs.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Get the currently visible pages in the {@link VersoFragment}
     * @return the visible pages
     */
    public int[] getVisiblePages() {
        return sortCollection(mCurrentVisiblePages);
    }

    public int[] getActivePages() {
        int position = mVersoViewPager.getCurrentItem();
        return mVersoPublication.getConfiguration().getSpreadProperty(position).getPages();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Configuration config = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        if (config.orientation != newConfig.orientation) {
            // Do work
            // To correctly destroy the state of the VersoAdapter
            // we will mimic the lifecycle of a fragment being destroyed
            // and restored.
            internalPause();
            Bundle b = new Bundle();
            onSaveInstanceState(b);
            onRestoreState(b);
//            mSavedInstanceState = null;
            internalResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        internalResume();
    }

    private void internalResume() {
        updateVisiblePages();
    }

    @Override
    public void onPause() {
        internalPause();
        super.onPause();
    }

    private void internalPause() {

    }

    private void onRestoreState(Bundle savedInstanceState) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
