package com.shopgun.android.verso.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.shopgun.android.utils.log.L;
import com.shopgun.android.verso.VersoPageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class CatalogPageView extends ImageView implements VersoPageView {

    public static final String TAG = CatalogPageView.class.getSimpleName();

    private final CatalogPage mCatalogPage;
    private Target mViewTarget;
    private Target mZoomTarget;

    public CatalogPageView(Context context, CatalogPage catalogPage) {
        super(context);
        mCatalogPage = catalogPage;
        setAdjustViewBounds(true);
        Picasso.with(getContext()).load(mCatalogPage.view).into(this);
    }

    @Override
    public boolean onZoom(float scale) {
        if (mZoomTarget == null && scale > 1.2) {
            mZoomTarget = new ZoomTarget();
            Picasso.with(getContext()).load(mCatalogPage.zoom).into(mZoomTarget);
        } else if (mZoomTarget != null && scale < 1.2) {
            mZoomTarget = null;
            mViewTarget = new ZoomTarget();
            Picasso.with(getContext()).load(mCatalogPage.view).into(mViewTarget);
        }
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

    class ZoomTarget implements Target {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) { }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) { }

    }

}
