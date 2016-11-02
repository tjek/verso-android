package com.shopgun.android.verso.sample.imageview;

import com.shopgun.android.verso.VersoSpreadConfiguration;
import com.shopgun.android.verso.sample.BasePublicationActivity;

public class ImageViewConfigChangeActivity extends BasePublicationActivity {

    public static final String TAG = ImageViewConfigChangeActivity.class.getSimpleName();

    @Override
    public VersoSpreadConfiguration getSpreadConfiguration() {
        return new ImageViewConfiguration();
    }

}