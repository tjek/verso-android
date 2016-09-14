package com.shopgun.android.verso;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class VersoViewPager extends ViewPager {

    VersoPublication mPublication;

    public VersoViewPager(Context context) {
        super(context);
    }

    public VersoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setVersoAdapter(VersoAdapter adapter) {
        super.setAdapter(adapter);
    }

    public VersoAdapter getVersoAdapter() {
        return (VersoAdapter) super.getAdapter();
    }

    @Override
    @Deprecated
    public void setAdapter(PagerAdapter adapter) {
        throw new UnsupportedOperationException("Custom adapters are not allowed. See VersoViewPager.setVersoAdapter(VersoAdapter).");
    }

    @Override
    @Deprecated
    public PagerAdapter getAdapter() {
        throw new UnsupportedOperationException("Custom adapters are not allowed. See VersoViewPager.getVersoAdapter().");
    }

    /**
     * Object used to keep some data when a configuration change happens and the activity is
     * re-created.
     * It's boiler-plate but this is how to save View state.
     */
    private static class SavedState extends BaseSavedState {

        int mCurrentItem;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mCurrentItem = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mCurrentItem);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in ) {
                return new SavedState( in );
            }

            public SavedState[] newArray(int size ) {
                return new SavedState[ size ];
            }
        };
    }

    /**
     * The default {@code super.onSaveInstanceState} and {@code onRestoreInstanceState} don't
     * restore the position on the map as expected (if the instance of {@link TileView} remains the
     * same). For this reason and if a new {@link TileView} instance is created, we have to save
     * the current scale and position on the map, to restore them later when the {@link TileView} is
     * recreated.
     */
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState( superState );
        ss.mCurrentItem = getCurrentItem();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState( ss.getSuperState() );
        post(new Runnable() {
            @Override
            public void run() {
                setCurrentItem(ss.mCurrentItem);
            }
        });
    }

}
