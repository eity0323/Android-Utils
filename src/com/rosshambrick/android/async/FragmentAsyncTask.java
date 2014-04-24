package com.rosshambrick.android.async;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;

public abstract class FragmentAsyncTask<TParams, TProgress, TResult>
        extends BetterAsyncTask<TParams, TProgress, TResult> {

    private static final String TAG = "FragmentAsyncTask";

    public static final int DELAY_MILLIS = 250;
    public static final int NUM_RETRIES = 5;

    private boolean mRetryingOnSuccess;
    private boolean mRetryingOnError;
    private int mErrorRetries;
    private int mFinishedRetries;
    private int mSuccessRetries;
    private int mProgressRetries;

    public interface Listener<TProgress, TResult> {
        void onTaskSuccess(TResult result);
        void onTaskProgress(TProgress[] values);
        void onTaskFinished();
        void onTaskError(Throwable error);
    }

    protected Fragment mFragment;
    private Listener mListener;
    protected boolean mIsFragmentRetained;

    public FragmentAsyncTask(Listener listener) {
        if (!(listener instanceof Fragment)) {
            throw new RuntimeException("Listener must be a Fragment");
        }
        init((Fragment) listener, listener);
    }

    public FragmentAsyncTask(Fragment fragment, Listener listener) {
        init(fragment, listener);
    }

    private void init(Fragment fragment, Listener listener) {
        mListener = listener;
        mFragment = fragment;

        mIsFragmentRetained = mFragment.getRetainInstance();
        if (!mIsFragmentRetained) {
            mFragment.setRetainInstance(true);
        }
    }

    protected abstract TResult doBackgroundWork(TParams[] params) throws Throwable;

    @Override
    protected void onProgressUpdate(final TProgress[] values) {
        new Runnable() {
            @Override
            public void run() {
                if (mFragment.isVisible()) {
                    mListener.onTaskProgress(values);
                } else if (mProgressRetries < NUM_RETRIES) {
                    mProgressRetries++;
                    Log.i(TAG, "Fragment not visible - delaying onProgressUpdate()");
                    new Handler().postDelayed(this, DELAY_MILLIS);
                }
            }
        }.run();
    }

    @Override
    protected void onSuccess(final TResult result) {
        new Runnable() {
            @Override
            public void run() {
                if (mFragment.isVisible()) {
                    mRetryingOnSuccess = false;
                    mListener.onTaskSuccess(result);
                } else if (mSuccessRetries < NUM_RETRIES) {
                    mRetryingOnSuccess = true;
                    mSuccessRetries++;
                    Log.i(TAG, "Fragment not visible - delaying onSuccess()");
                    new Handler().postDelayed(this, DELAY_MILLIS);
                }
            }
        }.run();
    }

    @Override
    protected void onError(final Throwable error) {
        new Runnable() {
            @Override
            public void run() {
                if (mFragment.isVisible()) {
                    mRetryingOnError = false;
                    mListener.onTaskError(error);
                } else if (mErrorRetries < NUM_RETRIES) {
                    mRetryingOnError = true;
                    mErrorRetries++;
                    Log.i(TAG, "Fragment not visible - delaying onError()");
                    new Handler().postDelayed(this, DELAY_MILLIS);
                }
            }
        }.run();
    }

    @Override
    protected void onFinished() {
        new Runnable() {
            @Override
            public void run() {
                if (mFragment.isVisible() && !mRetryingOnSuccess && !mRetryingOnError) {
                    mListener.onTaskFinished();
                    resetRetainedState();
                } else if (mFinishedRetries < NUM_RETRIES) {
                    mFinishedRetries++;
                    Log.i(TAG, "Fragment not visible - delaying onFinished()");
                    new Handler().postDelayed(this, DELAY_MILLIS);
                } else {
                    resetRetainedState();
                }
            }
        }.run();
    }

    private void resetRetainedState() {
        if (!mIsFragmentRetained) {
            mFragment.setRetainInstance(false);
        }
    }
}
