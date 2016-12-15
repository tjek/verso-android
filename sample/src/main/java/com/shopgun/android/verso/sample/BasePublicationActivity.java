package com.shopgun.android.verso.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.shopgun.android.utils.TextUtils;
import com.shopgun.android.verso.VersoFragment;
import com.shopgun.android.verso.VersoPageViewFragment;
import com.shopgun.android.verso.VersoSpreadConfiguration;
import com.shopgun.android.verso.VersoTapInfo;
import com.shopgun.android.verso.VersoZoomPanInfo;

import java.util.Locale;

public abstract class BasePublicationActivity extends AppCompatActivity {

    public static final String TAG = BasePublicationActivity.class.getSimpleName();

    public static final String FRAG_TAG = "frag_tag";

    TextView mInfo;
    String mPagesInfoScroll = "no info";
    String mPagesInfoChange = "no info";
    String mZoomInfo = "no info";
    String mPanInfo = "no info";
    String mVisiblePagesInfo = "no info";

    public abstract VersoSpreadConfiguration getSpreadConfiguration();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verso);

        VersoFragment fragment = (VersoFragment) getSupportFragmentManager().findFragmentById(R.id.verso);
        if (fragment == null) {
            fragment = new VersoFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.verso, fragment, FRAG_TAG)
                    .addToBackStack(FRAG_TAG)
                    .commit();
        }
        fragment.enableBounceDecore();
        fragment.setVersoSpreadConfiguration(getSpreadConfiguration());
        setupListeners(fragment);

        mInfo = (TextView) findViewById(R.id.info);
        mInfo.setMinLines(5);
        mInfo.setMaxLines(5);
        updateInfo();

    }

    private void setupListeners(VersoFragment fragment) {

        fragment.addOnPageChangeListener(new VersoFragment.OnPageChangeListener() {
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
            public void onVisiblePageIndexesChanged(int[] pages, int[] added, int[] removed) {
                mVisiblePagesInfo = "visible    [ p:" + TextUtils.join(",", pages) +
                        ", a:" + TextUtils.join(",", added) +
                        ", r:" + TextUtils.join(",", removed) + " ]";
                updateInfo();
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
        fragment.setOnZoomListener(new VersoPageViewFragment.OnZoomListener() {
            @Override
            public void onZoomBegin(VersoZoomPanInfo info) {
                log("onZoomBegin", info);
            }

            @Override
            public void onZoom(VersoZoomPanInfo info) {
                log("onZoom     ", info);
            }

            @Override
            public void onZoomEnd(VersoZoomPanInfo info) {
                log("onZoomEnd  ", info);
            }

            private void log(String what, VersoZoomPanInfo info) {
                mZoomInfo = String.format(Locale.US, "%s[pages:%s, scale:%.2f]", what, TextUtils.join(",", info.getPages()), info.getScale());
                updateInfo();
            }

        });

        fragment.setOnPanListener(new VersoPageViewFragment.OnPanListener() {
            @Override
            public void onPanBegin(VersoZoomPanInfo info) {
                log("onPanBegin ", info);
            }

            @Override
            public void onPan(VersoZoomPanInfo info) {
                log("onPan ", info);
            }

            @Override
            public void onPanEnd(VersoZoomPanInfo info) {
                log("onPanEnd ", info);
            }

            private void log(String what, VersoZoomPanInfo info) {
                mPanInfo = String.format(Locale.US, "%s[pages:%s, rect:%s]", what, TextUtils.join(",", info.getPages()), info.getViewRect().toShortString());
                updateInfo();
            }

        });

        fragment.setOnTapListener(new VersoPageViewFragment.OnTapListener() {
            @Override
            public boolean onTap(VersoTapInfo info) {
                toast("onTap", info);
                return false;
            }
        });

        fragment.setOnDoubleTapListener(new VersoPageViewFragment.OnDoubleTapListener() {
            @Override
            public boolean onDoubleTap(VersoTapInfo info) {
                toast("onDoubleTap", info);
                return false;
            }
        });

        fragment.setOnLongTapListener(new VersoPageViewFragment.OnLongTapListener() {
            @Override
            public void onLongTap(VersoTapInfo info) {
                toast("onLongTap", info);
            }
        });

    }

    private void toast(String what, VersoTapInfo info) {
        String text = String.format(Locale.US, "%s[ pages:%s, x:%.0f, y:%.0f ]", what, info.getPageTapped(), info.getPercentX(), info.getPercentY());
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void updateInfo() {
        StringBuilder sb = new StringBuilder()
                .append(mPagesInfoChange).append("\n")
                .append(mPagesInfoScroll).append("\n")
                .append(mZoomInfo).append("\n")
                .append(mPanInfo).append("\n")
                .append(mVisiblePagesInfo);
        mInfo.setText(sb.toString());
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
