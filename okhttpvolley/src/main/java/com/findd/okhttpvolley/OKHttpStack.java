package com.findd.okhttpvolley;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Troy Tang on 2015/7/22.
 */
public class OKHttpStack implements HttpStack {

    private final OkHttpClient mHttpClient;

    public OKHttpStack() {
        mHttpClient = new OkHttpClient();
    }

    public OKHttpStack(OkHttpClient client) {
        this.mHttpClient = client;
    }

    public OkHttpClient getOKHttpClient() {
        return this.mHttpClient;
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
        OkHttpClient client = mHttpClient.clone();
        int timeoutMs = request.getTimeoutMs();
        client.setConnectTimeout(timeoutMs, TimeUnit.MILLISECONDS);
        client.setReadTimeout(timeoutMs, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(timeoutMs, TimeUnit.MILLISECONDS);

        com.squareup.okhttp.Request.Builder builder = new com.squareup.okhttp.Request.Builder();
        builder.url(request.getUrl());

        Map<String, String> headers = request.getHeaders();

        for (String name : headers.keySet()) {
            builder.addHeader(name, headers.get(name));
        }

        for (String name : additionalHeaders.keySet()) {
            builder.addHeader(name, additionalHeaders.get(name));
        }

        setConnectionParametersForRequest(builder, request);

        com.squareup.okhttp.Request okHttpRequest = builder.build();
        Call okHttpCall = client.newCall(okHttpRequest);
        Response okHttpResponse = okHttpCall.execute();

        setProgressIfFileRequest(request, okHttpResponse, okHttpCall);

        StatusLine responseStatus = new BasicStatusLine(parseProtocol(okHttpResponse.protocol()), okHttpResponse.code(), okHttpResponse.message());
        BasicHttpResponse response = new BasicHttpResponse(responseStatus);
        response.setEntity(entityFromOKHttpResponse(okHttpResponse));

        Headers responseHeaders = okHttpResponse.headers();

        for (int i = 0, len = responseHeaders.size(); i < len; i++) {
            final String name = responseHeaders.name(i), value = responseHeaders.value(i);
            if (null != name) {
                response.addHeader(new BasicHeader(name, value));
            }
        }
        return response;
    }

    /**
     * 从Response对象中解析出HttpEntity对象
     *
     * @param response {@link com.squareup.okhttp.Response} 对象
     * @return
     * @throws IOException
     */
    private static HttpEntity entityFromOKHttpResponse(Response response) throws IOException {
        BasicHttpEntity entity = new BasicHttpEntity();
        ResponseBody body = response.body();

        entity.setContent(body.byteStream());
        entity.setContentLength(body.contentLength());
        entity.setContentEncoding(response.header("Content-Encoding"));

        if (null != body.contentType()) {
            entity.setContentType(body.contentType().type());
        }

        return entity;
    }

    /**
     * 设置Http请求参数
     *
     * @param builder
     * @param request
     * @throws IOException
     * @throws AuthFailureError
     */
    private static void setConnectionParametersForRequest(com.squareup.okhttp.Request.Builder builder,
                                                          Request<?> request) throws IOException, AuthFailureError {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                byte[] postBody = request.getPostBody();
                if (null != postBody) {
                    builder.post(RequestBody.create(MediaType.parse(request.getPostBodyContentType()), postBody));
                }
                break;
            case Request.Method.GET:
                builder.get();
                break;
            case Request.Method.DELETE:
                builder.delete();
                break;
            case Request.Method.POST:
                builder.post(createRequestBody(request));
                break;
            case Request.Method.PUT:
                builder.put(createRequestBody(request));
                break;
            case Request.Method.HEAD:
                builder.head();
                break;
            case Request.Method.OPTIONS:
                builder.method("OPTIONS", null);
                break;
            case Request.Method.TRACE:
                builder.method("TRACE", null);
                break;
            case Request.Method.PATCH:
                builder.patch(createRequestBody(request));
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    /**
     * 如果是文件下载请求，则回调下载进度
     *
     * @param request
     */
    private static void setProgressIfFileRequest(Request request, Response response, Call call) {
        if (request instanceof FileRequest) {
            if (200 == response.code()) {
                InputStream inputStream = null;
                OutputStream output = null;
                try {
                    inputStream = response.body().byteStream();
                    output = new FileOutputStream(((FileRequest) request).getFile());
                    byte[] buff = new byte[1024 * 4];
                    long dowloaded = 0;
                    long total = response.body().contentLength();
                    int readed;
                    while (-1 != (readed = inputStream.read(buff))) {
                        // write buff
                        if (request.isCanceled()) {
                            call.cancel();
                            break;
                        } else {
                            output.write(buff, 0, readed);
                            dowloaded += readed;
                            ((FileRequest) request).sendProgressMessage(dowloaded, total);
                        }
                    }
                    if (!request.isCanceled()) {
                        output.flush();
                        ((FileRequest) request).sendFileDoneMessage(((FileRequest) request).getFile());
                    } else {
                        output.flush();
                        ((FileRequest) request).sendCancelMessage(((FileRequest) request).getFile());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    ((FileRequest) request).sendErrorMessage(e);
                } catch (NullPointerException e) {
                    ((FileRequest) request).sendErrorMessage(new Exception("Download file can not be null!"));
                } finally {
                    if (null != inputStream) {
                        try {
                            inputStream.close();
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            ((FileRequest) request).sendErrorMessage(e);
                        } catch (NullPointerException e) {
                            ((FileRequest) request).sendErrorMessage(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * 通过{@link com.squareup.okhttp.Protocol}解析出{@link org.apache.http.ProtocolVersion} 对象
     *
     * @param protocol
     * @return
     */
    private static ProtocolVersion parseProtocol(final Protocol protocol) {
        switch (protocol) {
            case HTTP_1_0:
                return new ProtocolVersion("HTTP", 1, 0);
            case HTTP_1_1:
                return new ProtocolVersion("HTTP", 1, 1);
            case HTTP_2:
                return new ProtocolVersion("HTTP", 2, 0);
            case SPDY_3:
                return new ProtocolVersion("SPDY", 3, 1);
        }

        throw new IllegalAccessError("Unknown protocol");
    }

    /**
     * 创建{@link com.squareup.okhttp.RequestBody}对象用于请求
     *
     * @param request {@link com.android.volley.Request}
     * @return
     * @throws AuthFailureError
     */
    private static RequestBody createRequestBody(Request request) throws AuthFailureError {
        RequestBody requestBody;
        if (request instanceof MultiPartRequest) {
            // multipart post requestbody
            MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
            for (FileWrapper fileWrapper : ((MultiPartRequest) request).getFileWrappers()) {
                builder.addFormDataPart("file", fileWrapper.file.getName(), RequestBody.create(MediaType.parse(fileWrapper.fileType), fileWrapper.file));
            }
            for (Map.Entry<String, String> param : ((MultiPartRequest) request).getParams().entrySet()) {
                builder.addFormDataPart(param.getKey(), param.getValue());
            }
            requestBody = builder.build();
        } else {
            // normal post requestbody
            final byte[] body = request.getBody();
            if (null == body) {
                return null;
            }
            requestBody = RequestBody.create(MediaType.parse(request.getBodyContentType()), body);
        }

        return requestBody;
    }
}
