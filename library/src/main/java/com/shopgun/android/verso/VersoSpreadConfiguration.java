package com.shopgun.android.verso;

public interface VersoSpreadConfiguration {
    int getPageCount();
    int getSpreadCount();
    int getSpreadMargin();
    VersoSpreadProperty getSpreadProperty(int spreadPosition);
}
