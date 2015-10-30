package com.flatstack.touchme;

import android.app.Application;
import android.content.Context;

import com.flatstack.touchme.data.Api;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Ilya Eremin on 10/30/15.
 */
public class App extends Application {

    private static Api     api;
    public static  Context applicationContext;

    @Override public void onCreate() {
        super.onCreate();
        applicationContext = this;
    }

    public static Api getApi() {
        if (api == null) {
            api = new RestAdapter.Builder()
                .setEndpoint(Api.BASE_URL)
                .setConverter(new GsonConverter(provideGson()))
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(Api.class);
        }
        return api;
    }

    private static Gson provideGson() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    }

}
