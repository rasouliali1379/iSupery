package com.rayanandisheh.isuperynew.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.models.product_model.ProductAttributes;

import java.util.List;

public class AttributeListAdapter  extends RecyclerView.Adapter<AttributeListAdapter.MyViewHolder> {

    private Context context;
    private List<ProductAttributes> productAttributes;

    public AttributeListAdapter(Context context, List<ProductAttributes> productAttributes) {
        this.context = context;
        this.productAttributes = productAttributes;

    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_attributes, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ProductAttributes attribute = productAttributes.get(position);
        holder.attribute_name.setText(attribute.getName());

        List<String> attributeValues = attribute.getOptions();

        holder.attribute_value.setText(concatenateAttributes(attributeValues));
    }

    private String concatenateAttributes(List<String> attributeValues) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < attributeValues.size(); i++){
            if (i + 1 == attributeValues.size()){
                sb.append(attributeValues.get(i));
            } else {
                sb.append(attributeValues.get(i));
                sb.append("\n");
            }
        }
        return sb.toString();
    }


    @Override
    public int getItemCount() {
        return productAttributes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

//        RelativeLayout attribute;
        TextView attribute_name, attribute_value;

        public MyViewHolder(final View itemView) {
            super(itemView);
//            attribute =itemView.findViewById(R.id.attribute);
            attribute_name = itemView.findViewById(R.id.attribute_name);
            attribute_value = itemView.findViewById(R.id.attribute_value);
        }

    }

}

