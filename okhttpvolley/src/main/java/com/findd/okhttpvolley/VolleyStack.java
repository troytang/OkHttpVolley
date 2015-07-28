package com.findd.okhttpvolley;

import android.content.Context;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;

/**
 * Created by Troy Tang on 2015/7/22.
 */
public abstract class VolleyStack {

    private Context mContext;

    public VolleyStack(Context ctx) {
        this.mContext = ctx;
    }

    public final Context getContext() {
        return mContext;
    }

    public abstract RequestQueue getRequestQueue();

    public abstract HttpStack getHttpStack();

    public abstract Network getNetwork();
}
