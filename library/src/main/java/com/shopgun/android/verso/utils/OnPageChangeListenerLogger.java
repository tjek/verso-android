package com.shopgun.android.verso.utils;

import com.shopgun.android.utils.TextUtils;
import com.shopgun.android.utils.log.L;
import com.shopgun.android.utils.log.Logger;
import com.shopgun.android.verso.VersoFragment;

import java.util.Locale;

public class OnPageChangeListenerLogger implements VersoFragment.OnPageChangeListener {

    public static final String TAG = OnPageChangeListenerLogger.class.getSimpleName();

    private static final String PAGE_CHANGE_FORMAT = "onPagesChanged[ currentPosition:%s currentPages: %s previousPosition: %s previousPages:%s ]";
    private static final String PAGE_SCROLLED_FORMAT = "onPagesScrolled[ currentPosition:%s currentPages: %s previousPosition: %s previousPages:%s ]";
    private static final String VISIBLE_PAGES_CHANGED = "onVisiblePageIndexesChanged[ pages:%s, added:%s, removed:%s ]";

    private String mTag;
    private Logger mLogger;

    public OnPageChangeListenerLogger() {
        this(L.getLogger(), TAG);
    }

    public OnPageChangeListenerLogger(String tag) {
        this(L.getLogger(), tag);
    }

    public OnPageChangeListenerLogger(Logger logger, String tag) {
        mLogger = logger;
        mTag = tag;
    }

    @Override
    public void onPagesScrolled(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages) {
        log(PAGE_SCROLLED_FORMAT, currentPosition, TextUtils.join(currentPages), previousPosition, TextUtils.join(previousPages));
    }

    @Override
    public void onPagesChanged(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages) {
        log(PAGE_CHANGE_FORMAT, currentPosition, TextUtils.join(currentPages), previousPosition, TextUtils.join(previousPages));
    }

    @Override
    public void onVisiblePageIndexesChanged(int[] pages, int[] added, int[] removed) {
        log(VISIBLE_PAGES_CHANGED, TextUtils.join(pages), TextUtils.join(added), TextUtils.join(removed));
    }

    private void log(String format, Object... args) {
        mLogger.d(mTag, String.format(Locale.US, format, args));
    }

}
