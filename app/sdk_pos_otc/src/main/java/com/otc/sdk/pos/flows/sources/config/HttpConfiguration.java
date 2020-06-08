package com.otc.sdk.pos.flows.sources.config;

import android.content.Context;

import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.otc.sdk.pos.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class HttpConfiguration {

    final static int timeOut = 30;
    private OkHttpClient httpClient;
    private static HttpConfiguration configuration;
    private Context context;

    public static HttpConfiguration getInstance(Context context) {

        if (configuration == null) {
            configuration = new HttpConfiguration();
            configuration.context = context;
        }
        return configuration;
    }

    public OkHttpClient buildCustomHttpClient() {
        if (httpClient == null) {
            httpClient = getHttpClient();
        }
        return httpClient;
    }


    private OkHttpClient getHttpClient() {

        httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                //.sslSocketFactory(new TLSSocketFactory())
                //.cookieJar(cookieJar)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder builder = request.newBuilder();
                        return chain.proceed(builder.build());
                    }
                })
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(BuildConfig.DEBUG_LOG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .build();

        return httpClient;
    }

}