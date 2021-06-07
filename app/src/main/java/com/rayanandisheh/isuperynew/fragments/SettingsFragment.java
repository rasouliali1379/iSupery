package com.rayanandisheh.isuperynew.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rayanandisheh.isuperynew.R;

import com.rayanandisheh.isuperynew.activities.Login;
import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.app.App;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.customs.CircularImageView;
import com.rayanandisheh.isuperynew.customs.DialogLoader;
import com.rayanandisheh.isuperynew.app.MyAppPrefsManager;
import com.rayanandisheh.isuperynew.utils.NotificationScheduler;
import com.rayanandisheh.isuperynew.utils.Utilities;

import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends Fragment {

    View rootView;

    DialogLoader dialogLoader;
    MyAppPrefsManager appPrefs;
    SharedPreferences sharedPreferences;
    
    CircularImageView profile_image;
    Button btn_edit_profile, btn_logout;
    TextView profile_name, profile_email;
    SwitchCompat push_notification;
    TextView official_web, share_app, rate_app, privacy_policy, service_terms, test_ad_interstitial;


    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.SETTINGS_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.SETTINGS_PAGE;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.settings, container, false);

        dialogLoader = new DialogLoader(getContext());
        appPrefs = new MyAppPrefsManager(getContext());
        sharedPreferences = getContext().getSharedPreferences("UserInfo", MODE_PRIVATE);

        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.actionSettings));


        // Binding Layout Views
        rate_app = (TextView) rootView.findViewById(R.id.rate_app);
        share_app = (TextView) rootView.findViewById(R.id.share_app);
        official_web = (TextView) rootView.findViewById(R.id.official_web);
//        refund_policy = (TextView) rootView.findViewById(R.id.refund_policy);
        service_terms = (TextView) rootView.findViewById(R.id.service_terms);
        privacy_policy = (TextView) rootView.findViewById(R.id.privacy_policy);
        test_ad_interstitial = (TextView) rootView.findViewById(R.id.test_ad_interstitial);
        push_notification =  rootView.findViewById(R.id.switch_push_notification);

        btn_logout = (Button) rootView.findViewById(R.id.btn_logout);
        btn_edit_profile = (Button) rootView.findViewById(R.id.btn_edit_account);
        profile_name = (TextView) rootView.findViewById(R.id.profile_name);
        profile_email = (TextView) rootView.findViewById(R.id.profile_email);
        profile_image = (CircularImageView) rootView.findViewById(R.id.profile_image);
        
    
        setupAppBarHeader();
    
        if (!ConstantValues.IS_USER_LOGGED_IN) {
            btn_logout.setText(getString(R.string.login));
        }
        
        
        push_notification.setChecked(appPrefs.isPushNotificationsEnabled());

    
        push_notification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            appPrefs.setPushNotificationsEnabled(isChecked);
            ConstantValues.IS_PUSH_NOTIFICATIONS_ENABLED = appPrefs.isPushNotificationsEnabled();

//                TogglePushNotification(ConstantValues.IS_PUSH_NOTIFICATIONS_ENABLED);
        });
    
        
        if (!ConstantValues.IS_CLIENT_ACTIVE)


        official_web.setOnClickListener(v -> {
            String web_url = ((App) getActivity().getApplicationContext()).getAppSettingsDetails().getSiteUrl();
            if (!web_url.startsWith("https://")  &&  !web_url.startsWith("http://"))
                web_url = "http://" + web_url;

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(web_url)));
        });
    
        share_app.setOnClickListener(v -> Utilities.shareMyApp(getContext()));
    
    
        rate_app.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.rayanandisheh.isuperynew"))));

//        rate_app.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://cafebazaar.ir/app/com.rayanandisheh.isupery"))));

        privacy_policy.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_NoTitleBar);
            View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final ImageButton dialog_button =  dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
            final WebView dialog_webView = (WebView) dialogView.findViewById(R.id.dialog_webView);

            dialog_title.setText(getString(R.string.privacy_policy));


            String description = ConstantValues.PRIVACY_POLICY;
            String styleSheet =  "<style>" +
                    "@font-face{font-family:'iransans'; src: url('file:///android_res/font/iransans.ttf')}" +
                    "body { font-family: iransans;margin:0; padding: 10px; color:#00000; font-size:0.85em; direction: rtl; display: inline-block} " +
                    "img{display:inline; height:auto; max-width:100%;}" +
                    "</style>";

            dialog_webView.setHorizontalScrollBarEnabled(false);
            dialog_webView.loadDataWithBaseURL(null, styleSheet+description, "text/html", "utf-8", null);


            final AlertDialog alertDialog = dialog.create();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                alertDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            }

            dialog_button.setOnClickListener(v1 -> alertDialog.dismiss());

            alertDialog.show();

        });
    
//        refund_policy.setOnClickListener((View.OnClickListener) v -> {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_NoTitleBar);
//            View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
//            dialog.setView(dialogView);
//            dialog.setCancelable(false);
//
//            final ImageButton dialog_button = (ImageButton) dialogView.findViewById(R.id.dialog_button);
//            final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
//            final WebView dialog_webView = (WebView) dialogView.findViewById(R.id.dialog_webView);
//
//            dialog_title.setText(getString(R.string.refund_policy));
//
//
//            String description = ConstantValues.REFUND_POLICY;
//            String styleSheet =  "<style>" +
//                    "@font-face{font-family:'iransans'; src: url('file:///android_res/font/iransans.ttf')}" +
//                    "body { font-family: iransans;margin:0; padding:0; color:#00000; font-size:0.85em; direction: rtl; display: inline-block} " +
//                    "img{display:inline; height:auto; max-width:100%;}" +
//                    "</style>";
//
//            dialog_webView.setHorizontalScrollBarEnabled(false);
//            dialog_webView.loadDataWithBaseURL(null, styleSheet+description, "text/html", "utf-8", null);
//
//
//            final AlertDialog alertDialog = dialog.create();
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
//            }
//
//            dialog_button.setOnClickListener(v12 -> alertDialog.dismiss());
//
//            alertDialog.show();
//        });
    
        service_terms.setOnClickListener((View.OnClickListener) v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_NoTitleBar);
            View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
            dialog.setView(dialogView);
            dialog.setCancelable(false);

            final ImageButton dialog_button = (ImageButton) dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
            final WebView dialog_webView = (WebView) dialogView.findViewById(R.id.dialog_webView);

            dialog_title.setText(getString(R.string.service_terms));


            String description = ConstantValues.TERMS_SERVICES;
            String styleSheet =  "<style>" +
                    "@font-face{font-family:'iransans'; src: url('file:///android_res/font/iransans.ttf')}" +
                    "body { font-family: iransans;margin:0; padding: 10px; color:#00000; font-size:0.85em; direction: rtl; display: inline-block} " +
                    "h1 { padding: 0px !important; margin: 0px !important}" +
                    "img{display:inline; height:auto; max-width:100%;}" +
                    "</style>";

            dialog_webView.setHorizontalScrollBarEnabled(false);
            dialog_webView.loadDataWithBaseURL(null, styleSheet+description, "text/html", "utf-8", null);


            final AlertDialog alertDialog = dialog.create();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                alertDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

            dialog_button.setOnClickListener(v12 -> alertDialog.dismiss());

            alertDialog.show();
        });
    
    
        btn_logout.setOnClickListener(v -> {

            // Check if the User is Authenticated
            if (ConstantValues.IS_USER_LOGGED_IN) {

                // Edit UserID in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userID", "");
                editor.putString("userCookie", "");
                editor.putString("userPicture", "");
                editor.putString("userName", "");
                editor.putString("userDisplayName", "");
                editor.apply();

                // Set UserLoggedIn in MyAppPrefsManager
                MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(getContext());
                myAppPrefsManager.setUserLoggedIn(false);

                // Set isLogged_in of ConstantValues
                ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();

                setupAppBarHeader();
                ((MainActivity) getContext()).setupExpandableDrawerList();
                ((MainActivity) getContext()).setupExpandableDrawerHeader();


                btn_logout.setText(getString(R.string.login));

            }
            else {
                // Navigate to Login Activity
                startActivity(new Intent(getContext(), Login.class));
                ((MainActivity) getContext()).finish();
                ((MainActivity) getContext()).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }
        });


        return rootView;
    }
    
    
    
    //*********** Setup Header of Navigation Drawer ********//
    
    public void setupAppBarHeader() {
        
        // Check if the User is Authenticated
        if (ConstantValues.IS_USER_LOGGED_IN) {
            // Check User's Info from SharedPreferences
            if (!TextUtils.isEmpty(sharedPreferences.getString("userEmail", ""))) {
    
                // Set User's Name, Email and Photo
                profile_email.setText(sharedPreferences.getString("userEmail", ""));
                
                if (!TextUtils.isEmpty(sharedPreferences.getString("userDisplayName", ""))) {
                    profile_name.setText(sharedPreferences.getString("userDisplayName", ""));
                }
                else {
                    profile_name.setText(sharedPreferences.getString("userName", ""));
                }
    
                if (!TextUtils.isEmpty(sharedPreferences.getString("userPicture", "")))
                    Glide.with(this)
                            .asBitmap()
                            .load(sharedPreferences.getString("userPicture", ""))
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .into(profile_image);
    
                btn_edit_profile.setText(getString(R.string.edit_profile));
                btn_edit_profile.setBackground(getResources().getDrawable(R.drawable.rounded_corners_primary_color_bg));
                
            }
            else {
                // Set Default Name, Email and Photo
                profile_image.setImageResource(R.drawable.profile);
                profile_name.setText(getString(R.string.login_or_signup));
                profile_email.setText(getString(R.string.login_or_create_account));
                btn_edit_profile.setText(getString(R.string.login));
                btn_edit_profile.setBackground(getResources().getDrawable(R.drawable.rounded_corners_primary_color_bg));
            }
            
        }
        else {
            // Set Default Name, Email and Photo
            profile_image.setImageResource(R.drawable.profile);
            profile_name.setText(getString(R.string.login_or_signup));
            profile_email.setText(getString(R.string.login_or_create_account));
            btn_edit_profile.setText(getString(R.string.login));
            btn_edit_profile.setBackground(getResources().getDrawable(R.drawable.rounded_corners_primary_color_bg));
        }
        
        
        // Handle DrawerHeader Click Listener
        btn_edit_profile.setOnClickListener(view -> {

            // Check if the User is Authenticated
            if (ConstantValues.IS_USER_LOGGED_IN) {

                // Navigate to Update_Account Fragment
                Fragment fragment = new Update_Account();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionCart)).commit();

            }
            else {
                // Navigate to Login Activity
                startActivity(new Intent(getContext(), Login.class));
                ((MainActivity) getContext()).finish();
                ((MainActivity) getContext()).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }
        });
    }

}
