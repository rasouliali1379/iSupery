package com.rayanandisheh.isuperynew.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.rayanandisheh.isuperynew.customs.CircularImageView;

import com.rayanandisheh.isuperynew.R;

import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.models.user_model.Nonce;
import com.rayanandisheh.isuperynew.models.user_model.UserData;
import com.rayanandisheh.isuperynew.network.APIClient;
import com.rayanandisheh.isuperynew.utils.Utilities;
import com.rayanandisheh.isuperynew.utils.ValidateInputs;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;


/**
 * SignUp activity handles User's Registration
 **/

public class Signup extends AppCompatActivity {

    View parentView;
    String registerNonce = "";

    Toolbar toolbar;
    ActionBar actionBar;


    FrameLayout signUpBtn;
    FrameLayout banner_adView;
    TextView signup_loginText;
    CircularImageView user_photo;
    TextView service_terms, privacy_policy, signUpBtnTxt;
    EditText user_firstname, username, user_email, user_password;
    SpinKitView progress;

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.SIGN_UP_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.SIGN_UP_PAGE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utilities.changeLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        // setting Toolbar
        toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.signup));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        // Binding Layout Views
        user_photo = findViewById(R.id.user_photo);
        user_firstname = findViewById(R.id.user_firstname);
        username = findViewById(R.id.username);
        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        signUpBtn = findViewById(R.id.signup_btn);
        service_terms = findViewById(R.id.service_terms);
        privacy_policy = findViewById(R.id.privacy_policy);
        signup_loginText = findViewById(R.id.signup_loginText);
        banner_adView = findViewById(R.id.banner_adView);
        progress = findViewById(R.id.signup_progress);
        signUpBtnTxt = findViewById(R.id.signup_btn_text);

    
        user_photo.setVisibility(View.GONE);
    
        getRegistrationNonce();
    
        
        privacy_policy.setOnClickListener((View.OnClickListener) v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(Signup.this, android.R.style.Theme_NoTitleBar);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final ImageButton dialog_button = (ImageButton) dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
            final WebView dialog_webView = (WebView) dialogView.findViewById(R.id.dialog_webView);

            dialog_title.setText(getString(R.string.privacy_policy));


            String description = ConstantValues.PRIVACY_POLICY;
            String styleSheet = "<style>" +
                    "@font-face{font-family:'iransans'; src: url('file:///android_res/font/iransans.ttf')}" +
                    "body { font-family: iransans;margin:0; padding: 10px; color:#00000; font-size:0.85em; direction: rtl; display: inline-block} " +
                    "img{display:inline; height:auto; max-width:100%;}" +
                    "</style>";

            dialog_webView.setVerticalScrollBarEnabled(true);
            dialog_webView.setHorizontalScrollBarEnabled(false);
            dialog_webView.setBackgroundColor(Color.TRANSPARENT);
            dialog_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            dialog_webView.loadDataWithBaseURL(null, styleSheet+description, "text/html", "utf-8", null);


            final AlertDialog alertDialog = dialog.create();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(Signup.this, R.color.colorPrimaryDark));
                alertDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

            dialog_button.setOnClickListener(v1 -> alertDialog.dismiss());

            alertDialog.show();

        });
    
//        refund_policy.setOnClickListener((View.OnClickListener) v -> {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(Signup.this, android.R.style.Theme_NoTitleBar);
//            View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
//            dialog.setView(dialogView);
//            dialog.setCancelable(true);
//
//            final ImageButton dialog_button = (ImageButton) dialogView.findViewById(R.id.dialog_button);
//            final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
//            final WebView dialog_webView = (WebView) dialogView.findViewById(R.id.dialog_webView);
//
//            dialog_title.setText(getString(R.string.refund_policy));
//
//
//            String description = ConstantValues.REFUND_POLICY;
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
//            dialog_webView.loadDataWithBaseURL(null, styleSheet+description, "text/html", "utf-8", null);
//
//
//            final AlertDialog alertDialog = dialog.create();
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(Signup.this, R.color.colorPrimaryDark));
//            }
//
//            dialog_button.setOnClickListener(v12 -> alertDialog.dismiss());
//
//            alertDialog.show();
//        });
    
        service_terms.setOnClickListener((View.OnClickListener) v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(Signup.this, android.R.style.Theme_NoTitleBar);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final ImageButton dialog_button = (ImageButton) dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
            final WebView dialog_webView = (WebView) dialogView.findViewById(R.id.dialog_webView);

            dialog_title.setText(getString(R.string.service_terms));


            String description = ConstantValues.TERMS_SERVICES;
            String styleSheet = "<style>" +
                    "@font-face{font-family:'iransans'; src: url('file:///android_res/font/iransans.ttf')}" +
                    "body { font-family: iransans;margin:0; padding: 10px; color:#00000; font-size:0.85em; direction: rtl; display: inline-block} " +
                    "img{display:inline; height:auto; max-width:100%;}" +
                    "</style>";

            dialog_webView.setVerticalScrollBarEnabled(true);
            dialog_webView.setHorizontalScrollBarEnabled(false);
            dialog_webView.setBackgroundColor(Color.TRANSPARENT);
            dialog_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            dialog_webView.loadDataWithBaseURL(null, styleSheet+description, "text/html", "utf-8", null);


            final AlertDialog alertDialog = dialog.create();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(Signup.this, R.color.colorPrimaryDark));
                alertDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

            dialog_button.setOnClickListener(v13 -> alertDialog.dismiss());

            alertDialog.show();
        });


        // Handle Click event of signup_loginText TextView
        signup_loginText.setOnClickListener(v -> {
            // Finish SignUpActivity to goto the LoginActivity
            finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        });


        // Handle Click event of signupBtn Button
        signUpBtn.setOnClickListener(v -> {
            // Validate Login Form Inputs
            boolean isValidData = validateForm();

            if (isValidData) {
                parentView = v;

                if (!TextUtils.isEmpty(registerNonce))
                    processRegistration();

            }
        });


    }
    
    
    
    //*********** Proceed User Registration Request ********//
    
    private void getRegistrationNonce() {
    
        Map<String, String> params = new LinkedHashMap<>();
        params.put("controller", "AndroidAppUsers");
        params.put("method", "android_register");
        
        Call<Nonce> call = APIClient.getInstance().getNonce(params);
        
        call.enqueue(new Callback<Nonce>() {
            @Override
            public void onResponse(Call<Nonce> call, retrofit2.Response<Nonce> response) {
                
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    
                    if (!TextUtils.isEmpty(response.body().getNonce())) {
                        registerNonce = response.body().getNonce();
                    }
                    else {
                        Toast.makeText(Signup.this, "Nonce is Empty", Toast.LENGTH_SHORT).show();
                    }
                    
                }
                else {
                    Toast.makeText(Signup.this, "Nonce is Empty", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Nonce> call, Throwable t) {
                Toast.makeText(Signup.this, "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    
    //*********** Proceed User Registration Request ********//

    private void processRegistration() {

        signUpBtnTxt.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);

        Call<UserData> call = APIClient.getInstance()
                .processRegistration
                        (
                                "cool",
                                user_firstname.getText().toString().trim(),
                                username.getText().toString().trim(),
                                user_email.getText().toString().trim(),
                                user_password.getText().toString().trim(),
                                registerNonce
                        );

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, retrofit2.Response<UserData> response) {

                signUpBtnTxt.setVisibility(View.VISIBLE);
                progress.setVisibility(View.INVISIBLE);
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    
                    if ("ok".equalsIgnoreCase(response.body().getStatus())) {
                        
                        // Finish SignUpActivity to goto the LoginActivity
                        finish();
                        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                        
                    }
                    else if ("error".equalsIgnoreCase(response.body().getStatus())) {
                        Toast.makeText(Signup.this, response.body().getError(), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(Signup.this, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Converter<ResponseBody, UserData> converter = APIClient.retrofit.responseBodyConverter(UserData.class, new Annotation[0]);
                    UserData userData;
                    try {
                        userData = converter.convert(response.errorBody());
                    } catch (IOException e) {
                        userData = new UserData();
                    }

                    switch (userData.getError()) {
                        case " This username Already Exist ":
                            Toast.makeText(Signup.this, getString(R.string.username_exist), Toast.LENGTH_SHORT).show();
                            break;
                        case " This Email Already exist ":
                            Toast.makeText(Signup.this, getString(R.string.email_exist), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(Signup.this, "خطایی رخ داد!", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                signUpBtnTxt.setVisibility(View.VISIBLE);
                progress.setVisibility(View.INVISIBLE);
                Toast.makeText(Signup.this, "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
    }



    //*********** Validate SignUp Form Inputs ********//

    private boolean validateForm() {
        if (!ValidateInputs.isValidName(user_firstname.getText().toString().trim())) {
            user_firstname.setError(getString(R.string.invalid_first_name));
            return false;
        } else if (ValidateInputs.hasPersianChar(username.getText().toString().trim())) {
            username.setError(getString(R.string.english_characters_needed));
            return false;
        } else if (!ValidateInputs.isValidEmail(user_email.getText().toString().trim())) {
            user_email.setError(getString(R.string.invalid_email));
            return false;
        } else if (!ValidateInputs.isValidPassword(user_password.getText().toString().trim())) {
            user_password.setError(getString(R.string.invalid_password));
            return false;
        } else {
            return true;
        }
    }
    
    //*********** Called when the Activity has detected the User pressed the Back key ********//
    
    @Override
    public void onBackPressed() {
        // Finish SignUpActivity to goto the LoginActivity
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            
            case android.R.id.home:
    
                // Finish SignUpActivity to goto the LoginActivity
                finish();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
                
                return true;
            
            default:
                return super.onOptionsItemSelected(item);
            
        }
    }
    
}

