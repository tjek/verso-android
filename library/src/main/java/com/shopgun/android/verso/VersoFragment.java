package com.shopgun.android.verso;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VersoFragment extends Fragment {

    public static final String PUBLICATION = "publication";

    public static VersoFragment newInstance(VersoPublication publication) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(PUBLICATION, publication);
        VersoFragment fragment = new VersoFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    VersoPublication mVersoPublication;
    VersoViewPager mVersoViewPager;
    VersoAdapter mVersoAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVersoPublication = getArguments().getParcelable(PUBLICATION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVersoViewPager = (VersoViewPager) inflater.inflate(R.layout.verso_fragment, container, false);
        mVersoAdapter = new VersoAdapter(getChildFragmentManager(), mVersoPublication);
        mVersoViewPager.setVersoAdapter(mVersoAdapter);
        return mVersoViewPager;
    }

}
