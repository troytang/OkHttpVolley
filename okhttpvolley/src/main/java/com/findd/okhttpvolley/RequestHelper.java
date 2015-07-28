package com.findd.okhttpvolley;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.findd.okhttpvolley.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Troy Tang on 2015/7/22.
 */
public class RequestHelper {

    private String url;
    private boolean download;
    private Map<String, String> params;
    private Map<String, String> headers;
    private Callback callback;
    private ProgressCallback progressCallback;
    private List<FileWrapper> fileWrappers;
    private File downloadFile;

    public Request createRequest(int mothod) {
        Request request = null;
        switch (mothod) {
            case Request.Method.GET:
                request = createGetRequest();
                break;
            case Request.Method.POST:
                request = createPostRequest();
                break;
        }
        return request;
    }

    public Request post() {
        Request request = createRequest(Request.Method.POST);
        return OKHttpVolley.getInstance().getOKHttpVolleyStack().getRequestQueue().add(request);
    }

    public Request get() {
        Request request = createRequest(Request.Method.GET);
        return OKHttpVolley.getInstance().getOKHttpVolleyStack().getRequestQueue().add(request);
    }

    public Request start() {
        Request request = new FileRequest(Request.Method.GET, generateGetUrl(), null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressCallback.error(error);
            }
        }, downloadFile, progressCallback);
        return OKHttpVolley.getInstance().getOKHttpVolleyStack().getRequestQueue().add(request);
    }

    public RequestHelper url(String url) {
        this.url = url;
        return this;
    }

    public RequestHelper param(String key, String value) {
        if (null == params) {
            params = new HashMap<>();
        }
        params.put(key, value);
        return this;
    }

    public RequestHelper header(String key, String value) {
        if (null == headers) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
        return this;
    }

    public RequestHelper file(File file) {
        FileWrapper fileWrapper = new FileWrapper(Util.httpFileType(file), file);
        if (null == fileWrappers) {
            fileWrappers = new ArrayList<>();
        }

        fileWrappers.add(fileWrapper);
        return this;
    }

    public RequestHelper callback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public RequestHelper download(String url) {
        this.url = url;
        this.download = true;
        return this;
    }

    public RequestHelper save(File file) {
        this.downloadFile = file;
        return this;
    }

    public RequestHelper progress(ProgressCallback callback) {
        this.progressCallback = callback;
        return this;
    }

    public RequestHelper params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public RequestHelper headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Callback getCallback() {
        return callback;
    }

    /**
     * 生成完整Url,只有在Get方式时调用
     */
    public String generateGetUrl(){
        if (null != params && params.size() > 0){
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(url);
            stringBuffer.append("?");
            Iterator iterator = params.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry =(Map.Entry)iterator.next();
                String key = (String)entry.getKey();
                stringBuffer.append(key);
                stringBuffer.append("=");
                stringBuffer.append((String)entry.getValue());
                if (iterator.hasNext()){
                    stringBuffer.append("&");
                }
            }
            return stringBuffer.toString();
        }
        return url;
    }

    private Request createGetRequest() {

        url = generateGetUrl();

        Request request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (null != callback) {
                    callback.onSuccess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != callback) {
                    callback.onFail(error);
                }
            }
        });

        if (null != headers && 0 < headers.size()) {
            try {
                request.getHeaders().putAll(headers);
            } catch (AuthFailureError authFailureError) {
                authFailureError.printStackTrace();
            }
        }

        return request;
    }

    private Request createPostRequest() {
        Request request;
        if (null == fileWrappers || 0 == fileWrappers.size()) {
            // create normal post request
            request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (null != callback) {
                        callback.onSuccess(response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (null != callback) {
                        callback.onFail(error);
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
        } else {
            // create multipart post request
            request = new MultiPartRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (null != callback) {
                        callback.onSuccess(response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (null != callback) {
                        callback.onFail(error);
                    }
                }
            });
            ((MultiPartRequest) request).setFileWrappers(fileWrappers);
            ((MultiPartRequest) request).setParams(params);
        }

        if (null != headers && 0 < headers.size()) {
            try {
                request.getHeaders().putAll(headers);
            } catch (AuthFailureError authFailureError) {
                authFailureError.printStackTrace();
            }
        }

        return request;
    }
}
