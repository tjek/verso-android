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
import com.shopgun.android.verso.VersoPublication;
import com.shopgun.android.verso.VersoSpreadConfiguration;
import com.shopgun.android.verso.VersoSpreadProperty;
import com.shopgun.android.verso.sample.SpreadLayoutImpl;
import com.shopgun.android.verso.sample.SpreadPropertyImpl;

public class TextViewPublication implements VersoPublication {

    TextViewSpreadConfiguration mConfiguration;

    public TextViewPublication() {
        mConfiguration = new TextViewSpreadConfiguration();
    }

    @NonNull
    @Override
    public View getPageView(ViewGroup container, int page) {
        Context ctx = container.getContext();
        VersoTextView tv = new VersoTextView(ctx);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setTextSize(UnitUtils.dpToPx(30, ctx));
        tv.setText("Page " + page);
        return tv;
    }

    private class TextViewSpreadConfiguration implements VersoSpreadConfiguration {

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
            return new SpreadPropertyImpl(new int[]{spreadPosition}, 0.8f, 4.0f);
        }

        @Override
        public int getSpreadPositionFromPage(int page) {
            return page;
        }

    }

    private class VersoTextView extends TextView implements VersoPageView {

        public VersoTextView(Context context) {
            super(context);
        }

        @Override
        public boolean onZoom(float scale) {
            return false;
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

    @NonNull
    @Override
    public VersoSpreadConfiguration getConfiguration() {
        return mConfiguration;
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

    protected TextViewPublication(Parcel in) {
        this();
    }

    public static final Creator<TextViewPublication> CREATOR = new Creator<TextViewPublication>() {
        @Override
        public TextViewPublication createFromParcel(Parcel source) {
            return new TextViewPublication(source);
        }

        @Override
        public TextViewPublication[] newArray(int size) {
            return new TextViewPublication[size];
        }
    };
}
