package com.rayanandisheh.isuperynew.customs;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.adapters.ProductAdapter;
import com.rayanandisheh.isuperynew.fragments.Product_Description;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;

import java.util.List;

public class OnSaleDialogFragment extends DialogFragment {

    ImageView closeBtn;
    RecyclerView recyclerView;


    Context context;
    List<ProductDetails> products;

    public OnSaleDialogFragment(Context context, List<ProductDetails> products) {
        this.context = context;
        this.products = products;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_sale_dialog, container, false);

        closeBtn = view.findViewById(R.id.on_sale_dialog_close_btn);
        recyclerView = view.findViewById(R.id.on_sale_dialog_recyclerview);

        closeBtn.setOnClickListener(v -> dismiss());
        ProductAdapter productAdapter = new ProductAdapter(context, false, this::gotoProductDetails);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        productAdapter.toggleLayout(false);
        Log.e("onSaleProduct", String.valueOf(products.size()));
        recyclerView.setAdapter(productAdapter);
        productAdapter.setData(products);
        return view;
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

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
}