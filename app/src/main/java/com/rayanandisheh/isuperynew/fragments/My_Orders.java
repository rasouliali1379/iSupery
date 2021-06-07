package com.rayanandisheh.isuperynew.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.FrameLayout;

import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.activities.MainActivity;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.rayanandisheh.isuperynew.network.APIClient;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.adapters.OrdersListAdapter;
import com.rayanandisheh.isuperynew.models.order_model.OrderDetails;

import retrofit2.Call;
import retrofit2.Callback;


public class My_Orders extends Fragment {

    View rootView;
    String customerID;
    
    TextView emptyRecord;
    FrameLayout banner_adView;
    RecyclerView orders_recycler;
    SwipeRefreshLayout refresh;

    OrdersListAdapter ordersListAdapter;

    List<OrderDetails> ordersList = new ArrayList<>();


    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.MY_ORDER_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.MY_ORDER_PAGE;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.my_orders, container, false);

        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionOrders));

        // Get the CustomerID from SharedPreferences
        customerID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");
        
        
        // Binding Layout Views
        emptyRecord = (TextView) rootView.findViewById(R.id.empty_record);
        banner_adView = (FrameLayout) rootView.findViewById(R.id.banner_adView);
        orders_recycler = (RecyclerView) rootView.findViewById(R.id.orders_recycler);
        refresh = rootView.findViewById(R.id.my_orders_swipe_refresh);
        refresh.setColorSchemeResources(R.color.colorPrimaryDark);
        refresh.setRefreshing(true);

        // Hide some of the Views
        emptyRecord.setVisibility(View.GONE);

        
        ordersList = new ArrayList<>();
    
    
        ordersListAdapter = new OrdersListAdapter(getContext(), ordersList, this::openDetails);
    
        // Set the Adapter and LayoutManager to the RecyclerView
        orders_recycler.setAdapter(ordersListAdapter);
        orders_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        


        // Request for User's Orders
        if (!ConstantValues.IS_GUEST_LOGGED_IN) {
            RequestMyOrders();
        }
        else {
            emptyRecord.setVisibility(View.VISIBLE);
        }

        refresh.setOnRefreshListener(() -> {
            refresh.setRefreshing(true);
            ordersList.clear();
            ordersListAdapter.notifyDataSetChanged();
            RequestMyOrders();
        });

        return rootView;
    }

    private void openDetails(int orderId) {
        Bundle itemInfo = new Bundle();
        itemInfo.putInt("orderID", orderId);

        Fragment fragment = new Order_Details();
        fragment.setArguments(itemInfo);
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null).commit();
    }

    public void RequestMyOrders() {


        Map<String, String> params = new LinkedHashMap<>();
        params.put("per_page", String.valueOf(100));
        params.put("customer", String.valueOf(customerID));
        

        Call<List<OrderDetails>> call = APIClient.getInstance()
                .getAllOrders
                        (
                                params
                        );

        call.enqueue(new Callback<List<OrderDetails>>() {
            @Override
            public void onResponse(Call<List<OrderDetails>> call, retrofit2.Response<List<OrderDetails>> response) {

                refresh.setRefreshing(false);
                // Check if the Response is successful
                if (response.isSuccessful()) {
    
                    ordersList.addAll(response.body());
                    ordersListAdapter.notifyDataSetChanged();
    
    
                    if (ordersListAdapter.getItemCount() < 1)
                        emptyRecord.setVisibility(View.VISIBLE);
                    
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderDetails>> call, Throwable t) {
                refresh.setRefreshing(false);
                Toast.makeText(getContext(), "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
    }

}

