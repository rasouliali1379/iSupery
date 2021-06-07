package com.rayanandisheh.isuperynew.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.models.category_model.CategoryDetails;

import java.util.List;

public class CategoriesListAdapter  extends RecyclerView.Adapter<CategoriesListAdapter.ViewHolder> {

    private List<CategoryDetails> categoriesItems;
    private ItemClickListener listener;
    private Context context;


    public CategoriesListAdapter(ItemClickListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selector_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (categoriesItems.get(position).getId() == -1){
            holder.title.setText(context.getResources().getString(R.string.all));
        } else {
            holder.title.setText(categoriesItems.get(position).getName());
        }
        holder.bind(position, listener);
    }

    @Override
    public int getItemCount() {
        if(categoriesItems != null){
            return categoriesItems.size();
        }
        return 0;
    }

    public void setData(List<CategoryDetails> categoriesItem){
        this.categoriesItems = categoriesItem;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        public ViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.selector_item_txt);
        }

        public void bind (int position, ItemClickListener listener){
            itemView.setOnClickListener(v -> listener.onItemClick(position));
        }

    }
}

