package com.rayanandisheh.isuperynew.fragments;

import android.graphics.Color;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rayanandisheh.isuperynew.Interfaces.ModifyBasketFab;
import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.app.App;
import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.utils.TextJustification.JustifiedTextView;


public class About extends Fragment {

    JustifiedTextView content;
    TextView official_web, privacy_policy, service_terms;


    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.ABOUT_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.ABOUT_PAGE;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about, container, false);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        // Set the Title of Toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionAbout));
        ((ModifyBasketFab) getContext()).hideFab();

        // Binding Layout Views
        official_web = rootView.findViewById(R.id.official_web);
        privacy_policy = rootView.findViewById(R.id.privacy_policy);
//        refund_policy = rootView.findViewById(R.id.refund_policy);
        service_terms = rootView.findViewById(R.id.service_terms);
        content =  rootView.findViewById(R.id.about_us_content);
        
    
        String description = ConstantValues.ABOUT_US;

        content.setText(description, true);
        
        

        official_web.setOnClickListener(v -> {
            String web_url = ((App) getActivity().getApplicationContext()).getAppSettingsDetails().getSiteUrl();
            if (!web_url.startsWith("https://")  &&  !web_url.startsWith("http://"))
                web_url = "http://" + web_url;

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(web_url)));
        });

        privacy_policy.setOnClickListener( v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_NoTitleBar);
            View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
            dialog.setView(dialogView);
            dialog.setCancelable(false);

            final ImageButton dialog_button =  dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
            final WebView dialog_webView = dialogView.findViewById(R.id.dialog_webView);

            dialog_title.setText(getString(R.string.privacy_policy));


            String description1 = ConstantValues.PRIVACY_POLICY;
            String styleSheet1 ="<style>" +
                    "@font-face{font-family:'iransans'; src: url('file:///android_res/font/iransans.ttf')}" +
                    "body { font-family: iransans;margin:0; padding: 10px; color:#00000; font-size:0.85em; direction: rtl; display: inline-block} " +
                    "img{display:inline; height:auto; max-width:100%;}" +
                    "</style>";

            dialog_webView.setVerticalScrollBarEnabled(true);
            dialog_webView.setHorizontalScrollBarEnabled(false);
            dialog_webView.setBackgroundColor(Color.TRANSPARENT);
            dialog_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            dialog_webView.loadDataWithBaseURL(null, styleSheet1 + description1, "text/html", "utf-8", null);


            final AlertDialog alertDialog = dialog.create();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                alertDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

            dialog_button.setOnClickListener(v1 -> alertDialog.dismiss());

            alertDialog.show();

        });

//        refund_policy.setOnClickListener( v -> {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_NoTitleBar);
//            View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
//            dialog.setView(dialogView);
//            dialog.setCancelable(false);
//
//            final ImageButton dialog_button = dialogView.findViewById(R.id.dialog_button);
//            final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
//            final WebView dialog_webView = dialogView.findViewById(R.id.dialog_webView);
//
//            dialog_title.setText(getString(R.string.refund_policy));
//
//
//            String description12 = ConstantValues.REFUND_POLICY;
//            String styleSheet = "<style> " +
//                                    "body{background:#eeeeee; margin:10; padding:10; direction:rtl;} " +
//                                    "p{color:#757575;} " +
//                                    "img{display:inline; height:auto; max-width:100%;}" +
//                                "</style>";
//
//            dialog_webView.setVerticalScrollBarEnabled(true);
//            dialog_webView.setHorizontalScrollBarEnabled(false);
//            dialog_webView.setBackgroundColor(Color.TRANSPARENT);
//            dialog_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//            dialog_webView.loadDataWithBaseURL(null, styleSheet+ description12, "text/html", "utf-8", null);
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

        service_terms.setOnClickListener( v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_NoTitleBar);
            View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
            dialog.setView(dialogView);
            dialog.setCancelable(false);

            final ImageButton dialog_button = dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
            final WebView dialog_webView = dialogView.findViewById(R.id.dialog_webView);

            dialog_title.setText(getString(R.string.service_terms));


            String description13 = ConstantValues.TERMS_SERVICES;
            String styleSheet = "<style>" +
                    "@font-face{font-family:'iransans'; src: url('file:///android_res/font/iransans.ttf')}" +
                    "body { font-family: iransans;margin:0; padding: 10px; color:#00000; font-size:0.85em; direction: rtl; display: inline-block} " +
                    "img{display:inline; height:auto; max-width:100%;}" +
                    "</style>";

            dialog_webView.setVerticalScrollBarEnabled(true);
            dialog_webView.setHorizontalScrollBarEnabled(false);
            dialog_webView.setBackgroundColor(Color.TRANSPARENT);
            dialog_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            dialog_webView.loadDataWithBaseURL(null, styleSheet+ description13, "text/html", "utf-8", null);


            final AlertDialog alertDialog = dialog.create();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                alertDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

            dialog_button.setOnClickListener(v13 -> alertDialog.dismiss());

            alertDialog.show();
        });



        return rootView;
    }

}



