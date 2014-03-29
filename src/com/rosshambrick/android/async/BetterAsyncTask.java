package com.rosshambrick.android.async;

import android.os.AsyncTask;

public abstract class BetterAsyncTask<TParams, TProgress, TResult> extends AsyncTask<TParams, TProgress, Result<TResult>> {

    @Override
    protected final Result<TResult> doInBackground(TParams... params) {

        TResult result = null;
        Throwable error = null;

        try {
            result = doBackgroundWork(params);
        } catch (Throwable e) {
            error = e;
        }

        return new Result<TResult>(result, error);
    }

    protected abstract TResult doBackgroundWork(TParams[] params) throws Throwable;

    @Override
    protected void onProgressUpdate(TProgress... values) {
        //do nothing by default
    }

    @Override
    protected final void onPostExecute(Result<TResult> taskResult) {

        if (taskResult.getError() != null) {
            onError(taskResult.getError());
        } else {
            onSuccess(taskResult.getResult());
        }

        onFinished();

    }

    @Override
    protected void onPreExecute() {
        //do nothing by default
    }

    @Override
    protected void onCancelled() {
        //do nothing by default
    }

    protected void onSuccess(TResult result) {
        //do nothing by default
    }

    protected void onError(Throwable error) {
        //do nothing by default
    }

    protected void onFinished() {
        //do nothing by default
    }

}

class Result<T> {

    private Throwable mError;
    private T mResult;

    public Result(T result, Throwable error) {
        mResult = result;
        mError = error;
    }

    public Throwable getError() {
        return mError;
    }

    public void setError(Throwable error) {
        mError = error;
    }

    public T getResult() {
        return mResult;
    }

    public void setResult(T result) {
        mResult = result;
    }

}
