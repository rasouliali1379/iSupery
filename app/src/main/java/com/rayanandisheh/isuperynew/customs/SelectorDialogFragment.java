package com.rayanandisheh.isuperynew.customs;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.adapters.CategoriesListAdapter;
import com.rayanandisheh.isuperynew.models.category_model.CategoryDetails;
import com.rayanandisheh.isuperynew.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

public class SelectorDialogFragment extends DialogFragment {

    CategoriesListAdapter categoriesListAdapter;

    RecyclerView recyclerView;
    EditText searchEt;
    ImageView clearBtn;
    TextView nothingFoundTxt;

    List<CategoryDetails> categoryItems;
    private onDismiss listener;

    public SelectorDialogFragment(List<CategoryDetails> categoryItems, onDismiss listener) {
        this.categoryItems = categoryItems;
        this.listener = listener;
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
        return inflater.inflate(R.layout.fragment_selector_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListeners();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        categoriesListAdapter = new CategoriesListAdapter( position -> listener.dismiss(getDialog(), position), getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        recyclerView.setAdapter(categoriesListAdapter);
        categoriesListAdapter.setData(categoryItems);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public interface onDismiss {
        void dismiss(Dialog dialog, int position);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.selector_dialog_recyclerview);
        searchEt = view.findViewById(R.id.selector_dialog_search_et);
        clearBtn = view.findViewById(R.id.selector_dialog_clear_btn);
        nothingFoundTxt = view.findViewById(R.id.selector_dialog_nothing_found_txt);
    }

    private void initListeners() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchEt.removeTextChangedListener(this);
                if (s.toString().length() == 0){
                    clearBtn.setVisibility(View.INVISIBLE);
                    nothingFoundTxt.setVisibility(View.INVISIBLE);
                    categoriesListAdapter.setData(categoryItems);
                } else {
                    clearBtn.setVisibility(View.VISIBLE);
                    searchQuery(s.toString());
                }
                searchEt.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clearBtn.setOnClickListener(v -> searchEt.setText(""));
    }


    private void searchQuery(String query) {
        List<CategoryDetails> results = new ArrayList<>();

        for (int i = 0; i < categoryItems.size(); i++){
            Log.e("categoryItem", String.valueOf(i));
            if (Utilities.convertToPersian(categoryItems.get(i).getName().toLowerCase()).contains(Utilities.convertToPersian(query.toLowerCase()))){
                results.add(categoryItems.get(i));
            }
        }

        categoriesListAdapter.setData(results);

        if (results.size() > 0){
            nothingFoundTxt.setVisibility(View.INVISIBLE);
        } else {
            nothingFoundTxt.setVisibility(View.VISIBLE);
        }
    }
}