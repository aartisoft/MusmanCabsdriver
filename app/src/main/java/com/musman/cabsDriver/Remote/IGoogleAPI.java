package com.musman.cabsDriver.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Musman on 2018-01-03.
 */

public interface IGoogleAPI {
    @GET
    Call<String> getPath(@Url String url);
}
