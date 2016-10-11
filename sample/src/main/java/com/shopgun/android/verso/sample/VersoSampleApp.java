package com.shopgun.android.verso.sample;

import android.app.Application;
import android.content.Context;

public class VersoSampleApp extends Application {

    public static VersoSampleApp mVersoSampleApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mVersoSampleApp = this;
    }

    public static Context getContext() {
        return mVersoSampleApp.getApplicationContext();
    }

}
