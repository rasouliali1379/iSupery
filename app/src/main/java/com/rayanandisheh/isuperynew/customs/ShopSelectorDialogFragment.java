package com.rayanandisheh.isuperynew.customs;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.rayanandisheh.isuperynew.Interfaces.DismissDialog;
import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.Retrofit.APIClientShopList;
import com.rayanandisheh.isuperynew.adapters.ShopListAdapter;
import com.rayanandisheh.isuperynew.app.MyAppPrefsManager;
import com.rayanandisheh.isuperynew.models.ShopDetails;
import com.rayanandisheh.isuperynew.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopSelectorDialogFragment extends DialogFragment {

    List<ShopDetails> shopDetails;
    RecyclerView recyclerView;
    ShopListAdapter shopListAdapter;
    SpinKitView progress;
    Button tryAgainBtn;
    ImageView clearBtn;
    EditText searchEt;
    TextView nothingFoundTxt;

    private boolean canSearch = false;


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_selector_dialog, container, false);
        initViews(view);
        initListeners();
        setupRecyclerview();
        RequestShopList();
        return view;
    }

    private void initListeners() {
        tryAgainBtn.setOnClickListener(v -> RequestShopList());
        clearBtn.setOnClickListener(v ->{
            searchEt.setText("");
            nothingFoundTxt.setVisibility(View.INVISIBLE);
        } );

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchEt.removeTextChangedListener(this);
                if (s.toString().length() == 0){
                    clearBtn.setVisibility(View.INVISIBLE);
                    shopListAdapter.setData(shopDetails);
                } else {
                    clearBtn.setVisibility(View.VISIBLE);
                    if (canSearch){
                        searchQuery(s.toString());
                    }
                }
                searchEt.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchQuery(String query) {
        List<ShopDetails> results = new ArrayList<>();

        for (int i = 0; i < shopDetails.size(); i++){
            if (Utilities.convertToPersian(shopDetails.get(i).getStrName().toLowerCase()).contains(Utilities.convertToPersian(query.toLowerCase()))){
                results.add(shopDetails.get(i));
            }
        }

        shopListAdapter.setData(results);
        if (results.size() > 0){
            nothingFoundTxt.setVisibility(View.INVISIBLE);
        } else {
            nothingFoundTxt.setVisibility(View.VISIBLE);
        }
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.shop_selector_recyclerview);
        tryAgainBtn = view.findViewById(R.id.shop_selector_dialog_try_again);
        progress = view.findViewById(R.id.shop_selector_progress_bar);
        searchEt = view.findViewById(R.id.shop_selector_dialog_search_et);
        clearBtn = view.findViewById(R.id.shop_selector_dialog_clear_btn);
        nothingFoundTxt = view.findViewById(R.id.shop_selector_dialog_nothing_found_txt);
    }



    private void RequestShopList() {
        tryAgainBtn.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        Call<List<ShopDetails>> call = APIClientShopList.getInstance().getShopsList();
        call.enqueue(new Callback<List<ShopDetails>>() {
            @Override
            public void onResponse(Call<List<ShopDetails>> call, Response<List<ShopDetails>> response) {
                progress.setVisibility(View.INVISIBLE);
                if(response.isSuccessful()){
                    if (response.body().size() > 0){
                        shopListAdapter.setData(response.body());
                        shopDetails = response.body();
                        canSearch = true;
                        searchEt.setText("");
                    } else {
                        tryAgainBtn.setVisibility(View.VISIBLE);
                        canSearch = false;
                    }
                } else {
                    tryAgainBtn.setVisibility(View.VISIBLE);
                    canSearch = false;
                }
            }

            @Override
            public void onFailure(Call<List<ShopDetails>> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                tryAgainBtn.setVisibility(View.VISIBLE);
                canSearch = false;
            }
        });
    }

    private void setupRecyclerview() {
        shopListAdapter = new ShopListAdapter(this::selectShop);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(shopListAdapter);
    }

    private void selectShop(ShopDetails shopDetails){
        new AlertDialog.Builder(getContext())
                .setMessage(getResources().getString(R.string.shop_selection_message))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(getContext());
                    myAppPrefsManager.setConsumerKey(shopDetails.getStrToken1());
                    myAppPrefsManager.setConsumerSecret(shopDetails.getStrToken2());
                    myAppPrefsManager.setAppName(shopDetails.getStrName());
                    myAppPrefsManager.setAppUrl(shopDetails.getStrURL());
                    ((DismissDialog) getContext()).dismissed();
                    dismiss();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}