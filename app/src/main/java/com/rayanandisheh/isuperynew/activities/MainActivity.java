package com.rayanandisheh.isuperynew.activities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import com.andremion.counterfab.CounterFab;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rayanandisheh.isuperynew.Interfaces.ModifyBasketFab;
import com.rayanandisheh.isuperynew.app.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.customs.CircularImageView;
import com.rayanandisheh.isuperynew.fragments.HomePage;
import com.rayanandisheh.isuperynew.fragments.My_Orders;
import com.rayanandisheh.isuperynew.fragments.News;
import com.rayanandisheh.isuperynew.fragments.About;
import com.rayanandisheh.isuperynew.fragments.My_Cart;
import com.rayanandisheh.isuperynew.fragments.NotesFragment;
import com.rayanandisheh.isuperynew.fragments.Thank_You;
import com.rayanandisheh.isuperynew.fragments.WishList;
import com.rayanandisheh.isuperynew.fragments.ContactUs;
import com.rayanandisheh.isuperynew.fragments.SearchFragment;
import com.rayanandisheh.isuperynew.fragments.SettingsFragment;
import com.rayanandisheh.isuperynew.databases.User_Cart_DB;
import com.rayanandisheh.isuperynew.network.APIClient;
import com.rayanandisheh.isuperynew.utils.Utilities;
import com.rayanandisheh.isuperynew.app.MyAppPrefsManager;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.adapters.DrawerListAdapter;
import com.rayanandisheh.isuperynew.models.drawer_model.Drawer_Items;
import com.rayanandisheh.isuperynew.models.device_model.AppSettingsDetails;

public class MainActivity extends AppCompatActivity implements ModifyBasketFab {
    
    Toolbar toolbar;
    ActionBar actionBar;
    
    ImageView drawer_header;
    DrawerLayout drawerLayout;
    NavigationView navigationDrawer;


    SharedPreferences sharedPreferences;
    MyAppPrefsManager myAppPrefsManager;

    RecyclerView drawerRecyclerView;
    DrawerListAdapter drawerListAdapter;
    CounterFab basketFab;

    boolean doublePressedBackToExit = false;

    public static ActionBarDrawerToggle actionBarDrawerToggle;
    public static int currentFragment = -1;
    public static boolean onSaleShown = false;

    List<Drawer_Items> drawerItems = new ArrayList<>();
    Map<Drawer_Items, List<Drawer_Items>> listDataChild = new HashMap<>();
    
    @Override
    protected void onStart() {
        Utilities.changeLocale(this);
        super.onStart();

        currentFragment = -1;

        myAppPrefsManager.setFirstTimeLaunch(false);
        User_Cart_DB.initCartInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Intent intent = getIntent();

        if (intent.hasExtra("code")) {
            Bundle bundle = new Bundle();
            bundle.putInt("response_code", Integer.valueOf(intent.getStringExtra("code")));

            Fragment fragment = new Thank_You();
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack(getString(R.string.actionCart), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment).commit();
        }

        // Get MyAppPrefsManager
        myAppPrefsManager = new MyAppPrefsManager(MainActivity.this);
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);


        // Binding Layout Views
        toolbar = findViewById(R.id.myToolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationDrawer = findViewById(R.id.main_drawer);

        //Basket Fab Setup
        basketFab = findViewById(R.id.basket_fab);
        syncBasket();

        // Get ActionBar and Set the Title and HomeButton of Toolbar
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(ConstantValues.APP_NAME);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        // Setup Expandable DrawerList
        setupExpandableDrawerList();

        // Setup Expandable Drawer Header
        setupExpandableDrawerHeader();


        // Initialize ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Hide OptionsMenu
                invalidateOptionsMenu();
                hideFab();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Recreate the OptionsMenu
                invalidateOptionsMenu();
                appearFab();
            }
        };


        // Add ActionBarDrawerToggle to DrawerLayout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // Synchronize the indicator with the state of the linked DrawerLayout
        actionBarDrawerToggle.syncState();


        // Get the default ToolbarNavigationClickListener
        final View.OnClickListener toggleNavigationClickListener = actionBarDrawerToggle.getToolbarNavigationClickListener();

        // Handle ToolbarNavigationClickListener with OnBackStackChangedListener
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {

            // Check BackStackEntryCount of FragmentManager
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

                // Set new ToolbarNavigationClickListener
                actionBarDrawerToggle.setToolbarNavigationClickListener(v -> {
                    // Close the Drawer if Opened
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawers();
                    } else {
                        // Pop previous Fragment from BackStack
                        getSupportFragmentManager().popBackStack();
                    }
                    appearFab();
                    syncBasket();
                });


            } else {
                // Set DrawerToggle Indicator and default ToolbarNavigationClickListener
                actionBar.setTitle(ConstantValues.APP_NAME);
                actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
                actionBarDrawerToggle.setToolbarNavigationClickListener(toggleNavigationClickListener);
            }
        });

        // Navigate to SelectedItem
        drawerSelectedItemNavigation(0);
//        throw new RuntimeException("Test Crash");

        basketFab.setOnClickListener( v-> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = new My_Cart();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();
        });
    }


    public void setupExpandableDrawerHeader() {

        // Binding Layout Views of DrawerHeader
        drawer_header = findViewById(R.id.drawer_header);
        CircularImageView drawer_profile_image = findViewById(R.id.drawer_profile_image);
        TextView drawer_profile_name = findViewById(R.id.drawer_profile_name);
        TextView drawer_profile_email = findViewById(R.id.drawer_profile_email);
        TextView drawer_profile_welcome = findViewById(R.id.drawer_profile_welcome);

        // Check if the User is Authenticated
        if (ConstantValues.IS_USER_LOGGED_IN) {
            // Check User's Info from SharedPreferences
            if (!TextUtils.isEmpty(sharedPreferences.getString("userEmail", ""))) {
                
                // Set User's Name, Email and Photo
                drawer_profile_email.setVisibility(View.VISIBLE);
                drawer_profile_email.setText(sharedPreferences.getString("userEmail", ""));
                
                if (!TextUtils.isEmpty(sharedPreferences.getString("userDisplayName", ""))) {
                    drawer_profile_name.setText(sharedPreferences.getString("userDisplayName", ""));
                }
                else {
                    drawer_profile_name.setText(sharedPreferences.getString("userName", ""));
                }
    
                if (!TextUtils.isEmpty(sharedPreferences.getString("userPicture", "")))
                    Glide.with(this)
                            .asBitmap()
                            .load(sharedPreferences.getString("userPicture", ""))
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .into(drawer_profile_image);

            } else {
                // Set Default Name, Email and Photo
                drawer_profile_image.setImageResource(R.drawable.profile);
                drawer_profile_name.setText(getString(R.string.login_or_signup));
                drawer_profile_email.setVisibility(View.GONE);

            }

        }
        else {
            // Set Default Name, Email and Photo
            drawer_profile_welcome.setVisibility(View.GONE);
            drawer_profile_image.setImageResource(R.drawable.profile);
            drawer_profile_name.setText(getString(R.string.login_or_signup));
            drawer_profile_email.setVisibility(View.GONE);
        }


        // Handle DrawerHeader Click Listener
        drawer_header.setOnClickListener(view -> {

            // Check if the User is Authenticated
            if (ConstantValues.IS_USER_LOGGED_IN) {

                // Navigate to Update_Account Fragment
                Fragment fragment = new SettingsFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();

                // Close NavigationDrawer
                drawerLayout.closeDrawers();
            }
            else {
                // Navigate to Login Activity
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }
        });
    }

    public void setupExpandableDrawerList() {
    
        drawerItems = new ArrayList<>();
        listDataChild = new HashMap<>();
        AppSettingsDetails appSettings = ((App) getApplicationContext()).getAppSettingsDetails();
    
        if (appSettings != null) {
//            listDataHeader.add(new Drawer_Items(0, R.drawable.ic_home, getString(R.string.actionHome)));

            if ("1".equalsIgnoreCase(appSettings.getMyOrdersPage()))
                drawerItems.add(new Drawer_Items(1, R.drawable.ic_order, getString(R.string.actionOrders)));
            if ("1".equalsIgnoreCase(appSettings.getWishListPage()))
                drawerItems.add(new Drawer_Items(2, R.drawable.ic_heart_white_filled, getString(R.string.actionFavourites)));

            drawerItems.add(new Drawer_Items(3, R.drawable.ic_note, getString(R.string.actionNotes)));

            if ("1".equalsIgnoreCase(appSettings.getNewsPage()))
                drawerItems.add(new Drawer_Items(4, R.drawable.ic_newspaper, getString(R.string.actionNews)));
            if ("1".equalsIgnoreCase(appSettings.getContactUsPage()))
                drawerItems.add(new Drawer_Items(5, R.drawable.ic_chat_bubble, getString(R.string.actionContactUs)));
            if ("1".equalsIgnoreCase(appSettings.getAboutUsPage()))
                drawerItems.add(new Drawer_Items(6, R.drawable.ic_info, getString(R.string.actionAbout)));
            drawerItems.add(new Drawer_Items(7, R.drawable.ic_guide, getString(R.string.actionGuide)));
            if ("1".equalsIgnoreCase(appSettings.getShareApp()))
                drawerItems.add(new Drawer_Items(8, R.drawable.ic_share, getString(R.string.actionShareApp)));
//            if ("1".equalsIgnoreCase(appSettings.getRateApp()))
//                listDataHeader.add(new Drawer_Items(9, R.drawable.ic_star_circle, getString(R.string.actionRateApp)));
            if ("1".equalsIgnoreCase(appSettings.getSettingPage()))
                drawerItems.add(new Drawer_Items(10, R.drawable.ic_settings, getString(R.string.actionSettings)));

            drawerItems.add(new Drawer_Items(11, R.drawable.ic_power, getString(R.string.actionLogoutStore)));
            // Add last Header Item in Drawer Header List
//            if (ConstantValues.IS_USER_LOGGED_IN) {
//                listDataHeader.add(new Drawer_Items(11, R.drawable.ic_logout, getString(R.string.actionLogout)));
//            } else {
//                listDataHeader.add(new Drawer_Items(12, R.drawable.ic_logout, getString(R.string.actionLogin)));
//            }
        }
        else {
//            listDataHeader.add(new Drawer_Items(0, R.drawable.ic_home, getString(R.string.actionHome)));
            drawerItems.add(new Drawer_Items(1, R.drawable.ic_order, getString(R.string.actionOrders)));
            drawerItems.add(new Drawer_Items(2, R.drawable.ic_heart_white_filled, getString(R.string.actionFavourites)));
            drawerItems.add(new Drawer_Items(3, R.drawable.ic_note, getString(R.string.actionNotes)));
//            listDataHeader.add(new Drawer_Items(3, R.drawable.ic_intro, getString(R.string.actionIntro)));
            drawerItems.add(new Drawer_Items(4, R.drawable.ic_newspaper, getString(R.string.actionNews)));
            drawerItems.add(new Drawer_Items(5, R.drawable.ic_info, getString(R.string.actionAbout)));
            drawerItems.add(new Drawer_Items(6, R.drawable.ic_chat_bubble, getString(R.string.actionContactUs)));
            drawerItems.add(new Drawer_Items(7, R.drawable.ic_guide, getString(R.string.actionGuide)));
            drawerItems.add(new Drawer_Items(8, R.drawable.ic_share, getString(R.string.actionShareApp)));
            drawerItems.add(new Drawer_Items(9, R.drawable.ic_settings, getString(R.string.actionSettings)));
            drawerItems.add(new Drawer_Items(10, R.drawable.ic_power, getString(R.string.actionLogoutStore)));

//            if (ConstantValues.IS_USER_LOGGED_IN) {
//                listDataHeader.add(new Drawer_Items(10, R.drawable.ic_logout, getString(R.string.actionLogout)));
//            } else {
//                listDataHeader.add(new Drawer_Items(11, R.drawable.ic_logout, getString(R.string.actionLogin)));
//            }
        }

        drawerListAdapter = new DrawerListAdapter(this, drawerItems, this::findFragment);

        drawerRecyclerView = findViewById(R.id.main_drawer_list);
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        drawerRecyclerView.setAdapter(drawerListAdapter);
        
        drawerListAdapter.notifyDataSetChanged();

        

        // Handle Group Item Click Listener
//        main_drawer_list.setOnGroupClickListener((parent, v, groupPosition, id) -> {
//
//            if (drawerExpandableAdapter.getChildrenCount(groupPosition) < 1) {
//                drawerSelectedItemNavigation(listDataHeader.get(groupPosition).getId());
//            }
//            return false;
//        });


//        main_drawer_list.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
//
//            drawerSelectedItemNavigation(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getId());
//            return false;
//        });


        // Handle Group Expand Listener
//        main_drawer_list.setOnGroupExpandListener(groupPosition -> {});
        // Handle Group Collapse Listener
//        main_drawer_list.setOnGroupCollapseListener(groupPosition -> {});

    }

    private void findFragment(int which) {
        switch(which){
            case 0:
                drawerSelectedItemNavigation(ConstantValues.MY_ORDER_PAGE);
                break;
            case 1:
                drawerSelectedItemNavigation(ConstantValues.WISH_LIST_PAGE);
                break;
            case 2:
                drawerSelectedItemNavigation(ConstantValues.NOTES_PAGE);
                break;
            case 3:
                drawerSelectedItemNavigation(ConstantValues.NEWS_PAGE);
                break;
            case 4:
                drawerSelectedItemNavigation(ConstantValues.CONTACT_US_PAGE);
                break;
            case 5:
                drawerSelectedItemNavigation(ConstantValues.ABOUT_PAGE);
                break;
            case 6:
                drawerSelectedItemNavigation(ConstantValues.GUIDE_PAGE);
                break;
            case 7:
                drawerSelectedItemNavigation(ConstantValues.SHARE_PAGE);
                break;
            case 8:
                drawerSelectedItemNavigation(ConstantValues.SETTINGS_PAGE);
                break;
            case 9:
                drawerSelectedItemNavigation(ConstantValues.EXIT_FROM_STORE);
                break;
        }
    }


    private void drawerSelectedItemNavigation(int which) {
        Log.e("which", String.valueOf(which));
        Log.e("currentFragment", String.valueOf(currentFragment));
        if (currentFragment != which){
            Fragment fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();

            switch (which){
                case ConstantValues.HOME_PAGE:
                    fragment = new HomePage();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    break;
                case ConstantValues.MY_ORDER_PAGE:
                    if (ConstantValues.IS_USER_LOGGED_IN) {
                        fragment = new My_Orders();
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_fragment, fragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .addToBackStack(getString(R.string.actionHome)).commit();
                    } else {
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finish();
                        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                    }
                    break;
                case ConstantValues.NOTES_PAGE:
                    fragment = new NotesFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(getString(R.string.actionHome)).commit();
                    break;
                case ConstantValues.WISH_LIST_PAGE:
                    fragment = new WishList();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(getString(R.string.actionHome)).commit();
                    break;
//                case 3:
//                    startActivity(new Intent(getBaseContext(), IntroScreen.class));
//                    break;
                case ConstantValues.NEWS_PAGE:
                    fragment = new News();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(getString(R.string.actionHome)).commit();
                    break;
                case ConstantValues.CONTACT_US_PAGE:
                    fragment = new ContactUs();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(getString(R.string.actionHome)).commit();

                    break;
                case ConstantValues.ABOUT_PAGE:
                    fragment = new About();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(getString(R.string.actionHome)).commit();
                    break;
                case ConstantValues.GUIDE_PAGE:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://isupery.com/%d8%b1%d8%a7%d9%87%d9%86%d9%85%d8%a7%db%8c-%d8%ae%d8%b1%db%8c%d8%af-%d8%a7%d8%b2-%d9%86%d8%b3%d8%ae%d9%87-%d8%a7%d9%86%d8%af%d8%b1%d9%88%db%8c%d8%af-%d9%81%d8%b1%d9%88%d8%b4%da%af%d8%a7%d9%87/"));
                    startActivity(browserIntent);
                    break;
                case ConstantValues.SHARE_PAGE:
                    Utilities.shareMyApp(MainActivity.this);
                    break;
//                case 9:
//                    Utilities.rateMyApp(MainActivity.this);
//                    break;
                case ConstantValues.SETTINGS_PAGE:
                    fragment = new SettingsFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(getString(R.string.actionHome)).commit();
                    break;
                case ConstantValues.EXIT_FROM_STORE:
                    new AlertDialog.Builder(this)
                            .setMessage(getResources().getString(R.string.exit_shop_alert))
                            .setPositiveButton(R.string.yes, (dialog, which1) -> {
//                                myAppPrefsManager.setAppName("");
//                                myAppPrefsManager.setAppUrl("");
//                                myAppPrefsManager.setConsumerSecret("0");
//                                myAppPrefsManager.setConsumerKey("0");
                                myAppPrefsManager.clearAll();
                                My_Cart.ClearCart();
                                APIClient.apiRequests = null;
                                currentFragment = -1;
                                finish();
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();

                    break;
//                case 11:
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("userID", "");
//                    editor.putString("userCookie", "");
//                    editor.putString("userPicture", "");
//                    editor.putString("userName", "");
//                    editor.putString("userDisplayName", "");
//                    editor.apply();
//
//                    MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(this);
//                    myAppPrefsManager.setUserLoggedIn(false);
//
//                    ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
//
//                    startActivity(new Intent(MainActivity.this, Login.class));
//                    finish();
//                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
//                    break;

//                case 12:
//                    startActivity(new Intent(MainActivity.this, Login.class));
//                    finish();
//                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
//                    break;

            }
        }

        drawerLayout.closeDrawers();
    }

    //*********** Called by the System when the Device's Configuration changes while Activity is Running ********//

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Configure ActionBarDrawerToggle with new Configuration
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate toolbar_menu Menu
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        // Bind Menu Items
        MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        MenuItem cartItem = menu.findItem(R.id.toolbar_home);

        Utilities.tintMenuIcon(MainActivity.this, searchItem, R.color.white);
        Utilities.tintMenuIcon(MainActivity.this, cartItem, R.color.white);

        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Clear OptionsMenu if NavigationDrawer is Opened
        if (drawerLayout.isDrawerOpen(navigationDrawer)) {
            
            MenuItem searchItem = menu.findItem(R.id.toolbar_search);
            MenuItem cartItem = menu.findItem(R.id.toolbar_home);

            searchItem.setVisible(false);
            cartItem.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    //*********** Called whenever an Item in OptionsMenu is Selected ********//

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(item.getItemId()){
            case R.id.toolbar_search:
                fragment = new SearchFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
                break;
            case R.id.toolbar_home:
                drawerSelectedItemNavigation(0);
                syncBasket();
                break;
        }
        return true;
    }



    //*********** Called when the Activity has detected the User pressed the Back key ********//

    @Override
    public void onBackPressed() {

        // Get FragmentManager
        FragmentManager fm = getSupportFragmentManager();


        // Check if NavigationDrawer is Opened
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();

        }
        // Check if BackStack has some Fragments
        else  if (fm.getBackStackEntryCount() > 0) {
            // Pop previous Fragment
            fm.popBackStack();
            syncBasket();
        }
        // Check if doubleBackToExitPressed is true
        else if (doublePressedBackToExit) {
            super.onBackPressed();

        }
        else {
            this.doublePressedBackToExit = true;
            Toast.makeText(this, "برای خروج از برنامه یک بار دیگر دکمه ی بازگشت را بزنید", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doublePressedBackToExit = false, 2000);
        }
    }


    @Override
    public void syncBasket() {
        basketFab.setCount(My_Cart.getCartSize());
    }

    @Override
    public void hideFab() {
        basketFab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void appearFab() {
        basketFab.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onSaleShown = false;
    }
}

