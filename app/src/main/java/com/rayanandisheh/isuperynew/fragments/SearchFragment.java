package com.rayanandisheh.isuperynew.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.R;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rayanandisheh.isuperynew.adapters.ProductAdapter;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.models.api_response_model.ErrorResponse;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;
import com.rayanandisheh.isuperynew.network.APIClient;
import com.rayanandisheh.isuperynew.utils.Utilities;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;


public class SearchFragment extends Fragment {

    View rootView;
    
    EditText search_editText;
    RecyclerView products_recycler;
    ImageView searchBtn, clearBtn;
    TextView nothingFoundTxt;
    SpinKitView progressBar;
//    List<CategoryDetails> allCategoriesList;
//    List<CategoryDetails> subCategoriesList;
    List<ProductDetails> searchedProductsList;

    ProductAdapter searchProductsAdapter;

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.SEARCH_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.SEARCH_PAGE;
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.search_fragment, container, false);

        setHasOptionsMenu(true);

        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionSearch));
        
        
        // Binding Layout Views
        search_editText = rootView.findViewById(R.id.search_editText);
        products_recycler = rootView.findViewById(R.id.products_recycler);
        searchBtn = rootView.findViewById(R.id.search_btn);
        clearBtn = rootView.findViewById(R.id.search_fragment_clear_btn);
        progressBar = rootView.findViewById(R.id.search_fragment_progress_bar);
        nothingFoundTxt = rootView.findViewById(R.id.search_fragment_nothing_found_txt);
//        categories_recycler = rootView.findViewById(R.id.categories_recycler);

        products_recycler.setNestedScrollingEnabled(false);
//        categories_recycler.setNestedScrollingEnabled(false);

        // Hide some of the Views
        products_recycler.setVisibility(View.GONE);
//        categories_recycler.setVisibility(View.GONE);

    

        
//        subCategoriesList = new ArrayList<>();
        searchedProductsList = new ArrayList<>();
        
        
        // Get All CategoriesList from ApplicationContext
//        allCategoriesList = ((App) getContext().getApplicationContext()).getCategoriesList();

//        for (int i=0;  i<allCategoriesList.size();  i++) {
//            if (allCategoriesList.get(i).getId() != 0) {
//                subCategoriesList.add(allCategoriesList.get(i));
//            }
//        }


        // Initialize the SearchResultsAdapter for RecyclerView
        searchProductsAdapter = new ProductAdapter(getContext(), false, this::gotoProductDetails);
        searchProductsAdapter.toggleLayout(false);
    
        // Set the Adapter, LayoutManager and ItemDecoration to the RecyclerView
        products_recycler.setAdapter(searchProductsAdapter);
        products_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    
//        searchProductsAdapter.toggleLayout(false);
        searchProductsAdapter.notifyDataSetChanged();
        
        
    
        // Initialize the CategoryListAdapter for RecyclerView
//        categoryListAdapter = new CategoryListAdapter_5(getContext(), subCategoriesList, false);
//
//        // Set the Adapter and LayoutManager to the RecyclerView
//        categories_recycler.setAdapter(categoryListAdapter);
//        categories_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        categoryListAdapter.notifyDataSetChanged();
        
        
        
        // Show Categories
    
//        search_editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (search_editText.getText().toString().isEmpty()) {
//                    // Show Categories
//                    showCategories();
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
        
        searchBtn.setOnClickListener(v -> {
            if (search_editText.getText() != null){
                if (!search_editText.getText().toString().isEmpty()) {
                    RequestSearchData(search_editText.getText().toString());
                    Utilities.hideKeyboard(getActivity());
                }
            }
        });

        search_editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!search_editText.getText().toString().isEmpty()) {
                    RequestSearchData(search_editText.getText().toString());
                    return true;
                }
            }
            return false;
        });

        clearBtn.setOnClickListener(v -> search_editText.setText(""));

        search_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0 ){
                    clearBtn.setVisibility(View.VISIBLE);
                } else {
                    clearBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootView;
    }

    private void gotoProductDetails(ProductDetails product) {
        // Get Product Info
        Bundle itemInfo = new Bundle();
        itemInfo.putParcelable("productDetails", product);

        Fragment fragment = new Product_Description();

        fragment.setArguments(itemInfo);
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null).commit();

    }

    private void addResults(List<ProductDetails> productsList, String searchValue) {
        
        if (productsList.size() > 0) {
            
            searchedProductsList.clear();
            searchedProductsList.addAll(productsList);
    
            for (int i=0;  i<productsList.size();  i++) {
                if ((productsList.get(i).getStatus() != null  &&  !"publish".equalsIgnoreCase(productsList.get(i).getStatus())) || !productsList.get(i).getName().contains(searchValue)) {
                    for (int j=0;  j < searchedProductsList.size();  j++) {
                        if (productsList.get(i).getId() == searchedProductsList.get(j).getId()) {
                            searchedProductsList.remove(j);
                        }
                    }
                }
            }

            if (searchedProductsList.size() > 0){
                searchProductsAdapter.setData(searchedProductsList);
                nothingFoundTxt.setVisibility(View.INVISIBLE);
                products_recycler.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                nothingFoundTxt.setVisibility(View.VISIBLE);
                searchedProductsList.clear();
                searchProductsAdapter.notifyDataSetChanged();
            }
        } else {
            nothingFoundTxt.setVisibility(View.VISIBLE);
            searchedProductsList.clear();
            searchProductsAdapter.notifyDataSetChanged();
        }
    }

    public void RequestSearchData(String searchValue) {
        searchedProductsList.clear();
        searchProductsAdapter.notifyDataSetChanged();

        progressBar.setVisibility(View.VISIBLE);
        nothingFoundTxt.setVisibility(View.INVISIBLE);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("per_page", "10");
        params.put("search", searchValue);
    
        Call<List<ProductDetails>> call = APIClient.getInstance().getAllProducts(params);
    
        call.enqueue(new Callback<List<ProductDetails>>() {
            @Override
            public void onResponse(Call<List<ProductDetails>> call, retrofit2.Response<List<ProductDetails>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    
                    addResults(response.body(), searchValue);
                
                }
                else {
                    Converter<ResponseBody, ErrorResponse> converter = APIClient.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                    ErrorResponse error;
                    try {
                        error = converter.convert(response.errorBody());
                    } catch (IOException e) {
                        error = new ErrorResponse();
                    }

                    progressBar.setVisibility(View.GONE);
                
                    if (error.getMessage() != null) {
                        Toast.makeText(getContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Snackbar.make(rootView, getString(R.string.record_not_found), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        
            @Override
            public void onFailure(Call<List<ProductDetails>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
        
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem cartItem = menu.findItem(R.id.toolbar_home);
        MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        cartItem.setVisible(false);
        searchItem.setVisible(false);
    }

}



