package com.yaofaquan.lib_network.okhttp;

import android.util.Log;

import com.yaofaquan.lib_network.okhttp.listener.DisposeDataHandle;
import com.yaofaquan.lib_network.okhttp.response.CommonFileCallback;
import com.yaofaquan.lib_network.okhttp.response.CommonJsonCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommonOkHttpClient {
    private final String TAG = "CommonOkHttpClient";
    private final int TIME_OUT = 30;
    private OkHttpClient mOkHttpClient;
    private List<Cookie> mCookies = new ArrayList<>();
    private boolean mNeedSaveCookies = false;

    private volatile static CommonOkHttpClient sInstance;

    private CommonOkHttpClient() {
        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();
        okhttpClientBuilder.hostnameVerifier((s, sslSession) -> true);
        okhttpClientBuilder.addInterceptor(chain -> {
            Request request = chain.request().newBuilder().addHeader("User-Agent", "FaMusic").build();
            return chain.proceed(request);
        });
        okhttpClientBuilder.cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                Log.d(TAG, "saveFromResponse...");
                if (mNeedSaveCookies) {
                    if (mCookies == null) {
                        mCookies = new ArrayList<>();
                    }
                    mCookies.clear();
                    mCookies.addAll(list);
                    mNeedSaveCookies = false;
                }
            }

            @Override
            public @NotNull List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                Log.d(TAG, "loadForRequest...");
                if (mNeedSaveCookies) {
                    return new ArrayList<>();
                } else {
                    return mCookies;
                }
            }
        });
        okhttpClientBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okhttpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        okhttpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        okhttpClientBuilder.followRedirects(true);
        mOkHttpClient = okhttpClientBuilder.build();
    }

    public static CommonOkHttpClient getInstance() {
        if (sInstance == null) {
            synchronized (CommonOkHttpClient.class) {
                if (sInstance == null) {
                    sInstance = new CommonOkHttpClient();
                }
            }
        }
        return sInstance;
    }

    public CommonOkHttpClient clearCookies() {
        mCookies.clear();
        mCookies = null;
        return this;
    }

    public CommonOkHttpClient needSaveCookies() {
        mNeedSaveCookies = true;
        return this;
    }

    public Response syncGet(Request request) {
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return response;
        }
    }

    public Call get(Request request, DisposeDataHandle disposeDataHandle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(disposeDataHandle));
        return call;
    }

    public Call post(Request request, DisposeDataHandle disposeDataHandle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(disposeDataHandle));
        return call;
    }

    public Call downloadFile(Request request, DisposeDataHandle disposeDataHandle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonFileCallback(disposeDataHandle));
        return call;
    }
}
