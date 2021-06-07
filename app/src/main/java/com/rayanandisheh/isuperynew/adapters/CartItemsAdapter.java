package com.rayanandisheh.isuperynew.adapters;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.rayanandisheh.isuperynew.Interfaces.ModifyBasketFab;
import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.databases.User_Recents_DB;
import com.rayanandisheh.isuperynew.fragments.My_Cart;
import com.rayanandisheh.isuperynew.fragments.Product_Description;
import com.rayanandisheh.isuperynew.models.cart_model.CartDetails;
import com.rayanandisheh.isuperynew.models.product_model.ProductAttributes;
import com.rayanandisheh.isuperynew.models.product_model.ProductMetaData;
import com.rayanandisheh.isuperynew.utils.Utilities;


/**
 * CartItemsAdapter is the adapter class of RecyclerView holding List of Cart Items in My_Cart
 **/

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.MyViewHolder> {

    private Context context;
    private My_Cart cartFragment;
    private List<CartDetails> cartItemsList;

    private User_Recents_DB recents_db;

    public CartItemsAdapter(Context context, List<CartDetails> cartItemsList, My_Cart cartFragment) {
        this.context = context;
        this.cartItemsList = cartItemsList;
        this.cartFragment = cartFragment;

        recents_db = new User_Recents_DB();
    }

    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
    
        View itemView;
    
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_cart_items, parent, false);
        
        return new MyViewHolder(itemView);
    }

    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
    
        // Get the data model based on Position
        final CartDetails cartDetails = cartItemsList.get(position);
    
        // Set Product Image on ImageView with Glide Library
        Glide.with(context).load(cartDetails.getCartProduct().getImage()).into(holder.cart_item_cover);
    
        holder.cart_item_title.setText(cartDetails.getCartProduct().getName());
        holder.cart_item_category.setText(cartDetails.getCartProduct().getCategoryNames());
        holder.cart_item_quantity.setText(Utilities.convertToPersian(String.valueOf(cartDetails.getCartProduct().getCustomersBasketQuantity())));
        holder.cart_item_base_price.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format((int) Double.parseDouble(cartDetails.getCartProduct().getPrice())) )+ " "  + ConstantValues.CURRENCY_SYMBOL);
        Double finalPrice = Double.parseDouble(cartDetails.getCartProduct().getPrice()) * cartDetails.getCartProduct().getCustomersBasketQuantity();
        holder.cart_item_sub_price.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format((int) Double.parseDouble(String.valueOf(finalPrice)))) + " " + ConstantValues.CURRENCY_SYMBOL);
//        holder.cart_item_total_price.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(cartDetails.getCartProduct().getTotalPrice())));
        
    
        List<ProductMetaData> productMetaDataList = new ArrayList<>();
        List<ProductAttributes> productAttributesList;
    
        productAttributesList = cartDetails.getCartProduct().getAttributes();
    
        for (int i=0;  i<productAttributesList.size();  i++) {
            ProductMetaData metaData = new ProductMetaData();
            metaData.setId(productAttributesList.get(i).getId());
            metaData.setKey(productAttributesList.get(i).getName());
            metaData.setValue(productAttributesList.get(i).getOption());
        
            productMetaDataList.add(metaData);
        }

        // Holds Product Quantity
        final int[] number = {1};
        number[0] = cartDetails.getCartProduct().getCustomersBasketQuantity();
    
    
        // Decrease Product Quantity
        holder.cart_item_quantity_minusBtn.setOnClickListener(v -> {
            // Check if the Quantity is greater than the minimum Quantity
            if (number[0] > 1)
            {
                // Decrease Quantity by 1
                number[0] = number[0] - 1;
                holder.cart_item_quantity.setText(Utilities.convertToPersian(String.valueOf(number[0])));

                // Calculate Product Price with selected Quantity
                double price = Double.parseDouble(cartDetails.getCartProduct().getPrice()) * number[0];

                // Set Final Price and Quantity
                cartDetails.getCartProduct().setTotalPrice(""+ price);
                cartDetails.getCartProduct().setProductsFinalPrice(""+ price);
                cartDetails.getCartProduct().setCustomersBasketQuantity(number[0]);

                My_Cart.UpdateCartItem(cartDetails);
                notifyItemChanged(holder.getAdapterPosition());

                cartFragment.updateCart();
            }
        });
    
    
        // Increase Product Quantity
        holder.cart_item_quantity_plusBtn.setOnClickListener(v -> {

            if (cartDetails.getCartProduct().getStockQuantity() == null ||  number[0] < Long.parseLong(cartDetails.getCartProduct().getStockQuantity())) {
                number[0] = number[0] + 1;
                holder.cart_item_quantity.setText(Utilities.convertToPersian(String.valueOf(number[0])));

                double price = Double.parseDouble(cartDetails.getCartProduct().getPrice()) * number[0];

                cartDetails.getCartProduct().setTotalPrice(""+ price);
                cartDetails.getCartProduct().setProductsFinalPrice(""+ price);
                cartDetails.getCartProduct().setCustomersBasketQuantity(number[0]);

                My_Cart.UpdateCartItem(cartDetails);

                notifyItemChanged(holder.getAdapterPosition());

                cartFragment.updateCart();
            }
        });
    
        holder.cart_item_viewBtn.setOnClickListener(v -> {

            int productID = cartDetails.getCartProduct().getId();

            Bundle itemInfo = new Bundle();
            itemInfo.putInt("itemID", productID);

            // Navigate to Product_Description of selected Product
            Fragment fragment = new Product_Description();
            fragment.setArguments(itemInfo);
            FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();


            // Add the Product to User's Recently Viewed Products
            if (!recents_db.getUserRecents().contains(cartDetails.getCartProduct().getId())) {
                recents_db.insertRecentItem(cartDetails.getCartProduct().getId());
            }
        });
    
    
    
        // Delete relevant Product from Cart
        holder.cart_item_removeBtn.setOnClickListener(view -> {

            holder.cart_item_removeBtn.setClickable(false);

            // Delete CartItem from Local Database using static method of My_Cart
            My_Cart.DeleteCartItem
                    (
                            cartDetails.getCartID()
                    );


            // Remove CartItem from Cart List
            cartItemsList.remove(holder.getAdapterPosition());

            // Notify that item at position has been removed
            notifyItemRemoved(holder.getAdapterPosition());


            // Calculate Cart's Total Price Again
            cartFragment.updateCart();

            ((ModifyBasketFab) context).syncBasket();

            // Update Cart View from Local Database using static method of My_Cart
            cartFragment.updateCartView(getItemCount());

        });
        

    }



    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return cartItemsList.size();
    }
    



    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public static class MyViewHolder extends RecyclerView.ViewHolder {
    
        private LinearLayout cart_item_viewBtn, cart_item_removeBtn;
        private ImageView cart_item_cover;
        private ImageButton cart_item_quantity_minusBtn, cart_item_quantity_plusBtn;
        private TextView cart_item_title, cart_item_category, cart_item_base_price, cart_item_sub_price, cart_item_total_price, cart_item_quantity;


        public MyViewHolder(final View itemView) {
            super(itemView);
            cart_item_title = itemView.findViewById(R.id.cart_item_title);
            cart_item_base_price = itemView.findViewById(R.id.cart_item_base_price);
            cart_item_sub_price = itemView.findViewById(R.id.cart_item_sub_price);
            cart_item_total_price = itemView.findViewById(R.id.cart_item_total_price);
            cart_item_quantity = itemView.findViewById(R.id.cart_item_quantity);
            cart_item_category = itemView.findViewById(R.id.cart_item_category);
            cart_item_cover = itemView.findViewById(R.id.cart_item_cover);
            cart_item_viewBtn = itemView.findViewById(R.id.cart_item_viewBtn);
            cart_item_removeBtn = itemView.findViewById(R.id.cart_item_removeBtn);
            cart_item_quantity_plusBtn = itemView.findViewById(R.id.cart_item_quantity_plusBtn);
            cart_item_quantity_minusBtn = itemView.findViewById(R.id.cart_item_quantity_minusBtn);
    
            cart_item_total_price.setVisibility(View.GONE);
        }
        
    }

}

