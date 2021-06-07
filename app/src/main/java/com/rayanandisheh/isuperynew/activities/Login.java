package com.rayanandisheh.isuperynew.activities;


import androidx.annotation.Nullable;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.app.App;
import com.rayanandisheh.isuperynew.app.MyAppPrefsManager;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.fragments.Checkout;
import com.rayanandisheh.isuperynew.models.api_response_model.ErrorResponse;
import com.rayanandisheh.isuperynew.models.user_model.UserData;
import com.rayanandisheh.isuperynew.models.user_model.UserDetails;
import com.rayanandisheh.isuperynew.network.APIClient;
import com.rayanandisheh.isuperynew.utils.Utilities;
import com.rayanandisheh.isuperynew.utils.ValidateInputs;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;


/**
 * Login activity handles User's Email, Facebook and Google Login
 **/


public class Login extends AppCompatActivity {

    View parentView;
    Boolean showGuest = false;
    Boolean cartNavigation = false;

    Toolbar toolbar;
    ActionBar actionBar;

    EditText user_email, user_password;
    TextView forgotPasswordText, signupText, login_btn_txt;
    Button guest_button;
    FrameLayout loginBtn;
    SpinKitView progress;
    LinearLayout loginFields;
    
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    private static final int RC_SIGN_IN = 100;

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.LOGIN_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.LOGIN_PAGE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.changeLocale(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("show_guest")) {
                showGuest = getIntent().getExtras().getBoolean("show_guest", false);
            }
            if (getIntent().getExtras().containsKey("cart_navigation")) {
                cartNavigation = getIntent().getExtras().getBoolean("cart_navigation", false);
            }
        }

        
        setContentView(R.layout.login);
        


        // setting Toolbar
        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.login));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);


        // Binding Layout Views
        user_email = (EditText) findViewById(R.id.user_email);
        user_password = (EditText) findViewById(R.id.user_password);
        loginBtn = findViewById(R.id.loginBtn);
        guest_button = (Button) findViewById(R.id.guest_button);

        signupText = (TextView) findViewById(R.id.login_signupText);
        forgotPasswordText = (TextView) findViewById(R.id.forgot_password_text);
        login_btn_txt = findViewById(R.id.login_btn_txt);
        progress = findViewById(R.id.login_progress);
        loginFields = findViewById(R.id.login_fields_layout);
        parentView = signupText;
    
        if (showGuest) {
            guest_button.setVisibility(View.VISIBLE);
            hideFields();
        } else {
            guest_button.setVisibility(View.GONE);
            showFields();
        }


        // Handle on Forgot Password Click
        forgotPasswordText.setOnClickListener( view -> {

            AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_input, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final Button dialog_button = dialogView.findViewById(R.id.dialog_button);
            final EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
            final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);

            dialog_button.setText(getString(R.string.send));
            dialog_title.setText(getString(R.string.forgot_your_password));


            final AlertDialog alertDialog = dialog.create();
            alertDialog.show();
            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            dialog_button.setOnClickListener(v -> {

                if (ValidateInputs.isValidEmail(dialog_input.getText().toString().trim())) {

                    processForgotPassword(dialog_input.getText().toString());

                }
                else {
                    Snackbar.make(parentView, getString(R.string.invalid_email), Snackbar.LENGTH_LONG).show();
                }

                alertDialog.dismiss();
            });
        });
    
    
    
        signupText.setOnClickListener(v -> {
            // Navigate to SignUp Activity
            startActivity(new Intent(Login.this, Signup.class));
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
        });


        loginBtn.setOnClickListener(v -> {
            if (loginFields.getVisibility() == View.GONE) {
                showFields();
            } else {
                boolean isValidData = validateLogin();

                if (isValidData) {

                    // Proceed User Login
                    processLogin();
                }
            }
        });

        // Handle Guest Login Button click
        guest_button.setOnClickListener(v -> {
            ConstantValues.IS_GUEST_LOGGED_IN = true;

            // Navigate back to MainActivity
            if (!cartNavigation)
                startActivity(new Intent(Login.this, MainActivity.class));
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        });
    }

    private void showFields() {
        loginFields.setVisibility(View.VISIBLE);
        forgotPasswordText.setVisibility(View.VISIBLE);
        user_email.setText(sharedPreferences.getString("userName", ""));
    }

    private void hideFields () {
        loginFields.setVisibility(View.GONE);
        forgotPasswordText.setVisibility(View.GONE);
    }

    private void openCheckout(){
        Fragment fragment = new Checkout();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment).addToBackStack(null).commit();
    }

    //*********** Proceed Login with User Email and Password ********//
    private void processLogin() {

        progress.setVisibility(View.VISIBLE);
        login_btn_txt.setVisibility(View.INVISIBLE);

        Call<UserData> call = APIClient.getInstance()
                .processLogin
                        (
                                "cool",
                                user_email.getText().toString().trim(),
                                user_password.getText().toString().trim()
                        );

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                progress.setVisibility(View.INVISIBLE);
                login_btn_txt.setVisibility(View.VISIBLE);

                if (response.isSuccessful()) {
                    
                    if ("ok".equalsIgnoreCase(response.body().getStatus())  &&  response.body().getCookie() != null) {
    
                        // Get the User Details from Response
                        UserDetails userDetails = response.body().getUserDetails();
                        userDetails.setCookie(response.body().getCookie());
                        
                        
                        if (response.body().getId() != null) {
                            userDetails.setId(response.body().getId());
                        }
                        else {
                            userDetails.setId(userDetails.getId());
                        }
                        
                        
                        if (response.body().getUser_login() != null) {
                            userDetails.setUsername(response.body().getUser_login());
                        }
                        else {
                            userDetails.setUsername(userDetails.getUsername());
                        }
    
                        
                        if (userDetails.getName() != null) {
                            userDetails.setDisplay_name(userDetails.getName());
                        }
    
                        
                        ((App) getApplicationContext().getApplicationContext()).setUserDetails(userDetails);
    
    
                        // Save necessary details in SharedPrefs
                        editor = sharedPreferences.edit();
                        editor.putString("userID", userDetails.getId());
                        editor.putString("userCookie", userDetails.getCookie());
                        editor.putString("userEmail", userDetails.getEmail());
                        editor.putString("userName", userDetails.getUsername());
                        editor.putString("userDisplayName", userDetails.getDisplay_name());
                        editor.putString("userPicture", "");
    
                        if (userDetails.getPicture() != null  &&  userDetails.getPicture().getData() != null)
                            if (!TextUtils.isEmpty(userDetails.getPicture().getData().getUrl()))
                                editor.putString("userPicture", userDetails.getPicture().getData().getUrl());
    
                        editor.putBoolean("isLogged_in", true);
                        editor.apply();
    
    
                        // Set UserLoggedIn in MyAppPrefsManager
                        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(Login.this);
                        myAppPrefsManager.setUserLoggedIn(true);
    
                        // Set isLogged_in of ConstantValues
                        ConstantValues.IS_GUEST_LOGGED_IN = false;
                        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
    
    
                        // Navigate back to MainActivity
                        if (!cartNavigation) {
                            startActivity(new Intent(Login.this, MainActivity.class));
                        }
                        finish();
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
                        
                    }
                    else if ("ok".equalsIgnoreCase(response.body().getStatus())) {
                        if (response.body().getMsg() != null)
                            Snackbar.make(parentView, response.body().getMsg(), Snackbar.LENGTH_SHORT).show();
                    }
                    else {
                        if (response.body().getError() != null)
                            Snackbar.make(parentView, response.body().getError(), Snackbar.LENGTH_SHORT).show();
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
    
                    Toast.makeText(Login.this, "Error : "+userData.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                login_btn_txt.setVisibility(View.VISIBLE);
                Toast.makeText(Login.this, "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    
    //*********** Proceed Forgot Password Request ********//

    private void processForgotPassword(String email) {

        progress.setVisibility(View.VISIBLE);
        login_btn_txt.setVisibility(View.INVISIBLE);

        Call<UserData> call = APIClient.getInstance()
                .processForgotPassword
                        (
                                "cool",
                                email
                        );

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                progress.setVisibility(View.INVISIBLE);
                login_btn_txt.setVisibility(View.VISIBLE);

                if (response.isSuccessful()) {
                    // Show the Response Message
                    if (response.body().getMsg() != null) {
                        Snackbar.make(parentView, response.body().getMsg(), Snackbar.LENGTH_LONG).show();
                    }
                    else if (response.body().getError() != null) {
                        Snackbar.make(parentView, response.body().getError(), Snackbar.LENGTH_LONG).show();
                    }

                }
                else {
                    Converter<ResponseBody, ErrorResponse> converter = APIClient.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                    ErrorResponse error;
                    try {
                        error = converter.convert(response.errorBody());
                    } catch (IOException e) {
                        error = new ErrorResponse();
                    }
    
                    Toast.makeText(Login.this, "Error : "+error.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                login_btn_txt.setVisibility(View.VISIBLE);
                Toast.makeText(Login.this, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //*********** Validate Login Form Inputs ********//

    private boolean validateLogin() {
        if (!ValidateInputs.isValidName(user_email.getText().toString().trim())) {
            user_email.setError(getString(R.string.invalid_first_name));
            return false;
        }
        else if (!ValidateInputs.isValidPassword(user_password.getText().toString().trim())) {
            user_password.setError(getString(R.string.invalid_password));
            return false;
        }
        else {
            return true;
        }
    }

    //*********** Called when the Activity has detected the User pressed the Back key ********//
    
    @Override
    public void onBackPressed() {
    
        // Navigate back to MainActivity
        if (!cartNavigation)
            startActivity(new Intent(Login.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            
            case android.R.id.home:
    
                // Navigate back to MainActivity
                if (!cartNavigation)
                    startActivity(new Intent(Login.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
                
                return true;
            
            default:
                return super.onOptionsItemSelected(item);
                
        }
    }
    
}

