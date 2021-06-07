package com.rayanandisheh.isuperynew.customs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.rayanandisheh.isuperynew.Interfaces.FilterDialogInterfaces;
import com.rayanandisheh.isuperynew.R;

import com.rayanandisheh.isuperynew.app.App;
import com.rayanandisheh.isuperynew.models.category_model.CategoryDetails;
import com.rayanandisheh.isuperynew.models.product_model.Filters;
import com.rayanandisheh.isuperynew.utils.Utilities;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * FilterDialog will be used to implement Price and Attribute Filters on Products in different categories
 **/

public class FilterDialog extends DialogFragment {
    private Context context;
    private Filters filters;

    private Button filter_cancel_btn, filter_clear_btn, filter_apply_btn;
    private RelativeLayout categorySpinner, subCategorySpinner;
    private TextView categorySpinnerTxt, subCategorySpinnerTxt;
    private EditText startPriceEt, endPriceEt;
    private SwitchCompat inStockSwitch, onSaleSwitch;
    List<CategoryDetails> allCategories, parentCategories, subCategories;
    LinearLayout subCategorySpinnerContainer;

    CategoryDetails selectedCategory, selectedSubCategory;

    public FilterDialog(Context context, Filters filters) {
        this.context = context;
        this.filters = filters;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupSelector();
        initListeners();

        if(filters != null){
            fillFilters();
        } else {
            inStockSwitch.setChecked(true);
        }
    }

    private void initListeners() {
        categorySpinner.setOnClickListener(v -> {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            androidx.fragment.app.Fragment prev = getChildFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            SelectorDialogFragment selectorDialogFragment = new SelectorDialogFragment(parentCategories, this::selectedCategoryItem);
            selectorDialogFragment.show(ft, "dialog");
        });

        subCategorySpinner.setOnClickListener(v -> {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            androidx.fragment.app.Fragment prev = getChildFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            SelectorDialogFragment selectorDialogFragment = new SelectorDialogFragment(subCategories, this::selectedSubCategoryItem);
            selectorDialogFragment.show(ft, "dialog");
        });

        // Dismiss the FilterDialog
        filter_cancel_btn.setOnClickListener(view -> dismiss());
        // Clear Selected Filters
        filter_clear_btn.setOnClickListener(view -> {
            // Clear Filters
            ((FilterDialogInterfaces)getParentFragment()).clearFilters();
            dismiss();
        });
        // Apply Selected Filters
        filter_apply_btn.setOnClickListener(view -> {

            Filters appliedFilters = new Filters();

            if (startPriceEt.getText() != null){
                if (startPriceEt.getText().toString().length() > 0){
                    appliedFilters.setMin_price(Utilities.convertToEnglish(startPriceEt.getText().toString().replaceAll(",", "")));
                }
            }

            if (endPriceEt.getText() != null){
                if (endPriceEt.getText().toString().length() > 0){
                    appliedFilters.setMax_price(Utilities.convertToEnglish(endPriceEt.getText().toString().replaceAll(",", "")));
                }
            }


            if (selectedCategory.getId() == -1){
                appliedFilters.setCategoryId(selectedCategory.getId());
            } else {
                if (selectedSubCategory.getId() == -1){
                    appliedFilters.setCategoryId(selectedCategory.getId());
                } else {
                    appliedFilters.setCategoryId(selectedSubCategory.getId());
                }
            }

            appliedFilters.setInStock(inStockSwitch.isChecked());
            appliedFilters.setOnSale(onSaleSwitch.isChecked());

            // Apply Filters
            ((FilterDialogInterfaces)getParentFragment()).applyFilters(appliedFilters);
            dismiss();
        });

        startPriceEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()){
                    startPriceEt.removeTextChangedListener(this);
                    String price = Utilities.convertToEnglish(s.toString().replaceAll(",", ""));
                    startPriceEt.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format((Integer.parseInt(price)))));
                    startPriceEt.addTextChangedListener(this);
                    startPriceEt.setSelection(startPriceEt.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        endPriceEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()){
                    endPriceEt.removeTextChangedListener(this);
                    String price = Utilities.convertToEnglish(s.toString().replaceAll(",", ""));
                    endPriceEt.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format((Integer.parseInt(price)))));
                    endPriceEt.addTextChangedListener(this);
                    endPriceEt.setSelection(endPriceEt.getText().toString().length());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void selectedSubCategoryItem(Dialog dialog,int position) {
        selectedSubCategory = subCategories.get(position);
        subCategorySpinnerTxt.setText(subCategories.get(position).getName());
        dialog.dismiss();
    }

    private void selectedCategoryItem(Dialog dialog, int position) {
        selectedCategory = parentCategories.get(position);
        if (parentCategories.get(position).getId() == -1){
            subCategorySpinnerContainer.setVisibility(View.GONE);
            categorySpinnerTxt.setText(context.getResources().getString(R.string.all));
        } else {
            categorySpinnerTxt.setText(parentCategories.get(position).getName());
            subCategories = getSubCategory(position);
            subCategories.add(0, new CategoryDetails(-1, context.getResources().getString(R.string.all)));
            subCategorySpinnerContainer.setVisibility(View.VISIBLE);
            subCategorySpinnerTxt.setText(context.getResources().getString(R.string.all));
            selectedSubCategory = subCategories.get(0);
        }

        dialog.dismiss();
    }

    private void setupSelector() {
        allCategories = ((App) getContext().getApplicationContext()).getCategoriesList();
        parentCategories = getParentCategories(allCategories);
        parentCategories.add(0, new CategoryDetails(-1, context.getResources().getString(R.string.all)));
        selectedCategory = parentCategories.get(0);
        categorySpinnerTxt.setText(selectedCategory.getName());
    }

    private void initViews(View view) {
        filter_cancel_btn = view.findViewById(R.id.filter_cancel_btn);
        filter_clear_btn = view.findViewById(R.id.filter_clear_btn);
        filter_apply_btn = view.findViewById(R.id.filter_apply_btn);
        categorySpinner = view.findViewById(R.id.filter_dialog_category_spinner);
        startPriceEt = view.findViewById(R.id.filter_dialog_start_price);
        endPriceEt = view.findViewById(R.id.filter_dialog_end_price);
        inStockSwitch = view.findViewById(R.id.filter_dialog_existance_switch);
        subCategorySpinner = view.findViewById(R.id.filter_dialog_subcategory_spinner);
        subCategorySpinnerContainer = view.findViewById(R.id.filter_dialog_subCategory_container);
        onSaleSwitch = view.findViewById(R.id.filter_dialog_on_sale_switch);
        categorySpinnerTxt = view.findViewById(R.id.filter_dialog_category_spinner_txt);
        subCategorySpinnerTxt =view.findViewById(R.id.filter_dialog_subcategory_spinner_txt);
    }

    private List<CategoryDetails> getSubCategory(int position){
        List<CategoryDetails> subCategories = new ArrayList<>();
        int id = parentCategories.get(position).getId();

        for (int i = 0 ; i < allCategories.size(); i++){
            if (allCategories.get(i).getParent() == id){
                subCategories.add(allCategories.get(i));
            }
        }

        return subCategories;
    }

    private List<CategoryDetails> getParentCategories(List<CategoryDetails> categories){
        List<CategoryDetails> parentCategories = new ArrayList<>();
        for (int i = 0; i< categories.size(); i++){
            if (categories.get(i).getParent() < 1) {
                parentCategories.add(categories.get(i));
            }
        }
        return parentCategories;
    }

    private void fillFilters() {

        if(filters.getMin_price() != null){
            if (filters.getMin_price().length() > 0){
                startPriceEt.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format((Integer.parseInt(filters.getMin_price())))));
            }
        }

        if(filters.getMax_price() != null){
            if (filters.getMax_price().length() > 0){
                endPriceEt.setText(Utilities.convertToPersian(NumberFormat.getInstance(Locale.US).format((Integer.parseInt(filters.getMax_price())))));
            }
        }

        if (hasParentCategory(filters.getCategoryId())){
            int parentPosition = getParentPosition(filters.getCategoryId());
            selectedCategory = parentCategories.get(parentPosition);

            categorySpinnerTxt.setText(parentCategories.get(parentPosition).getName());

            subCategories = getSubCategory(parentPosition);
            subCategories.add(0, new CategoryDetails(-1, context.getResources().getString(R.string.all)));
            subCategorySpinnerContainer.setVisibility(View.VISIBLE);
            int childPosition = getChildPosition(filters.getCategoryId());
            selectedSubCategory = subCategories.get(childPosition);
            subCategorySpinnerTxt.setText(subCategories.get(childPosition).getName());
        } else {
            if (filters.getCategoryId() == -1){
                categorySpinnerTxt.setText(getContext().getResources().getString(R.string.all));

            } else {
                int parentPosition = getPositionInParentCategories(filters.getCategoryId());
                categorySpinnerTxt.setText(parentCategories.get(parentPosition).getName());
                selectedCategory = parentCategories.get(parentPosition);
                subCategories = getSubCategory(parentPosition);
                subCategories.add(0, new CategoryDetails(-1, context.getResources().getString(R.string.all)));
                selectedSubCategory = subCategories.get(0);
                subCategorySpinnerContainer.setVisibility(View.VISIBLE);
                subCategorySpinnerTxt.setText(getContext().getResources().getString(R.string.all));
            }
        }

        inStockSwitch.setChecked(filters.isInStock());
        onSaleSwitch.setChecked(filters.isOnSale());
    }

    private int getPositionInParentCategories(int categoryId) {
        for (int i = 0; i < parentCategories.size(); i++){
            if (parentCategories.get(i).getId() == categoryId){
                return i;
            }
        }
        return 0;
    }

    private int getChildPosition(int categoryId) {
        for (int i = 0 ; i < subCategories.size(); i++){
            if (subCategories.get(i).getId() == categoryId){
                return i;
            }
        }
        return 0;
    }

    private int getParentPosition(int categoryId){
        int parentId = -1;
        for (int i = 0; i < allCategories.size(); i++){
            if (allCategories.get(i).getId() == categoryId){
                parentId = allCategories.get(i).getParent();
            }
        }

        int parentPosition = 0;

        for (int i = 0; i < parentCategories.size(); i++){
            if (parentCategories.get(i).getId() == parentId){
                parentPosition = i;
            }
        }

        return parentPosition;
    }

    private boolean hasParentCategory(int id){
        for (int i = 0; i < allCategories.size(); i++) {
            if (allCategories.get(i).getId() == id) {
                if (allCategories.get(i).getParent() > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}

