package com.shopgun.android.verso;

import android.os.Parcelable;

public interface VersoSpreadConfiguration extends Parcelable {
    int getPageCount();
    int getSpreadCount();
    int getSpreadMargin();
    VersoSpreadProperty getSpreadProperty(int spreadPosition);
}
