package com.rayanandisheh.isuperynew.customs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.activities.Login;
import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.adapters.ProductReviewsAdapter;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.models.product_model.ProductDetails;
import com.rayanandisheh.isuperynew.models.product_model.ProductReviews;

import java.util.List;

public abstract class CommentsDialog extends Dialog {

    TextView        average_rating;
    TextView        total_rating_count;
    ProgressBar     rating_progress_5;
    ProgressBar     rating_progress_4;
    ProgressBar     rating_progress_3;
    ProgressBar     rating_progress_2;
    ProgressBar     rating_progress_1;
    Button          rate_product_button;
    ImageView       dialog_back_button;
    RecyclerView    reviews_list_recycler;

    Context context;
    private ProductDetails productDetails;
    private List<ProductReviews> productReviews;

    public CommentsDialog(@NonNull Context context, ProductDetails productDetails, List<ProductReviews> productReviews) {
        super(context);
        this.context = context;
        this.productDetails = productDetails;
        this.productReviews = productReviews;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setContentView(R.layout.fragment_comments_dialog);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        initViews();
        initListners();

        int rating_1_count = 0;
        int rating_2_count = 0;
        int rating_3_count = 0;
        int rating_4_count = 0;
        int rating_5_count = 0;


        // Bind Dialog Views


//        reviews_list_recycler.setNestedScrollingEnabled(false);
//        ViewCompat.setNestedScrollingEnabled(reviews_list_recycler, false);

        average_rating.setText(productDetails.getAverageRating());
        total_rating_count.setText(String.valueOf(productDetails.getRatingCount()));

        rate_product_button.setVisibility(productDetails.isReviewsAllowed()? View.VISIBLE : View.GONE);

        rating_progress_1.setMax(productDetails.getRatingCount());
        rating_progress_2.setMax(productDetails.getRatingCount());
        rating_progress_3.setMax(productDetails.getRatingCount());
        rating_progress_4.setMax(productDetails.getRatingCount());
        rating_progress_5.setMax(productDetails.getRatingCount());

        for (int i=0;  i<productReviews.size();  i++) {
            if (productReviews.get(i).getRating() == 1)
                rating_1_count += 1;
            else if (productReviews.get(i).getRating() == 2)
                rating_2_count += 1;
            else if (productReviews.get(i).getRating() == 3)
                rating_3_count += 1;
            else if (productReviews.get(i).getRating() == 4)
                rating_4_count += 1;
            else if (productReviews.get(i).getRating() == 5)
                rating_5_count += 1;
        }

        rating_progress_1.setProgress(rating_1_count);
        rating_progress_2.setProgress(rating_2_count);
        rating_progress_3.setProgress(rating_3_count);
        rating_progress_4.setProgress(rating_4_count);
        rating_progress_5.setProgress(rating_5_count);

        // Initialize the ReviewsAdapter for RecyclerView
        ProductReviewsAdapter reviewsAdapter = new ProductReviewsAdapter(getContext(), productReviews);

        // Set the Adapter and LayoutManager to the RecyclerView
        reviews_list_recycler.setAdapter(reviewsAdapter);
        reviews_list_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        reviewsAdapter.notifyDataSetChanged();




    }

    private void initListners() {

        rate_product_button.setOnClickListener(v -> {
            if (ConstantValues.IS_USER_LOGGED_IN) {
                showRateProductDialog();
            }
            else {
                getContext().startActivity(new Intent(getContext(), Login.class));
                ((MainActivity) context).finish();
                ((MainActivity) context).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }
        });


        dialog_back_button.setOnClickListener(v -> dismiss());
    }

    private void initViews() {
        average_rating          = findViewById(R.id.average_rating);
        total_rating_count      = findViewById(R.id.total_rating_count);
        rating_progress_5       = findViewById(R.id.rating_progress_5);
        rating_progress_4       = findViewById(R.id.rating_progress_4);
        rating_progress_3       = findViewById(R.id.rating_progress_3);
        rating_progress_2       = findViewById(R.id.rating_progress_2);
        rating_progress_1       = findViewById(R.id.rating_progress_1);
        rate_product_button     = findViewById(R.id.rate_product);
        dialog_back_button      = findViewById(R.id.dialog_button);
        reviews_list_recycler   = findViewById(R.id.reviews_list_recycler);
    }


    public abstract void showRateProductDialog();
}