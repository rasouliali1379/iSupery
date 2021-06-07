package com.rayanandisheh.isuperynew.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.customs.LinedEditText;
import com.rayanandisheh.isuperynew.models.Notes.NotesDataModel;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;
import com.rayanandisheh.isuperynew.network.APIClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private Context context;
    private List<NotesDataModel> noteRows;
    private ProductClickListener productClickListener;
    private RemoveBtnClickListener removeBtnClickListener;
    private BindModelsInterface bindModelsInterface;

    Call<ProductDetails> call;

    public NoteAdapter(Context context, List<NotesDataModel> noteRows,
                       ProductClickListener productClickListener,
                       RemoveBtnClickListener removeBtnClickListener,
                       BindModelsInterface bindModelsInterface) {
        this.context = context;
        this.noteRows = noteRows;
        this.productClickListener = productClickListener;
        this.removeBtnClickListener = removeBtnClickListener;
        this.bindModelsInterface = bindModelsInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        setupTextRow(holder, noteRows.get(position), position);
        setupProductRow(holder, noteRows.get(position), position);

    }

    private void setupProductRow(ViewHolder holder, NotesDataModel row, int position) {
        RequestSingleProducts(row.getProductId(), holder, position);
    }

    private void setupTextRow(ViewHolder holder, NotesDataModel row, int position) {
        holder.textEt.setText("");
        if (row.getComment() != null){
            if (!row.getComment().trim().isEmpty()){
                holder.textEt.setText(row.getComment());
            }
        }

//        holder.textEt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                holder.textEt.removeTextChangedListener(this);
//                noteRows.get(position).setComment(s.toString());
//                holder.bindModelsInterface(noteRows, bindModelsInterface);
//                holder.textEt.addTextChangedListener(this);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    public void RequestSingleProducts(int products_id, ViewHolder holder, int position) {
        holder.loadingFrame.setVisibility(View.VISIBLE);
        call = APIClient.getInstance().getSingleProduct(String.valueOf(products_id));

        call.enqueue(new Callback<ProductDetails>() {
            @Override
            public void onResponse(@NonNull Call<ProductDetails> call, @NonNull retrofit2.Response<ProductDetails> response) {
                if (response.isSuccessful()) {
                    holder.loadingFrame.setVisibility(View.GONE);
                    if (response.body().getStatus() == null  ||  response.body().getStatus().equalsIgnoreCase("publish")){
                        holder.titleTxt.setText(response.body().getName());
                        holder.priceWebView.setBackgroundColor(Color.TRANSPARENT);
                        holder.priceWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

                        String price_styleSheet = "<style>" +
                                "@font-face{font-family:'iransans'; src: url('file:///android_res/font/iransans.ttf')}" +
                                "body { font-family: iransans;margin:0; padding:0; color:#00000; font-size:0.85em; direction: rtl; display: inline-block} " +
                                "img{display:inline; height:auto; max-width:100%;}" +
                                "</style>";

                        holder.priceWebView.loadDataWithBaseURL(null, price_styleSheet + response.body().getPriceHtml(), "text/html", "utf-8", null);

                        Glide.with(context)
                                .load(response.body().getImages().get(0).getSrc())
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        holder.imageLoading.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        holder.imageLoading.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(holder.coverImg);
                        holder.bindProduct(response.body(), productClickListener);
                        holder.bindRemoveBtn(position, removeBtnClickListener);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductDetails> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if(noteRows != null){
            return noteRows.size();

        }
        return 0;
    }

    public void setData(List<NotesDataModel> notes){
        noteRows = notes;
        notifyDataSetChanged();
    }

    public interface ProductClickListener {
        void onItemClick(ProductDetails product);
    }

    public interface RemoveBtnClickListener {
        void onItemClick(int position);
    }

    public interface BindModelsInterface {
        void sendNotes(List<NotesDataModel> notes);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinedEditText textEt;
        public ImageView coverImg, removeBtn;
        public TextView titleTxt;
        public WebView priceWebView;
        public FrameLayout loadingFrame;
        public SpinKitView imageLoading;

        public ViewHolder(final View itemView) {
            super(itemView);
            textEt = itemView.findViewById(R.id.note_text_item_txt);
            coverImg = itemView.findViewById(R.id.note_product_item_cover);
            titleTxt = itemView.findViewById(R.id.note_product_item_title);
            priceWebView = itemView.findViewById(R.id.note_product_item_price_webView);
            loadingFrame = itemView.findViewById(R.id.note_product_item_main_progress);
            imageLoading = itemView.findViewById(R.id.note_product_item_cover_loader);
            removeBtn = itemView.findViewById(R.id.product_item_remove_btn);
        }

        public void bindProduct (ProductDetails product, ProductClickListener listener){
            itemView.setOnClickListener(v -> listener.onItemClick(product));
        }

        public void bindRemoveBtn(int position, RemoveBtnClickListener listener){
            removeBtn.setOnClickListener(v -> listener.onItemClick(position));
        }

        public void bindModelsInterface(List<NotesDataModel> notes, BindModelsInterface binder){
            binder.sendNotes(notes);
        }
    }

}

