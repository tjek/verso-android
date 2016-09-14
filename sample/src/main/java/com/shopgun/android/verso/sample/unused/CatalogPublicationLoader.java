package com.shopgun.android.verso.sample.unused;

import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CatalogPublicationLoader implements Runnable {

    public interface OnCompleteListener {
        void onComplete(Point point);
    }

    final String mUrl;
    final OnCompleteListener mOnCompleteListener;

    public CatalogPublicationLoader(String url) {
        this(url, null);
    }

    public CatalogPublicationLoader(String url, OnCompleteListener onCompleteListener) {
        mUrl = url;
        mOnCompleteListener = onCompleteListener;
    }

    @Override
    public void run() {
        Point point = execute();
        if (mOnCompleteListener != null) {
            mOnCompleteListener.onComplete(point);
        }
    }

    public Point execute() {
        try {
            Response response = new OkHttpClient.Builder()
                    .build()
                    .newCall(new Request.Builder()
                            .url(mUrl)
                            .build())
                    .execute();
            if (response.isSuccessful()) {
                InputStream inputStream = response.body().byteStream();
                return decode(inputStream);
            }
        } catch (IOException e) {
        }
        return new Point();
    }

    private Point decode(InputStream inputStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream);
        return new Point(options.outWidth, options.outHeight);
    }

}
