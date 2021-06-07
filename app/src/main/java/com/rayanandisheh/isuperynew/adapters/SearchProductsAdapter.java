package com.rayanandisheh.isuperynew.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.databases.User_Favorites_DB;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;

import java.util.List;

public class SearchProductsAdapter extends RecyclerView.Adapter<SearchProductsAdapter.ViewHolder> {

    private Context context;
    private Boolean isGridView;
    private Boolean isHorizontal;

    private User_Favorites_DB favorites_db;
    private List<ProductDetails> productList;
    private ItemClickListener listener;

    public SearchProductsAdapter(Context context, Boolean isHorizontal, ItemClickListener listener) {
        this.context = context;
        this.isHorizontal = isHorizontal;
        this.listener = listener;
        favorites_db = new User_Favorites_DB();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_product_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (position != productList.size()  &&  productList.get(position).getStatus().equalsIgnoreCase("publish")) {

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

            String styleSheet = "<style>" +
                    "@font-face{font-family:'iransans'; src: url('file:///android_res/font/iransans.ttf')}" +
                    "body { font-family: iransans;margin:0; padding:0; color:#00000; font-size:0.85em; direction: rtl; display: inline-block} " +
                    ".woocommerce-Price-amount, .amount { font-family: iransans; color:#00000; font-size:0.85em; direction: rtl; }" +
                    "img{display:inline; height:auto; max-width:100%;}" +
                    "</style>";

            holder.product_price_webView.setVerticalScrollBarEnabled(false);
            holder.product_price_webView.setHorizontalScrollBarEnabled(false);
            holder.product_price_webView.setBackgroundColor(Color.TRANSPARENT);
            holder.product_price_webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
            holder.product_price_webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            holder.product_price_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            holder.product_price_webView.loadDataWithBaseURL(null, styleSheet+product.getPriceHtml(), "text/html", "utf-8", null);
            Log.e("getPrice", product.getPriceHtml());

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

            holder.bind(product, listener);
        }

    }

    public interface ItemClickListener {
        void onItemClick(ProductDetails product);
    }

    public void setData (List<ProductDetails> productList){
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


    public static class ViewHolder extends RecyclerView.ViewHolder {

        SpinKitView cover_loader;
        WebView product_price_webView;
        ImageView product_cover;
        TextView product_title;


        public ViewHolder(final View itemView) {
            super(itemView);

            cover_loader = itemView.findViewById(R.id.product_cover_loader);
            product_cover = itemView.findViewById(R.id.product_cover);
            product_title = itemView.findViewById(R.id.product_title);
            product_price_webView = itemView.findViewById(R.id.product_price_webView);

        }

        public void bind (ProductDetails product, ItemClickListener listener){
            itemView.setOnClickListener(v -> listener.onItemClick(product));
        }

    }
}
