package com.shopgun.android.verso.sample;

import android.content.Context;
import android.widget.ImageView;

import com.shopgun.android.utils.log.L;
import com.shopgun.android.verso.VersoPageView;
import com.squareup.picasso.Picasso;

public class CatalogPageView extends ImageView implements VersoPageView {

    public static final String TAG = CatalogPageView.class.getSimpleName();

    private final CatalogPage mCatalogPage;

    public CatalogPageView(Context context, CatalogPage catalogPage) {
        super(context);
        mCatalogPage = catalogPage;
        setAdjustViewBounds(true);
        Picasso.with(getContext()).load(mCatalogPage.thumb).into(this);
    }

    @Override
    public boolean onZoom(float scale) {
        log("onZoom: " + scale);
        return false;
    }

    @Override
    public void setOnCompletionListener() {
        log("setOnCompletionListener");
    }

    @Override
    public OnLoadCompletionListener getOnLoadCompleteListener() {
        log("getOnLoadCompleteListener");
        return null;
    }

    private void log(String msg) {
        L.d(TAG, String.format("[%s] %s", mCatalogPage.page, msg));
    }

}
