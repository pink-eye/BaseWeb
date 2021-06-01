package com.baseweb;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LinkPreviewService {
    private static LinkPreviewService mInstance;
    private static final String BASE_URL = "https://api.linkpreview.net/";
    private Retrofit mRetrofit;

    private LinkPreviewService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static LinkPreviewService getInstance() {
        if (mInstance == null) {
            mInstance = new LinkPreviewService();
        }
        return mInstance;
    }

    public LinkPreviewAPI getAPI() {
        return mRetrofit.create(LinkPreviewAPI.class);
    }
}
