package com.rayanandisheh.isuperynew.app;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * MyAppPrefsManager handles some Prefs of AndroidShopApp Application
 **/


public class MyAppPrefsManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;

    private SharedPreferences userPrefs;
    private SharedPreferences.Editor userPrefsEditor;
    
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "AndroidWooShop_Prefs";


    private static final String USER_LANGUAGE_CODE  = "language_Code";
    private static final String IS_USER_LOGGED_IN = "isLogged_in";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_PUSH_NOTIFICATIONS_ENABLED = "isPushNotificationsEnabled";
    private static final String IS_LOCAL_NOTIFICATIONS_ENABLED = "isLocalNotificationsEnabled";

    private static final String LOCAL_NOTIFICATIONS_TITLE = "localNotificationsTitle";
    private static final String LOCAL_NOTIFICATIONS_DURATION = "localNotificationsDuration";
    private static final String LOCAL_NOTIFICATIONS_DESCRIPTION = "localNotificationsDescription";

    private static final String WOOCOMMERCE_CONSUMER_KEY = "consumer_key";
    private static final String WOOCOMMERCE_CONSUMER_SECRET = "consumer_secret";

    private static final String APP_NAME = "app_name";
    private static final String APP_URL = "app_url";

    public MyAppPrefsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        prefsEditor = sharedPreferences.edit();

        userPrefs = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        userPrefsEditor = sharedPreferences.edit();
    }
    
    public void setUserLanguageCode(String langCode) {
        prefsEditor.putString(USER_LANGUAGE_CODE, langCode);
        prefsEditor.commit();
    }
    
    public String getUserLanguageCode() {
        return sharedPreferences.getString(USER_LANGUAGE_CODE, "fa");
    }
    
    
    public void setUserLoggedIn(boolean isUserLoggedIn) {
        prefsEditor.putBoolean(IS_USER_LOGGED_IN, isUserLoggedIn);
        prefsEditor.commit();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(IS_USER_LOGGED_IN, false);
    }


    public void setFirstTimeLaunch(boolean isFirstTimeLaunch) {
        prefsEditor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTimeLaunch);
        prefsEditor.commit();
    }


    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void cacheCleared(boolean cacheCleared) {
        prefsEditor.putBoolean("cache_cleared", cacheCleared);
        prefsEditor.commit();
    }


    public boolean isCacheCleared() {
        return sharedPreferences.getBoolean("cache_cleared", false);
    }

    public void cacheCleared1(boolean cacheCleared) {
        prefsEditor.putBoolean("cache_cleared1", cacheCleared);
        prefsEditor.commit();
    }


    public boolean isCacheCleared1() {
        return sharedPreferences.getBoolean("cache_cleared1", false);
    }

    public void setPushNotificationsEnabled(boolean isPushNotificationsEnabled) {
        prefsEditor.putBoolean(IS_PUSH_NOTIFICATIONS_ENABLED, isPushNotificationsEnabled);
        prefsEditor.commit();
    }

    public boolean isPushNotificationsEnabled() {
        return sharedPreferences.getBoolean(IS_PUSH_NOTIFICATIONS_ENABLED, true);
    }

    public void setLocalNotificationsEnabled(boolean isLocalNotificationsEnabled) {
        prefsEditor.putBoolean(IS_LOCAL_NOTIFICATIONS_ENABLED, isLocalNotificationsEnabled);
        prefsEditor.commit();
    }

    public boolean isLocalNotificationsEnabled() {
        return sharedPreferences.getBoolean(IS_LOCAL_NOTIFICATIONS_ENABLED, true);
    }


    public void setLocalNotificationsTitle(String localNotificationsTitle) {
        prefsEditor.putString(LOCAL_NOTIFICATIONS_TITLE, localNotificationsTitle);
        prefsEditor.commit();
    }

    public String getLocalNotificationsTitle() {
        return sharedPreferences.getString(LOCAL_NOTIFICATIONS_TITLE, "Android Woocommerce");
    }

    public void setLocalNotificationsDuration(String localNotificationsDuration) {
        prefsEditor.putString(LOCAL_NOTIFICATIONS_DURATION, localNotificationsDuration);
        prefsEditor.commit();
    }

    public String getLocalNotificationsDuration() {
        return sharedPreferences.getString(LOCAL_NOTIFICATIONS_DURATION, "day");
    }

    public void setLocalNotificationsDescription(String localNotificationsDescription) {
        prefsEditor.putString(LOCAL_NOTIFICATIONS_DESCRIPTION, localNotificationsDescription);
        prefsEditor.commit();
    }

    public String getLocalNotificationsDescription() {
        return sharedPreferences.getString(LOCAL_NOTIFICATIONS_DESCRIPTION, "Check bundle of new Products");
    }



    public String getConsumerKey() {
        return sharedPreferences.getString(WOOCOMMERCE_CONSUMER_KEY, "0");
    }

    public void setConsumerKey(String key){
        prefsEditor.putString(WOOCOMMERCE_CONSUMER_KEY, key).commit();
    }

    public String getConsumerSecret() {
        return sharedPreferences.getString(WOOCOMMERCE_CONSUMER_SECRET, "0");
    }

    public void setConsumerSecret(String key){
        prefsEditor.putString(WOOCOMMERCE_CONSUMER_SECRET, key).commit();
    }

    public String getAppName(){
        return sharedPreferences.getString(APP_NAME, "");
    }

    public void setAppName(String name){
        prefsEditor.putString(APP_NAME, name).commit();
    }

    public String getAppUrl(){
        return sharedPreferences.getString(APP_URL, "");
    }

    public void setAppUrl(String url){
        prefsEditor.putString(APP_URL, url).commit();
    }

    public void clearAll(){
        userPrefsEditor.clear().commit();
        prefsEditor.clear().commit();
    }
}
