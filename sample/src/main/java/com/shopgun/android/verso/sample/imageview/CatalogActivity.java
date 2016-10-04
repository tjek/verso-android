package com.shopgun.android.verso.sample.imageview;

import com.shopgun.android.verso.VersoPublication;
import com.shopgun.android.verso.sample.BasePublicationActivity;

public class CatalogActivity extends BasePublicationActivity {

    public static final String TAG = CatalogActivity.class.getSimpleName();

    @Override
    public VersoPublication getPublication() {
        return new CatalogPublication();
    }

}