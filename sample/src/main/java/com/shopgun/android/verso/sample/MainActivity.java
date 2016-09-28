package com.shopgun.android.verso.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.shopgun.android.utils.TextUtils;
import com.shopgun.android.verso.VersoFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    
    public static final String FRAG_TAG = "frag_tag";

    TextView mInfo;
    String mScrollInfo = "no info";
    String mChangeInfo = "no info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VersoFragment fragment = (VersoFragment) getSupportFragmentManager().findFragmentById(R.id.verso);
        if (fragment == null) {
            fragment = VersoFragment.newInstance(new CatalogPublication());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.verso, fragment, FRAG_TAG)
                    .addToBackStack(FRAG_TAG)
                    .commit();
        }

        mInfo = (TextView) findViewById(R.id.info);
        updateInfo();

        fragment.setOnPageChangeListener(new VersoFragment.OnPageChangeListener() {
            @Override
            public void onPagesScrolled(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages) {
                mScrollInfo = make("scroll   ", currentPosition, currentPages, previousPosition, previousPages);
                updateInfo();
            }

            @Override
            public void onPagesChanged(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages) {
                mChangeInfo = make("change", currentPosition, currentPages, previousPosition, previousPages);
                updateInfo();
            }

            @Override
            public void onVisiblePageIndexesChanged(int[] pages, int[] removedPages) {

            }

            private String make(String what, int currentPosition, int[] currentPages, int previousPosition, int[] previousPages) {
                if (currentPosition > previousPosition) {
                    return String.format(Locale.US, "%s pos[ %s -> %s ], pages[ %s -> %s ]",
                            what, previousPosition, currentPosition, TextUtils.join(",", previousPages), TextUtils.join(",", currentPages));
                } else {
                    return String.format(Locale.US, "%s pos[ %s <- %s ], pages[ %s <- %s ]",
                            what, currentPosition, previousPosition, TextUtils.join(",", currentPages), TextUtils.join(",", previousPages));
                }
            }

        });

    }

    private void updateInfo() {
        mInfo.setText(mChangeInfo + "\n" + mScrollInfo);
    }

}