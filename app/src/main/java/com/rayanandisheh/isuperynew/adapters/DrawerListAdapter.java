package com.rayanandisheh.isuperynew.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rayanandisheh.isuperynew.R;

import java.util.List;

import com.rayanandisheh.isuperynew.models.drawer_model.Drawer_Items;


/**
 * DrawerExpandableListAdapter is the adapter class of ExpandableList of NavigationDrawer in MainActivity
 **/

public class DrawerListAdapter  extends RecyclerView.Adapter<DrawerListAdapter.ViewHolder> {

    Context context;
    List<Drawer_Items> drawerItems;
    ItemClickListener listener;

    public DrawerListAdapter(Context context, List<Drawer_Items> drawerItems, ItemClickListener listener) {
        this.context = context;
        this.drawerItems = drawerItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_list_header_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.title.setText(drawerItems.get(position).getTitle());
        holder.icon.setImageResource(drawerItems.get(position).getIcon());
        holder.bind(position, listener);
    }

    @Override
    public int getItemCount() {
        return drawerItems.size();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;

        public ViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.main_drawer_list_header_text);
            icon = itemView.findViewById(R.id.main_drawer_list_header_icon);
        }

        public void bind (int position, ItemClickListener listener){
            itemView.setOnClickListener(v -> listener.onItemClick(position));
        }
    }

}

