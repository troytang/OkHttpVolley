package com.findd.okhttpvolley;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上传文件Request
 *
 * Created by Troy Tang on 2015/7/23.
 */
public class MultiPartRequest extends Request<String> {

    private final Response.Listener<String> mListener;
    private final List<FileWrapper> fileWrappers;
    private final Map<String, String> params;

    public MultiPartRequest(String url, Response.Listener listener) {
        this(Method.POST, url, listener, null);
    }

    public MultiPartRequest(int method, String url, Response.Listener listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        fileWrappers = new ArrayList<>();
        params = new HashMap<>();
    }

    public void setFileWrappers(List<FileWrapper> fileWrappers) {
        if (null != fileWrappers){
            this.fileWrappers.addAll(fileWrappers);
        }
    }
    
    public void setParams(Map<String, String> params) {
        if (null != params) {
            this.params.putAll(params);
        }
    }

    public List<FileWrapper> getFileWrappers() {
        return this.fileWrappers;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
