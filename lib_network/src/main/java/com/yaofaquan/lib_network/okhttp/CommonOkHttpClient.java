package com.yaofaquan.lib_network.okhttp;

import com.yaofaquan.lib_network.okhttp.listener.DisposeDataHandle;
import com.yaofaquan.lib_network.okhttp.response.CommonFileCallback;
import com.yaofaquan.lib_network.okhttp.response.CommonJsonCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommonOkHttpClient {
    private static final int TIME_OUT = 30;
    private static OkHttpClient mOkHttpClient;

    static {
        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();
        okhttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        okhttpClientBuilder.addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("User-Agent", "Fa-Mobile").build();
                return chain.proceed(request);
            }
        });
        okhttpClientBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okhttpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        okhttpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        okhttpClientBuilder.followRedirects(true);
        mOkHttpClient = okhttpClientBuilder.build();
    }

    public static Call get(Request request, DisposeDataHandle disposeDataHandle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(disposeDataHandle));
        return call;
    }

    public static Call post(Request request, DisposeDataHandle disposeDataHandle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(disposeDataHandle));
        return call;
    }

    public static Call downloadFile(Request request, DisposeDataHandle disposeDataHandle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonFileCallback(disposeDataHandle));
        return call;
    }
}