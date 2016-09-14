package com.shopgun.android.verso;

import android.os.Parcelable;

public interface VersoSpreadConfiguration extends Parcelable {
    int getPageCount();
    int getSpreadCount();
    VersoSpreadProperty getSpreadProperty(int spreadPosition);
}
