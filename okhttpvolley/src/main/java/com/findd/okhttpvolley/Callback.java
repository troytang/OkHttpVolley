package com.findd.okhttpvolley;

import com.android.volley.VolleyError;

/**
 * Created by Troy Tang on 2015/7/22.
 */
public interface Callback<T> {

    void onSuccess(T result);

    void onFail(VolleyError error);

}
