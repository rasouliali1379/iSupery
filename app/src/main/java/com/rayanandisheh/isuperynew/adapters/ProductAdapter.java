package com.rayanandisheh.isuperynew.adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.rayanandisheh.isuperynew.Interfaces.ModifyBasketFab;
import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.activities.MainActivity;

import com.rayanandisheh.isuperynew.fragments.My_Cart;
import com.rayanandisheh.isuperynew.fragments.Product_Description;
import com.rayanandisheh.isuperynew.databases.User_Favorites_DB;
import com.rayanandisheh.isuperynew.models.cart_model.CartDetails;
import com.rayanandisheh.isuperynew.models.product_model.ProductAttributes;
import com.rayanandisheh.isuperynew.models.product_model.ProductMetaData;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;
import com.rayanandisheh.isuperynew.utils.Utilities;


/**
 * ProductAdapter is the adapter class of RecyclerView holding List of Products in All_Products and other Product relevant Classes
 **/

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context context;
    private Boolean isGridView;
    private Boolean isHorizontal;
    
    private User_Favorites_DB favorites_db;
    private List<ProductDetails> productList;
    private ItemClickListener listener;


    public ProductAdapter(Context context, Boolean isHorizontal, ItemClickListener listener) {
        this.context = context;
        this.isHorizontal = isHorizontal;
        this.listener = listener;
        favorites_db = new User_Favorites_DB();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = null;
        
        // Check which Layout will be Inflated
        if (isHorizontal) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_grid_sm, parent, false);
        }
        else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(isGridView ? R.layout.layout_product_grid_lg : R.layout.layout_product_list_lg, parent, false);
        }
        

        // Return a new holder instance
        return new MyViewHolder(itemView);
    }



    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

            final ProductDetails product = productList.get(position);

            Glide.with(context)
                    .load(product.getImages().get(0).getSrc())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.cover_loader.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.cover_loader.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.product_cover);
    
    
            holder.product_title.setText(product.getName());
            product.setImage(product.getImages().get(0).getSrc());
            
            if (product.isOnSale()) {
                if (!product.getPrice().isEmpty() && !product.getRegularPrice().isEmpty()){
                    holder.product_price_txt.setText(strikeThrough(
                            Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int)Double.parseDouble(product.getRegularPrice())))) + " " +
                                    Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int)Double.parseDouble(product.getSalePrice())))) + " " + ConstantValues.CURRENCY_SYMBOL,
                            Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int)Double.parseDouble(product.getRegularPrice())))),
                            Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int)Double.parseDouble(product.getSalePrice()))))) , TextView.BufferType.SPANNABLE);
                    long regularPrice = Long.parseLong(product.getRegularPrice());
                    long salePrice = Long.parseLong(product.getSalePrice());
                    int percentage = (int) ((regularPrice - salePrice) * 100 / regularPrice);
                    holder.discount_percentage_txt.setText("%" + Utilities.convertToPersian(String.valueOf(percentage)) + "-");
                    holder.discount_percentage_txt.setVisibility(View.VISIBLE);
                    holder.discount_tag_img.setVisibility(View.VISIBLE);
                } else {
                    holder.product_price_txt.setText("");
                    holder.discount_percentage_txt.setVisibility(View.INVISIBLE);
                    holder.discount_tag_img.setVisibility(View.INVISIBLE);
                }
            } else {
                if (!product.getPrice().isEmpty()){
                    holder.product_price_txt.setText(increaseSize(
                            Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int)Double.parseDouble(product.getPrice())))) + " " + ConstantValues.CURRENCY_SYMBOL,
                            Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int)Double.parseDouble(product.getPrice()))))) , TextView.BufferType.SPANNABLE);
                } else {
                    holder.product_price_txt.setText("");
                }

                holder.discount_percentage_txt.setVisibility(View.INVISIBLE);
                holder.discount_tag_img.setVisibility(View.INVISIBLE);
            }

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

            holder.product_like_btn.setOnCheckedChangeListener(null);
    
            if (favorites_db.getUserFavorites().contains(product.getId())) {
                product.setIsLiked("1");
                holder.product_like_btn.setChecked(true);
            }
            else {
                product.setIsLiked("0");
                holder.product_like_btn.setChecked(false);
            }
    
    
            // Handle the Click event of product_like_btn ToggleButton
            holder.product_like_btn.setOnClickListener(view -> {

                if(holder.product_like_btn.isChecked()) {
                    product.setIsLiked("1");
                    holder.product_like_btn.setChecked(true);

                    // Add the Product to User's Favorites
                    if (!favorites_db.getUserFavorites().contains(product.getId())) {
                        favorites_db.insertFavoriteItem(product.getId());
                    }
                }
                else {
                    product.setIsLiked("0");
                    holder.product_like_btn.setChecked(false);

                    // Remove the Product from User's Favorites
                    if (favorites_db.getUserFavorites().contains(product.getId())) {
                        favorites_db.deleteFavoriteItem(product.getId());
                    }
                }
            });
    
    
            
            //holder.product_title.setOnClickListener(v -> gotoProductDetails(product));
            
            // Handle the Click event of product_thumbnail ImageView
            holder.bind(product, listener);
    
    
            // Handle the Click event of product_checked ImageView
//            holder.product_checked.setOnClickListener(view -> gotoProductDetails(product));
    
    
    
            // Check the Button's Visibility
            if (ConstantValues.IS_ADD_TO_CART_BUTTON_ENABLED) {
    
                holder.product_add_cart_btn.setVisibility(View.VISIBLE);
                holder.product_add_cart_btn.setOnClickListener(null);
    
                if (product.getType().equalsIgnoreCase("simple") && (!product.getPrice().isEmpty() || !product.getRegularPrice().isEmpty())) {
                    if (!product.isInStock()) {
                        holder.product_add_cart_btn.setText(context.getString(R.string.outOfStock));
                        if (isGridView) {
                            holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.primary_dark_bg));
                        }
                        else {
                            holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_primary_dark_bg));
                        }
                    } else {
                        holder.product_add_cart_btn.setText(context.getString(R.string.addToCart));
                        if (isGridView){
                            holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.primary_color_bg));
                        } else {
                            holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_primary_color_bg));
                        }
                    }
    
                    holder.product_add_cart_btn.setOnClickListener(view -> {
                        if (product.isInStock()) {

                            // Add Product to User's Cart
                            addProductToCart(product);
                            ((ModifyBasketFab) context).syncBasket();
//                            holder.product_checked.setVisibility(View.VISIBLE);
//                            AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 0.0f);
//                            alphaAnimation.setDuration(1000);
//                            alphaAnimation.setStartOffset(1000);
//                            alphaAnimation.setFillAfter(true);
//                            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
//                                @Override
//                                public void onAnimationStart(Animation animation) {
//
//                                }
//
//                                @Override
//                                public void onAnimationEnd(Animation animation) {
//                                    holder.product_checked.setVisibility(View.GONE);
//                                    Log.e("animation end", "ok");
//                                }
//
//                                @Override
//                                public void onAnimationRepeat(Animation animation) {
//
//                                }
//                            });
//                            holder.product_checked.startAnimation(alphaAnimation);
//                            new Handler().postDelayed(()->holder.product_checked.setVisibility(View.GONE),2000 );
//                            Utilities.animateCartMenuIcon(context, (MainActivity)context);

                            Snackbar.make(view, context.getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    
                } else {
                    holder.product_add_cart_btn.setText(context.getString(R.string.view_product));
                    if (isGridView){
                        holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.primary_color_bg));
                    } else {
                        holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_primary_color_bg));
                    }
                    holder.product_add_cart_btn.setOnClickListener(view -> gotoProductDetails(product));
                }
            } else {
                // Make the Button Invisible
                holder.product_add_cart_btn.setVisibility(View.GONE);
            }
    }

    private Spannable strikeThrough(String content, String target, String sizeTarget){
        Spannable spannable = new SpannableString(content);
        int indexOfTarget = content.indexOf(target);
        int indexOfSizeTarget = content.indexOf(sizeTarget);
        spannable.setSpan(new StrikethroughSpan(), indexOfTarget, indexOfTarget + target.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(isGridView){
            if (content.length() < 19){
                spannable.setSpan(new RelativeSizeSpan(1.5f), indexOfSizeTarget,indexOfSizeTarget + sizeTarget.length(), 0);
            } else {
                spannable.setSpan(new RelativeSizeSpan(1.25f), indexOfSizeTarget,indexOfSizeTarget + sizeTarget.length(), 0);
            }
        } else if (content.length() < 21){
            spannable.setSpan(new RelativeSizeSpan(1.25f), indexOfSizeTarget,indexOfSizeTarget + sizeTarget.length(), 0);
        } else {
            spannable.setSpan(new RelativeSizeSpan(1.25f), indexOfSizeTarget,indexOfSizeTarget + sizeTarget.length(), 0);
        }
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
//        itemInfo.putParcelable("productDetails", product);
        itemInfo.putInt("itemID", product.getId());

        Fragment fragment = new Product_Description();

        fragment.setArguments(itemInfo);
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null).commit();

    }

    public interface ItemClickListener {
        void onItemClick(ProductDetails product);
    }

    public void setData (List<ProductDetails> productList){
        Log.e("realtedproduct", "ok");
        this.productList = productList;
        notifyDataSetChanged();
    }

    public void clearData (){
        if (productList != null){
            productList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if(productList != null){
            return productList.size();
        }
        return 0;
    }
    

    
    
    
    //********** Toggles the RecyclerView LayoutManager *********//
    
    public void toggleLayout(Boolean isGridView) {
        this.isGridView = isGridView;
    }



    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        SpinKitView cover_loader;
        Button product_add_cart_btn;
        ToggleButton product_like_btn;
        ImageView product_cover, discount_tag_img;
        TextView product_title, product_price_txt, discount_percentage_txt;


        public MyViewHolder(final View itemView) {
            super(itemView);
            
            cover_loader = itemView.findViewById(R.id.product_cover_loader);
//            product_checked = itemView.findViewById(R.id.product_checked);
            product_cover = itemView.findViewById(R.id.product_cover);
            product_title = itemView.findViewById(R.id.product_title);
            product_add_cart_btn = itemView.findViewById(R.id.product_card_Btn);
            product_like_btn = itemView.findViewById(R.id.product_like_btn);
            product_price_txt = itemView.findViewById(R.id.product_price_webView);
            discount_percentage_txt = itemView.findViewById(R.id.product_card_sale_tag_txt);
            discount_tag_img = itemView.findViewById(R.id.product_card_sale_tag_img);
        }

        public void bind (ProductDetails product, ItemClickListener listener){
            itemView.setOnClickListener(v -> listener.onItemClick(product));
        }

    }



    //********** Adds the Product to User's Cart *********//
    
    private void addProductToCart(ProductDetails product) {

        CartDetails cartDetails = new CartDetails();

        double productBasePrice = 0.0;

        if (product.getPrice() != null  &&  !TextUtils.isEmpty(product.getPrice()))
            productBasePrice = Double.parseDouble(product.getPrice());


        List<ProductMetaData> productMetaData = new ArrayList<>();
        List<ProductAttributes> selectedAttributes = new ArrayList<>();


        // Set Product's Price and Quantity
        product.setCustomersBasketQuantity(1);
        product.setSelectedVariationID(0);
        product.setProductsFinalPrice(String.valueOf(productBasePrice));
        product.setTotalPrice(String.valueOf(productBasePrice));

        // Set Customer's Basket Product and selected Attributes Info
        cartDetails.setCartProduct(product);
        cartDetails.setCartProductMetaData(productMetaData);
        cartDetails.setCartProductAttributes(selectedAttributes);


        // Add the Product to User's Cart with the help of static method of My_Cart class
        My_Cart.AddCartItem(cartDetails);


        // Recreate the OptionsMenu
        ((MainActivity) context).invalidateOptionsMenu();

    }
}

