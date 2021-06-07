package com.rayanandisheh.isuperynew.customs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.adapters.SearchProductsAdapter;
import com.rayanandisheh.isuperynew.models.api_response_model.ErrorResponse;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;
import com.rayanandisheh.isuperynew.network.APIClient;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;

public abstract class SearchProductsDialog extends Dialog {

    AppCompatEditText searchEt;
    ImageView searchBtn, clearBtn;
    RecyclerView recyclerView;
    SpinKitView progressBar;
    TextView nothingFoundTxt;

    SearchProductsAdapter searchProductsAdapter;

    Context context;
    Activity activity;

    public SearchProductsDialog(Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setContentView(R.layout.search_products_dialog);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        initViews();
        initListeners();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        searchProductsAdapter = new SearchProductsAdapter(context, false, this::chooseProduct);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(searchProductsAdapter);
    }

    protected void initListeners(){
        searchBtn.setOnClickListener(v -> {
            if (searchEt.getText() != null){
                if (!searchEt.getText().toString().isEmpty()){
                    searchProductsAdapter.clearData();
                    RequestSearchData(searchEt.getText().toString());
                    hideKeyBoard();
                }
            }
        });

        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!searchEt.getText().toString().isEmpty()) {
                    searchProductsAdapter.clearData();
                    RequestSearchData(searchEt.getText().toString());
                    hideKeyBoard();
                    return true;
                }
            }
            return false;
        });

        clearBtn.setOnClickListener(v -> searchEt.setText(""));

        searchEt.addTextChangedListener(new TextWatcher() {
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
    }

    private void initViews() {
        recyclerView = findViewById(R.id.search_dialog_recyclerview);
        searchEt = findViewById(R.id.search_products_et);
        searchBtn = findViewById(R.id.search_products_btn);
        progressBar = findViewById(R.id.search_dialog_progress_bar);
        nothingFoundTxt = findViewById(R.id.search_dialog_nothing_found_txt);
        clearBtn = findViewById(R.id.search_products_clear_btn);
    }

    public void RequestSearchData(String searchValue) {

        progressBar.setVisibility(View.VISIBLE);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("per_page", "10");
        params.put("search", searchValue);

        Call<List<ProductDetails>> call = APIClient.getInstance().getAllProducts(params);

        call.enqueue(new Callback<List<ProductDetails>>() {
            @Override
            public void onResponse(Call<List<ProductDetails>> call, retrofit2.Response<List<ProductDetails>> response) {

                if (response.isSuccessful()) {

                    progressBar.setVisibility(View.GONE);

                    if (response.body().size() > 0){
                        nothingFoundTxt.setVisibility(View.GONE);
                        addResults(response.body(), searchValue);
                    } else {
                        nothingFoundTxt.setVisibility(View.VISIBLE);
                    }

                } else {
                    Converter<ResponseBody, ErrorResponse> converter = APIClient.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                    ErrorResponse error;
                    try {
                        error = converter.convert(response.errorBody());
                    } catch (IOException e) {
                        error = new ErrorResponse();
                    }

                    progressBar.setVisibility(View.GONE);
                    nothingFoundTxt.setVisibility(View.VISIBLE);

                    if (error.getMessage() != null) {
                        Toast.makeText(getContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void addResults(List<ProductDetails> productsList, String searchValue) {
        List<ProductDetails> products = new ArrayList<>();
        if (productsList.size() > 0) {
            for (int i=0;  i < productsList.size();  i++) {
                if (productsList.get(i).getName().contains(searchValue)) {
                    products.add(productsList.get(i));
                }
            }
            searchProductsAdapter.setData(products);
        }
    }

    public abstract void chooseProduct(ProductDetails product);

}
