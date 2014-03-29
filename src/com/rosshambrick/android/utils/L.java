package com.rosshambrick.android.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class L {
    private static Map<Object, String> sMap = new HashMap<Object, String>();

    //DEBUG
    public static void d(Object object, String message, Throwable e) {
        d(getName(object), message, e);
    }

    public static void d(Object object, String message) {
        d(getName(object), message);
    }

    public static void d(String tag, String message, Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message, e);
        }
    }

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    //ERROR
    public static void e(Object object, String message, Throwable e) {
        Log.e(getName(object), message, e);
        toastIfDebug(object, message);
    }

    public static void e(Object object, String message) {
        Log.e(getName(object), message);
        toastIfDebug(object, message);
    }

    public static void e(Object object, Exception e) {
        e(object, e.getMessage(), e);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void toastIfDebug(Object object, String message) {
        if (BuildConfig.DEBUG) {
            if (object instanceof Context) {
                toastOnMainThread((Context) object, message);
            }
            if (object instanceof android.support.v4.app.Fragment) {
                toastOnMainThread(((android.support.v4.app.Fragment) object).getActivity(), message);
            }
            if (object instanceof android.app.Fragment && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                toastOnMainThread(((android.app.Fragment) object).getActivity(), message);
            }
            if (object instanceof android.support.v4.content.Loader) {
                toastOnMainThread(((android.support.v4.content.Loader) object).getContext(), message);
            }
            if (object instanceof android.content.Loader && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
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

    //INFO
    public static void i(Object object, String message) {
        Log.i(getName(object), message);
    }

    public static void i(Object object, String message, Throwable e) {
        Log.i(getName(object), message, e);
    }

    //WARN
    public static void w(Object object, String message) {
        Log.w(getName(object), message);
    }

    public static void w(Object object, String message, Throwable e) {
        Log.w(getName(object), message, e);
    }

    //private
    private static String getName(Object object) {
        String name = sMap.get(object);
        if (name == null) {
            name = object.getClass().getName();
            sMap.put(object, name);
        }
        return name;
    }

}