package com.rayanandisheh.isuperynew.app;


import android.content.Context;
import androidx.multidex.MultiDexApplication;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.rayanandisheh.isuperynew.databases.DB_Handler;
import com.rayanandisheh.isuperynew.databases.DB_Manager;
import com.rayanandisheh.isuperynew.models.order_model.OrderDetails;
import com.rayanandisheh.isuperynew.models.drawer_model.Drawer_Items;
import com.rayanandisheh.isuperynew.models.banner_model.BannerDetails;
import com.rayanandisheh.isuperynew.models.category_model.CategoryDetails;
import com.rayanandisheh.isuperynew.models.device_model.AppSettingsDetails;
import com.rayanandisheh.isuperynew.models.user_model.UserDetails;
import com.rayanandisheh.isuperynew.utils.Utilities;


/**
 * App extending Application, is used to save some Lists and Objects with Application Context.
 **/


public class App extends MultiDexApplication {

    // Application Context
    private static Context context;
    private static DB_Handler db_handler;


    private List<Drawer_Items> drawerHeaderList;
    private Map<Drawer_Items, List<Drawer_Items>> drawerChildList;
    
    
    private AppSettingsDetails appSettings = null;
    private List<BannerDetails> bannersList = new ArrayList<>();
    private List<CategoryDetails> categoriesList = new ArrayList<>();
    
    
    private UserDetails userDetails = new UserDetails();
    private OrderDetails orderDetails = new OrderDetails();


    @Override
    public void onCreate() {
        super.onCreate();

        Utilities.changeLocale(this);

        // set App Context
        context = this.getApplicationContext();

        // initialize DB_Handler and DB_Manager
        db_handler = new DB_Handler();
        DB_Manager.initializeInstance(db_handler);
    }



    //*********** Returns Application Context ********//

    public static Context getContext() {
        return context;
    }




    public List<Drawer_Items> getDrawerHeaderList() {
        return drawerHeaderList;
    }

    public void setDrawerHeaderList(List<Drawer_Items> drawerHeaderList) {
        this.drawerHeaderList = drawerHeaderList;
    }

    public Map<Drawer_Items, List<Drawer_Items>> getDrawerChildList() {
        return drawerChildList;
    }

    public void setDrawerChildList(Map<Drawer_Items, List<Drawer_Items>> drawerChildList) {
        this.drawerChildList = drawerChildList;
    }
    
    
    
    public AppSettingsDetails getAppSettingsDetails() {
        return appSettings;
    }
    
    public void setAppSettingsDetails(AppSettingsDetails appSettings) {
        this.appSettings = appSettings;
    }
    
    
    public List<BannerDetails> getBannersList() {
        return bannersList;
    }
    
    public void setBannersList(List<BannerDetails> bannersList) {
        this.bannersList = bannersList;
    }
    
    public List<CategoryDetails> getCategoriesList() {
        return categoriesList;
    }
    
    public void setCategoriesList(List<CategoryDetails> categoriesList) {
        this.categoriesList = categoriesList;
    }
    
    
    public OrderDetails getOrderDetails() {
        return orderDetails;
    }
    
    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }
    
    public UserDetails getUserDetails() {
        return userDetails;
    }
    
    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
    
}

