package com.shopgun.android.verso.sample.textview;

import android.content.Context;
import android.os.Parcel;

import com.shopgun.android.verso.VersoSpreadConfiguration;
import com.shopgun.android.verso.VersoSpreadProperty;
import com.shopgun.android.verso.sample.SpreadPropertyImpl;

public class TextViewSpreadConfiguration implements VersoSpreadConfiguration {

    public TextViewSpreadConfiguration(Context context) {

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
        return new SpreadPropertyImpl(new int[]{spreadPosition}, 0.8f, 4.0f);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    protected TextViewSpreadConfiguration(Parcel in) {
    }

    public static final Creator<TextViewSpreadConfiguration> CREATOR = new Creator<TextViewSpreadConfiguration>() {
        @Override
        public TextViewSpreadConfiguration createFromParcel(Parcel source) {
            return new TextViewSpreadConfiguration(source);
        }

        @Override
        public TextViewSpreadConfiguration[] newArray(int size) {
            return new TextViewSpreadConfiguration[size];
        }
    };
}
