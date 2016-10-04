package com.shopgun.android.verso.sample.textview;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopgun.android.utils.UnitUtils;
import com.shopgun.android.verso.VersoPageView;
import com.shopgun.android.verso.VersoPublication;
import com.shopgun.android.verso.VersoSpreadConfiguration;

public class TextViewPublication implements VersoPublication {

    Context mContext;
    TextViewSpreadConfiguration mConfiguration;

    public TextViewPublication(Context context) {
        mContext = context;
        mConfiguration = new TextViewSpreadConfiguration(context);
    }

    @NonNull
    @Override
    public View getPageView(ViewGroup container, int page) {
        VersoTextView tv = new VersoTextView(container.getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setTextSize(UnitUtils.dpToPx(30, container.getContext()));
        tv.setText("Page " + page);
        return tv;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { }

    protected TextViewPublication(Parcel in) {
        this.mConfiguration = in.readParcelable(TextViewSpreadConfiguration.class.getClassLoader());
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
