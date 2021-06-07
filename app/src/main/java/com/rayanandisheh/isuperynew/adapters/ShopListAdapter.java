package com.rayanandisheh.isuperynew.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.models.ShopDetails;
import com.rayanandisheh.isuperynew.utils.Utilities;

import java.util.List;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ViewHolder> {

    private List<ShopDetails> shopDetails;
    private ItemClickListener listener;


    public ShopListAdapter( ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        byte[] decodedString = Base64.decode(shopDetails.get(position).getStrIcon(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.shopImg.setImageBitmap(decodedByte);
        holder.title.setText(Utilities.convertToPersian(shopDetails.get(position).getStrName()));
        holder.bind(shopDetails.get(position), listener);
    }

    @Override
    public int getItemCount() {
        if(shopDetails != null){
            return shopDetails.size();
        }
        return 0;
    }

    public void setData(List<ShopDetails> shopDetails){
        this.shopDetails = shopDetails;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClick(ShopDetails shopDetails);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView shopImg;

        public ViewHolder(final View itemView) {
            super(itemView);
            shopImg = itemView.findViewById(R.id.shop_item_img);
            title = itemView.findViewById(R.id.shop_item_title);
        }

        public void bind (ShopDetails shopDetails, ItemClickListener listener){
            itemView.setOnClickListener(v -> listener.onItemClick(shopDetails));
        }

    }
}

