package com.rayanandisheh.isuperynew.fragments;


import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;

import com.github.ybq.android.spinkit.SpinKitView;
import com.rayanandisheh.isuperynew.Interfaces.FilterDialogInterfaces;
import com.rayanandisheh.isuperynew.Interfaces.ModifyBasketFab;
import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.R;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rayanandisheh.isuperynew.adapters.ProductAdapter;
import com.rayanandisheh.isuperynew.app.App;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.customs.FilterDialog;
import com.rayanandisheh.isuperynew.customs.OnSaleDialogFragment;
import com.rayanandisheh.isuperynew.models.api_response_model.ErrorResponse;
import com.rayanandisheh.isuperynew.models.banner_model.BannerDetails;
import com.rayanandisheh.isuperynew.models.category_model.CategoryDetails;
import com.rayanandisheh.isuperynew.models.product_model.Filters;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;
import com.rayanandisheh.isuperynew.network.APIClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;


public class HomePage extends Fragment implements ViewTreeObserver.OnScrollChangedListener, FilterDialogInterfaces {

    View rootView;

    SliderLayout sliderLayout;
    PagerIndicator pagerIndicator;
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;
    ImageView switchLayout, sortBtn;
    TextView filterBtn;
    LoadMoreTask loadMoreTask;
    TextView nothingFoundTxt;
    SwipeRefreshLayout refresh;
    SpinKitView progressDialog, recyclerviewProgressBar;
    Filters filters = null;


    List<BannerDetails> bannerImages = new ArrayList<>();
    List<CategoryDetails> allCategoriesList = new ArrayList<>();
    List<ProductDetails> productsList = new ArrayList<>();
    List<ProductDetails> onSaleProductList = new ArrayList<>();

    FragmentManager fragmentManager;

    ScrollView scrollView;

    boolean isGridView = false, bottomReached = true, isFilterApplied = false, productFinished = false, taskPermitted = false;

    int pageNum = 1, onSalePageNum = 1;
    String order = "asc";
    String sortBy = "title";

    Call<List<ProductDetails>> onSaleCall;

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.HOME_PAGE;

        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount() - 1; ++i) {
            fm.popBackStack();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.HOME_PAGE;

        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (onSaleCall != null) {
            onSaleCall.cancel();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.homepage, container, false);

        productsList.clear();
        taskPermitted = false;
        pageNum = 1;

        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(ConstantValues.APP_NAME);

        bannerImages = ((App) getContext().getApplicationContext()).getBannersList();
        allCategoriesList = ((App) getContext().getApplicationContext()).getCategoriesList();
        ((ModifyBasketFab) getContext()).appearFab();


        sliderLayout = rootView.findViewById(R.id.banner_slider);
        pagerIndicator = rootView.findViewById(R.id.banner_slider_indicator);
        recyclerView = rootView.findViewById(R.id.homepage_recyclerview);
        nothingFoundTxt = rootView.findViewById(R.id.homepage_nothing_found);
        progressDialog = rootView.findViewById(R.id.homepage_progress_dialog);
        recyclerviewProgressBar = rootView.findViewById(R.id.homepage_recyclerview_progress_dialog);
        scrollView = rootView.findViewById(R.id.homepage_scrollview);
        switchLayout = rootView.findViewById(R.id.products_layout_btn);
        sortBtn = rootView.findViewById(R.id.products_sort_btn);
        filterBtn = rootView.findViewById(R.id.products_filter_txt);
        refresh = rootView.findViewById(R.id.homepage_swipe_refresh);

        refresh.setColorSchemeResources(R.color.colorPrimaryDark);

        setupBannerSlider(bannerImages);
        setupProductsRecyclerView();

        fragmentManager = getFragmentManager();
        initListeners();
        return rootView;
    }

    private void initListeners() {
        sortBtn.setOnClickListener(v -> {

            final String[] sortArray = getResources().getStringArray(R.array.sortBy_array);

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setCancelable(true);

            dialog.setItems(sortArray, (dialog1, which) -> {

                String selectedText = sortArray[which];

                if (selectedText.equalsIgnoreCase(sortArray[0])) {

                    order = "asc";
                    sortBy = "title";

                } else if (selectedText.equalsIgnoreCase(sortArray[1])) {

                    order = "desc";
                    sortBy = "title";

                } else if (selectedText.equalsIgnoreCase(sortArray[2])) {

                    order = "desc";
                    sortBy = "date";

                } else {

                    order = "desc";
                    sortBy = "date";

                }

                pageNum = 1;
                taskPermitted = false;
                productsList.clear();
                productAdapter.notifyDataSetChanged();
                recyclerviewProgressBar.setVisibility(View.INVISIBLE);
                progressDialog.setVisibility(View.VISIBLE);

                if(isFilterApplied){
                    RequestFilteredProducts(pageNum, filters);
                } else {
                    RequestAllProducts(pageNum);
                }


                dialog1.dismiss();

            });
            dialog.show();
        });

        filterBtn.setOnClickListener(view -> {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            androidx.fragment.app.Fragment prev = getChildFragmentManager().findFragmentByTag("filterDialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            FilterDialog filterDialog = new FilterDialog(getContext(), filters);
            filterDialog.show(ft, "filterDialog");
        });

        refresh.setOnRefreshListener(() -> {
            if (productsList != null && productAdapter != null){
                productsList.clear();
                productAdapter.notifyDataSetChanged();
                pageNum = 1;
                taskPermitted = false;
                progressDialog.setVisibility(View.VISIBLE);
                if (filters == null){
                    RequestAllProducts(pageNum);
                } else {
                    RequestFilteredProducts(pageNum, filters);
                }
            }
        });
        scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
    }



    private void setupProductsRecyclerView() {
        gridLayoutManager = new GridLayoutManager(getContext(), 2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        linearLayoutManager = new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        productAdapter = new ProductAdapter(getContext(), false, this::gotoProductDetails);
        setRecyclerViewLayoutManager(isGridView);
        recyclerView.setAdapter(productAdapter);


        switchLayout.setOnClickListener(v-> {
            isGridView = !isGridView;
            switchLayout.setImageResource(isGridView ? R.drawable.ic_column : R.drawable.ic_grid);
            setRecyclerViewLayoutManager(isGridView);
        });
        RequestAllProducts(pageNum);

        if (!MainActivity.onSaleShown){
            RequestOnSaleProducts();
        }
    }

    private void gotoProductDetails(ProductDetails product) {
        // Get Product Info
        Bundle itemInfo = new Bundle();
//        itemInfo.putParcelable("productDetails", product);

        itemInfo.putInt("itemID", product.getId());

        Fragment fragment = new Product_Description();

        fragment.setArguments(itemInfo);
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null).commit();

    }

    public void setRecyclerViewLayoutManager(Boolean isGridView) {
        int scrollPosition = 0;

        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        productAdapter.toggleLayout(isGridView);

        recyclerView.setLayoutManager(isGridView ? gridLayoutManager : linearLayoutManager);
        recyclerView.setAdapter(productAdapter);

        recyclerView.scrollToPosition(scrollPosition);
    }

    public void RequestOnSaleProducts() {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("page", String.valueOf(onSalePageNum));
        params.put("order", order);
        params.put("orderby", sortBy);
        params.put("in_stock", "true");
        params.put("on_sale", "true");

        onSaleCall = APIClient.getInstance().getAllProducts(params);

        onSaleCall.enqueue(new Callback<List<ProductDetails>>() {
            @Override
            public void onResponse(Call<List<ProductDetails>> call, retrofit2.Response<List<ProductDetails>> response) {


                if (response.isSuccessful()) {
                    if (response.body().size() != 0){
                        onSaleProductList.addAll(response.body());
                        onSalePageNum ++;
                        RequestOnSaleProducts();
                    } else {
                        if (onSaleProductList.size() > 0 ){
                            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                            androidx.fragment.app.Fragment prev = getChildFragmentManager().findFragmentByTag("dialog");
                            if (prev != null) {
                                ft.remove(prev);
                            }
                            ft.addToBackStack(null);
                            OnSaleDialogFragment onSaleDialogFragment = new OnSaleDialogFragment(getContext(), onSaleProductList);
                            onSaleDialogFragment.show(ft, "dialog");
                            MainActivity.onSaleShown = true;
                        }
                    }
                } else {
                    Converter<ResponseBody, ErrorResponse> converter = APIClient.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                    ErrorResponse error;
                    progressDialog.setVisibility(View.GONE);
                    try {
                        error = converter.convert(response.errorBody());
                    } catch (IOException e) {
                        error = new ErrorResponse();
                    }

                }
            }

            @Override
            public void onFailure(Call<List<ProductDetails>> call, Throwable t) {
            }
        });
    }


    public void RequestAllProducts(final int pageNumber) {

        nothingFoundTxt.setVisibility(View.GONE);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("page", String.valueOf(pageNumber));
        params.put("order", order);
        params.put("orderby", sortBy);
        params.put("in_stock", "true");

        Call<List<ProductDetails>> call = APIClient.getInstance().getAllProducts(params);

        call.enqueue(new Callback<List<ProductDetails>>() {
            @Override
            public void onResponse(Call<List<ProductDetails>> call, retrofit2.Response<List<ProductDetails>> response) {

                refresh.setRefreshing(false);
                if (response.isSuccessful()) {
                    if (response.body().size() > 0){
                        progressDialog.setVisibility(View.GONE);
                        addProducts(response.body());
                    } else {
                        if (filters == null){
                            filters = new Filters();
                        }
                        filters.setInStock(false);
                        filters.setCategoryId(allCategoriesList.get(0).getId());
                        isFilterApplied = true;
                        productFinished = false;
                        pageNum = 1;
                        taskPermitted = false;
                        Log.e("pageNum", String.valueOf(pageNum));
                        RequestFilteredProducts(pageNum, filters);
                    }
                } else {
                    Converter<ResponseBody, ErrorResponse> converter = APIClient.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                    ErrorResponse error;
                    progressDialog.setVisibility(View.GONE);
                    try {
                        error = converter.convert(response.errorBody());
                    } catch (IOException e) {
                        error = new ErrorResponse();
                    }

                    Toast.makeText(App.getContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductDetails>> call, Throwable t) {
                progressDialog.setVisibility(View.GONE);
                Toast.makeText(App.getContext(), "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
    }



    //*********** Request Products from the Server based on PageNo. against some Filters ********//

    public void RequestFilteredProducts(final int pageNumber, Filters filters) {

        nothingFoundTxt.setVisibility(View.GONE);
        Map<String, String> params = new LinkedHashMap<>();
        if(filters.getCategoryId() > -1){
            Log.e("category", String.valueOf(filters.getCategoryId()));
            params.put("category", String.valueOf(filters.getCategoryId()));
            params.put("min_price", "0");
            params.put("max_price", ConstantValues.FILTER_MAX_PRICE);

        }
        params.put("page", String.valueOf(pageNumber));
        params.put("order", order);
        params.put("orderby", sortBy);

        if (filters.isInStock()){
            params.put("in_stock", "true");
        }

        if (filters.isOnSale()){
            params.put("on_sale", "true");
        }

        if (filters.getMin_price() != null){
            if (filters.getMin_price().length() > 0){
                params.put("min_price", filters.getMin_price());
                Log.e("min_price", filters.getMin_price());
            } else {
                params.put("min_price", "0");
            }
        } else {
            params.put("min_price", "0");
        }

        if (filters.getMax_price() != null){
            if (filters.getMax_price().length() > 0){
                params.put("max_price", filters.getMax_price());
                Log.e("max_price", filters.getMax_price());
            } else {
                params.put("max_price", ConstantValues.FILTER_MAX_PRICE);
            }
        } else {
            params.put("max_price", ConstantValues.FILTER_MAX_PRICE);
        }

//        if (filters.getSale())
//            params.put("on_sale", ""+filters.getSale());
//
//        if (filters.getFeature())
//            params.put("featured", ""+filters.getFeature());


        Call<List<ProductDetails>> call = APIClient.getInstance().getAllProducts(params);

        call.enqueue(new Callback<List<ProductDetails>>() {
            @Override
            public void onResponse(Call<List<ProductDetails>> call, retrofit2.Response<List<ProductDetails>> response) {

                // Hide the ProgressBar
                refresh.setRefreshing(false);
                progressDialog.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.e("body", response.body().toString());
                    addProducts(response.body());
                }
                else {
                    Converter<ResponseBody, ErrorResponse> converter = APIClient.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                    ErrorResponse error;
                    try {
                        error = converter.convert(response.errorBody());
                    } catch (IOException e) {
                        error = new ErrorResponse();
                    }

                    Toast.makeText(App.getContext(), "Error : "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductDetails>> call, Throwable t) {
                progressDialog.setVisibility(View.GONE);
                Toast.makeText(App.getContext(), "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addProducts(List<ProductDetails> products) {
        if (products.size() > 0) {
            productsList.addAll(products);
            bottomReached = true;
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getStatus() != null && !"publish".equalsIgnoreCase(products.get(i).getStatus())) {
                    for (int j = 0; j < productsList.size(); j++) {
                        if (products.get(i).getId() == productsList.get(j).getId()) {
                            productsList.remove(j);
                        }
                    }
                }
            }
            Log.e("pageNum", String.valueOf(pageNum));
            productAdapter.setData(productsList);

            if (products.size() < 10){
                productFinished = true;
                recyclerviewProgressBar.setVisibility(View.INVISIBLE);
            } else {
                pageNum++;
                taskPermitted = true;
                recyclerviewProgressBar.setVisibility(View.VISIBLE);
//                scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
            }

        } else {
            productFinished = true;
            recyclerviewProgressBar.setVisibility(View.INVISIBLE);
            nothingFoundTxt.setVisibility(View.VISIBLE);
        }
    }

    //*********** Setup the BannerSlider with the given List of BannerImages ********//

    private void setupBannerSlider(final List<BannerDetails> bannerImages) {

        // Initialize new LinkedHashMap<ImageName, ImagePath>
        final LinkedHashMap<String, String> slider_covers = new LinkedHashMap<>();


        for (int i=0;  i< bannerImages.size();  i++) {
            // Get bannerDetails at given Position from bannerImages List
            BannerDetails bannerDetails =  bannerImages.get(i);

            // Put Image's Name and URL to the HashMap slider_covers
            slider_covers.put
                    (
                            "Image"+bannerDetails.getBannersTitle(),
                            bannerDetails.getBannersImage()
                    );
        }


        for(String name : slider_covers.keySet()) {
            // Initialize DefaultSliderView
            final DefaultSliderView defaultSliderView = new DefaultSliderView(getContext());

            // Set Attributes(Name, Image, Type etc) to DefaultSliderView
            defaultSliderView
                    .description(name)
                    .image(slider_covers.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            

            // Add DefaultSliderView to the SliderLayout
            sliderLayout.addSlider(defaultSliderView);
        }

        // Set PresetTransformer type of the SliderLayout
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);


        // Check if the size of Images in the Slider is less than 2
        if (slider_covers.size() < 2) {
            // Disable PagerTransformer
            sliderLayout.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float v) {
                }
            });

            // Hide Slider PagerIndicator
            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);

        } else {
            // Set custom PagerIndicator to the SliderLayout
            sliderLayout.setCustomIndicator(pagerIndicator);
            // Make PagerIndicator Visible
            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
        }
        
    }

    @Override
    public void onScrollChanged() {
        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int bottomDetector = view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY());
        if(bottomDetector == 0 && bottomReached && taskPermitted){
            loadMoreTask = new LoadMoreTask(pageNum, filters);
            loadMoreTask.execute();
            bottomReached = false;
            Log.e("scrolling", "bottom reached");
        }
    }

    @Override
    public void applyFilters(Filters appliedFilters) {
        isFilterApplied = true;
        filters = appliedFilters;
        pageNum = 1;
        taskPermitted = false;
        productFinished = false;
        productsList.clear();
        productAdapter.notifyDataSetChanged();
        progressDialog.setVisibility(View.VISIBLE);
        recyclerviewProgressBar.setVisibility(View.INVISIBLE);
        new LoadMoreTask(pageNum, filters).execute();
    }

    @Override
    public void clearFilters() {
        isFilterApplied = false;
        filters = null;
        pageNum = 1;
        taskPermitted = false;
        productFinished = false;
        productsList.clear();
        productAdapter.notifyDataSetChanged();
        progressDialog.setVisibility(View.VISIBLE);
        recyclerviewProgressBar.setVisibility(View.INVISIBLE);
        new LoadMoreTask(pageNum, filters).execute();
    }

    private class LoadMoreTask extends AsyncTask<String, Void, String> {

        int page_number;
        Filters filters;

        private LoadMoreTask(int page_number, Filters filters) {
            this.page_number = page_number;
            this.filters = filters;
        }


        //*********** Runs on the UI thread before #doInBackground() ********//

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        //*********** Performs some Processes on Background Thread and Returns a specified Result  ********//

        @Override
        protected String doInBackground(String... params) {
            Log.e("pageNumTask", String.valueOf(pageNum));
            if (!productFinished){
                if (isFilterApplied) {
                    RequestFilteredProducts(page_number, filters);
                }
                else {
                    RequestAllProducts(page_number);
                }
            }

            return "All Done!";
        }


        //*********** Runs on the UI thread after #doInBackground() ********//

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}

