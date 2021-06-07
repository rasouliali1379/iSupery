package com.rayanandisheh.isuperynew.adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.databases.User_Favorites_DB;
import com.rayanandisheh.isuperynew.databases.User_Recents_DB;
import com.rayanandisheh.isuperynew.fragments.Product_Description;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;
import com.rayanandisheh.isuperynew.utils.Utilities;


/**
 * ProductAdapterRemovable is the adapter class of RecyclerView holding List of Products in RecentlyViewed and WishList
 **/

public class ProductAdapterRemovable extends RecyclerView.Adapter<ProductAdapterRemovable.MyViewHolder> {

    private Context context;
    private TextView emptyText;

    private boolean isRecents;
    private boolean isHorizontal;

    private List<ProductDetails> productList;

    private User_Recents_DB recents_db;
    private User_Favorites_DB favorites_db;


    public ProductAdapterRemovable(Context context, List<ProductDetails> productList, boolean isHorizontal, boolean isRecents, TextView emptyText) {
        this.context = context;
        this.productList = productList;
        this.isHorizontal = isHorizontal;
        this.isRecents = isRecents;
        this.emptyText = emptyText;

        recents_db = new User_Recents_DB();
        favorites_db = new User_Favorites_DB();
    }



    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = null;

        if (isHorizontal) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_grid_sm, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_grid_lg, parent, false);
        }

        return new MyViewHolder(itemView);
    }



    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        // Get the data model based on Position
        final ProductDetails product = productList.get(position);

//        holder.product_checked.setVisibility(View.GONE);


        // Set Product Image on ImageView with Glide Library
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

        if (product.isOnSale()){
            if (!product.getPrice().equals("")){
                holder.product_price_webView.setText(strikeThrough(
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
                holder.discount_percentage_txt.setVisibility(View.INVISIBLE);
                holder.discount_tag_img.setVisibility(View.INVISIBLE);
            }
        } else {

            if (!product.getPrice().equals("")){
                holder.product_price_webView.setText(increaseSize(
                        Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int)Double.parseDouble(product.getPrice())))) + " " + ConstantValues.CURRENCY_SYMBOL,
                        Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format(((int)Double.parseDouble(product.getPrice()))))) , TextView.BufferType.SPANNABLE);
            }

            holder.discount_percentage_txt.setVisibility(View.INVISIBLE);
            holder.discount_tag_img.setVisibility(View.INVISIBLE);
        }
    
    
        holder.product_title.setOnClickListener(v -> gotoProductDetails(product));
    
        // Handle the Click event of product_thumbnail ImageView
        holder.product_cover.setOnClickListener(view -> gotoProductDetails(product));
    
    
        // Handle the Click event of product_checked ImageView
//        holder.product_checked.setOnClickListener(view -> gotoProductDetails(product));
    
    
        holder.product_remove_btn.setVisibility(View.VISIBLE);
    
        // Handle Click event of product_remove_btn Button
        holder.product_remove_btn.setOnClickListener(view -> {

            // Check if Product is in User's Recents
            if (isRecents) {
                // Delete Product from User_Recents_DB Local Database
                recents_db.deleteRecentItem(product.getId());

            } else {
                // Remove the Product from User's Favorites
                if (favorites_db.getUserFavorites().contains(product.getId())) {
                    favorites_db.deleteFavoriteItem(product.getId());
                }
            }

            // Remove Product from productList List
            productList.remove(holder.getAdapterPosition());

            notifyItemRemoved(holder.getAdapterPosition());

            // Update View
            updateView(isRecents);
        });

    }

    private Spannable strikeThrough(String content, String target, String sizeTarget){
        Spannable spannable = new SpannableString(content);
        int indexOfTarget = content.indexOf(target);
        int indexOfSizeTarget = content.indexOf(sizeTarget);
        spannable.setSpan(new StrikethroughSpan(), indexOfTarget, indexOfTarget + target.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (content.length() < 21){
            spannable.setSpan(new RelativeSizeSpan(1.5f), indexOfSizeTarget,indexOfSizeTarget + sizeTarget.length(), 0);
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


    //********** Returns the total number of items in the RecyclerView *********//

    @Override
    public int getItemCount() {
        return productList.size();
    }



    //********** Change the Layout View based on the total number of items in the RecyclerView *********//

    public void updateView(boolean isRecentProducts) {


        // Check if Product is in User's Recents
        if (isRecentProducts) {

            // Check if RecyclerView has some Items
            if (getItemCount() != 0) {
                emptyText.setVisibility(View.VISIBLE);
            } else {
                emptyText.setVisibility(View.GONE);
            }

        } else {

            // Check if RecyclerView has some Items
            if (getItemCount() != 0) {
                emptyText.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.VISIBLE);
            }
        }
        
        notifyDataSetChanged();

    }
    
    
    private void gotoProductDetails(ProductDetails product) {
        // Get Product Info
        Bundle itemInfo = new Bundle();
        itemInfo.putParcelable("productDetails", product);
        
        Fragment fragment = new Product_Description();
        
        fragment.setArguments(itemInfo);
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null).commit();
        
        
        // Add the Product to User's Recently Viewed Products
        if (!recents_db.getUserRecents().contains(product.getId())) {
            recents_db.insertRecentItem(product.getId());
        }
    }


    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public class MyViewHolder extends RecyclerView.ViewHolder {
    
        ProgressBar cover_loader;
        Button product_remove_btn;
        ToggleButton product_like_btn;
        ImageView product_cover, discount_tag_img;
        TextView product_title, product_price_webView, discount_percentage_txt;

        public MyViewHolder(final View itemView) {
            super(itemView);
            cover_loader = itemView.findViewById(R.id.product_cover_loader);
//            product_checked = itemView.findViewById(R.id.product_checked);
            product_cover = itemView.findViewById(R.id.product_cover);
            product_title = itemView.findViewById(R.id.product_title);
            product_remove_btn = itemView.findViewById(R.id.product_card_Btn);
            product_like_btn = itemView.findViewById(R.id.product_like_btn);
            product_price_webView = itemView.findViewById(R.id.product_price_webView);
            discount_percentage_txt = itemView.findViewById(R.id.product_card_sale_tag_txt);
            discount_tag_img = itemView.findViewById(R.id.product_card_sale_tag_img);

            product_like_btn.setVisibility(View.GONE);
            product_remove_btn.setText(context.getString(R.string.removeProduct));
            product_remove_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
        }
    }

}

