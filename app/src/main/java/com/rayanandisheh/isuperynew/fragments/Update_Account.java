package com.rayanandisheh.isuperynew.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.app.App;
import com.rayanandisheh.isuperynew.customs.CircularImageView;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.customs.DialogLoader;
import com.rayanandisheh.isuperynew.models.user_model.UpdateUser;
import com.rayanandisheh.isuperynew.models.user_model.UserData;
import com.rayanandisheh.isuperynew.models.user_model.UserDetails;
import com.rayanandisheh.isuperynew.network.APIClient;
import com.rayanandisheh.isuperynew.utils.ValidateInputs;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;

import static android.content.Context.MODE_PRIVATE;


public class Update_Account extends Fragment implements TextWatcher{

    View rootView;
    String customers_id;
    String profileImageCurrent = "";
    String profileImageChanged = "";
    private static final int PICK_IMAGE_ID = 360;           // the number doesn't matter
    
    Button updateInfoBtn;
    CircularImageView user_photo;
    EditText input_first_name, input_last_name, input_username, input_email, input_new_password;
    
    UserDetails userInfo;
    DialogLoader dialogLoader;
    SharedPreferences sharedPreferences;


    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.UPDATE_ACCOUNT_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.UPDATE_ACCOUNT_PAGE;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.update_account, container, false);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionAccount));
    
        sharedPreferences = getContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        
        // Get the CustomerID from SharedPreferences
        customers_id = sharedPreferences.getString("userID", "");
    
        // Get the User's Info from the AppContext
        userInfo = ((App) getContext().getApplicationContext()).getUserDetails();
        

        // Binding Layout Views
        user_photo = (CircularImageView) rootView.findViewById(R.id.user_photo);
        input_first_name = (EditText) rootView.findViewById(R.id.first_name);
        input_last_name = (EditText) rootView.findViewById(R.id.last_name);
        input_username = (EditText) rootView.findViewById(R.id.username);
        input_email = (EditText) rootView.findViewById(R.id.email);
        input_new_password = (EditText) rootView.findViewById(R.id.new_password);
        updateInfoBtn = (Button) rootView.findViewById(R.id.updateInfoBtn);

    
        input_username.setKeyListener(null);


        dialogLoader = new DialogLoader(getContext());
        

        // Get User's Details
        RequestUserDetails(customers_id);


        // Set User's Photo
        if (!TextUtils.isEmpty(userInfo.getAvatarUrl())) {
            profileImageCurrent = userInfo.getAvatarUrl();
            Glide.with(this)
                    .asBitmap()
                    .load(ConstantValues.WOOCOMMERCE_URL +profileImageCurrent)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(user_photo);
            
        }
        else if (!TextUtils.isEmpty(sharedPreferences.getString("userPicture", ""))) {
            profileImageCurrent = sharedPreferences.getString("userPicture", "");
            Glide.with(this)
                    .load(profileImageCurrent)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(user_photo);
            
        }
        else {
            profileImageCurrent = "";
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(user_photo);
        }


        // Handle Click event of updateInfoBtn Button
        updateInfoBtn.setOnClickListener(v -> {
            // Validate User's Info Form Inputs
            boolean isValidData = validateForm();

            if (isValidData) {
                processUpdateUser();
            }
        });
    
    
        if (validateFormHasInput()) {
            // Enable Update Button
            updateInfoBtn.setEnabled(true);
            updateInfoBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_button_accent));
        }
        else {
            // Disable Update Button
            updateInfoBtn.setEnabled(false);
            updateInfoBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_button_gray));
        }
    
        
        input_first_name.addTextChangedListener(this);
        input_last_name.addTextChangedListener(this);
        input_email.addTextChangedListener(this);
        input_new_password.addTextChangedListener(this);


        return rootView;

    }
    
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (validateFormHasInput()) {
            // Enable Update Button
            updateInfoBtn.setEnabled(true);
            updateInfoBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_button_accent));
        }
        else {
            // Disable Update Button
            updateInfoBtn.setEnabled(false);
            updateInfoBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_button_gray));
        }
    }
    
    @Override
    public void afterTextChanged(Editable s) {}

    //*********** Proceed Update of User's Details ********//
    
    private void processUpdateUser() {
        UpdateUser user = new UpdateUser();
        
        if (!TextUtils.isEmpty(input_first_name.getText().toString()))
            user.setFirstName(input_first_name.getText().toString());
    
        if (!TextUtils.isEmpty(input_last_name.getText().toString()))
            user.setLastName(input_last_name.getText().toString());
    
        if (!TextUtils.isEmpty(input_email.getText().toString()))
            user.setEmail(input_email.getText().toString());
    
        if (!TextUtils.isEmpty(input_new_password.getText().toString()))
            user.setPassword(input_new_password.getText().toString());
        
        updateCustomerInfo(user);
    }
    
    
    
    //*********** Updates User's Personal Information ********//

    private void updateCustomerInfo(UpdateUser userDetails) {

        dialogLoader.showProgressDialog();
    
        Map<String, String> params = new LinkedHashMap<>();
        params.put("insecure", "cool");
        params.put("user_id", customers_id);
        
        if (userDetails.getFirstName() != null)
            params.put("first_name", userDetails.getFirstName());
    
        if (userDetails.getLastName() != null)
            params.put("last_name", userDetails.getLastName());
    
        if (userDetails.getEmail() != null)
            params.put("email", userDetails.getEmail());
    
        if (userDetails.getPassword() != null)
            params.put("password", userDetails.getPassword());
        

        Call<UpdateUser> call = APIClient.getInstance()
                .updateCustomerInfo
                        (
                                params
                        );

        call.enqueue(new Callback<UpdateUser>() {
            @Override
            public void onResponse(Call<UpdateUser> call, retrofit2.Response<UpdateUser> response) {

                dialogLoader.hideProgressDialog();
    
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if ("ok".equalsIgnoreCase(response.body().getStatus())) {
    
                        if (response.body().getUserLogin() != null) {
    
                            // Update User Details
                            UserDetails userDetails = ((App) getContext().getApplicationContext()).getUserDetails();
                            
                            userDetails.setId(response.body().getID());
                            userDetails.setUsername(response.body().getUserLogin());
                            userDetails.setEmail(response.body().getUserEmail());
                            userDetails.setFirstName(response.body().getFirstName());
                            userDetails.setLastName(response.body().getLastName());
                            userDetails.setDisplay_name(response.body().getDisplayName());
    
                            ((App) getContext().getApplicationContext()).setUserDetails(userDetails);
    
    
                            // Save necessary details in SharedPrefs
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userID", userDetails.getId());
                            editor.putString("userCookie", userDetails.getCookie());
                            editor.putString("userEmail", userDetails.getEmail());
                            editor.putString("userName", userDetails.getUsername());
                            editor.putString("userDisplayName", userDetails.getDisplay_name());
                            editor.putString("userPicture", "");
    
                            if (userDetails.getPicture() != null  &&  userDetails.getPicture().getData() != null)
                                if (!TextUtils.isEmpty(userDetails.getPicture().getData().getUrl()))
                                    editor.putString("userPicture", userDetails.getPicture().getData().getUrl());
    
                            editor.apply();
    
                            ((MainActivity) getContext()).setupExpandableDrawerList();
                            ((MainActivity) getContext()).setupExpandableDrawerHeader();
                            
                            getFragmentManager().popBackStack();
                        }
                        
                        
                        if (response.body().getMessage() != null) {
                            Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(rootView, getString(R.string.account_updated), Snackbar.LENGTH_SHORT).show();
                        }
                        
                    }
                    else {
                        if (response.body().getError() != null)
                            Snackbar.make(rootView, response.body().getError(), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Converter<ResponseBody, UserData> converter = APIClient.retrofit.responseBodyConverter(UserData.class, new java.lang.annotation.Annotation[0]);
                    UserData userData;
                    try {
                        userData = converter.convert(response.errorBody());
                    } catch (IOException e) {
                        userData = new UserData();
                    }
    
                    Toast.makeText(App.getContext(), "Error : "+userData.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateUser> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    
    //*********** Get User Details based on User's Email ********//
    
    public void RequestUserDetails(String customerID) {
    
        dialogLoader.showProgressDialog();
    
        Call<UserDetails> call = APIClient.getInstance()
                .getUserInfo
                        (
                                customerID
                        );
    
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, retrofit2.Response<UserDetails> response) {
            
                dialogLoader.hideProgressDialog();
            
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    // Set User's Info to Form Inputs
                    if (response.body().getUsername() != null) {
                        input_first_name.setText(response.body().getFirstName());
                        input_last_name.setText(response.body().getLastName());
                        input_username.setText(response.body().getUsername());
                        input_email.setText(response.body().getEmail());
                    }
                }
            }
        
            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    
    //*********** Validate User Info Form Inputs ********//
    
    private boolean validateFormHasInput() {
        
        boolean hasInput = false;
        
        if (ValidateInputs.isValidInput(input_first_name.getText().toString())) {
            hasInput = true;
        }
        else if (ValidateInputs.isValidInput(input_last_name.getText().toString())) {
            hasInput = true;
        }
        else if (ValidateInputs.isValidInput(input_email.getText().toString())) {
            hasInput = true;
        }
        else if (ValidateInputs.isValidInput(input_new_password.getText().toString())) {
            hasInput = true;
        }
        else {
            hasInput = false;
        }
        
        return hasInput;
    }
    
    
    //*********** Validate User Info Form Inputs ********//
    
    private boolean validateForm() {

        if (!ValidateInputs.isIfValidInput(input_first_name.getText().toString())) {
            input_first_name.setError(getString(R.string.invalid_first_name));
            return false;
        }
        else if (!ValidateInputs.isIfValidInput(input_last_name.getText().toString())) {
            input_last_name.setError(getString(R.string.invalid_last_name));
            return false;
        }
        else if (!ValidateInputs.isIfValidInput(input_email.getText().toString())) {
            input_email.setError(getString(R.string.invalid_email));
            return false;
        }
        else if (!TextUtils.isEmpty(input_new_password.getText())) {
            if (!ValidateInputs.isValidPassword(input_new_password.getText().toString())) {
                input_new_password.setError(getString(R.string.invalid_password));
                return false;
            } else {
                return true;
            }
        }
        else {
            return true;
        }
    }

}
