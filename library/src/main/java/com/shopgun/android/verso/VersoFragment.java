package com.shopgun.android.verso;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.CenteredViewPager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.shopgun.android.utils.TextUtils;
import com.shopgun.android.utils.log.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import me.everything.android.ui.overscroll.HorizontalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;

public class VersoFragment extends Fragment {

    public static final String TAG = VersoFragment.class.getSimpleName();

    private static final String SAVED_STATE = "verso_saved_state";

    VersoSpreadConfiguration mVersoSpreadConfiguration;
    VersoViewPager mVersoViewPager;
    VersoAdapter mVersoAdapter;

    boolean mBounceDecoreEnabled = false;
    HorizontalOverScrollBounceEffectDecorator mBounceDecore;
    int mCurrentOrientation;
    int mPage = 0;
    HashSet<Integer> mCurrentVisiblePages = new HashSet<>();
    Rect mViewPagerHitRect = new Rect();
    int[] mOutLocation = new int[2];

    PageChangeDispatcher mPageChangeDispatcher;
    PageViewEventDispatcher mDispatcher;
    VersoOnLayoutChanged mVersoOnLayoutChanged;

    List<OnPageChangeListener> mPageChangeListeners;
    VersoPageViewFragment.OnZoomListener mZoomListener;
    VersoPageViewFragment.OnPanListener mPanListener;
    VersoPageViewFragment.OnTouchListener mTouchListener;
    VersoPageViewFragment.OnTapListener mTapListener;
    VersoPageViewFragment.OnDoubleTapListener mDoubleTapListener;
    VersoPageViewFragment.OnLongTapListener mLongTapListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVersoViewPager = (VersoViewPager) inflater.inflate(R.layout.verso_fragment, container, false);

        mVersoViewPager.setOffscreenPageLimit(2);

        mVersoOnLayoutChanged = new VersoOnLayoutChanged();
        mVersoViewPager.addOnLayoutChangeListener(mVersoOnLayoutChanged);
        mVersoViewPager.getViewTreeObserver().addOnPreDrawListener(mVersoOnLayoutChanged);

        mPageChangeDispatcher = new PageChangeDispatcher();
        mVersoViewPager.addOnPageChangeListener(mPageChangeDispatcher);

        // Omit the left/right edge compat, and use over-scrolling instead
        mBounceDecore = new HorizontalOverScrollBounceEffectDecorator(mPageChangeDispatcher);

        if (savedInstanceState != null) {
            SavedState savedState = savedInstanceState.getParcelable(SAVED_STATE);
            onRestoreState(savedState);
        }

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
                int[] pages = mVersoSpreadConfiguration.getSpreadProperty(getPosition()).getPages();
                dispatchOnPagesChanged(getPosition(), pages, getPosition(), pages);
                mVersoViewPager.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
            return false;
        }

    }

    private class PageChangeDispatcher implements CenteredViewPager.OnPageChangeListener,
            IOverScrollDecoratorAdapter {

        int mState = ViewPager.SCROLL_STATE_IDLE;
        int mCurrentPosition = 0;
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
            int[] previousPages = mVersoSpreadConfiguration.getSpreadProperty(prevPos).getPages();
            int[] currentPages = mVersoSpreadConfiguration.getSpreadProperty(mScrollPosition).getPages();
            dispatchOnPagesScrolled(mScrollPosition, currentPages, prevPos, previousPages);
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
            if (mState == ViewPager.SCROLL_STATE_IDLE && mCurrentPosition != position ) {
                int prevPos = mCurrentPosition;
                mCurrentPosition = position;
                mScrollPosition = mCurrentPosition;
                int[] previousPages = mVersoSpreadConfiguration.getSpreadProperty(prevPos).getPages();
                int[] currentPages = mVersoSpreadConfiguration.getSpreadProperty(mCurrentPosition).getPages();
                dispatchOnPagesChanged(mCurrentPosition, currentPages, prevPos, previousPages);
            }
        }

        @Override
        public View getView() {
            return mVersoViewPager;
        }

        @Override
        public boolean isInAbsoluteStart() {
            return mBounceDecoreEnabled && mScrollPosition == 0;
        }

        @Override
        public boolean isInAbsoluteEnd() {
            return mBounceDecoreEnabled && mScrollPosition == mVersoAdapter.getCount()-1;
        }
    }

    /**
     * Determine if there is any change to the currently visible VersoPageView's in the ViewPager.
     * If so,  callbacks will be triggered.
     */
    @SuppressWarnings("unchecked")
    private void updateVisiblePages() {
        if (mVersoAdapter == null) {
            return;
        }
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
            dispatchOnVisiblePageIndexesChanged(
                    getVisiblePages(),
                    collectionToArray(added),
                    collectionToArray(removed));
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

    public int getPosition() {
        return mPageChangeDispatcher == null ? 0 : mPageChangeDispatcher.mCurrentPosition;
    }

    public int[] getCurrentPages() {
        if (mVersoSpreadConfiguration != null) {
            VersoSpreadProperty property = mVersoSpreadConfiguration.getSpreadProperty(getPosition());
            return property.getPages();
        }
        return new int[]{};
    }

    public void setBounceDecoreEnabled(boolean bounce) {
        mBounceDecoreEnabled = bounce;
    }

    public boolean isBounceDecoreEnabled() {
        return mBounceDecoreEnabled;
    }

    /**
     * Set the {@link VersoFragment} to show the given page number in the catalog.
     * Note that page number doesn't directly correlate to the position of the {@link VersoViewPager}.
     *
     * @param page The page to turn to
     */
    public void setPage(int page) {
        if (page >= 0) {
            mPage = page;
            if (mVersoSpreadConfiguration != null) {
                int position = mVersoSpreadConfiguration.getSpreadPositionFromPage(page);
                setPosition(position);
            }
        }
    }

    /**
     * Set the position of the {@link VersoFragment}.
     * Note that this does not correlate directly to the catalog page number.
     *
     * @param position A position
     */
    private void setPosition(int position) {
        if (position >= 0 &&
                mVersoViewPager != null &&
                mVersoViewPager.getCurrentItem() != position) {
            mVersoViewPager.setCurrentItem(position);
        }
    }

    /**
     * Go to the next page in the catalog
     */
    public void nextPage() {
        mVersoViewPager.setCurrentItem(getPosition() + 1, true);
    }

    /**
     * Go to the previous page in the catalog
     */
    public void previousPage() {
        mVersoViewPager.setCurrentItem(getPosition() - 1, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        onInternalResume(getResources().getConfiguration());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mCurrentOrientation != newConfig.orientation) {
            // To correctly destroy the state of the VersoAdapter
            // we will mimic the lifecycle of a fragment being destroyed and restored.
            SavedState savedState = new SavedState(this);
            mVersoSpreadConfiguration.onConfigurationChanged(newConfig);
            notifyVersoConfigurationChanged();
            onRestoreState(savedState);
            onInternalResume(newConfig);
        }
    }

    private void onInternalResume(Configuration config) {
        mCurrentOrientation = config.orientation;
        if (mVersoSpreadConfiguration != null) {
            mVersoSpreadConfiguration.onConfigurationChanged(config);
            if (mVersoViewPager != null) {
                mVersoViewPager.setPageMargin(mVersoSpreadConfiguration.getSpreadMargin());
            }
        }
        ensureAdapter();
        setPage(mPage);
    }

    public void setVersoSpreadConfiguration(VersoSpreadConfiguration configuration) {
        mVersoSpreadConfiguration = configuration;
        ensureAdapter();
    }

    public VersoSpreadConfiguration getVersoSpreadConfiguration() {
        return mVersoSpreadConfiguration;
    }

    protected void onRestoreState(SavedState ss) {
        if (ss != null) {
            mBounceDecoreEnabled = ss.bounceDecore;
            mCurrentVisiblePages.clear();
            for (int page : ss.visiblePages) {
                mCurrentVisiblePages.add(page);
            }
            if (ss.pages != null && ss.pages.length > 0) {
                setPage(ss.pages[0]);
            }
        }
    }

    public void notifyVersoConfigurationChanged() {
        ensureAdapter();
        if (mVersoAdapter != null) {
            mVersoAdapter.notifyDataSetChanged();
        }
    }

    private void ensureAdapter() {
        if (mVersoSpreadConfiguration != null) {
            if (mVersoAdapter == null) {
                mVersoAdapter = new VersoAdapter(getFragmentManager(), mVersoSpreadConfiguration);
                mDispatcher = new PageViewEventDispatcher();
                mVersoAdapter.setOnTouchListener(mDispatcher);
                mVersoAdapter.setOnTapListener(mDispatcher);
                mVersoAdapter.setOnDoubleTapListener(mDispatcher);
                mVersoAdapter.setOnLongTapListener(mDispatcher);
                mVersoAdapter.setOnZoomListener(mDispatcher);
                mVersoAdapter.setOnPanListener(mDispatcher);
            }

            if (mVersoViewPager != null && mVersoSpreadConfiguration.hasData()) {
                mVersoViewPager.setAdapter(mVersoAdapter);
                setPage(mPage);
            }

        } else if (mVersoViewPager != null) {
            mVersoAdapter = null;
            mVersoViewPager.setAdapter(null);
            mVersoViewPager.setCurrentItem(0);
        }
    }

    @Override
    public void onPause() {
        clearAdapter();
        super.onPause();
    }

    /**
     * This MUST be called from {@link android.app.Activity#onSaveInstanceState(Bundle)}
     * prior to calling the super class.
     */
    public void clearAdapter() {
        if (mVersoAdapter != null) {
            mVersoAdapter.clearState();
        }
        mVersoViewPager.setAdapter(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_STATE, new SavedState(this));
    }

    private class PageViewEventDispatcher implements
            VersoPageViewFragment.OnTouchListener,
            VersoPageViewFragment.OnTapListener,
            VersoPageViewFragment.OnDoubleTapListener,
            VersoPageViewFragment.OnLongTapListener,
            VersoPageViewFragment.OnZoomListener,
            VersoPageViewFragment.OnPanListener {

        @Override
        public boolean onTouch(int action, VersoTapInfo info) {
            return mTouchListener != null && mTouchListener.onTouch(action, info);
        }

        @Override
        public boolean onTap(VersoTapInfo info) {
            return mTapListener != null && mTapListener.onTap(info);
        }

        @Override
        public boolean onDoubleTap(VersoTapInfo info) {
            return mDoubleTapListener != null && mDoubleTapListener.onDoubleTap(info);
        }

        @Override
        public void onLongTap(VersoTapInfo info) {
            if (mLongTapListener != null) mLongTapListener.onLongTap(info);
        }

        @Override
        public void onZoomBegin(VersoZoomPanInfo info) {
            if (mZoomListener != null) mZoomListener.onZoomBegin(info);
        }

        @Override
        public void onZoom(VersoZoomPanInfo info) {
            if (mZoomListener != null) mZoomListener.onZoom(info);
        }

        @Override
        public void onZoomEnd(VersoZoomPanInfo info) {
            if (mZoomListener != null) mZoomListener.onZoomEnd(info);
        }

        @Override
        public void onPanBegin(VersoZoomPanInfo info) {
            if (mPanListener != null) mPanListener.onPanBegin(info);
        }

        @Override
        public void onPan(VersoZoomPanInfo info) {
            if (mPanListener != null) mPanListener.onPan(info);
        }

        @Override
        public void onPanEnd(VersoZoomPanInfo info) {
            if (mPanListener != null) mPanListener.onPanEnd(info);
        }

    }

    public void addOnPageChangeListener(OnPageChangeListener pageChangeListener) {
        if (mPageChangeListeners == null) {
            mPageChangeListeners = new ArrayList<>();
        }
        mPageChangeListeners.add(pageChangeListener);
    }

    public void removeOnPageChangeListener(OnPageChangeListener pageChangeListener) {
        if (mPageChangeListeners != null) {
            mPageChangeListeners.remove(pageChangeListener);
        }
    }

    public void clearOnPageChangeListeners() {
        if (mPageChangeListeners != null) {
            mPageChangeListeners.clear();
        }
    }

    private void dispatchOnVisiblePageIndexesChanged(int[] pages, int[] added, int[] removed) {
        if (mPageChangeListeners != null) {
            for (int i = 0, z = mPageChangeListeners.size(); i < z; i++) {
                OnPageChangeListener listener = mPageChangeListeners.get(i);
                if (listener != null) {
                    listener.onVisiblePageIndexesChanged(pages, added, removed);
                }
            }
        }
    }

    private void dispatchOnPagesScrolled(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages) {
        if (mPageChangeListeners != null) {
            for (int i = 0, z = mPageChangeListeners.size(); i < z; i++) {
                OnPageChangeListener listener = mPageChangeListeners.get(i);
                if (listener != null) {
                    listener.onPagesScrolled(currentPosition, currentPages, previousPosition, previousPages);
                }
            }
        }
    }

    private void dispatchOnPagesChanged(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages) {
        if (mPageChangeListeners != null) {
            for (int i = 0, z = mPageChangeListeners.size(); i < z; i++) {
                OnPageChangeListener listener = mPageChangeListeners.get(i);
                if (listener != null) {
                    listener.onPagesChanged(currentPosition, currentPages, previousPosition, previousPages);
                }
            }
        }
    }

    public void setOnPanListener(VersoPageViewFragment.OnPanListener panListener) {
        mPanListener = panListener;
    }

    public void setOnZoomListener(VersoPageViewFragment.OnZoomListener zoomListener) {
        mZoomListener = zoomListener;
    }

    public void setOnTouchListener(VersoPageViewFragment.OnTouchListener touchListener) {
        mTouchListener = touchListener;
    }

    public void setOnTapListener(VersoPageViewFragment.OnTapListener tapListener) {
        mTapListener = tapListener;
    }

    public void setOnDoubleTapListener(VersoPageViewFragment.OnDoubleTapListener doubleTapListener) {
        mDoubleTapListener = doubleTapListener;
    }

    public void setOnLongTapListener(VersoPageViewFragment.OnLongTapListener longTapListener) {
        mLongTapListener = longTapListener;
    }

    public interface OnPageChangeListener {
        void onPagesScrolled(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages);
        void onPagesChanged(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages);
        void onVisiblePageIndexesChanged(int[] pages, int[] added, int[] removed);
    }

    private static class SavedState implements Parcelable {

        boolean bounceDecore = false;
        int[] pages;
        int[] visiblePages;

        private SavedState(VersoFragment f) {
            bounceDecore = f.mBounceDecoreEnabled;
            pages = f.getCurrentPages();
            pages = Arrays.copyOf(pages, pages.length);
            visiblePages = f.getVisiblePages();
            visiblePages = Arrays.copyOf(visiblePages, visiblePages.length);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(this.bounceDecore ? (byte) 1 : (byte) 0);
            dest.writeIntArray(this.pages);
            dest.writeIntArray(this.visiblePages);
        }

        protected SavedState(Parcel in) {
            this.bounceDecore = in.readByte() != 0;
            this.pages = in.createIntArray();
            this.visiblePages = in.createIntArray();
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        @Override
        public String toString() {
            return String.format("SavedState[ bounceDecore:%s, pages:%s, visiblePages:%s ]",
                    bounceDecore, TextUtils.join(pages), TextUtils.join(visiblePages));
        }
    }

}
