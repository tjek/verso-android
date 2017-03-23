package com.shopgun.android.verso;

public interface VersoPageView {
    boolean onZoom(float scale);
    int getPage();
    void setOnLoadCompleteListener(VersoPageViewFragment.OnLoadCompleteListener listener);
    void onVisible();
    void onInvisible();
}
