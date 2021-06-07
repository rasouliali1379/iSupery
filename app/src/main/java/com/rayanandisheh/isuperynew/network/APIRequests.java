package com.rayanandisheh.isuperynew.network;

import com.rayanandisheh.isuperynew.models.post_model.PostDetails;
import com.rayanandisheh.isuperynew.models.post_model.PostMedia;
import com.rayanandisheh.isuperynew.models.product_model.ProductReviews;
import com.rayanandisheh.isuperynew.models.shipping_model.ShippingMethods;
import com.rayanandisheh.isuperynew.models.shipping_model.ShippingZone;
import com.rayanandisheh.isuperynew.models.shipping_model.ShippingZoneLocations;
import com.rayanandisheh.isuperynew.models.user_model.Nonce;
import com.rayanandisheh.isuperynew.models.user_model.UpdateUser;
import com.rayanandisheh.isuperynew.models.user_model.UserData;
import com.rayanandisheh.isuperynew.models.user_model.UserDetails;
import com.rayanandisheh.isuperynew.models.post_model.PostCategory;
import com.rayanandisheh.isuperynew.models.banner_model.BannerData;
import com.rayanandisheh.isuperynew.models.order_model.OrderDetails;
import com.rayanandisheh.isuperynew.models.coupons_model.CouponDetails;
import com.rayanandisheh.isuperynew.models.contact_model.ContactUsData;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;
import com.rayanandisheh.isuperynew.models.category_model.CategoryDetails;
import com.rayanandisheh.isuperynew.models.device_model.AppSettingsDetails;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Field;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.FormUrlEncoded;


/**
 * APIRequests contains all the Network Request Methods with relevant API Endpoints
 **/

public interface APIRequests {


    //******************** User Data ********************//
    
    @GET("api/get_nonce")
    Call<Nonce> getNonce(                           @QueryMap Map<String, String> args );
    
    
    @FormUrlEncoded
    @POST("api/AndroidAppUsers/android_register")
    Call<UserData> processRegistration(             @Field("insecure") String insecure,
                                                    @Field("display_name") String name,
                                                    @Field("username") String username,
                                                    @Field("email") String email_address,
                                                    @Field("password") String password,
                                                    @Field("nonce") String nonce );

    
    @FormUrlEncoded
    @POST("api/AndroidAppUsers/android_generate_cookie")
    Call<UserData> processLogin(                    @Field("insecure") String insecure,
                                                    @Field("username") String customers_username,
                                                    @Field("password") String customers_password );

    @FormUrlEncoded
    @POST("api/AndroidAppUsers/android_forgot_password")
    Call<UserData> processForgotPassword(           @Field("insecure") String insecure,
                                                    @Field("email") String customers_email_address );
    
    
    @GET("wp-json/wc/v2/customers/{id}")
    Call<UserDetails> getUserInfo(                  @Path("id") String user_id );
    
    
    @POST("api/AndroidAppUsers/android_update_user_profile")
    Call<UpdateUser> updateCustomerInfo(            @QueryMap Map<String, String> args );
    
    

    //******************** Category Data ********************//
    
    @GET("wp-json/wc/v2/products/categories")
    Call<List<CategoryDetails>> getAllCategories(   @QueryMap Map<String, String> args );
    
    
    @GET("wp-json/wc/v2/products/categories/{id}")
    Call<CategoryDetails> getSingleCategory(        @Path("id") String category_id );


    
    //******************** Product Data ********************//
    
    @GET("wp-json/wc/v2/products")
    Call<List<ProductDetails>> getAllProducts(      @QueryMap Map<String, String> args );
    
    
    @GET("wp-json/wc/v2/products/{id}")
    Call<ProductDetails> getSingleProduct(          @Path("id") String product_id );
    
    
    @GET("wp-json/wc/v2/products/{id}/variations")
    Call<List<ProductDetails>> getVariations(       @Path("id") long product_id);
    
    
    @GET("wp-json/wc/v2/products/{id}/variations")
    Call<List<ProductDetails>> searchVariation(     @Path("id") String product_id,
                                                    @Query("search") String searchValue);
    
    
    @GET("wp-json/wc/v2/products/{id}/reviews")
    Call<List<ProductReviews>> getProductReviews(   @Path("id") String product_id);
    
    
    @FormUrlEncoded
    @POST("api/AndroidAppSettings/android_create_product_review")
    Call<UserData> addProductReview(                @Field("insecure") String insecure,
                                                    @Field("nonce") String nonce,
                                                    @Field("product_id") String product_id,
                                                    @Field("rate_star") String rate_star,
                                                    @Field("author_name") String author_name,
                                                    @Field("author_email") String author_email,
                                                    @Field("author_content") String author_content );
    

    
    //******************** News Data ********************//
    
    @GET("wp-json/wp/v2/posts")
    Call<List<PostDetails>> getAllPosts(            @QueryMap Map<String, String> args );
    
    @GET("wp-json/wp/v2/posts/{id}")
    Call<PostDetails> getSinglePost(                @Path("id") String post_id );

    @GET("wp-json/wp/v2/categories")
    Call<List<PostCategory>> getPostCategories(     @QueryMap Map<String, String> args );
    
    
    @GET("wp-json/wp/v2/media/{id}")
    Call<PostMedia> getPostMedia(                   @Path("id") String post_id );
    
    
    
    //******************** Shipping Data ********************//
    
    @GET("wp-json/wc/v2/shipping/zones")
    Call<List<ShippingZone>> getShippingZones();
    
    
    @GET("wp-json/wc/v2/shipping/zones/{id}/locations")
    Call<List<ShippingZoneLocations>> getShippingZoneLocations(@Path("id") String zone_id );
    
    
    @GET("wp-json/wc/v2/shipping/zones/{id}/methods")
    Call<List<ShippingMethods>> getShippingMethods( @Path("id") String zone_id );
    
    
    @GET("wp-json/wc/v2/shipping_methods")
    Call<List<ShippingMethods>> getDefaultShippingMethods();
    
    
    
    //******************** Order Data ********************//
    
    @GET("wp-json/wc/v2/orders")
    Call<List<OrderDetails>> getAllOrders(          @QueryMap Map<String, String> args );
    
    
    @GET("wp-json/wc/v2/orders/{id}")
    Call<OrderDetails> getSingleOrder(              @Path("id") String order_id );
    
    
    @PUT("wp-json/wc/v2/orders/{id}")
    Call<OrderDetails> updateOrder(                 @Path("id") String order_id,
                                                    @Query("status") String status);


    
    //******************** Coupon Data ********************//
    
    @GET("wp-json/wc/v2/coupons")
    Call<List<CouponDetails>> getCouponInfo(        @QueryMap Map<String, String> args );
    
    @GET("wp-json/wc/v2/coupons/{id}")
    Call<List<CouponDetails>> getSingleCoupon(      @Path("id") int coupon_id );
    
    
    
    //******************** Banner Data ********************//

    @GET("api/AndroidAppSettings/android_get_all_banners/?insecure=cool")
    Call<BannerData> getBanners();



    //******************** Contact Us Data ********************//

    @GET("api/AndroidAppUsers/android_send_mail")
    Call<ContactUsData> contactUs(                  @QueryMap Map<String, String> args );
    
    
    
    //******************** App Settings Data ********************//

    @GET("api/AndroidAppSettings/android_get_all_settings/?insecure=cool")
    Call<AppSettingsDetails> getAppSetting();



}

