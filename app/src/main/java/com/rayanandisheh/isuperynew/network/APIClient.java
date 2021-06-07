package com.rayanandisheh.isuperynew.network;


import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.oauth.BasicOAuth;
import com.rayanandisheh.isuperynew.oauth.OAuthInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * APIClient handles all the Network API Requests using Retrofit Library
 **/

public class APIClient {
    
    public static Retrofit retrofit;
    public static APIRequests apiRequests;
//    private static final String BASE_URL = ConstantValues.WOOCOMMERCE_URL;
    
    
    // Singleton Instance of APIRequests
    public static APIRequests getInstance() {
        if (apiRequests == null) {
    
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


            OAuthInterceptor oauth1Woocommerce = new OAuthInterceptor.Builder()
                    .consumerKey(ConstantValues.WOOCOMMERCE_CONSUMER_KEY)
                    .consumerSecret(ConstantValues.WOOCOMMERCE_CONSUMER_SECRET)
                    .build();
    
            BasicOAuth basicOAuthWoocommerce = new BasicOAuth.Builder()
                    .consumerKey(ConstantValues.WOOCOMMERCE_CONSUMER_KEY)
                    .consumerSecret(ConstantValues.WOOCOMMERCE_CONSUMER_SECRET)
                    .build();
    
            
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(ConstantValues.WOOCOMMERCE_URL.startsWith("http://")?  oauth1Woocommerce : basicOAuthWoocommerce)
                    .addInterceptor(interceptor)
                    .build();
            
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantValues.WOOCOMMERCE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            
            
            apiRequests = retrofit.create(APIRequests.class);

        }
        return apiRequests;
    }

}
