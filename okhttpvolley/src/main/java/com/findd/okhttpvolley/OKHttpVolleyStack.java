package com.findd.okhttpvolley;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

/**
 * Http请求统一入口
 *
 * Created by Troy Tang on 2015/7/22.
 */
public class OKHttpVolleyStack extends VolleyStack {

    private static final int DISK_CACHE_SIZE = 5 * 1024 * 1024;
    private String mCacheDir;

    private RequestQueue mRequestQueue;
    private HttpStack mHttpStack;
    private OkHttpClient mHttpClient;
    private Network mNetwork;
    private Cache mHttpCache;

    public OKHttpVolleyStack(Context ctx) {
        super(ctx);

        init();
    }

    private void init() {
        /* Http缓存 */
        mCacheDir = getContext().getCacheDir() + File.separator + "OKHttpVolley";
        File file = new File(mCacheDir);
        mHttpCache = new DiskBasedCache(file, DISK_CACHE_SIZE);

        mHttpClient = new OkHttpClient();
        mHttpStack = new OKHttpStack(mHttpClient);
        mNetwork = new BasicNetwork(mHttpStack);
        mRequestQueue = new RequestQueue(mHttpCache, mNetwork);
        mRequestQueue.start();
    }

    public OkHttpClient getOKHttpClient() {
        return mHttpClient;
    }

    @Override
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    @Override
    public HttpStack getHttpStack() {
        return mHttpStack;
    }

    @Override
    public Network getNetwork() {
        return mNetwork;
    }
}
