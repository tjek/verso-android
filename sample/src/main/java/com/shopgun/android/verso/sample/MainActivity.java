package com.shopgun.android.verso.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.shopgun.android.verso.VersoFragment;

public class MainActivity extends AppCompatActivity {

    public static final String FRAG_TAG = "frag_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.verso);
        if (fragment == null) {
            fragment = VersoFragment.newInstance(new CatalogPublication());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.verso, fragment, FRAG_TAG)
                    .addToBackStack(FRAG_TAG)
                    .commit();
        }

    }
}