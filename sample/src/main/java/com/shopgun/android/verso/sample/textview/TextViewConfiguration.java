package com.shopgun.android.verso.sample.textview;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopgun.android.utils.UnitUtils;
import com.shopgun.android.verso.VersoPageView;
import com.shopgun.android.verso.VersoSpreadConfiguration;
import com.shopgun.android.verso.VersoSpreadProperty;
import com.shopgun.android.verso.sample.SpreadLayoutImpl;
import com.shopgun.android.verso.sample.SpreadPropertyImpl;

public class TextViewConfiguration implements VersoSpreadConfiguration {

    public TextViewConfiguration() {
    }

    @NonNull
    @Override
    public View getPageView(ViewGroup container, int page) {
        return new VersoTextView(container.getContext(), page);
    }

    @Override
    public int getPageCount() {
        return 6;
    }

    @Override
    public int getSpreadCount() {
        return 6;
    }

    @Override
    public int getSpreadMargin() {
        return 30;
    }

    @Override
    public VersoSpreadProperty getSpreadProperty(int spreadPosition) {
        return new SpreadPropertyImpl(new int[]{spreadPosition}, 0.8f, 1.0f, (spreadPosition % 2 == 0 ? 1.0f : 4.0f));
    }

    @Override
    public int getSpreadPositionFromPage(int page) {
            return page;
        }

    private class VersoTextView extends TextView implements VersoPageView {

        private float mTextSize = 30;

        public VersoTextView(Context context, int page) {
            super(context);
            onZoom(1f);
            setText("Page " + page);
        }

        @Override
        public boolean onZoom(float scale) {
            setTextSize(UnitUtils.dpToPx(mTextSize/scale, getContext()));
            return true;
        }

        @Override
        public void setOnCompletionListener() {

        }

        @Override
        public OnLoadCompletionListener getOnLoadCompleteListener() {
            return null;
        }

        @Override
        public void onVisible() {

        }

        @Override
        public void onInvisible() {

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public View getSpreadOverlay(ViewGroup container, int[] pages) {
        return new SpreadLayoutImpl(container.getContext(), pages);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { }

    protected TextViewConfiguration(Parcel in) {

    }

    public static final Creator<TextViewConfiguration> CREATOR = new Creator<TextViewConfiguration>() {
        @Override
        public TextViewConfiguration createFromParcel(Parcel source) {
            return new TextViewConfiguration(source);
        }

        @Override
        public TextViewConfiguration[] newArray(int size) {
            return new TextViewConfiguration[size];
        }
    };
}
