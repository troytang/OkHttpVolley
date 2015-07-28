package com.findd.okhttpvolley;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by Troy Tang on 2015/7/24.
 */
public class FileRequest extends Request<String> {

    public static final int MESSAGE_WHAT_PROGRESS = 1;
    public static final int MESSAGE_WHAT_DONE = 2;
    public static final int MESSAGE_WHAT_ERROR = 3;
    public static final int MESSAGE_WHAT_CANCEL = 4;

    private Handler mMainHandler;
    private Response.Listener mListener;
    private ProgressCallback mProgressCallback;
    private File mFile;

    public FileRequest(String url, Response.Listener listener) {
        this(Method.GET, url, listener, null, null, null);
    }

    public FileRequest(int method, String url, File file, ProgressCallback progressCallback) {
        this(method, url, null, null, file, progressCallback);
    }

    public FileRequest(int method, String url, Response.Listener listener, Response.ErrorListener errorListener, File file, ProgressCallback callback) {
        super(method, url, errorListener);
        mListener = listener;
        mProgressCallback = callback;
        mFile = file;
        mMainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handlerMessage(msg);
            }
        };
    }

    private ProgressCallback getProgressCallback() {
        if (null == mProgressCallback) {
            return ProgressCallback.NONE;
        }
        return mProgressCallback;
    }

    public File getFile() {
        return mFile;
    }

    /**
     * 发送进度消息
     *
     * @param current
     * @param total
     */
    public void sendProgressMessage(long current, long total) {
        Message msg = new Message();
        msg.what = MESSAGE_WHAT_PROGRESS;
        Bundle bundle = new Bundle();
        bundle.putLong("current_progress", current);
        bundle.putLong("total_progress", total);
        msg.setData(bundle);
        mMainHandler.sendMessage(msg);
    }

    /**
     * 发送完成消息
     *
     * @param file
     */
    public void sendFileDoneMessage(File file) {
        Message msg = new Message();
        msg.what = MESSAGE_WHAT_DONE;
        msg.obj = file;
        mMainHandler.sendMessage(msg);
    }

    /**
     * 发送错误消息
     *
     * @param ex
     */
    public void sendErrorMessage(Exception ex) {
        Message msg = new Message();
        msg.what = MESSAGE_WHAT_ERROR;
        msg.obj = ex;
        mMainHandler.sendMessage(msg);
    }

    /**
     * 发送取消消息
     */
    public void sendCancelMessage(File file) {
        Message msg = new Message();
        msg.what = MESSAGE_WHAT_CANCEL;
        msg.obj = file;
        mMainHandler.sendMessage(msg);
    }

    /**
     * 解析消息体
     *
     * @param msg
     */
    private void handlerMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_WHAT_PROGRESS:
                Bundle bundle = msg.getData();
                long current = bundle.getLong("current_progress");
                long total = bundle.getLong("total_progress");
                getProgressCallback().pregress(current, total);
                break;
            case MESSAGE_WHAT_DONE:
                File file = (File) msg.obj;
                getProgressCallback().done(file);
                break;
            case MESSAGE_WHAT_ERROR:
                Exception exception = (Exception) msg.obj;
                getProgressCallback().error(exception);
                break;
            case MESSAGE_WHAT_CANCEL:
                File fileCanceled = (File) msg.obj;
                getProgressCallback().cancel(fileCanceled);
                break;
        }
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
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
        if (null != mListener) {
            mListener.onResponse(response);
        }
    }
}
