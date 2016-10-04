package com.shopgun.android.verso.sample.textview;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.shopgun.android.utils.TextUtils;
import com.shopgun.android.verso.VersoFragment;
import com.shopgun.android.verso.sample.R;
import com.shopgun.android.verso.sample.imageview.CatalogActivity;
import com.shopgun.android.verso.sample.imageview.CatalogPublication;

import java.util.Locale;

public class TextViewActivity extends AppCompatActivity {

    public static final String TAG = CatalogActivity.class.getSimpleName();

    public static final String FRAG_TAG = "frag_tag";

    TextView mInfo;
    String mPagesInfoScroll = "no info";
    String mPagesInfoChange = "no info";
    String mZoomInfo = "no info";
    String mPanInfo = "no info";
    String mVisiblePagesInfo = "no info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verso);

        VersoFragment fragment = (VersoFragment) getSupportFragmentManager().findFragmentById(R.id.verso);
        if (fragment == null) {
            fragment = VersoFragment.newInstance(new TextViewPublication(this));
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.verso, fragment, FRAG_TAG)
                    .addToBackStack(FRAG_TAG)
                    .commit();
        }

        mInfo = (TextView) findViewById(R.id.info);
        mInfo.setMinLines(5);
        mInfo.setMaxLines(5);
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

        fragment.setOnTapListener(new VersoFragment.OnTapListener() {
            @Override
            public boolean onContentTap(int[] pages, float absX, float absY, float relX, float relY) {
                toast("onContentTap", pages, relX, relY);
                return true;
            }

            @Override
            public boolean onViewTap(int[] pages, float absX, float absY) {
                toast("onViewTap", pages, absX, absY);
                return true;
            }
        });

        fragment.setOnDoubleTapListener(new VersoFragment.OnDoubleTapListener() {
            @Override
            public boolean onContentDoubleTap(int[] pages, float absX, float absY, float relX, float relY) {
                toast("onContentDoubleTap", pages, absX, absY);
                return false;
            }

            @Override
            public boolean onViewDoubleTap(int[] pages, float absX, float absY) {
                toast("onViewDoubleTap", pages, absX, absY);
                return false;
            }
        });

        fragment.setOnLongTapListener(new VersoFragment.OnLongTapListener() {
            @Override
            public void onContentLongTap(int[] pages, float absX, float absY, float relX, float relY) {
                toast("onContentLongTap", pages, relX, relY);
            }

            @Override
            public void onViewLongTap(int[] pages, float absX, float absY) {
                toast("onViewLongTap", pages, absX, absY);
            }
        });

    }

    private void toast(String what, int[] pages, float x, float y) {
        String text = String.format(Locale.US, "%s[ pages:%s, x:%.0f, y:%.0f ]", what, TextUtils.join(",", pages), x, y);
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

}
