package com.rosshambrick.android.utils.masterdetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.rosshambrick.android.utils.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MasterDetailFragmentActivity<T> extends FragmentActivity
        implements MasterFragment.Listener<T>, DetailFragment.Listener<T> {

    private static final String TAG = MasterDetailFragmentActivity.class.getSimpleName();

    protected MasterFragment<T> mMasterFragment;
    private boolean mLargeScreen;
    private FragmentManager mFragmentManager;
    private List<T> mMultiSelectItems = new ArrayList<T>();
    private Map<T, Fragment> mMultiSelectItemsMap = new HashMap<T, Fragment>();
    private FrameLayout mFragmentDetailContainer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_detail);

        mFragmentManager = getSupportFragmentManager();

        mMasterFragment = (MasterFragment<T>) mFragmentManager.findFragmentById(R.id.masterFragmentContainer);

        mFragmentDetailContainer = (FrameLayout) findViewById(R.id.detailFragmentContainer);

        if (mMasterFragment == null) {
            mMasterFragment = getMasterFragment();
            mFragmentManager.beginTransaction()
                    .add(R.id.masterFragmentContainer, mMasterFragment)
                    .commit();
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        mLargeScreen = dpWidth > 650;

        Log.d(TAG, "Screen width: " + dpWidth);

        if (!mLargeScreen) {
            mFragmentDetailContainer.setVisibility(View.GONE);
        }
    }

    abstract protected MasterFragment<T> getMasterFragment();

    abstract protected DetailFragment<T> getDetailFragment(T item);

    @Override
    public void onItemUpdated(T item) {
        mMasterFragment.onItemUpdated(item);
    }

    @Override
    public void onItemSelected(T item) {
        if (mLargeScreen) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();

            DetailFragment existingDetailFragment = (DetailFragment) mFragmentManager.findFragmentById(R.id.detailFragmentContainer);
            if (existingDetailFragment != null) {
                ft.remove(existingDetailFragment);
            }

            if (item != null) {
                ft.add(R.id.detailFragmentContainer, getDetailFragment(item));
            }

            ft.commit();

        } else if (item != null) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.masterFragmentContainer, getDetailFragment(item))
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onMultiItemSelected(T item) {
        if (mLargeScreen && item != null) {
            removeAllExistingFragments();
            mMultiSelectItems.add(item);
            updateDetailFragments();
        }
    }

    @Override
    public void onMultiItemUnselected(T item) {
        removeAllExistingFragments();
        mMultiSelectItems.remove(item);
        updateDetailFragments();
    }

    private void updateDetailFragments() {
        int i = 0;
        for (T item : mMultiSelectItems) {
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
            mFragmentManager.beginTransaction()
                    .add(id, detailFragment)
                    .commit();
            mMultiSelectItemsMap.put(item, detailFragment);

            i++;
        }
    }

    private void removeAllExistingFragments() {
        for (T item : mMultiSelectItems) {
            removeDetailFragment(item);
        }
    }

    @Override
    public void onClearSelections() {
        removeAllExistingFragments();
        mMultiSelectItems.clear();
    }

    private void removeDetailFragment(T item) {
        Fragment fragment = mMultiSelectItemsMap.get(item);
        if (fragment != null) {
            mFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit();
            for (int i = 0; i < mFragmentDetailContainer.getChildCount(); i++) {
                View child = mFragmentDetailContainer.getChildAt(i);
                if (child.getId() == fragment.getId()) {
                    mFragmentDetailContainer.removeView(child);
                }
            }
        }
    }

}
