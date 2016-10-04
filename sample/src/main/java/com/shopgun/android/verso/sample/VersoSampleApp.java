package com.shopgun.android.verso.sample;

import android.app.Application;
import android.content.Context;

public class VersoSampleApp extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
