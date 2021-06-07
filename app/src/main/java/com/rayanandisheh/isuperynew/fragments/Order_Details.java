package com.rayanandisheh.isuperynew.fragments;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.Nullable;
import android.os.Bundle;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rayanandisheh.isuperynew.R;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.adapters.OrderedProductsListAdapter;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.customs.DialogLoader;
import com.rayanandisheh.isuperynew.models.api_response_model.ErrorResponse;
import com.rayanandisheh.isuperynew.models.order_model.OrderDetails;
import com.rayanandisheh.isuperynew.customs.DividerItemDecoration;
import com.rayanandisheh.isuperynew.models.order_model.OrderProducts;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;
import com.rayanandisheh.isuperynew.network.APIClient;
import com.rayanandisheh.isuperynew.utils.Utilities;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import saman.zamani.persiandate.PersianDate;


public class Order_Details extends Fragment {

    View rootView;
    
    int orderID;
    OrderDetails orderDetails;

    Button cancel_order_btn;
    RecyclerView checkout_items_recycler;
    TextView checkout_subtotal, checkout_tax, checkout_discount, checkout_total;
    TextView order_price, order_products_count, order_status, order_date, shipping_method;
    TextView orderNote, orderTowerNum, orderTowerLevel, orderRoomNum, orderId, orderTime;
    SpinKitView productsProgress;
    RelativeLayout orderDescLayout;
    List<ProductDetails> productsList;
    List<OrderProducts> orderProductsList;

    DialogLoader dialogLoader;
    OrderedProductsListAdapter orderedProductsAdapter;


    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.ORDER_DETAIL_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.ORDER_DETAIL_PAGE;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.order_details, container, false);
        
        // Set the Title of Toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.order_details));
        

        // Binding Layout Views
        order_price = rootView.findViewById(R.id.order_price);
        order_products_count = rootView.findViewById(R.id.order_products_count);
        shipping_method = rootView.findViewById(R.id.shipping_method);
        order_status = rootView.findViewById(R.id.order_status);
        order_date = rootView.findViewById(R.id.order_date);
        checkout_subtotal = rootView.findViewById(R.id.checkout_subtotal);
        checkout_tax = rootView.findViewById(R.id.checkout_tax);
        checkout_discount = rootView.findViewById(R.id.checkout_discount);
        checkout_total = rootView.findViewById(R.id.checkout_total);
        cancel_order_btn = rootView.findViewById(R.id.cancel_order_btn);
        checkout_items_recycler = rootView.findViewById(R.id.checkout_items_recycler);
        orderNote = rootView.findViewById(R.id.order_detail_note);
        orderTowerLevel = rootView.findViewById(R.id.order_detail_tower_level);
        orderTowerNum = rootView.findViewById(R.id.order_detail_tower_num);
        orderRoomNum = rootView.findViewById(R.id.order_detail_room_num);
        productsProgress = rootView.findViewById(R.id.order_detail_products_progress);
        orderId = rootView.findViewById(R.id.order_products_id);
        orderTime = rootView.findViewById(R.id.order_details_time);
        orderDescLayout = rootView.findViewById(R.id.order_details_desc_layout);
//        cancel_order_btn.setVisibility(View.GONE);
        checkout_items_recycler.setNestedScrollingEnabled(false);

    
        dialogLoader = new DialogLoader(getContext());
    
        // Get product Info from bundle arguments
        if (getArguments() != null) {
            if (getArguments().containsKey("orderDetails")) {
                
                orderDetails = getArguments().getParcelable("orderDetails");
                // Set Product Details
                setOrderDetails(orderDetails);
                
            }
            else if (getArguments().containsKey("orderID")) {
                
                orderID = getArguments().getInt("orderID");
                // Get Product Details
                RequestOrderDetail(orderID);
    
            }
        }


        return rootView;

    }

    private void setOrderDetails(final OrderDetails orderDetails) {
        Log.e("productDescription", String.valueOf(orderDetails.getId()));

        productsList = new ArrayList<>();
        
        orderProductsList = orderDetails.getOrderProducts();
        
        if (orderDetails.getStatus().equalsIgnoreCase("on-hold")
                ||  orderDetails.getStatus().equalsIgnoreCase("completed")
                ||  orderDetails.getStatus().equalsIgnoreCase("cancelled")
                ||  orderDetails.getStatus().equalsIgnoreCase("failed")
                ||  orderDetails.getStatus().equalsIgnoreCase("refunded")) {
            cancel_order_btn.setVisibility(View.GONE);
        } else {
            cancel_order_btn.setVisibility(View.VISIBLE);
        }
        
    
        double subTotal = 0;
        int noOfProducts = 0;
        for (int i=0;  i<orderProductsList.size();  i++) {
            subTotal += Double.parseDouble(orderProductsList.get(i).getTotal());
            noOfProducts += orderProductsList.get(i).getQuantity();
        }
    
        orderId.setText(Utilities.convertToPersian(String.valueOf(orderDetails.getId())));
        order_products_count.setText(Utilities.convertToPersian(String.valueOf(noOfProducts)));
        order_date.setText(translateDate(orderDetails.getDateCreated().replaceAll("[a-zA-Z]", " ")));

        String translatedStatus = "";

        if (orderDetails.getStatus().equalsIgnoreCase("processing")) {
            translatedStatus = "در حال انجام";
            order_status.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else if (orderDetails.getStatus().equalsIgnoreCase("on-hold")) {
            translatedStatus = "در انتظار بررسی";
            order_status.setTextColor(ContextCompat.getColor(getContext(), R.color.progressYellow));
        } else if (orderDetails.getStatus().equalsIgnoreCase("completed")) {
            translatedStatus = "تکمیل شده";
            order_status.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryLight));
        } else if (orderDetails.getStatus().equalsIgnoreCase("cancelled")) {
            translatedStatus = "ناموفق";
            order_status.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccentRed));
        } else if (orderDetails.getStatus().equalsIgnoreCase("refunded")) {
            translatedStatus = "لغو شده";
            order_status.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryLight));
        } else if (orderDetails.getStatus().equalsIgnoreCase("failed")) {
            translatedStatus = "مسترد شده";
            order_status.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccentRed));
        } else {// pending
            translatedStatus = "در انتظار پرداخت";
            order_status.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }

        order_status.setText(translatedStatus);
        orderTime.setText(translateTime(orderDetails.getDateCreated().replaceAll("[a-zA-Z]", " ")));

        if (orderDetails.getOrderShippingMethods().size() > 0)
            shipping_method.setText(orderDetails.getOrderShippingMethods().get(0).getMethodTitle());
    
        checkout_subtotal.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(subTotal)) + " " + ConstantValues.CURRENCY_SYMBOL);
        checkout_tax.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format((int) Double.parseDouble(orderDetails.getTotalTax()))) + " " + ConstantValues.CURRENCY_SYMBOL);
        checkout_discount.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format((int) Double.parseDouble(orderDetails.getDiscountTotal()))) + " " + ConstantValues.CURRENCY_SYMBOL);
        checkout_total.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format((int) Double.parseDouble(orderDetails.getTotal()))) + " " + ConstantValues.CURRENCY_SYMBOL);
        order_price.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format((int) Double.parseDouble(orderDetails.getTotal()))) + " " + ConstantValues.CURRENCY_SYMBOL);
        orderTowerNum.setText(Utilities.convertToPersian(orderDetails.getBilling().getCompany()));
        orderTowerLevel.setText(Utilities.convertToPersian(orderDetails.getBilling().getAddress2()));
        orderRoomNum.setText(Utilities.convertToPersian(orderDetails.getBilling().getAddress1()));

        if (orderDetails.getCustomerNote() != null){
            if (orderDetails.getCustomerNote().trim().length() > 0){
                orderNote.setText(Utilities.convertToPersian(orderDetails.getCustomerNote()));
            } else {
                orderDescLayout.setVisibility(View.GONE);
            }
        } else {
            orderDescLayout.setVisibility(View.GONE);
        }
        GetOrderedProducts getOrderedProducts = new GetOrderedProducts(getContext(), orderProductsList);
        getOrderedProducts.execute();
        
    
        orderedProductsAdapter = new OrderedProductsListAdapter(getContext(), productsList);
    
        checkout_items_recycler.setAdapter(orderedProductsAdapter);
        checkout_items_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        checkout_items_recycler.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
    
    
    
        cancel_order_btn.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle(R.string.attention)
                .setMessage("آیا از لغو سفارش خود اطمینان دارید؟")
                .setPositiveButton(R.string.yes, (dialog, which) -> RequestCancelOrder(orderDetails.getId()))
                .setNegativeButton(R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());
        
    }

    private String translateTime(String dateAndTime) {
        String [] separated = dateAndTime.split(" ");
        String [] time = separated[1].split(":");

        return time[0] + ":" + time[1];
    }

    private String translateDate(String dateAndTime) {
        String [] separated =  dateAndTime.split(" ");
        String [] date = separated[0].split("-");

        PersianDate persianDate = new PersianDate();
        int [] jalaliDate = persianDate.toJalali(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]));

        String concatinatedDate = jalaliDate[2] + "/" + jalaliDate[1] + "/" + jalaliDate[0];


        return Utilities.convertToPersian(concatinatedDate);
    }
    
    //*********** Request Product Details from the Server based on productID ********//
    
    public void addOrderProduct(ProductDetails productDetails) {
        
        for (int i=0;  i<orderProductsList.size();  i++) {
            if (orderProductsList.get(i).getProductId() == productDetails.getId()) {
                productDetails.setPrice(orderProductsList.get(i).getPrice());
                productDetails.setProductsFinalPrice(orderProductsList.get(i).getSubtotal());
                productDetails.setTotalPrice(orderProductsList.get(i).getTotal());
                productDetails.setCustomersBasketQuantity(orderProductsList.get(i).getQuantity());
    
                if (productDetails.getImages() != null  &&  !TextUtils.isEmpty(productDetails.getImages().get(0).getSrc())) {
                    productDetails.setImage(productDetails.getImages().get(0).getSrc());
                }
                else {
                    productDetails.setImage("");
                }
    
                productsList.add(productDetails);
            }
        }
    }
    
    
    
    //*********** Request Order Details from the Server based on orderID ********//
    
    public void RequestOrderDetail(final int orderID) {
        
        dialogLoader.showProgressDialog();
        
        Call<OrderDetails> call = APIClient.getInstance()
                .getSingleOrder
                        (
                                String.valueOf(orderID)
                        );
        
        call.enqueue(new Callback<OrderDetails>() {
            @Override
            public void onResponse(Call<OrderDetails> call, retrofit2.Response<OrderDetails> response) {
                
                dialogLoader.hideProgressDialog();
                
                
                if (response.isSuccessful()) {
                    
                    setOrderDetails(response.body());
                    
                }
                else {
                    Converter<ResponseBody, ErrorResponse> converter = APIClient.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                    ErrorResponse error;
                    try {
                        error = converter.convert(response.errorBody());
                    } catch (IOException e) {
                        error = new ErrorResponse();
                    }
                    
                    Toast.makeText(getContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<OrderDetails> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
        
    }
    
    
    
    //*********** Request to cancel the Order from the Server based on orderID ********//
    
    public void RequestCancelOrder(final int orderID) {
        
        dialogLoader.showProgressDialog();
        
        Call<OrderDetails> call = APIClient.getInstance()
                .updateOrder
                        (
                                String.valueOf(orderID),
                                "cancelled"
                        );
        
        call.enqueue(new Callback<OrderDetails>() {
            @Override
            public void onResponse(Call<OrderDetails> call, retrofit2.Response<OrderDetails> response) {
                
                dialogLoader.hideProgressDialog();
                
                
                if (response.isSuccessful()) {
    
                    if (response.body().getStatus() != null) {
                        order_status.setText(response.body().getStatus());
                        cancel_order_btn.setVisibility(View.GONE);
    
                        Snackbar.make(rootView, getString(R.string.order_cancelled), Snackbar.LENGTH_SHORT).show();
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
                    
                    Toast.makeText(getContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<OrderDetails> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
        
    }
    
    
    
    /*********** LoadMoreTask Used to Load more Products from the Server in the Background Thread using AsyncTask ********/
    
    private class GetOrderedProducts extends AsyncTask<String, Void, String> {
        
        List<OrderProducts> orderProducts;
        
        
        private GetOrderedProducts(Context context, List<OrderProducts> orderProducts) {
            this.orderProducts = orderProducts;
        }
        
        
        //*********** Runs on the UI thread before #doInBackground() ********//
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        
        
        //*********** Performs some Processes on Background Thread and Returns a specified Result  ********//
        
        @Override
        protected String doInBackground(String... params) {
            
            for (int i=0;  i<orderProducts.size();  i++) {
                
                Call<ProductDetails> call = APIClient.getInstance()
                    .getSingleProduct
                        (
                            String.valueOf(orderProducts.get(i).getProductId())
                        );
    
                try {
        
                    Response<ProductDetails> response = call.execute();
        
                    if (response.isSuccessful()) {
    
                        addOrderProduct(response.body());
            
                    }
        
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
            
            return "All Done!";
        }
        
        
        //*********** Runs on the UI thread after #doInBackground() ********//
        
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            productsProgress.setVisibility(View.GONE);
            orderedProductsAdapter.notifyDataSetChanged();
        }
    }


}