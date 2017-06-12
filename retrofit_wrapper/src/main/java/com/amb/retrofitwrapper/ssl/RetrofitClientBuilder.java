package com.amb.retrofitwrapper.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class RetrofitClientBuilder {

    private static final long TIMEOUT = 2;

    private OkHttpClient.Builder builder = new OkHttpClient.Builder();

    public RetrofitClientBuilder ignoreCertificates()
            throws NoSuchAlgorithmException, KeyManagementException {
        builder.sslSocketFactory(FakeX509TrustManager.getAllSslSocketFactory());
        builder.hostnameVerifier((hostname, session) -> true);
        builder.connectTimeout(TIMEOUT, TimeUnit.MINUTES);
        builder.writeTimeout(TIMEOUT, TimeUnit.MINUTES);
        builder.readTimeout(TIMEOUT, TimeUnit.MINUTES);
        return this;
    }

    public OkHttpClient build() {
        return builder.build();
    }

    public RetrofitClientBuilder log(HttpLoggingInterceptor.Level body) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(body);
        builder.addInterceptor(interceptor);
        return this;
    }
}
