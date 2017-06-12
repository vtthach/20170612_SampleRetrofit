package com.amb.retrofitwrapper.scheduler;


import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Scheduler;

/**
 * create a wrapper around RxJavaCallAdapterFactory to set your threads for your by default.
 */
public class RxThreadCallAdapter extends CallAdapter.Factory {

    private RxJavaCallAdapterFactory rxFactory;
    private Scheduler subscribeScheduler;
    private Scheduler observerScheduler;

    public RxThreadCallAdapter(Scheduler subscribeScheduler, Scheduler observerScheduler) {
        this.subscribeScheduler = subscribeScheduler;
        this.observerScheduler = observerScheduler;
        this.rxFactory = RxJavaCallAdapterFactory.create();
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        CallAdapter<Observable<?>> callAdapter = (CallAdapter<Observable<?>>) rxFactory.get(returnType, annotations, retrofit);
        return callAdapter != null ? new ThreadCallAdapter(subscribeScheduler, observerScheduler, callAdapter) : null;
    }

    private static final class ThreadCallAdapter implements CallAdapter<Observable<?>> {

        private final CallAdapter<Observable<?>> delegateAdapter;
        private final Scheduler subscribeScheduler;
        private final Scheduler observerScheduler;

        ThreadCallAdapter(Scheduler subscribeScheduler, Scheduler observerScheduler, CallAdapter<Observable<?>> delegateAdapter) {
            this.delegateAdapter = delegateAdapter;
            this.subscribeScheduler = subscribeScheduler;
            this.observerScheduler = observerScheduler;
        }

        @Override
        public Type responseType() {
            return delegateAdapter.responseType();
        }

        @Override
        public <T> Observable<?> adapt(Call<T> call) {
            return delegateAdapter.adapt(call).subscribeOn(subscribeScheduler)
                    .observeOn(observerScheduler);
        }
    }
}
