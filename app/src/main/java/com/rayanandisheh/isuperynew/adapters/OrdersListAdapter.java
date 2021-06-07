package com.rayanandisheh.isuperynew.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.fragments.Order_Details;
import com.rayanandisheh.isuperynew.models.order_model.OrderDetails;
import com.rayanandisheh.isuperynew.utils.Utilities;

import saman.zamani.persiandate.PersianDate;


/**
 * OrdersListAdapter is the adapter class of RecyclerView holding List of Orders in My_Orders
 **/

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.MyViewHolder> {

    Context context;
    List<OrderDetails> ordersList;
    private ItemClickListener listener;

    public OrdersListAdapter(Context context, List<OrderDetails> ordersList, ItemClickListener listener) {
        this.context = context;
        this.ordersList = ordersList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_orders, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        
        // Get the data model based on Position
        final OrderDetails orderDetails = ordersList.get(position);
    
        int noOfProducts = 0;
        for (int i=0;  i<orderDetails.getOrderProducts().size();  i++) {
            // Count no of Products
            noOfProducts += orderDetails.getOrderProducts().get(i).getQuantity();
        }

        holder.order_id.setText(Utilities.convertToPersian(String.valueOf(orderDetails.getId())));
        holder.order_status.setText(translateStatus(orderDetails.getStatus()));
        setDateAndTime(holder, orderDetails.getDateCreated().replaceAll("[a-zA-Z]", " "));
        holder.order_product_count.setText(Utilities.convertToPersian(String.valueOf(noOfProducts)));
        holder.order_price.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format((int) Double.parseDouble(orderDetails.getTotal()))) + " " + ConstantValues.CURRENCY_SYMBOL);
    

        // Check Order's status
        if (orderDetails.getStatus().equalsIgnoreCase("processing")) {
            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else if (orderDetails.getStatus().equalsIgnoreCase("on-hold")) {
            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.progressYellow));
        } else if (orderDetails.getStatus().equalsIgnoreCase("completed")) {
            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryLight));
        } else if (orderDetails.getStatus().equalsIgnoreCase("cancelled")) {
            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentRed));
        } else if (orderDetails.getStatus().equalsIgnoreCase("refunded")) {
            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryLight));
        } else if (orderDetails.getStatus().equalsIgnoreCase("failed")) {
            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentRed));
        } else {// pending
            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
    
        
    
        holder.order_view_btn.setOnClickListener(v -> {
            // Get Order Info
            Bundle itemInfo = new Bundle();
            itemInfo.putInt("orderID", orderDetails.getId());

            // Navigate to Order_Details Fragment
            Fragment fragment = new Order_Details();
            fragment.setArguments(itemInfo);
            MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null).commit();
        });

        holder.bind(orderDetails.getId(), listener);

    }

    private void setDateAndTime(MyViewHolder holder, String dateAndTime) {
        String [] separated =  dateAndTime.split(" ");
        String [] date = separated[0].split("-");
        String [] time = separated[1].split(":");

        PersianDate persianDate = new PersianDate();
        int [] jalaliDate = persianDate.toJalali(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]));

        String concatinatedDate = jalaliDate[0] + "/" + jalaliDate[1] + "/" + jalaliDate[2];

        holder.order_time.setText(time[0] + ":" + time[1]);
        holder.order_date.setText(Utilities.convertToPersian(concatinatedDate));
    }


    private String translateStatus(String status) {
        String translatedStatus = "";
        switch (status){
            case "pending":
                translatedStatus = "در انتظار پرداخت";
                break;
            case "processing":
                translatedStatus = "در حال انجام";
                break;
            case "on-hold":
                translatedStatus = "در انتظار بررسی";
                break;
            case "completed":
                translatedStatus = "تکمیل شده";
                break;
            case "refunded":
                translatedStatus = "لغو شده";
                break;
            case "failed":
                translatedStatus = "مسترد شده";
                break;
            case "cancelled":
                translatedStatus = "ناموفق";
                break;
        }
        return translatedStatus;
    }


    public interface ItemClickListener {
        void onItemClick(int orderId);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private Button order_view_btn;
        private TextView order_id, order_product_count, order_status, order_price, order_date, order_time;


        public MyViewHolder(final View itemView) {
            super(itemView);
    
            order_view_btn = itemView.findViewById(R.id.order_view_btn);
            order_id = itemView.findViewById(R.id.order_id);
            order_product_count = itemView.findViewById(R.id.order_products_count);
            order_status = itemView.findViewById(R.id.order_status);
            order_price = itemView.findViewById(R.id.order_price);
            order_date = itemView.findViewById(R.id.card_orders_date_txt);
            order_time = itemView.findViewById(R.id.card_orders_time_txt);
        }

        public void bind (int orderId, ItemClickListener listener){
            itemView.setOnClickListener(v -> listener.onItemClick(orderId));
        }
    }
}

