package com.amb.retrofitwrapper.module;

import android.content.Context;
import android.os.Environment;

import com.amb.retrofitwrapper.Constants;
import com.amb.retrofitwrapper.ssl.RetrofitClientBuilder;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dmt.achilles.interceptor.FakeInterceptor;
import dmt.achilles.model.MockApiConfiguration;
import dmt.achilles.repository.AssetRepository;
import dmt.achilles.repository.ContentRepository;
import dmt.achilles.repository.ExternalStorageRepository;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

@Module
public class OkHttpModule {
    Context context;
    private String mockSdCardDirectory;

    public OkHttpModule(Context context) {
        this(context.getApplicationContext(), Environment.getExternalStorageDirectory() + Constants.MOCK_SD_CARD_FOLDER_PATH);
    }

    public OkHttpModule(Context applicationContext, String mockSdCardDirectory) {
        this.context = applicationContext;
        this.mockSdCardDirectory = mockSdCardDirectory;
    }

    @Provides
    @Singleton
    @Named(Constants.OK_HTTP_IGNORE_CERTIFICATE)
    OkHttpClient provideOkHttpClient() {
        try {
            return new RetrofitClientBuilder()
                    .ignoreCertificates()
                    .log(HttpLoggingInterceptor.Level.BODY)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Timber.e("NoSuchAlgorithmException error: " + e.getMessage());
        } catch (KeyManagementException e) {
            e.printStackTrace();
            Timber.e("KeyManagementException error: " + e.getMessage());
        }
        return null;
    }

    @Provides
    @Singleton
    @Named(Constants.OK_HTTP_MOCK_ASSET)
    OkHttpClient provideMockAssetOkHttpClient(@Named(Constants.CONTENT_REPOSITORY_MOCK_ASSET) ContentRepository assetContentRepository) {
        return new OkHttpClient.Builder().addInterceptor(new FakeInterceptor(assetContentRepository)).build();
    }

    @Provides
    @Singleton
    @Named(Constants.OK_HTTP_MOCK_SD_CARD)
    OkHttpClient provideMockSdCardOkHttpClient(@Named(Constants.CONTENT_REPOSITORY_MOCK_SD_CARD) ContentRepository sdcardContentRepository) {
        return new OkHttpClient.Builder().addInterceptor(new FakeInterceptor(sdcardContentRepository)).build();
    }

    @Provides
    @Singleton
    @Named(Constants.CONTENT_REPOSITORY_MOCK_SD_CARD)
    ContentRepository provideSdCardContentRepository(Context context, MockApiConfiguration mockApiConfiguration) {
        return new ExternalStorageRepository(context, mockApiConfiguration, mockSdCardDirectory);
    }

    @Provides
    @Singleton
    @Named(Constants.CONTENT_REPOSITORY_MOCK_ASSET)
    ContentRepository provideContentRepository(Context context, MockApiConfiguration mockApiConfiguration) {
        return new AssetRepository(context, mockApiConfiguration);
    }

    @Provides
    @Singleton
    MockApiConfiguration provideMockApiConfig() {
        // create default mock configuration
        return MockApiConfiguration.builder()
                .setEndpointConfigPath("mock_api/endpointconfigData.json")
                .setApiPath("mock_api/api-config")
                .setScenarioPath("mock_api/scenarios")
                .setScenarioName("All_Success_Flows")
                .build();
    }
}
