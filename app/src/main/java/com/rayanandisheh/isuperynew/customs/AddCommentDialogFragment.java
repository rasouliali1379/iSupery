package com.rayanandisheh.isuperynew.customs;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.app.App;
import com.rayanandisheh.isuperynew.models.api_response_model.ErrorResponse;
import com.rayanandisheh.isuperynew.models.user_model.Nonce;
import com.rayanandisheh.isuperynew.models.user_model.UserData;
import com.rayanandisheh.isuperynew.network.APIClient;
import com.rayanandisheh.isuperynew.utils.ValidateInputs;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

import hyogeun.github.com.colorratingbarlib.ColorRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;

import static android.content.Context.MODE_PRIVATE;

public class AddCommentDialogFragment extends DialogFragment {

    ColorRatingBar  dialog_rating_bar;
    EditText        dialog_author_name;
    EditText        dialog_author_email;
    EditText        dialog_author_message;
    Button          dialog_button;

    private int productId;
    private Context context;
    private SharedPreferences prefs;

    public AddCommentDialogFragment(int productId, Context context) {
        this.productId = productId;
        this.context = context;
        prefs = context.getSharedPreferences("UserInfo", MODE_PRIVATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_comment_dialogfragment, container, false);
        initViews(view);
        initEnvironment();
        initListeners();
        return view;
    }

    private void initEnvironment() {
        if (prefs.getBoolean("isLogged_in", false)){
            dialog_author_name.setVisibility(View.GONE);
            dialog_author_email.setVisibility(View.GONE);
        }
    }

    private void initListeners() {
        dialog_button.setOnClickListener(v -> {
            if (dialog_rating_bar.getRating() < 1.0f){
                if (prefs.getBoolean("isLogged_in", false)) {
                    if (!"".equalsIgnoreCase(dialog_author_message.getText().toString())) {
                        dismiss();
                        getNonceForProductRating
                                (
                                        String.valueOf(productId),
                                        String.valueOf(dialog_rating_bar.getRating()),
                                        prefs.getString("userDisplayName", ""),
                                        prefs.getString("userEmail", "") ,
                                        dialog_author_message.getText().toString().trim()
                                );

                    } else {
                        dialog_author_message.setError(context.getString(R.string.enter_message));
                    }
                } else {
                    if (ValidateInputs.isValidName(dialog_author_name.getText().toString())) {
                        if (ValidateInputs.isValidEmail(dialog_author_email.getText().toString())) {
                            if (!"".equalsIgnoreCase(dialog_author_message.getText().toString())) {

                                dismiss();

                                getNonceForProductRating
                                        (
                                                String.valueOf(productId),
                                                String.valueOf(dialog_rating_bar.getRating()),
                                                dialog_author_name.getText().toString().trim(),
                                                dialog_author_email.getText().toString().trim(),
                                                dialog_author_message.getText().toString().trim()
                                        );

                            } else {
                                dialog_author_message.setError(context.getString(R.string.enter_message));
                            }
                        } else {
                            dialog_author_email.setError(context.getString(R.string.invalid_email));
                        }
                    } else {
                        dialog_author_name.setError(context.getString(R.string.enter_name));
                    }
                }
            } else {
                Toast.makeText(context, "پایین ترین امتیاز یک ستاره است", Toast.LENGTH_LONG);
                dialog_rating_bar.setRating(1.0f);
            }
        });

    }

    private void initViews(View view) {
        dialog_rating_bar = view.findViewById(R.id.dialog_rating_bar);
        dialog_author_name = view.findViewById(R.id.dialog_author_name);
        dialog_author_email = view.findViewById(R.id.dialog_author_email);
        dialog_author_message = view.findViewById(R.id.dialog_author_message);
        dialog_button = view.findViewById(R.id.dialog_button);
    }

    private void getNonceForProductRating(final String productID, final String rate_star, final String a_name, final String a_email, final String a_message) {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("controller", "AndroidAppSettings");
        params.put("method", "android_create_product_review");


        Call<Nonce> call = APIClient.getInstance()
                .getNonce
                        (
                                params
                        );

        call.enqueue(new Callback<Nonce>() {
            @Override
            public void onResponse(Call<Nonce> call, retrofit2.Response<Nonce> response) {

                // Check if the Response is successful
                if (response.isSuccessful()) {

                    String nonce = "";

                    if (response.body().getNonce() != null)
                        nonce = response.body().getNonce();

                    if (!TextUtils.isEmpty(nonce)) {

                        CreateProductReview
                                (
                                        nonce,
                                        productID,
                                        rate_star,
                                        a_name,
                                        a_email,
                                        a_message
                                );

                    }
                    else {
                        Toast.makeText(App.getContext(), "خطایی رخ داد!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(App.getContext(), "خطایی رخ داد!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Nonce> call, Throwable t) {
            }
        });
    }

    public void CreateProductReview(String nonce, String productID, String rate_star, String a_name, String a_email, String a_message) {

        Call<UserData> call = APIClient.getInstance()
                .addProductReview
                        (
                                "cool",
                                nonce,
                                productID,
                                rate_star,
                                a_name,
                                a_email,
                                a_message
                        );

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, retrofit2.Response<UserData> response) {

                if (response.isSuccessful()) {
                    // Show the Response Message

                    if ("ok".equalsIgnoreCase(response.body().getStatus())) {
                        if (response.body().getMessage() != null)
                            Toast.makeText(context, context.getString(R.string.comment_submited), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (response.body().getError() != null)
                            Toast.makeText(context, response.body().getError(), Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Converter<ResponseBody, ErrorResponse> converter = APIClient.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                    ErrorResponse error;
                    try {
                        error = converter.convert(response.errorBody());
                    } catch (IOException e) {
                        error = new ErrorResponse();
                    }

                    Toast.makeText(context, "خطایی رخ داد!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Toast.makeText(context, "خطایی رخ داد!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}