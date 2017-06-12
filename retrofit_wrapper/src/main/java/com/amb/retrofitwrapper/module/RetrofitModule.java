package com.amb.retrofitwrapper.module;


import com.amb.retrofitwrapper.Constants;
import com.amb.retrofitwrapper.scheduler.RxThreadCallAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
@Singleton
public class RetrofitModule {

    protected final String baseUrl;

    public RetrofitModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder().serializeNulls().create();
    }

    @Provides
    @Singleton
    @Named(Constants.RETROFIT_IGNORE_CERTIFICATE)
    Retrofit provideRetrofit(Gson gson, @Named(Constants.OK_HTTP_IGNORE_CERTIFICATE) OkHttpClient okHttpClient, CallAdapter.Factory callAdapterFactory) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .addCallAdapterFactory(callAdapterFactory)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    CallAdapter.Factory provideCallAdapterFactory() {
        return new RxThreadCallAdapter(Schedulers.io(), AndroidSchedulers.mainThread());
    }
}
