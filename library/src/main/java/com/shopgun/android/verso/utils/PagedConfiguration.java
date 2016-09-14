package com.shopgun.android.verso.utils;

import com.shopgun.android.utils.enums.Orientation;
import com.shopgun.android.verso.VersoSpreadConfiguration;

public abstract class PagedConfiguration implements VersoSpreadConfiguration {

    private Orientation mOrientation;
    private boolean mIntro = false;
    private boolean mOutro = false;

    public PagedConfiguration() {
        this(Orientation.LANDSCAPE, 0, false, false);
    }

    public PagedConfiguration(Orientation orientation, int pageCount, boolean intro, boolean outro) {
        mOrientation = orientation;
        mIntro = intro;
        mOutro = outro;
    }

    public Orientation getOrientation() {
        return mOrientation;
    }

    public void setOrientation(Orientation orientation) {
        mOrientation = orientation;
    }

    public boolean hasIntro() {
        return mIntro;
    }

    public void setIntro(boolean intro) {
        mIntro = intro;
    }

    public boolean hasOutro() {
        return mOutro;
    }

    public void setOutro(boolean outro) {
        mOutro = outro;
    }

    public int pageToPosition(int[] pages) {
        return pageToPosition(pages[0]);
    }

    public int pageToPosition(int page) {
        if (mOrientation.isLandscape() && page > 1) {
            // normalize so we'll get the first actual page from a position
            page -= page % 2;
            return mIntro ? (page/2)+1 : page/2;
        }
        return mIntro ? page : page-1;
    }

    public int[] positionToPages(int position, int pageCount) {

        if (mIntro) {
            if (position > 0) {
                // if the intro is present just offset everything by one,
                // except if it's the intro position it self
                position--;
            } else {
                // Intro doesn't have a pageNum, so we'll return empty
                return new int[]{};
            }
        }

        if (mOutro) {
            int maxPagePos = mOrientation.isLandscape() ? pageCount/2 : pageCount-1;
            if (maxPagePos < position) {
                // Outro doesn't have a pageNum, so we'll return empty
                return new int[]{};
            }
        }

        // default is offset by one
        int page;
        if (mOrientation.isLandscape() && position > 0) {
            page = (position * 2);
        } else {
            page = position + 1;
        }

        int[] pages;
        if (mOrientation.isPortrait() || page == 1 || page == pageCount) {
            // first, last, and everything in portrait is single-page
            pages = new int[]{page};
        } else {
            // Anything else is double page
            pages = new int[]{page, (page + 1)};
        }
        return pages;
    }

}
