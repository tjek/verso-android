package com.shopgun.android.verso.sample.imageview;

import com.shopgun.android.verso.VersoSpreadConfiguration;
import com.shopgun.android.verso.sample.BasePublicationActivity;

public class ImageViewActivity extends BasePublicationActivity {

    public static final String TAG = ImageViewActivity.class.getSimpleName();

    @Override
    public VersoSpreadConfiguration getSpreadConfiguration() {
        return new ImageViewConfiguration();
    }

}