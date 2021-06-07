package com.rayanandisheh.isuperynew.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.github.ybq.android.spinkit.SpinKitView;

import com.rayanandisheh.isuperynew.Interfaces.DismissDialog;
import com.rayanandisheh.isuperynew.app.App;
import com.rayanandisheh.isuperynew.app.MyAppPrefsManager;
import com.rayanandisheh.isuperynew.customs.ShopSelectorDialogFragment;
import com.rayanandisheh.isuperynew.models.device_model.AppSettingsDetails;

import com.rayanandisheh.isuperynew.R;

import com.rayanandisheh.isuperynew.utils.Utilities;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.network.StartAppRequests;


/**
 * SplashScreen activity, appears on App Startup
 **/

public class SplashScreen extends AppCompatActivity implements DismissDialog {

    MyTask myTask;
    StartAppRequests startAppRequests;
    MyAppPrefsManager myAppPrefsManager;
    SpinKitView progress;
    Button tryAgainBtn;
    TextView appTitle, versionNameTxt;

    public static boolean [] tasks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utilities.changeLocale(this);
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        initViews();
        initListeners();


        try {
            versionNameTxt.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionNameTxt.setVisibility(View.INVISIBLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        // Initializing StartAppRequests and PreferencesManager
        startAppRequests = new StartAppRequests(this);
        myAppPrefsManager = new MyAppPrefsManager(this);

        if (!myAppPrefsManager.isCacheCleared1()){
            myAppPrefsManager.clearAll();
            myAppPrefsManager.cacheCleared1(true);
        }

        ConstantValues.LANGUAGE_CODE = myAppPrefsManager.getUserLanguageCode();
        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
        ConstantValues.IS_PUSH_NOTIFICATIONS_ENABLED = myAppPrefsManager.isPushNotificationsEnabled();
        ConstantValues.IS_LOCAL_NOTIFICATIONS_ENABLED = myAppPrefsManager.isLocalNotificationsEnabled();

        if (myAppPrefsManager.getConsumerKey().equals("0") && myAppPrefsManager.getConsumerSecret().equals("0")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            androidx.fragment.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            ShopSelectorDialogFragment shopDialog = new ShopSelectorDialogFragment();
            shopDialog.show(ft, "dialog");
        } else {
            initVars();
            loadTasks();
        }
    }

    private void initVars() {
        ConstantValues.WOOCOMMERCE_CONSUMER_KEY = myAppPrefsManager.getConsumerKey();
        ConstantValues.WOOCOMMERCE_CONSUMER_SECRET = myAppPrefsManager.getConsumerSecret();
        ConstantValues.WOOCOMMERCE_URL = myAppPrefsManager.getAppUrl();
        ConstantValues.APP_NAME = Utilities.convertToPersian(myAppPrefsManager.getAppName());
        appTitle.setText(getResources().getString(R.string.exclusive_store) + " " + ConstantValues.APP_NAME);
    }

    private void loadTasks() {
        tasks = new boolean[3];
        tasks[0]= false;
        tasks[1] = false;
        tasks[2] = false;

        myTask = new MyTask(this);
        myTask.execute();
    }


    private void initViews() {
        tryAgainBtn = findViewById(R.id.splash_try_again_btn);
        progress = findViewById(R.id.splash_progress);
        appTitle = findViewById(R.id.splash_app_title_txt);
        versionNameTxt = findViewById(R.id.splash_version_name_txt);

    }

    private void initListeners() {
        tryAgainBtn.setOnClickListener(v -> {
            MyTask myTask = new MyTask(this);
            myTask.execute();
        });
    }

    private void setAppConfig() {
    
        AppSettingsDetails appSettings = ((App) getApplicationContext()).getAppSettingsDetails();
        
        if (appSettings != null) {
            
            if (appSettings.getCurrencySymbol() != null  &&  !TextUtils.isEmpty(appSettings.getCurrencySymbol())) {
                ConstantValues.CURRENCY_SYMBOL = appSettings.getCurrencySymbol();
            } else {
                ConstantValues.CURRENCY_SYMBOL = "تومان";
            }
            
            
            if (appSettings.getFilterMaxPrice() != null  &&  !TextUtils.isEmpty(appSettings.getFilterMaxPrice())) {
                ConstantValues.FILTER_MAX_PRICE = appSettings.getFilterMaxPrice();
            } else {
                ConstantValues.FILTER_MAX_PRICE = "1000000000";
            }
            
            
            if (appSettings.getNewProductDuration() != null  &&  !TextUtils.isEmpty(appSettings.getNewProductDuration())) {
                ConstantValues.NEW_PRODUCT_DURATION = Long.parseLong(appSettings.getNewProductDuration());
            } else {
                ConstantValues.NEW_PRODUCT_DURATION = 30;
            }
            
            
            ConstantValues.DEFAULT_HOME_STYLE = getString(R.string.actionHome) +" "+ appSettings.getHomeStyle();
            ConstantValues.DEFAULT_CATEGORY_STYLE = getString(R.string.actionCategory) +" "+ appSettings.getCategoryStyle();
            
            ConstantValues.IS_GUEST_CHECKOUT_ENABLED = ("yes".equalsIgnoreCase(appSettings.getGuestCheckout()));
            ConstantValues.IS_ONE_PAGE_CHECKOUT_ENABLED = ("1".equalsIgnoreCase(appSettings.getOnePageCheckout()));
            
            ConstantValues.IS_GOOGLE_LOGIN_ENABLED = ("1".equalsIgnoreCase(appSettings.getGoogleLogin()));
            ConstantValues.IS_FACEBOOK_LOGIN_ENABLED = ("1".equalsIgnoreCase(appSettings.getFacebookLogin()));
            ConstantValues.IS_ADD_TO_CART_BUTTON_ENABLED = ("1".equalsIgnoreCase(appSettings.getCartButton()));
            
            ConstantValues.IS_ADMOBE_ENABLED = ("1".equalsIgnoreCase(appSettings.getAdmob()));
            ConstantValues.ADMOBE_ID = appSettings.getAdmobId();
            ConstantValues.AD_UNIT_ID_BANNER = appSettings.getAdUnitIdBanner();
            ConstantValues.AD_UNIT_ID_INTERSTITIAL = appSettings.getAdUnitIdInterstitial();
    
            ConstantValues.ABOUT_US = appSettings.getAboutPage();
            ConstantValues.TERMS_SERVICES = appSettings.getTermsPage();
            ConstantValues.PRIVACY_POLICY = appSettings.getPrivacyPage();
            ConstantValues.REFUND_POLICY = appSettings.getRefundPage();
            
            myAppPrefsManager.setLocalNotificationsTitle(appSettings.getNotificationTitle());
            myAppPrefsManager.setLocalNotificationsDuration(appSettings.getNotificationDuration());
            myAppPrefsManager.setLocalNotificationsDescription(appSettings.getNotificationText());
            
        }
        else {

            ConstantValues.CURRENCY_SYMBOL = "تومان";
            ConstantValues.FILTER_MAX_PRICE = "10000";
            ConstantValues.NEW_PRODUCT_DURATION = 30;
    
            ConstantValues.IS_GUEST_CHECKOUT_ENABLED =  false;
            ConstantValues.IS_ONE_PAGE_CHECKOUT_ENABLED = false;
    
            ConstantValues.IS_ADMOBE_ENABLED = false;
            ConstantValues.IS_GOOGLE_LOGIN_ENABLED = false;
            ConstantValues.IS_FACEBOOK_LOGIN_ENABLED = false;
            ConstantValues.IS_ADD_TO_CART_BUTTON_ENABLED = true;
    
            ConstantValues.DEFAULT_HOME_STYLE = getString(R.string.actionHome) +" "+ 1;
            ConstantValues.DEFAULT_CATEGORY_STYLE = getString(R.string.actionCategory) +" "+ 1;
    
            ConstantValues.ABOUT_US = getString(R.string.lorem_ipsum);
            ConstantValues.TERMS_SERVICES = getString(R.string.lorem_ipsum);
            ConstantValues.PRIVACY_POLICY = getString(R.string.lorem_ipsum);
            ConstantValues.REFUND_POLICY = getString(R.string.lorem_ipsum);
        }
        
    }

    @Override
    public void dismissed() {
            initVars();
            Log.e("link", ConstantValues.WOOCOMMERCE_URL);
            loadTasks();
    }

    private class MyTask extends AsyncTask<String, Void, String> {

        Context context;

        public MyTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            if (Utilities.isNetworkAvailable(SplashScreen.this)) {

                runOnUiThread(() -> {
                    progress.setVisibility(View.VISIBLE);
                    tryAgainBtn.setVisibility(View.GONE);
                });
                startAppRequests.StartRequests();

                return "1";
            } else {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equalsIgnoreCase("0")) {

                Toast.makeText(context, getResources().getString(R.string.no_connection),Toast.LENGTH_SHORT).show();
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    tryAgainBtn.setVisibility(View.VISIBLE);
                });
            }
            else {
                setAppConfig();

//                if (myAppPrefsManager.isFirstTimeLaunch()) {
//                    // Navigate to IntroScreen
//                    startActivity(new Intent(getBaseContext(), IntroScreen.class));
//                    finish();
//                }
//                else {
//                    // Navigate to MainActivity
//                    startActivity(new Intent(getBaseContext(), MainActivity.class));
//                    finish();
//                }
                if (tasks[0] && tasks[1] && tasks[2]){
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                } else {

                    Toast.makeText(context, getResources().getString(R.string.no_connection),Toast.LENGTH_SHORT).show();
                    runOnUiThread(() -> {
                        progress.setVisibility(View.GONE);
                        tryAgainBtn.setVisibility(View.VISIBLE);
                    });

                }

            }
        }

    }

}


