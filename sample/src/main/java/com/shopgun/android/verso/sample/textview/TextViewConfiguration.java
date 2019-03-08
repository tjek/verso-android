package com.shopgun.android.verso.sample.textview;

import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;

import com.shopgun.android.utils.UnitUtils;
import com.shopgun.android.verso.VersoPageView;
import com.shopgun.android.verso.VersoPageViewFragment;
import com.shopgun.android.verso.VersoSpreadConfiguration;
import com.shopgun.android.verso.VersoSpreadProperty;
import com.shopgun.android.verso.sample.SpreadLayoutImpl;
import com.shopgun.android.verso.sample.SpreadPropertyImpl;

public class TextViewConfiguration implements VersoSpreadConfiguration {

    public TextViewConfiguration() {
    }

    @NonNull
    @Override
    public View getPageView(ViewGroup container, int page) {
        return new VersoTextView(container.getContext(), page);
    }

    @Override
    public int getPageCount() {
        return 6;
    }

    @Override
    public int getSpreadCount() {
        return 6;
    }

    @Override
    public int getSpreadMargin() {
        return 30;
    }

    @Override
    public VersoSpreadProperty getSpreadProperty(int spreadPosition) {
        return new SpreadPropertyImpl(new int[]{spreadPosition}, 0.8f, 1.0f, (spreadPosition % 2 == 0 ? 1.0f : 4.0f));
    }

    @Override
    public int getSpreadPositionFromPage(int page) {
            return page;
        }

    @Override
    public int[] getPagesFromSpreadPosition(int spreadPosition) {
        return new int[spreadPosition];
    }

    @Override
    public boolean hasData() {
        return true;
    }

    private class VersoTextView extends AppCompatTextView implements VersoPageView {

        private int mPage;
        private float mTextSize = 30;

        public VersoTextView(Context context, int page) {
            super(context);
            mPage = page;
            onZoom(1f);
            setText("Page " + page);
        }

        @Override
        public boolean onZoom(float scale) {
            setTextSize(UnitUtils.dpToPx(mTextSize/scale, getContext()));
            return true;
        }

        @Override
        public int getPage() {
            return mPage;
        }

        @Override
        public void setOnLoadCompleteListener(VersoPageViewFragment.OnLoadCompleteListener listener) {

        }

        @Override
        public void onVisible() {

        }

        @Override
        public void onInvisible() {

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public View getSpreadOverlay(ViewGroup container, int[] pages) {
        return new SpreadLayoutImpl(container.getContext(), pages);
    }

}
