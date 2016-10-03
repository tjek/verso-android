package com.shopgun.android.verso;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.CenteredViewPager;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;

public class VersoViewPager extends CenteredViewPager {

    public static final String TAG = VersoViewPager.class.getSimpleName();

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

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (!(adapter instanceof VersoAdapter)) {
            throw new UnsupportedOperationException("The adapter must be an instance of VersoAdapter.");
        }
        super.setAdapter(adapter);
    }

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
