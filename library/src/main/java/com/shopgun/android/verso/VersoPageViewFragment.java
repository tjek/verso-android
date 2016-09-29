package com.shopgun.android.verso;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shopgun.android.zoomlayout.ZoomLayout;
import com.shopgun.android.zoomlayout.ZoomOnDoubleTapListener;

public class VersoPageViewFragment extends Fragment implements
        ZoomLayout.OnZoomListener, ZoomLayout.OnPanListener {

    public static final String TAG = VersoPageViewFragment.class.getSimpleName();
    
    private static final String VERSO_PUBLICATION_KEY = "verso_publication";
    private static final String VERSO_POSITION_KEY = "verso_position";

    public static VersoPageViewFragment newInstance(VersoPublication publication, int position) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(VERSO_PUBLICATION_KEY, publication);
        arguments.putInt(VERSO_POSITION_KEY, position);
        VersoPageViewFragment fragment = new VersoPageViewFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    VersoPublication mVersoPublication;
    int mPosition;
    ZoomLayout.OnZoomListener mOnZoomListener;
    ZoomLayout.OnPanListener mOnPanListener;
    ZoomLayout mZoomLayout;
    LinearLayout mPageContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVersoPublication = getArguments().getParcelable(VERSO_PUBLICATION_KEY);
            mPosition = getArguments().getInt(VERSO_POSITION_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mZoomLayout = (ZoomLayout) inflater.inflate(R.layout.verso_page_layout, container, false);
        mZoomLayout.setOnDoubleTapListener(new ZoomOnDoubleTapListener(false));
        mZoomLayout.setOnZoomListener(VersoPageViewFragment.this);
        mZoomLayout.setOnPanListener(VersoPageViewFragment.this);
        mPageContainer = (LinearLayout) mZoomLayout.findViewById(R.id.verso_pages_container);

        VersoSpreadConfiguration config = mVersoPublication.getConfiguration();
        VersoSpreadProperty spreadConfig = config.getSpreadProperty(mPosition);
        int[] pages = spreadConfig.getPages();

        for (int page : pages) {
            View view = mVersoPublication.getPageView(mPageContainer, page);
            if (!(view instanceof VersoPageView)) {
                throw new IllegalArgumentException("The view must implement VersoPageView");
            }
            mPageContainer.addView(view);
        }
        return mZoomLayout;

    }

    public void setOnZoomListener(ZoomLayout.OnZoomListener listener) {
        mOnZoomListener = listener;
    }

    public void setOnPanListener(ZoomLayout.OnPanListener listener) {
        mOnPanListener = listener;
    }

    @Override
    public void onZoomBegin(ZoomLayout view, float scale) {
        if (mOnZoomListener != null) {
            mOnZoomListener.onZoomBegin(view, scale);
        }
    }

    @Override
    public void onZoom(ZoomLayout view, float scale) {
        int count = mPageContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            final View v = mPageContainer.getChildAt(i);
            if (v instanceof VersoPageView) {
                ((VersoPageView)v).onZoom(scale);
            }
        }
        if (mOnZoomListener != null) {
            mOnZoomListener.onZoom(view, scale);
        }
    }

    @Override
    public void onZoomEnd(ZoomLayout view, float scale) {
        if (mOnZoomListener != null) {
            mOnZoomListener.onZoomEnd(view, scale);
        }
    }

    @Override
    public void onPanBegin(ZoomLayout view) {
        if (mOnPanListener != null) {
            mOnPanListener.onPanBegin(view);
        }
    }

    @Override
    public void onPan(ZoomLayout view) {
        if (mOnPanListener != null) {
            mOnPanListener.onPan(view);
        }
    }

    @Override
    public void onPanEnd(ZoomLayout view) {
        if (mOnPanListener != null) {
            mOnPanListener.onPanEnd(view);
        }
    }

}
