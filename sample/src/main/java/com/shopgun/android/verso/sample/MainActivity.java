package com.shopgun.android.verso.sample;

import android.graphics.Rect;
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
    String mPagesInfoScroll = "no info";
    String mPagesInfoChange = "no info";
    String mZoomInfo = "no info";
    String mPanInfo = "no info";

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
                mPagesInfoScroll = make("onScroll   ", currentPosition, currentPages, previousPosition, previousPages);
                updateInfo();
            }

            @Override
            public void onPagesChanged(int currentPosition, int[] currentPages, int previousPosition, int[] previousPages) {
                mPagesInfoChange = make("onChanged  ", currentPosition, currentPages, previousPosition, previousPages);
                updateInfo();
            }

            @Override
            public void onVisiblePageIndexesChanged(int[] pages, int[] removedPages) {

            }

            private String make(String what, int currentPosition, int[] currentPages, int previousPosition, int[] previousPages) {
                if (currentPosition > previousPosition) {
                    return String.format(Locale.US, "%s[pos:%s -> %s, pages: %s -> %s]",
                            what, previousPosition, currentPosition, TextUtils.join(",", previousPages), TextUtils.join(",", currentPages));
                } else {
                    return String.format(Locale.US, "%s[pos:%s <- %s, pages: %s <- %s]",
                            what, currentPosition, previousPosition, TextUtils.join(",", currentPages), TextUtils.join(",", previousPages));
                }
            }

        });
        fragment.setOnZoomListener(new VersoFragment.OnZoomListener() {
            @Override
            public void onZoomBegin(int[] pages, float scale) {
                log("onZoomBegin", pages, scale);
            }

            @Override
            public void onZoom(int[] pages, float scale) {
                log("onZoom     ", pages, scale);
            }

            @Override
            public void onZoomEnd(int[] pages, float scale) {
                log("onZoomEnd  ", pages, scale);
            }

            private void log(String what, int[] pages, float scale) {
                mZoomInfo = String.format(Locale.US, "%s[pages:%s, scale:%.2f]", what, TextUtils.join(",", pages), scale);
                updateInfo();
            }

        });

        fragment.setOnPanListener(new VersoFragment.OnPanListener() {
            @Override
            public void onPanBegin(int[] pages, Rect viewRect) {
                log("onPanBegin ", pages, viewRect);
            }

            @Override
            public void onPan(int[] pages, Rect viewRect) {
                log("onPan      ", pages, viewRect);
            }

            @Override
            public void onPanEnd(int[] pages, Rect viewRect) {
                log("onPanEnd   ", pages, viewRect);
            }

            private void log(String what, int[] pages, Rect rect) {
                mPanInfo = String.format(Locale.US, "%s[pages:%s, rect:%s]", what, TextUtils.join(",", pages), rect.toShortString());
                updateInfo();
            }

        });

    }

    private void updateInfo() {
        StringBuilder sb = new StringBuilder()
                .append(mPagesInfoChange).append("\n")
                .append(mPagesInfoScroll).append("\n")
                .append(mZoomInfo).append("\n")
                .append(mPanInfo);
        mInfo.setText(sb.toString());
    }

}