package com.yaofaquan.lib_network.okhttp.request;

import android.app.DownloadManager;

import com.yaofaquan.lib_network.R;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 对外提供get/post/文件上传请求
 */
public class CommonRequest {

    public static Request createPostRequest(String url, RequestParams params) {
        return createPostRequest(url, params, null);
    }

    /**
     * 对外创建post请求对象
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static Request createPostRequest(String url, RequestParams params, RequestParams headers) {
        FormBody.Builder mFromBodyBuilder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                mFromBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Headers.Builder mHeaderBuilder = new Headers.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                mHeaderBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder().url(url)
                .headers(mHeaderBuilder.build())
                .post(mFromBodyBuilder.build())
                .build();
        return request;
    }

    public static Request createGetRequest(String url, RequestParams params) {
        return createPostRequest(url, params, null);
    }

    /**
     * 可以带请求头的get方法
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static Request createGetRequest(String url, RequestParams params, RequestParams headers) {
        StringBuffer urlBuilder = new StringBuffer(url).append("?");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        Headers.Builder mHeaderBuilder = new Headers.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                mHeaderBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        return new Request.Builder().url(url)
                .headers(mHeaderBuilder.build())
                .get()
                .build();
    }

    public static final MediaType FILE_TYPE = MediaType.parse("application/octet-stream");

    public static Request createMultiPostRequest(String url, RequestParams params, RequestParams headers) {
        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        if (params != null) {
            for (Map.Entry<String,Object> entry : params.fileParams.entrySet()) {
                if (entry.getValue() instanceof Field) {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(FILE_TYPE, (File) entry.getValue()));
                } else {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, (String) entry.getValue()));
                }
            }
        }
        return new Request.Builder().url(url)
                .post(requestBody.build())
                .build();
    }
}
