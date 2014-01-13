package com.rosshambrick.android.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class L {
    //DEBUG
    public static void d(Object object, String message, Exception e) {
        if (BuildConfig.DEBUG) {
            Log.d(object.getClass().getSimpleName(), message, e);
        }
    }

    public static void d(Object object, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(object.getClass().getSimpleName(), message);
        }
    }

    //ERROR
    public static void e(Object object, String message, Exception e) {
        Log.e(object.getClass().getSimpleName(), message, e);
        toastIfDebug(object, message);
    }

    public static void e(Object object, String message) {
        Log.e(object.getClass().getSimpleName(), message);
        toastIfDebug(object, message);
    }

    private static void toastIfDebug(Object object, String message) {
        if (BuildConfig.DEBUG) {
            if (object instanceof Context) {
                toastOnMainThread((Context) object, message);
            }
            if (object instanceof android.support.v4.app.Fragment) {
                toastOnMainThread(((android.support.v4.app.Fragment) object).getActivity(), message);
            }
            if (object instanceof android.app.Fragment) {
                toastOnMainThread(((android.app.Fragment) object).getActivity(), message);
            }
            if (object instanceof android.support.v4.content.Loader) {
                toastOnMainThread(((android.support.v4.content.Loader) object).getContext(), message);
            }
            if (object instanceof android.content.Loader) {
                toastOnMainThread(((android.content.Loader) object).getContext(), message);
            }
        }
    }

    private static void toastOnMainThread(final Context context, final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}