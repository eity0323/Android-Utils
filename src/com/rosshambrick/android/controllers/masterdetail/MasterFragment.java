package com.rosshambrick.android.controllers.masterdetail;

import android.app.Activity;
import android.support.v4.app.ListFragment;

public abstract class MasterFragment<T> extends ListFragment {
    public interface Listener<T> {

        void onItemSelected(T item);

        void onMultiItemSelected(T item);

        void onMultiItemUnselected(T item);

        void onClearSelections();
    }
    protected Listener<T> mListener;

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

    abstract public void onItemUpdated(T item);

    protected void onSingleItemSelected(T item) {
        mListener.onItemSelected(item);
    }

    protected void onMultiItemSelected(T item) {
        mListener.onMultiItemSelected(item);
    }

    protected void onMultiItemUnselected(T item) {
        mListener.onMultiItemUnselected(item);
    }

    protected void onClearSelections() {
        mListener.onClearSelections();
    }

}
