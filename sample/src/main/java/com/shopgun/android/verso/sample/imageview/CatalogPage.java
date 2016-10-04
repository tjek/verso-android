package com.shopgun.android.verso.sample.imageview;

import java.util.ArrayList;
import java.util.List;

public class CatalogPage {

    public static final String TAG = CatalogPage.class.getSimpleName();

    public static List<CatalogPage> create() {
        ArrayList<CatalogPage> pages = new ArrayList<>();
        for (int i = 1; i < 7 ; i++) {
            pages.add(new CatalogPage(i));
        }
        return pages;
    }

    private static final String URL = "https://akamai.shopgun.com/img/catalog/%s/%s-%s.jpg?m=ocgidm";

    public final String id;
    public final int page;
    public final int width;
    public final int height;
    public final float aspectRatio;
    public final String thumb;
    public final String view;
    public final String zoom;

    public CatalogPage(int page) {
        this("6cefeh3", page, 1525, 2008);
    }

    public CatalogPage(String id, int page, int width, int height) {
        this.id = id;
        this.page = page;
        this.width = width;
        this.height = height;
        this.aspectRatio = (float) height / (float) width;
        this.thumb = String.format(URL, "thumb", id, page);
        this.view = String.format(URL, "view", id, page);
        this.zoom = String.format(URL, "zoom", id, page);
    }

    @Override
    public String toString() {
        return String.format("page[%s], view[%s], zoom[%s]", page, view, zoom);
    }

}
