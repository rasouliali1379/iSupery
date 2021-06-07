package com.rayanandisheh.isuperynew.fragments;

import android.content.Intent;

import androidx.annotation.Nullable;

import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.rayanandisheh.isuperynew.R;

import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.app.App;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.models.order_model.OrderDetails;
import com.rayanandisheh.isuperynew.models.order_model.PostOrder;
import com.rayanandisheh.isuperynew.oauth.OAuthEncoder;


public class Checkout extends Fragment {
    
    View rootView;
    
    WebView checkout_webView;
    
    PostOrder postOrder;
    OrderDetails orderDetails;
    LinearLayout coverLayout, progressDialog, noConnectionLayout;

    String ORDER_RECEIVED = "order-received";
    String CHECKOUT_URL = ConstantValues.WOOCOMMERCE_URL+"android-mobile-checkout";

    String url;

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.CHECKOUT_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.CHECKOUT_PAGE;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.checkout, container, false);
        setHasOptionsMenu(true);

        // Set the Title of Toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.checkout));

        // Get the User's Info from the AppContext
        orderDetails = ((App) getContext().getApplicationContext()).getOrderDetails();
        

        // Binding Layout Views
        checkout_webView = rootView.findViewById(R.id.checkout_webView);
        postOrder = new PostOrder();
        postOrder.setOrderProducts(orderDetails.getOrderProducts());
        postOrder.setOrderCoupons(orderDetails.getOrderCoupons());

//        if (!ConstantValues.IS_ONE_PAGE_CHECKOUT_ENABLED) {
//            postOrder.setBilling(orderDetails.getBilling());
//            postOrder.setSameAddress(orderDetails.isSameAddress());
//            postOrder.setOrderShippingMethods(orderDetails.getOrderShippingMethods());
//        }

        if (!ConstantValues.IS_GUEST_LOGGED_IN) {
            postOrder.setToken(orderDetails.getToken());
            postOrder.setCustomerId(orderDetails.getCustomerId());
        }
    
        progressDialog = rootView.findViewById(R.id.checkout_progress_layout);
        coverLayout = rootView.findViewById(R.id.checkout_cover_layout);
        noConnectionLayout = rootView.findViewById(R.id.checkout_no_connection_layout);

        Gson gson = new Gson();
    
        // 2. Java object to JSON, and assign to a String
        Log.e("postOrder", postOrder.toString());
        String jsonData = gson.toJson(postOrder);
        Log.e("jsonData", jsonData);
        String encodedData = OAuthEncoder.encode(jsonData);
        Log.e("encodedData", encodedData);
        String url = CHECKOUT_URL+"?order="+encodedData;
    
        
        Log.e("VC_Shop", "customer_ID= "+orderDetails.getCustomerId());
        Log.e("VC_Shop", "customer_Token= "+orderDetails.getToken());
        Log.e("VC_Shop", "order_url= "+CHECKOUT_URL);
        Log.e("VC_Shop", "order_url_params= "+jsonData);
        Log.e("VC_Shop", "url= "+url);
        
//        checkout_webView.clearCache(true);
//        checkout_webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onLoadResource(WebView view, String url) {
//                super.onLoadResource(view, url);
//                Log.i("order", "onPageStarted: url="+url);
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                Log.i("VC_Shop", "onPageStarted: url= "+url);
//
//                if (url.contains(ORDER_RECEIVED)) {
//                    view.stopLoading();
//
//                    Intent notificationIntent = new Intent(getContext(), MainActivity.class);
//                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                    // Order has been placed Successfully
//                    NotificationHelper.showNewNotification(getContext(), notificationIntent, getString(R.string.thank_you), getString(R.string.order_placed));
//
//                    // Clear User's Cart
//                    My_Cart.ClearCart();
//
//                    // Navigate to Thank_You Fragment
//                    Fragment fragment = new Thank_You();
//                    FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.popBackStack(getString(R.string.actionCart), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.main_fragment, fragment)
//                            .commit();
//                }
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                progressDialog.setVisibility(View.GONE);
//                coverLayout.setVisibility(View.GONE);
//                Log.i("VC_Shop", "onPageFinished: url= "+url);
//            }
//
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
//                progressDialog.setVisibility(View.GONE);
//                coverLayout.setVisibility(View.GONE);
//                noConnectionLayout.setVisibility(View.VISIBLE);
//                Log.i("VC_Shop", "onReceivedError: error= "+error);
//            }
//        });
//
//        checkout_webView.setVerticalScrollBarEnabled(false);
//        checkout_webView.setHorizontalScrollBarEnabled(false);
//        checkout_webView.setBackgroundColor(Color.TRANSPARENT);
//        checkout_webView.getSettings().setJavaScriptEnabled(true);
//        checkout_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        this.url = url;
//        checkout_webView.loadUrl(url);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        return rootView;
    }

    public void tryAgain (View view) {
        checkout_webView.loadUrl(url);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Hide Search Icon in the Toolbar
        MenuItem cartItem = menu.findItem(R.id.toolbar_home);
        MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        cartItem.setVisible(false);
        searchItem.setVisible(false);
    }
}



