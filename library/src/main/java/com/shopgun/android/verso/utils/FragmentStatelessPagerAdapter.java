/*******************************************************************************
 * Copyright 2015 ShopGun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.shopgun.android.verso.utils;

import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public abstract class FragmentStatelessPagerAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = FragmentStatelessPagerAdapter.class.getSimpleName();

    private Fragment[] mFragments;

    public FragmentStatelessPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        ensureFragmentArray();
        if (mFragments.length > position) {
            Fragment f = mFragments[position];
            if (f != null) {
                return f;
            }
        }
        return createItem(position);
    }

    protected Fragment[] getFragments() {
        ensureFragmentArray();
        return mFragments;
    }

    /**
     * Return the Fragment associated with a specified position.
     * @param position The position to create an item for
     */
    public abstract Fragment createItem(int position);

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ensureFragmentArray();
        Fragment f = (Fragment) super.instantiateItem(container, position);
        mFragments[position] = f;
        return f;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ensureFragmentArray();
        mFragments[position] = null;
        super.destroyItem(container, position, object);
    }

    /**
     * Clear the current state of the {@link FragmentStatelessPagerAdapter}.
     *
     * <p>Technically the state is temporarily stored in FragmentStatePagerAdapter,
     * but the state will be cleared when {@link FragmentStatelessPagerAdapter#saveState()}
     * is called.</p>
     */
    public void clearState() {
        ensureFragmentArray();
        clearFragmentsFromFragmentManager();
    }

    @Override
    public Parcelable saveState() {
        ensureFragmentArray();
        if (isEmpty()) {
            // Don't allow adapter to save state, if clear have been called
            return null;
        } else {
            return super.saveState();
        }
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    private boolean isEmpty() {
        for (int i = 0; i < mFragments.length; i++) {
            if (mFragments[i] != null) {
                return false;
            }
        }
        return true;
    }
    /**
     * Ensures that the fragment array is instantiated
     */
    private void ensureFragmentArray() {
        if (mFragments == null) {
            mFragments = new Fragment[getCount()];
        }
        if (mFragments.length != getCount()) {
            clearFragmentsFromFragmentManager();
            mFragments = new Fragment[getCount()];
        }
    }

    /**
     * Clear the remaining fragments from FragmentManager
     */
    private void clearFragmentsFromFragmentManager() {
        if (mFragments != null) {
            for (int i = 0; i < mFragments.length; i++) {
                Fragment f = mFragments[i];
                if (f != null) {
                    destroyItem(null, i, f);
                }
            }
        }
        mFragments = null;
    }

    @Override
    public void notifyDataSetChanged() {
        ensureFragmentArray();
//        for (int i = 0; i < mFragments.length; i++) {
//            mFragments[i] = null;
//        }
        super.notifyDataSetChanged();
    }

}
