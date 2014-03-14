package com.rosshambrick.android.utils.masterdetail;

import android.app.Activity;
import android.support.v4.app.Fragment;

public abstract class DetailFragment<T> extends Fragment {
    public interface Listener<T> {
        void onItemUpdated(T crime);
    }

    private DetailFragment.Listener<T> mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (Listener<T>) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected void onItemUpdated(T item) {
        mListener.onItemUpdated(item);
    }

}
