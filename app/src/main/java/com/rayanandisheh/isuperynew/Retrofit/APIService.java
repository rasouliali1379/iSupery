package com.rayanandisheh.isuperynew.Retrofit;

import com.rayanandisheh.isuperynew.models.ShopDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;

public interface APIService {
    @POST("ISuperyApp")
    Call<List<ShopDetails>> getShopsList();
}
