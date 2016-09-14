package com.shopgun.android.verso;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shopgun.android.utils.TextUtils;
import com.shopgun.android.utils.log.L;
import com.shopgun.android.zoomlayout.ZoomLayout;

public class VersoPageViewFragment extends Fragment {

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

        ZoomLayout zoomLayout = (ZoomLayout) inflater.inflate(R.layout.verso_page_layout, container, false);
        LinearLayout pageContainer = (LinearLayout) zoomLayout.findViewById(R.id.verso_pages_container);

        VersoSpreadProperty spreadConfig = mVersoPublication.getConfiguration().getSpreadProperty(mPosition);
        int[] pages = spreadConfig.getPages();
        L.d(TAG, "pages: " + TextUtils.join(",", pages));

        for (int page : pages) {
            View view = mVersoPublication.getPageView(pageContainer, page);
            pageContainer.addView(view);
        }
        return zoomLayout;

    }

}
