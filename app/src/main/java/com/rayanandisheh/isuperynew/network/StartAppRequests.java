package com.rayanandisheh.isuperynew.network;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rayanandisheh.isuperynew.activities.SplashScreen;
import com.rayanandisheh.isuperynew.app.App;
import com.rayanandisheh.isuperynew.utils.Utilities;
import com.rayanandisheh.isuperynew.models.banner_model.BannerDetails;
import com.rayanandisheh.isuperynew.models.device_model.AppSettingsDetails;
import com.rayanandisheh.isuperynew.models.banner_model.BannerData;
import com.rayanandisheh.isuperynew.models.category_model.CategoryDetails;

import retrofit2.Call;
import retrofit2.Response;


/**
 * StartAppRequests contains some Methods and API Requests, that are Executed on Application Startup
 **/

public class StartAppRequests {

    private int page_number = 1;
    private App app = new App();
    private List<CategoryDetails> categoriesList = new ArrayList<>();

    public StartAppRequests(Context context) {
        app = ((App) context.getApplicationContext());
    }



    //*********** Contains all methods to Execute on Startup ********//

    public void StartRequests(){
        RequestBanners();
        RequestAppSetting();
        RequestAllCategories(page_number);
    }

    

    //*********** API Request Method to Fetch App Banners ********//

    private void RequestBanners() {

        Call<BannerData> call = APIClient.getInstance()
                .getBanners();

        try {
            Response<BannerData> response = call.execute();
    
            if (response.isSuccessful()) {
                if ("ok".equalsIgnoreCase(response.body().getStatus())) {

                    SplashScreen.tasks[0] = true;

                    List<BannerDetails> bannersList = response.body().getData();
                    
                    for (int i=0;  i<bannersList.size();  i++) {
                        
                        BannerDetails banner = bannersList.get(i);
    
                        if ("0".equalsIgnoreCase(banner.getStatus())) {
                            bannersList.remove(i);
                        }
                        else if (banner.getExpiresDate() != null) {
                            if (!"0000-00-00 00:00:00".equalsIgnoreCase(banner.getExpiresDate())  &&  Utilities.checkIsDatePassed(banner.getExpiresDate())) {
                                bannersList.remove(i);
                            }
                        }
                    }
                    
                    app.setBannersList(bannersList);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    
    
    //*********** Request App Settings from the Server ********//
    
    private void RequestAppSetting() {
        
        Call<AppSettingsDetails> call = APIClient.getInstance().getAppSetting();
    
        try {
        
            Response<AppSettingsDetails> response = call.execute();
        
            if (response.isSuccessful()) {

                SplashScreen.tasks[1] = true;
                app.setAppSettingsDetails(response.body());
            
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    //*********** API Request Method to Fetch All Categories ********//
    

    
    
    //*********** Register Device to Admin Panel with the Device's Info ********//
    /*
    public static void RegisterDeviceForFCM(final Context context) {
        
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo", MODE_PRIVATE);
        
        String deviceID = "";
        DeviceInfo device = Utilities.getDeviceInfo(context);
        String customer_ID = sharedPreferences.getString("userID", "");
        
        
        if (ConstantValues.DEFAULT_NOTIFICATION.equalsIgnoreCase("onesignal")) {
            deviceID = OneSignal.getPermissionSubscriptionState ().getSubscriptionStatus().getUserId();
        }
        else if (ConstantValues.DEFAULT_NOTIFICATION.equalsIgnoreCase("fcm")) {
            deviceID = FirebaseInstanceId.getInstance().getToken();
        }
        
        
        
        Call<UserData> call = APIClient.getInstance()
                .registerDeviceToFCM
                        (
                                deviceID,
                                device.getDeviceType(),
                                device.getDeviceRAM(),
                                device.getDeviceProcessors(),
                                device.getDeviceAndroidOS(),
                                device.getDeviceLocation(),
                                device.getDeviceModel(),
                                device.getDeviceManufacturer(),
                                customer_ID
                        );
        
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, retrofit2.Response<UserData> response) {
                
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        
                        Log.i("notification", response.body().getMessage());
//                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    
                    }
                    else {
                        
                        Log.i("notification", response.body().getMessage());
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Log.i("notification", response.errorBody().toString());
                }
            }
            
            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Toast.makeText(context, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
        
    }
    */

    private void RequestAllCategories(int page_no) {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("page", String.valueOf(page_no));
        params.put("per_page", String.valueOf(100));

        Call<List<CategoryDetails>> call = APIClient.getInstance().getAllCategories(params);

        try {

            Response<List<CategoryDetails>> response = call.execute();

            if (response.isSuccessful()) {
                if (response.body().size() != 0) {
                    categoriesList.addAll(response.body());
                    page_number += 1;
                    RequestAllCategories(page_number);
                }
                else {
                    app.setCategoriesList(categoriesList);
                    SplashScreen.tasks[2] = true;
                }

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}

