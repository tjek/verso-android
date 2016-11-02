package com.shopgun.android.verso.sample.textview;

import com.shopgun.android.verso.VersoSpreadConfiguration;
import com.shopgun.android.verso.sample.BasePublicationActivity;

public class TextViewActivity extends BasePublicationActivity {

    public static final String TAG = TextViewActivity.class.getSimpleName();

    @Override
    public VersoSpreadConfiguration getSpreadConfiguration() {
        return new TextViewConfiguration();
    }

}
