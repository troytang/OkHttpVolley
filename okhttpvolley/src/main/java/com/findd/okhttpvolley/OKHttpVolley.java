package com.findd.okhttpvolley;

import android.content.Context;

/**
 * Created by Troy Tang on 2015/7/22.
 */
public class OKHttpVolley {

    private static OKHttpVolley INSTANCE;

    public static void init(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new OKHttpVolley(context);
        }
    }

    public static OKHttpVolley getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new OKHttpVolley(context);
        }

        return INSTANCE;
    }

    public static OKHttpVolley getInstance() {
        return INSTANCE;
    }

    public static RequestHelper from(Context context) {
        Context appContext = context.getApplicationContext();
        return getInstance(appContext).createRequestHelper();
    }

    private OKHttpVolleyStack okHttpVolleyStack;
    private RequestHelper requestHelper;

    private OKHttpVolley(Context context) {
        okHttpVolleyStack = new OKHttpVolleyStack(context);
    }

    public RequestHelper createRequestHelper() {
        requestHelper = new RequestHelper();
        return requestHelper;
    }

    public OKHttpVolleyStack getOKHttpVolleyStack() {
        return okHttpVolleyStack;
    }
}
