package com.rayanandisheh.isuperynew.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClientShopList {
    public static Retrofit retrofit;
    private static APIService apiRequests;
    private static final String BASE_URL = "http://mitbarwebservice.mitbar.ir/api/Home/";

    public static APIService getInstance() {
        if (apiRequests == null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();


            apiRequests = retrofit.create(APIService.class);

            return apiRequests;

        }
        else {
            return apiRequests;
        }
    }
}
