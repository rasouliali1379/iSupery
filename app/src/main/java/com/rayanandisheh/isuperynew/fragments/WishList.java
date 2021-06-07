package com.rayanandisheh.isuperynew.fragments;


import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

import com.rayanandisheh.isuperynew.app.App;
import com.rayanandisheh.isuperynew.adapters.ProductAdapterRemovable;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.databases.User_Favorites_DB;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;
import com.rayanandisheh.isuperynew.network.APIClient;

import retrofit2.Call;
import retrofit2.Callback;


public class WishList extends Fragment {

    View rootView;
    String customerID;

    TextView emptyRecord;
    ProgressBar progressBar;
    ProgressBar loadingProgress;
    RecyclerView favourites_recycler;

    User_Favorites_DB user_favorites;
    GridLayoutManager gridLayoutManager;
    ProductAdapterRemovable productAdapter;
    
    ArrayList<Integer> favorites;
    List<ProductDetails> favouriteProductsList;

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.WISH_LIST_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.WISH_LIST_PAGE;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.f_products_vertical, container, false);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionFavourites));
    
    
        favorites = new ArrayList<>();
        favouriteProductsList  = new ArrayList<>();
        
        user_favorites = new User_Favorites_DB();
    
        // Get the List of Favorite Product's IDs from the Local Databases User_Favorites_DB
        favorites = user_favorites.getUserFavorites();
        
        // Get the CustomerID from SharedPreferences
        customerID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");


        // Binding Layout Views

        emptyRecord = (TextView) rootView.findViewById(R.id.empty_record);
        progressBar = (ProgressBar) rootView.findViewById(R.id.loading_bar);
        loadingProgress = (ProgressBar) rootView.findViewById(R.id.loading_progress);
        favourites_recycler = (RecyclerView) rootView.findViewById(R.id.products_recycler);


        // Hide some of the Views
        emptyRecord.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        loadingProgress.setVisibility(View.GONE);


        // Initialize the ProductAdapter and GridLayoutManager for RecyclerView
        productAdapter = new ProductAdapterRemovable(getContext(), favouriteProductsList, false, false, emptyRecord);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);

        
        // Set the Adapter and LayoutManager to the RecyclerView
        favourites_recycler.setAdapter(productAdapter);
        favourites_recycler.setLayoutManager(gridLayoutManager);
    
    
        // Check if the recents List isn't empty
        if (favorites.size() < 1) {
            emptyRecord.setVisibility(View.VISIBLE);
        }
        else {
            emptyRecord.setVisibility(View.GONE);
        
            for (int i=0;  i<favorites.size();  i++) {
                // Request current Product's Details
                RequestProductDetails(favorites.get(i));
            }
        }


        return rootView;
    }



    //*********** Adds Products returned from the Server to the FavouriteProductsList ********//
    
    private void addFavouriteProducts(ProductDetails product) {
        
        // Add Products to recentViewedList
        favouriteProductsList.add(product);
        
        // Notify the Adapter
        productAdapter.notifyDataSetChanged();
    
    
        if (productAdapter.getItemCount() < 1)
            emptyRecord.setVisibility(View.VISIBLE);
    }



    //*********** Request Product's Details from the Server based on Product's ID ********//

    public void RequestProductDetails(final int products_id) {
    
        Call<ProductDetails> call = APIClient.getInstance()
                .getSingleProduct
                        (
                                String.valueOf(products_id)
                        );
    
        call.enqueue(new Callback<ProductDetails>() {
            @Override
            public void onResponse(Call<ProductDetails> call, retrofit2.Response<ProductDetails> response) {
            
                if (response.isSuccessful()) {
    
                    addFavouriteProducts(response.body());
                }
            }
        
            @Override
            public void onFailure(Call<ProductDetails> call, Throwable t) {
                Toast.makeText(App.getContext(), "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
    }
    
}