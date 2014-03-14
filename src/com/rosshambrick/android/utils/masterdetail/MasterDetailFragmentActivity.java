package com.rosshambrick.android.utils.masterdetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.rosshambrick.android.utils.R;

import java.util.HashMap;
import java.util.Map;

public abstract class MasterDetailFragmentActivity<T> extends FragmentActivity
        implements MasterFragment.Listener<T>, DetailFragment.Listener<T> {

    protected MasterFragment<T> mMasterFragment;
    private boolean mLargeScreen;
    private FragmentManager mFragmentManager;
    private Map<T, Fragment> mMultiSelectItemsMap = new HashMap<T, Fragment>();
    private FrameLayout mFragmentDetailContainer;

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mMultiSelectItemsMap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_detail);

        mFragmentManager = getSupportFragmentManager();

        Fragment fragment = mFragmentManager.findFragmentById(R.id.masterFragmentContainer);
        if (fragment instanceof MasterFragment) {
            mMasterFragment = (MasterFragment<T>) fragment;
        } else if (fragment instanceof DetailFragment) {
            mFragmentManager.popBackStack();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            showSingleDetailFragment((DetailFragment<T>) fragment, ft);
            ft.commit();
        }

        if (mMasterFragment == null) {
            mMasterFragment = getMasterFragment();
            mFragmentManager.beginTransaction()
                    .add(R.id.masterFragmentContainer, mMasterFragment)
                    .commit();
        }

        mFragmentDetailContainer = (FrameLayout) findViewById(R.id.detailFragmentContainer);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        mLargeScreen = dpWidth > 650;

        if (!mLargeScreen) {
            mFragmentDetailContainer.setVisibility(View.GONE);
        }

        mMultiSelectItemsMap = (Map<T, Fragment>) getLastCustomNonConfigurationInstance();
        if (mMultiSelectItemsMap == null) {
            mMultiSelectItemsMap = new HashMap<T, Fragment>();
        }

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        updateDetailFragments(ft);
        ft.commit();

    }

    abstract protected MasterFragment<T> getMasterFragment();

    abstract protected DetailFragment<T> getDetailFragment(T item);

    @Override
    public void onItemUpdated(T item) {
        if (mMasterFragment.isAdded()) {
            mMasterFragment.onItemUpdated(item);
        }
    }

    @Override
    public void onItemSelected(T item) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (item == null) {
            removeSingleDetailFragment(ft);
        } else {
            DetailFragment<T> detailFragment = getDetailFragment(item);
            showSingleDetailFragment(detailFragment, ft);
        }
        ft.commit();
    }

    @Override
    public void onMultiItemSelected(T item) {
        if (item != null) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            removeAllMultiselectFragments(ft);
            mMultiSelectItemsMap.put(item, null);
            updateDetailFragments(ft);
            ft.commit();
        }
    }

    @Override
    public void onMultiItemUnselected(T item) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        removeAllMultiselectFragments(ft);
        mMultiSelectItemsMap.remove(item);
        updateDetailFragments(ft);
        ft.commit();
    }

    @Override
    public void onClearSelections() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        removeAllMultiselectFragments(ft);
        mMultiSelectItemsMap.clear();
        ft.commit();
    }

    private void removeSingleDetailFragment(FragmentTransaction ft) {
        DetailFragment existingDetailFragment = (DetailFragment) mFragmentManager.findFragmentById(R.id.detailFragmentContainer);
        if (existingDetailFragment != null) {
            ft.remove(existingDetailFragment);
        }
    }

    private void showSingleDetailFragment(DetailFragment<T> detailFragment, FragmentTransaction ft) {
        if (mLargeScreen) {
            removeSingleDetailFragment(ft);
            ft.add(R.id.detailFragmentContainer, detailFragment);
        } else {
            ft.replace(R.id.masterFragmentContainer, detailFragment);
            ft.addToBackStack(null);
        }
    }

    private void updateDetailFragments(FragmentTransaction ft) {
        int i = 0;
        for (T item : mMultiSelectItemsMap.keySet()) {
            int id = i + 1;

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            FrameLayout multiSelectContainer = new FrameLayout(this);
            multiSelectContainer.setId(id);
            multiSelectContainer.setBackgroundColor(0x11000000);
            multiSelectContainer.setPadding(i * 20, i * 20, 0, 0);

            FrameLayout multiSelectContainerInner = new FrameLayout(this);
            multiSelectContainerInner.setBackgroundResource(android.R.color.white);
            multiSelectContainer.addView(multiSelectContainerInner, params);

            FrameLayout container = (FrameLayout) findViewById(R.id.detailFragmentContainer);
            container.addView(multiSelectContainer, params);

            DetailFragment<T> detailFragment = getDetailFragment(item);

            ft.add(id, detailFragment);

            mMultiSelectItemsMap.put(item, detailFragment);

            i++;
        }
    }

    private void removeAllMultiselectFragments(FragmentTransaction ft) {
        for (T item : mMultiSelectItemsMap.keySet()) {
            removeDetailFragment(item, ft);
        }
    }

    private void removeDetailFragment(T item, FragmentTransaction ft) {
        Fragment fragment = mMultiSelectItemsMap.get(item);
        if (fragment != null) {
            ft.remove(fragment);
            for (int i = 0; i < mFragmentDetailContainer.getChildCount(); i++) {
                View child = mFragmentDetailContainer.getChildAt(i);
                if (child.getId() == fragment.getId()) {
                    mFragmentDetailContainer.removeView(child);
                }
            }
        }
    }

}
