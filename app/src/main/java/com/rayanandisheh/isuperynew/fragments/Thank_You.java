package com.rayanandisheh.isuperynew.fragments;

import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.rayanandisheh.isuperynew.Interfaces.ModifyBasketFab;
import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.app.App;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.models.user_model.UpdateUser;
import com.rayanandisheh.isuperynew.models.user_model.UserShipping;
import com.rayanandisheh.isuperynew.network.APIClient;

import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


public class Thank_You extends Fragment {
    
    String customerID;
    
    FrameLayout banner_adView;
    Button order_status_btn, continue_shopping_btn, return_btn;
    ImageView order_status_img;
    TextView thank_you_msg;

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.THANK_YOU_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.THANK_YOU_PAGE;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.thank_you, container, false);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);



        // Get the customersID and defaultAddressID from SharedPreferences
        customerID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");

        
        // Binding Layout Views
        banner_adView = (FrameLayout) rootView.findViewById(R.id.banner_adView);
        order_status_btn = (Button) rootView.findViewById(R.id.order_status_btn);
        continue_shopping_btn = (Button) rootView.findViewById(R.id.continue_shopping_btn);
        order_status_img = rootView.findViewById(R.id.thank_you_img);
        return_btn = rootView.findViewById(R.id.thank_you_return_btn);
        thank_you_msg = rootView.findViewById(R.id.thank_you_msg);

        switch (getArguments().getInt("response_code", 0)){
            case 1:
                continue_shopping_btn.setVisibility(View.VISIBLE);
                order_status_btn.setVisibility(View.VISIBLE);
                return_btn.setVisibility(View.GONE);
                thank_you_msg.setText(getContext().getResources().getString(R.string.thank_you_for_shopping));
                order_status_img.setImageResource(R.drawable.ic_items_purchased);
                My_Cart.ClearCart();
                ((ModifyBasketFab) getContext()).syncBasket();
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.order_submited));
                break;
            case 2:
                continue_shopping_btn.setVisibility(View.GONE);
                order_status_btn.setVisibility(View.GONE);
                thank_you_msg.setText(getContext().getResources().getString(R.string.purchase_failed));
                order_status_img.setImageResource(R.drawable.ic_alert);
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.order_failed));
                break;
        }
    
        if (!ConstantValues.IS_ONE_PAGE_CHECKOUT_ENABLED)
            updateCustomerInfo();


        if (ConstantValues.IS_USER_LOGGED_IN) {
            order_status_btn.setOnClickListener(view -> {

                // Navigate to My_Orders Fragment
                Fragment fragment = new My_Orders();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();

            });
        } else {
            order_status_btn.setVisibility(View.GONE);
        }
        // Binding Layout Views


        continue_shopping_btn.setOnClickListener(view -> {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            Fragment fragment = new HomePage();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        });

        return_btn.setOnClickListener(v -> {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            Fragment fragment = new My_Cart();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        });

        return rootView;
    }

    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            getChildFragmentManager().popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        return super.onOptionsItemSelected(item);
    }



    //*********** Updates User's Personal Information ********//

    private void updateCustomerInfo() {
        //UserBilling userBilling = ((App) getContext().getApplicationContext()).getUserDetails().getBilling();
        UserShipping userShipping = ((App) getContext().getApplicationContext()).getUserDetails().getShipping();
        Map<String, String> params = new LinkedHashMap<>();
        params.put("insecure", "cool");
        params.put("user_id", customerID);

//        params.put("billing_first_name", userBilling.getFirstName());
//        params.put("billing_last_name", userBilling.getLastName());
//        params.put("billing_address_1", userBilling.getAddress1());
//        params.put("billing_address_2", userBilling.getAddress2());
//        params.put("billing_company", userBilling.getCompany());
//        params.put("billing_country", userBilling.getCountry());
//        params.put("billing_state", userBilling.getState());
//        params.put("billing_city", userBilling.getCity());
//        params.put("billing_postcode", userBilling.getPostcode());
//        params.put("billing_email", userBilling.getEmail());
//        params.put("billing_phone", userBilling.getPhone());
        if(userShipping != null){
            params.put("shipping_first_name", userShipping.getFirstName());
            params.put("shipping_last_name", userShipping.getLastName());
            params.put("shipping_address_1", userShipping.getAddress1());
            params.put("shipping_address_2", userShipping.getAddress2());
            params.put("shipping_company", userShipping.getCompany());
            params.put("shipping_country", userShipping.getCountry());
            params.put("shipping_state", userShipping.getState());
            params.put("shipping_city", userShipping.getCity());
            params.put("shipping_postcode", userShipping.getPostcode());

            Call<UpdateUser> call = APIClient.getInstance().updateCustomerInfo(params);
            call.enqueue(new Callback<UpdateUser>() {
                @Override
                public void onResponse(Call<UpdateUser> call, retrofit2.Response<UpdateUser> response) {}
                @Override
                public void onFailure(Call<UpdateUser> call, Throwable t) {}
            });
        }



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem cartItem = menu.findItem(R.id.toolbar_home);
        MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        cartItem.setVisible(false);
        searchItem.setVisible(false);
    }
    
}

