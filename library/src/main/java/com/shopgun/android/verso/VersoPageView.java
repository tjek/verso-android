package com.shopgun.android.verso;

public interface VersoPageView {
    boolean onZoom(float scale);
    void setOnCompletionListener();
    OnLoadCompletionListener getOnLoadCompleteListener();
    interface OnLoadCompletionListener {
        void onLoaded(VersoPageView view);
    }
}
