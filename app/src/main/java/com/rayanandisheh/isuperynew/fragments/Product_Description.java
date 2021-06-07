package com.rayanandisheh.isuperynew.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.rayanandisheh.isuperynew.Interfaces.ModifyBasketFab;
import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.adapters.AttributeListAdapter;
import com.rayanandisheh.isuperynew.adapters.ProductAdapter;
import com.rayanandisheh.isuperynew.customs.AddCommentDialogFragment;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.customs.CommentsDialog;
import com.rayanandisheh.isuperynew.databases.User_Favorites_DB;
import com.rayanandisheh.isuperynew.models.api_response_model.ErrorResponse;
import com.rayanandisheh.isuperynew.models.cart_model.CartDetails;
import com.rayanandisheh.isuperynew.models.product_model.ProductAttributes;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;
import com.rayanandisheh.isuperynew.models.product_model.ProductImages;
import com.rayanandisheh.isuperynew.models.product_model.ProductReviews;
import com.rayanandisheh.isuperynew.network.APIClient;
import com.rayanandisheh.isuperynew.utils.TextJustification.JustifiedTextView;
import com.rayanandisheh.isuperynew.utils.Utilities;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import hyogeun.github.com.colorratingbarlib.ColorRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;


public class Product_Description extends Fragment implements BaseSliderView.OnSliderClickListener {
    
    View rootView;
    
    int productID;
    int selectedVariationID = 0;
    double productBasePrice = 0;
    double productFinalPrice = 0;
    
    String customerID;

    Button productCartBtn, qualityVarTryAgainBtn;
    ImageView sliderImageView;
    SliderLayout sliderLayout;
    PagerIndicator pagerIndicator;
    ToggleButton product_like_btn;
    ColorRatingBar product_rating_bar;
    ImageButton product_share_btn, product_quantity_plusBtn, product_quantity_minusBtn;
    RecyclerView grouped_products_recycler, product_metadata_recycler, related_products_recycler, attributes_recyclerview;
    TextView title, category, product_ratings_count, product_stock, product_total_price, product_item_quantity, product_suffix, not_found, product_price_txt;
    JustifiedTextView product_description_txt;
    LinearLayout product_reviews_ratings, simple_product, grouped_products, product_metadata, related_products, product_description, qualityVarLayout;
    CardView product_attributes, qualityVarCardView, productDescLayout;
    FrameLayout loadingLayout;
    Spinner qualityVarSpinner;
    SpinKitView loadingBar, qualityVarProgressBar, relatedProductsProgress;
    public ProductDetails productDetails;
    ProductAdapter relatedProductsAdapter;
    AttributeListAdapter attributesAdapter;
    NestedScrollView nestedScrollView;
    private User_Favorites_DB favorites_db;
    
    List<Integer> productIDsList;
    List<ProductImages> itemImages;
    List<ProductDetails> relatedProductsList;
    List<ProductDetails> allProductVariationsList;
    
    public List<ProductReviews> productReviews;
    public List<ProductAttributes> productAttributesList;

    Call<List<ProductDetails>> callVars;
    Call<ProductDetails> callDetails, callRelated;

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.PRODUCT_DESC_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.PRODUCT_DESC_PAGE;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (callVars != null) {
            callVars.cancel();
        }

        if (callDetails != null) {
            callDetails.cancel();
        }

        if (callRelated != null) {
            callRelated.cancel();
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.product_description, container, false);
    
        setHasOptionsMenu(true);
        
        // Set the Title of Toolbar
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.product_description));
        
        // Get the CustomerID from SharedPreferences
        customerID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");
        
        favorites_db = new User_Favorites_DB();

        // Binding Layout Views
        initViews(rootView);

//        grouped_products_recycler.setNestedScrollingEnabled(false);
//        product_metadata_recycler.setNestedScrollingEnabled(false);
//        related_products_recycler.setNestedScrollingEnabled(false);
        
        category.setVisibility(View.GONE);
        related_products.setVisibility(View.GONE);
        product_metadata.setVisibility(View.GONE);
        grouped_products.setVisibility(View.GONE);
        //product_attributes.setVisibility(View.GONE);

        productIDsList = new ArrayList<>();
        productReviews = new ArrayList<>();
        relatedProductsList = new ArrayList<>();
        allProductVariationsList = new ArrayList<>();
        
        // Get product Info from bundle arguments
        if (getArguments() != null) {
            
            if (getArguments().containsKey("itemID")) {
                productID = getArguments().getInt("itemID");
                RequestProductDetail(productID);
                Log.e("product", "request");
            }
            else if (getArguments().containsKey("productDetails")) {
                productDetails = getArguments().getParcelable("productDetails");
                Log.e("product", "args");

                // Set Product Details
                setProductDetails(productDetails);
                RequestProductVariations(productDetails.getId());
            } else {
                loadingBar.setVisibility(View.INVISIBLE);
                not_found.setVisibility(View.VISIBLE);
            }
        } else {
            loadingBar.setVisibility(View.INVISIBLE);
            not_found.setVisibility(View.VISIBLE);
        }

        qualityVarTryAgainBtn.setOnClickListener(v -> {
            RequestProductVariations(productDetails.getId());
            qualityVarTryAgainBtn.setVisibility(View.INVISIBLE);
            qualityVarProgressBar.setVisibility(View.VISIBLE);
        });

        return rootView;
    }

    private void initViews(View rootView) {
        productCartBtn = rootView.findViewById(R.id.product_cart_btn);
        title = rootView.findViewById(R.id.product_title);
        category = rootView.findViewById(R.id.product_category);
        product_stock = rootView.findViewById(R.id.product_stock);
        product_total_price = rootView.findViewById(R.id.product_total_price);
        product_item_quantity = rootView.findViewById(R.id.product_item_quantity);
        product_ratings_count = rootView.findViewById(R.id.product_ratings_count);
        product_like_btn = rootView.findViewById(R.id.product_like_btn);
        product_share_btn = rootView.findViewById(R.id.product_share_btn);
        product_quantity_plusBtn = rootView.findViewById(R.id.product_item_quantity_plusBtn);
        product_quantity_minusBtn = rootView.findViewById(R.id.product_item_quantity_minusBtn);
        related_products = rootView.findViewById(R.id.related_products);
        simple_product = rootView.findViewById(R.id.simple_product);
        grouped_products = rootView.findViewById(R.id.grouped_products);
        product_metadata = rootView.findViewById(R.id.product_metadata);
        product_attributes = rootView.findViewById(R.id.product_attributes);
        product_description = rootView.findViewById(R.id.product_description);
        product_reviews_ratings = rootView.findViewById(R.id.product_reviews_ratings);
        product_rating_bar = rootView.findViewById(R.id.product_rating_bar);
        sliderLayout =  rootView.findViewById(R.id.product_cover_slider);
        pagerIndicator = rootView.findViewById(R.id.product_slider_indicator);
        product_price_txt = rootView.findViewById(R.id.product_price_webView);
        product_description_txt = rootView.findViewById(R.id.product_description_webView);
        attributes_recyclerview = rootView.findViewById(R.id.attributes_list_view);
        related_products_recycler =  rootView.findViewById(R.id.related_products_recycler);
        grouped_products_recycler =  rootView.findViewById(R.id.grouped_products_recycler);
        product_metadata_recycler =  rootView.findViewById(R.id.product_metadata_recycler);
        qualityVarCardView = rootView.findViewById(R.id.product_description_quality_variation_cv);
        qualityVarLayout = rootView.findViewById(R.id.product_description_quality_variation_layout);
        qualityVarProgressBar = rootView.findViewById(R.id.product_description_quality_variation_progress);
        qualityVarSpinner = rootView.findViewById(R.id.product_description_quality_variation_spinner);
        qualityVarTryAgainBtn = rootView.findViewById(R.id.product_description_quality_variation_try_again_btn);
        nestedScrollView = rootView.findViewById(R.id.product_description_scrollview);
        loadingLayout = rootView.findViewById(R.id.product_description_main_progress);
        product_suffix = rootView.findViewById(R.id.product_description_suffix_txt);
        not_found = rootView.findViewById(R.id.product_description_not_found_txt);
        loadingBar = rootView.findViewById(R.id.product_description_progress_bar);
        productDescLayout = rootView.findViewById(R.id.product_description_desc_layout);
        relatedProductsProgress = rootView.findViewById(R.id.product_description_related_progress_bar);
    }

    private void setProductDetails(ProductDetails product) {
        getProductReviews(String.valueOf(product.getId()));

        if (product.getType().equalsIgnoreCase("variable")) {
            if (product.getVariations().size() > 0) {
    
                simple_product.setVisibility(View.VISIBLE);
                grouped_products.setVisibility(View.GONE);

                productAttributesList = product.getAttributes();
                productAttributesList.remove(0);

                if (productAttributesList != null){
                    if (productAttributesList.size() > 0){
                        attributesAdapter = new AttributeListAdapter(getContext(), productAttributesList);
                        attributes_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                        attributes_recyclerview.setAdapter(attributesAdapter);
                    } else {
                        product_attributes.setVisibility(View.GONE);
                    }
                } else {
                    product_attributes.setVisibility(View.GONE);
                }
                
                productIDsList.add(productDetails.getId());

                if (!product.isInStock() && (!product.getPrice().isEmpty() || !product.getRegularPrice().isEmpty())) {
                    simple_product.setVisibility(View.GONE);
                    product_stock.setText(getString(R.string.outOfStock));
                    product_stock.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccentRed));
                    productCartBtn.setText(getString(R.string.outOfStock));
                    productCartBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.top_round_red_bg));
                } else if (product.getPrice().isEmpty() && product.getRegularPrice().isEmpty()) {
                    simple_product.setVisibility(View.GONE);
                    productCartBtn.setVisibility(View.GONE);
                    product_stock.setText("");
                } else {
                    Log.e("pass2", "ok");
                    simple_product.setVisibility(View.VISIBLE);
                    product_stock.setText(getString(R.string.in_stock));
                    product_stock.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryLight));
                    productCartBtn.setText(getString(R.string.addToCart));
                    productCartBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.top_corners_rounded_primary_bg));
                }


//                if (productDetails.getAttributes().size() > 0) {
//                    toggleCartButton(false);
//                }


            }
        
        } else {
            simple_product.setVisibility(View.VISIBLE);
            grouped_products.setVisibility(View.GONE);

            productAttributesList = product.getAttributes();

            if (productAttributesList != null){
                if (productAttributesList.size() > 0){
                    productAttributesList.remove(0);
                    attributesAdapter = new AttributeListAdapter(getContext(), productAttributesList);
                    attributes_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                    attributes_recyclerview.setAdapter(attributesAdapter);
                } else {
                    product_attributes.setVisibility(View.GONE);
                }
            } else {
                product_attributes.setVisibility(View.GONE);
            }


            // Check if the Product is Out of Stock
            if (!product.isInStock() ) {
                simple_product.setVisibility(View.GONE);
                product_stock.setText(getString(R.string.outOfStock));
                product_stock.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccentRed));
                productCartBtn.setText(getString(R.string.outOfStock));
                productCartBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.top_round_red_bg));
            } else if(product.getPrice().isEmpty() && product.getRegularPrice().isEmpty()){
                simple_product.setVisibility(View.GONE);
                productCartBtn.setVisibility(View.GONE);
                product_stock.setText("");
            } else {
                Log.e("pass", "ok");
                simple_product.setVisibility(View.VISIBLE);
                product_stock.setText(getString(R.string.in_stock));
                product_stock.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryLight));
                productCartBtn.setText(getString(R.string.addToCart));
                productCartBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.top_corners_rounded_primary_bg));
            }
            
        }
        
//        List<ProductMetaData> metaData = product.getMetaData();
//
//        Log.e("metadata", metaData.size());
        
        // Get Product Images and Attributes
        itemImages = new ArrayList<>();
        itemImages.addAll(product.getImages());
        product.setImage(itemImages.get(0).getSrc());
        
        // Setup the ImageSlider of Product Images
        ImageSlider(product.getImage(), itemImages);
        
        
        // Set Product's Information
        title.setText(product.getName());
    
    
        String[] categoryIDs = new String[product.getCategories().size()];
        String[] categoryNames = new String[product.getCategories().size()];
        if (product.getCategories().size() > 0) {
        
            for (int i=0;  i<product.getCategories().size();  i++) {
                categoryIDs[i] = String.valueOf(product.getCategories().get(i).getId());
                categoryNames[i] = product.getCategories().get(i).getName();
            }

            product.setCategoryIDs(TextUtils.join(",", categoryIDs));
            product.setCategoryNames(TextUtils.join(",", categoryNames));
        }
        else {
            product.setCategoryIDs("");
            product.setCategoryNames("");
        }
    
    
    
        product_ratings_count.setText(String.valueOf(product.getRatingCount()));
    
        if (product.getAverageRating() != null  &&  !TextUtils.isEmpty(product.getAverageRating())) {
            product_rating_bar.setRating(Float.parseFloat(product.getAverageRating()));
        }
    
        if (product.getRelatedIds().size() > 0) {
            related_products.setVisibility(View.VISIBLE);
            
            // Initialize the ProductAdapter and LayoutManager for Related Products RecyclerView
            relatedProductsAdapter = new ProductAdapter(getContext(), true, this::gotoProductDetails);
            relatedProductsAdapter.toggleLayout(true);
            related_products_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            related_products_recycler.setAdapter(relatedProductsAdapter);
            
            for (int i=0;  i < productDetails.getRelatedIds().size();  i++) {
                RequestRelatedProducts(product.getRelatedIds().get(i));
            }

            relatedProductsProgress.setVisibility(View.GONE);

//            if (relatedProductsList.size() == 0){
//                related_products.setVisibility(View.GONE);
//            }
        } else {
            related_products.setVisibility(View.GONE);
        }


        product.setCustomersBasketQuantity(1);
        
        if (!"".equalsIgnoreCase(product.getPrice()))
            productBasePrice = Double.parseDouble(product.getPrice());
            
        updateProductPrices();


        if (product.isOnSale()){
            if (!product.getPrice().isEmpty() && !product.getRegularPrice().isEmpty() && product.isPurchasable() && product.isInStock()){
                product_price_txt.setText(strikeThrough(
                        Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int)Double.parseDouble(product.getRegularPrice())))) + " " +
                                Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int)Double.parseDouble(product.getSalePrice())))) + " " + ConstantValues.CURRENCY_SYMBOL,
                        Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int)Double.parseDouble(product.getRegularPrice())))),
                        Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int)Double.parseDouble(product.getSalePrice()))))) , TextView.BufferType.SPANNABLE);
            }
        } else {
            if (product.getPrice() != null && product.isPurchasable() && product.isInStock() && !product.getPrice().isEmpty()){
                product_price_txt.setText(increaseSize(
                        Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int) Double.parseDouble(product.getPrice())))) + " " + ConstantValues.CURRENCY_SYMBOL,
                        Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int) Double.parseDouble(product.getPrice()))))) , TextView.BufferType.SPANNABLE);
            }
        }
        
        
        
        if (product.getDescription() == null  &&  TextUtils.isEmpty(product.getDescription())) {
            product_description.setVisibility(View.GONE);
        }
        else {
            product_description.setVisibility(View.VISIBLE);
            
            String description = product.getDescription();

            if (description.length() > 0 ){

                product_description_txt.setText(Utilities.removeHtmlTags(description), true);
                productDescLayout.setVisibility(View.VISIBLE);
            } else {
                productDescLayout.setVisibility(View.GONE);
            }
        }
        
    
        // Holds Product Quantity
        final int[] number = {1};
        number[0] = product.getCustomersBasketQuantity();
        product_item_quantity.setText(String.valueOf(product.getCustomersBasketQuantity()));
    
    
        // Decrease Product Quantity
        product_quantity_minusBtn.setOnClickListener(view -> {
            // Check if the Quantity is greater than the minimum Quantity
            if (number[0] > 1)
            {
                // Decrease Quantity by 1
                number[0] = number[0] - 1;

                product_item_quantity.setText(""+ number[0]);
                product.setCustomersBasketQuantity(number[0]);

                updateProductPrices();
            }
        });
    
    
        // Increase Product Quantity
        product_quantity_plusBtn.setOnClickListener(view -> {
            // Check if the Quantity is less than the maximum or stock Quantity
            if (product.getStockQuantity() == null ||  number[0] < Long.parseLong(product.getStockQuantity())) {
                // Increase Quantity by 1
                number[0] = number[0] + 1;

                product_item_quantity.setText(""+ number[0]);
                product.setCustomersBasketQuantity(number[0]);

                updateProductPrices();
            }
        });
        
        
        // Handle Click event of product_share_btn Button
        product_share_btn.setOnClickListener(view -> {
            // Share Product with the help of static method of Helper class
            Utilities.shareProduct
                    (
                            getContext(),
                            product.getName(),
                            sliderImageView,
                            product.getPermalink()
                    );
        });
        
        
        if (favorites_db.getUserFavorites().contains(productDetails.getId())) {
            product.setIsLiked("1");
            product_like_btn.setChecked(true);
        }
        else {
            product.setIsLiked("0");
            product_like_btn.setChecked(false);
        }
        
        
        // Handle Click event of product_like_btn Button
        product_like_btn.setOnClickListener(view -> {

            // Check if the User has Checked the Like Button
            if(product_like_btn.isChecked()) {
                product.setIsLiked("1");
                product_like_btn.setChecked(true);

                // Add the Product to User's Favorites
                if (!favorites_db.getUserFavorites().contains(product.getId())) {
                    favorites_db.insertFavoriteItem(product.getId());
                }

                Snackbar.make(view, getContext().getString(R.string.added_to_favourites), Snackbar.LENGTH_SHORT).show();

            } else {
                productDetails.setIsLiked("0");
                product_like_btn.setChecked(false);

                // Remove the Product from User's Favorites
                if (favorites_db.getUserFavorites().contains(product.getId())) {
                    favorites_db.deleteFavoriteItem(product.getId());
                }

                Snackbar.make(view, getContext().getString(R.string.removed_from_favourites), Snackbar.LENGTH_SHORT).show();
            }
        });
    
    
        // Handle Click event of product_reviews_ratings Button
        product_reviews_ratings.setOnClickListener(v -> showRatingsAndReviewsOfProduct());
        
        
        // Handle Click event of productCartBtn Button
        productCartBtn.setOnClickListener(view -> {

            if (product.getType().equalsIgnoreCase("simple")) {
                if (product.isInStock()) {

                    updateProductPrices();

                    CartDetails cartDetails = new CartDetails();

                    cartDetails.setCartProduct(product);
//                        cartDetails.setCustomersBasketProductAttributes(selectedAttributesList);


                    // Add the Product to User's Cart with the help of static method of My_Cart class
                    My_Cart.AddCartItem(cartDetails);
                    ((ModifyBasketFab) getContext()).syncBasket();

                    // Recreate the OptionsMenu
                    getActivity().invalidateOptionsMenu();

                    Snackbar.make(view, getContext().getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
                }

            } else if (product.getType().equalsIgnoreCase("variable")) {
                Log.e("productsize", String.valueOf(product.getId()));
                Log.e("productVarSize", String.valueOf(allProductVariationsList.size()));
                if (allProductVariationsList.size() > 0
                        &&  selectedVariationID != 0) {
                    updateProductPrices();

                    CartDetails cartDetails = new CartDetails();
//                        selectedAttributesList = new LinkedList<>();

                    cartDetails.setCartProduct(product);
                    cartDetails.getCartProduct().setName(title.getText().toString());
                    cartDetails.getCartProduct().setSelectedVariationID(selectedVariationID);
//                        cartDetails.setCustomersBasketProductAttributes(selectedAttributesList);


                    // Add the Product to User's Cart with the help of static method of My_Cart class
                    My_Cart.AddCartItem(cartDetails);
                    ((ModifyBasketFab) getContext()).syncBasket();

                    // Recreate the OptionsMenu
                    ((MainActivity) getContext()).invalidateOptionsMenu();

                    Snackbar.make(view, getContext().getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();

                } else {
                    updateProductPrices();

                    CartDetails cartDetails = new CartDetails();

                    cartDetails.setCartProduct(product);


                    // Add the Product to User's Cart with the help of static method of My_Cart class
                    My_Cart.AddCartItem(cartDetails);
                    ((ModifyBasketFab) getContext()).syncBasket();


                    // Recreate the OptionsMenu
                    ((MainActivity) getContext()).invalidateOptionsMenu();

                    Snackbar.make(view, getContext().getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();

                }
            } else {
                updateProductPrices();

                CartDetails cartDetails = new CartDetails();

                cartDetails.setCartProduct(product);
//                        cartDetails.setCustomersBasketProductAttributes(selectedAttributesList);


                // Add the Product to User's Cart with the help of static method of My_Cart class
                My_Cart.AddCartItem(cartDetails);
                ((ModifyBasketFab) getContext()).syncBasket();

                // Recreate the OptionsMenu
                ((MainActivity) getContext()).invalidateOptionsMenu();

                Snackbar.make(view, getContext().getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
            }

        });
        nestedScrollView.scrollTo(0, nestedScrollView.getTop());
        loadingLayout.setVisibility(View.GONE);
    }


    private Spannable strikeThrough(String content, String target, String sizeTarget){
        Spannable spannable = new SpannableString(content);
        int indexOfTarget = content.indexOf(target);
        int indexOfSizeTarget = content.indexOf(sizeTarget);
        spannable.setSpan(new StrikethroughSpan(), indexOfTarget, indexOfTarget + target.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(1.5f), indexOfSizeTarget,indexOfSizeTarget + sizeTarget.length(), 0);
        return spannable;
    }

    private Spannable increaseSize(String content, String sizeTarget){
        Spannable spannable = new SpannableString(content);
        int indexOfSizeTarget = content.indexOf(sizeTarget);
        spannable.setSpan(new RelativeSizeSpan(1.5f), indexOfSizeTarget,indexOfSizeTarget + sizeTarget.length(), 0);
        return spannable;
    }

    private void gotoProductDetails(ProductDetails product) {
        // Get Product Info
        Bundle itemInfo = new Bundle();
        itemInfo.putParcelable("productDetails", product);

        Fragment fragment = new Product_Description();

        fragment.setArguments(itemInfo);
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        FragmentManager fragmentManager = ((MainActivity) getActivity()).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null).commit();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    
    
    //*********** Update Product's final Price based on selected Attributes ********//
    
    public void toggleCartButton(boolean isEnabled) {
    
        productCartBtn.setEnabled(isEnabled);
        
        if (isEnabled) {
            productCartBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.top_corners_rounded_primary_bg));
        }
        else {
            productCartBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.top_rounded_gray_color_bg));
        }
    }
    
    
    
    //*********** Update Product's final Price based on selected Attributes ********//
    
    @SuppressLint("SetTextI18n")
    public void updateProductPrices() {
        
        productFinalPrice = productBasePrice * productDetails.getCustomersBasketQuantity();
    
        // Set Final Price and Quantity
        productDetails.setPrice(String.valueOf(productBasePrice));
        productDetails.setTotalPrice(String.valueOf(productFinalPrice));
        productDetails.setProductsFinalPrice(String.valueOf(productFinalPrice));
        
        product_total_price.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format((int) productFinalPrice)) + " " + ConstantValues.CURRENCY_SYMBOL);
        
    }

    //*********** Setup the ImageSlider with the given List of Product Images ********//
    
    public void showRatingsAndReviewsOfProduct() {

        CommentsDialog commentsDialog = new CommentsDialog(getContext(), productDetails, productReviews) {
            @Override
            public void showRateProductDialog() {
                showRateProductDialogFragment();
            }
        };

        commentsDialog.show();
    }
    
    
    
    public void showRateProductDialogFragment() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        androidx.fragment.app.Fragment prev = getChildFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        AddCommentDialogFragment dialog = new AddCommentDialogFragment(productDetails.getId(), getContext());
        dialog.show(ft, "dialog");
    }

    
    
    
    //*********** Setup the ImageSlider with the given List of Product Images ********//
    
    private void ImageSlider(String itemThumbnail, List<ProductImages> itemImages) {
    
        sliderLayout.removeAllSliders();
        
        // Initialize new HashMap<ImageName, ImagePath>
        final LinkedHashMap<String, String> slider_covers = new LinkedHashMap<>();
        // Initialize new Array for Image's URL
        final String[] images = new String[itemImages.size()];
        
        
        if (itemImages.size() > 0) {
            for (int i=0;  i< itemImages.size();  i++) {
                // Get Image's URL at given Position from itemImages List
                images[i] = itemImages.get(i).getSrc();
            }
        }
        
        
        // Put Image's Name and URL to the HashMap slider_covers
        if (images.length == 0) {
            if ("".equalsIgnoreCase(itemThumbnail)) {
                slider_covers.put("a", ""+R.drawable.placeholder);
            }
            else {
                slider_covers.put("a", itemThumbnail);
            }
        }
        else {
            for (int i=0;  i<images.length;  i++) {
                slider_covers.put("b"+i, images[i]);
            }
        }
        
        
        
        for(String name : slider_covers.keySet()) {
            
            // Initialize DefaultSliderView
            DefaultSliderView defaultSliderView = new DefaultSliderView(getContext()) {
                @Override
                public View getView() {
                    View v = LayoutInflater.from(getContext()).inflate(R.layout.render_type_default,null);
                    
                    // Get daimajia_slider_image ImageView of DefaultSliderView
                    sliderImageView = v.findViewById(R.id.daimajia_slider_image);
                    
                    // Set ScaleType of ImageView
                    sliderImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    bindEventAndShow(v, sliderImageView);
                    
                    return v;
                }
            };
            
            // Set Attributes(Name, Placeholder, Image, Type etc) to DefaultSliderView
            defaultSliderView
                    .description(name)
                    .empty(R.drawable.placeholder)
                    .image(slider_covers.get(name))
                    .setScaleType(DefaultSliderView.ScaleType.CenterInside)
                    .setOnSliderClickListener(this);
            
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
            
        }
        else {
            // Set custom PagerIndicator to the SliderLayout
            sliderLayout.setCustomIndicator(pagerIndicator);
            // Make PagerIndicator Visible
            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
        }
    }
    
    
    
    //*********** Handle the Click Listener on BannerImages of Slider ********//
    
    @Override
    public void onSliderClick(BaseSliderView slider) {
        
        int position = sliderLayout.getCurrentPosition();
        String img_url = itemImages.get(position).getSrc();
    
        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        ImageView dialog_image = dialog.findViewById(R.id.dialog_image);
        ImageButton dialog_cancel = dialog.findViewById(R.id.dialog_cancel);
        
        Glide.with(getContext())
                .load(img_url)
                .error(R.drawable.placeholder)
                .into(dialog_image);

        dialog_cancel.setOnClickListener(v -> dialog.dismiss());
    
        dialog.show();
    }
    
    
    
    //*********** Request Product Details from the Server based on productID ********//
    
    public void RequestProductDetail(final int productID) {
    

        callDetails = APIClient.getInstance()
                .getSingleProduct
                        (
                                String.valueOf(productID)
                        );

        callDetails.enqueue(new Callback<ProductDetails>() {
            @Override
            public void onResponse(Call<ProductDetails> call, retrofit2.Response<ProductDetails> response) {
    

                if (response.isSuccessful()) {
                    
                    productDetails = response.body();
    
                    setProductDetails(productDetails);
                    RequestProductVariations(productID);
                } else {
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
            public void onFailure(Call<ProductDetails> call, Throwable t) {

            }
        });
        
    }

    //*********** Request Product Details from the Server based on productID ********//
    
    public void RequestProductVariations(final int productID) {
        
        callVars = APIClient.getInstance().getVariations(productID);

        callVars.enqueue(new Callback<List<ProductDetails>>() {
                @Override
                public void onResponse(Call<List<ProductDetails>> call, Response<List<ProductDetails>> response) {
                    if (response.isSuccessful()) {

                        allProductVariationsList.addAll(response.body());
                        defineVariations();
                    }
                    else {
                        Converter<ResponseBody, ErrorResponse> converter = APIClient.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                        ErrorResponse error;
                        try {
                            error = converter.convert(response.errorBody());
                        } catch (Exception e) {
                            error = new ErrorResponse();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ProductDetails>> call, Throwable t) {
                    qualityVarTryAgainBtn.setVisibility(View.VISIBLE);
                }
            });

        
    }

    private void defineVariations() {
        if (allProductVariationsList.size() > 0){
            Log.e("allProducts", String.valueOf(allProductVariationsList.size()));
            List<ProductDetails> pricedVariations = getPricedVariations();
            if (pricedVariations.size() > 0){
                Log.e("pricedVariations", String.valueOf(pricedVariations.size()));
                String [] spinnerItems = new String[pricedVariations.size()];
                for (int i = 0; i < pricedVariations.size(); i++){
                    spinnerItems [i] = pricedVariations.get(i).getAttributes().get(0).getOption();
                }
                ArrayAdapter<String> qualitySpinnerAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,spinnerItems);
                qualitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                qualityVarSpinner.setAdapter(qualitySpinnerAdapter);
                qualityVarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedVariationID = pricedVariations.get(position).getId();
                        productBasePrice = Double.parseDouble(pricedVariations.get(position).getPrice());
                        updateProductPrices();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                qualityVarSpinner.setSelection(0);
                qualityVarProgressBar.setVisibility(View.GONE);
                qualityVarLayout.setVisibility(View.VISIBLE);
            } else {
                qualityVarCardView.setVisibility(View.GONE);
            }
        } else {
            qualityVarCardView.setVisibility(View.GONE);
        }
    }

    private List<ProductDetails> getPricedVariations() {
        List<ProductDetails> pricedVariations = new ArrayList<>();
        for (int i = 0; i < allProductVariationsList.size(); i++){
            if (allProductVariationsList.get(i).getPrice() != null &&
                    allProductVariationsList.get(i).isInStock() &&
                    allProductVariationsList.get(i).getAttributes().size() > 0){
                if (allProductVariationsList.get(i).getPrice().length() > 0){
                    pricedVariations.add(allProductVariationsList.get(i));
                }
            }
        }
        return pricedVariations;
    }

    //*********** Proceed User Registration Request ********//
    
    private void getProductReviews(final String productID) {

        Call<List<ProductReviews>> call = APIClient.getInstance()
                .getProductReviews
                        (
                                productID
                        );
        
        call.enqueue(new Callback<List<ProductReviews>>() {
            @Override
            public void onResponse(Call<List<ProductReviews>> call, retrofit2.Response<List<ProductReviews>> response) {
                

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    
                    productReviews = new ArrayList<>();
                    
                    if (response.body() !=  null)
                        productReviews.addAll(response.body());
                
                }
            }
            
            @Override
            public void onFailure(Call<List<ProductReviews>> call, Throwable t) {
            }
        });
    }
    
    public void RequestRelatedProducts(int products_id) {
        
        callRelated = APIClient.getInstance()
                .getSingleProduct(String.valueOf(products_id));

        callRelated.enqueue(new Callback<ProductDetails>() {
            @Override
            public void onResponse(Call<ProductDetails> call, retrofit2.Response<ProductDetails> response) {
                
                if (response.isSuccessful()) {
                    
                    if (response.body().getStatus() == null  ||  response.body().getStatus().equalsIgnoreCase("publish"))
                        relatedProductsList.add(response.body());

                    relatedProductsAdapter.setData(relatedProductsList);

                }
            }
            
            @Override
            public void onFailure(Call<ProductDetails> call, Throwable t) {}
        });
    }
}

