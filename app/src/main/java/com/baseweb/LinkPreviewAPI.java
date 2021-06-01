package com.baseweb;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface LinkPreviewAPI {
    @GET
    Call<LinkPreviewObject> getJSON(@Url String url);
}
