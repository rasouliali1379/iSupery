package com.rayanandisheh.isuperynew.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.ColorRes;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.graphics.drawable.DrawableCompat;

import com.rayanandisheh.isuperynew.BuildConfig;
import com.rayanandisheh.isuperynew.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.rayanandisheh.isuperynew.constant.ConstantValues;


/**
 * Utilities class has different static methods, that can be implemented in any other class
 **/

public class Utilities {




    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();

        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isNetworkAvailable(Activity activity) {

        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Network[] networks = connectivity.getAllNetworks();
            NetworkInfo networkInfo;

            for (Network mNetwork : networks) {

                networkInfo = connectivity.getNetworkInfo(mNetwork);

                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }

        }
        else {
            if (connectivity != null) {

                NetworkInfo[] info = connectivity.getAllNetworkInfo();

                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
    
    //*********** Returns the current DataTime of Device ********//

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        return dateFormat.format(date);
    }

    public static String convertToPersian(String value)
    {
        String newValue = (((((((((((value+"")
                .replaceAll("1", "١")).replaceAll("2", "٢"))
                .replaceAll("3", "٣")).replaceAll("4", "٤"))
                .replaceAll("5", "٥")).replaceAll("6", "٦"))
                .replaceAll("7", "٧")).replaceAll("8", "٨"))
                .replaceAll("9", "٩")).replaceAll("0", "٠"));
        return newValue;
    }

    public static String convertToEnglish(String value)
    {
        String newValue = (((((((((((value+"")
                .replaceAll("١", "1")).replaceAll("٢", "2"))
                .replaceAll("٣", "3")).replaceAll("٤", "4"))
                .replaceAll("٥", "5")).replaceAll("٦", "6"))
                .replaceAll("٧", "7")).replaceAll("٨", "8"))
                .replaceAll("٩", "8")).replaceAll("٠", "0"));
        return newValue;
    }

    public static String removeHtmlTags(String htmlContent){
        return htmlContent.replaceAll("\\<.*?\\>", "");
    }

    //*********** Used to Tint the MenuIcons ********//

    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));

        item.setIcon(wrapDrawable);
    }

    //*********** Checks if the Date is not Passed ********//

    public static boolean checkIsDatePassed(String givenDate) {

        boolean isPassed = false;

        Date dateGiven, dateSystem;

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(calender.getTime());

        try {
            dateSystem = dateFormat.parse(currentDate);
            dateGiven = dateFormat.parse(givenDate.replaceAll("[a-zA-Z]", " "));

            if (dateSystem.getTime() >= dateGiven.getTime()) {
                isPassed = true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.i("exception", "ParseException= "+e);
        }
        

        return isPassed;
    }



    //*********** Used to Calculate the Discount Percentage between New and Old Prices ********//
    /*
    public static String checkDiscount(String actualPrice, String discountedPrice) {

        if (discountedPrice == null) {
            discountedPrice = actualPrice;
        }

        Double oldPrice = Double.parseDouble(actualPrice);
        Double newPrice = Double.parseDouble(discountedPrice);

        double discount = (oldPrice - newPrice)/oldPrice * 100;

        return (discount > 0) ? Math.round(discount) +"% " + App.getContext().getString(R.string.OFF) : null;
    }
    */
    
    
    //*********** Used to Share the App with Others ********//
    
    public static void rateMyApp(final Context context) {
    
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.dialog_info, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
    
        final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
        final TextView dialog_message = (TextView) dialogView.findViewById(R.id.dialog_message);
        final Button dialog_button_positive = (Button) dialogView.findViewById(R.id.dialog_button_positive);
        final Button dialog_button_negative = (Button) dialogView.findViewById(R.id.dialog_button_negative);
    
        dialog_title.setText(context.getString(R.string.rate_app));
        dialog_message.setText(context.getString(R.string.rate_app_msg));
        dialog_button_positive.setText(context.getString(R.string.rate_now));
        dialog_button_negative.setText(context.getString(R.string.not_now));
    
    
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    
        dialog_button_negative.setOnClickListener(v -> alertDialog.dismiss());
    
        dialog_button_positive.setOnClickListener(v -> {
            alertDialog.dismiss();

            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

            try {
                context.startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "Couldn't launch the market", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    
    
    //*********** Used to Share the App with Others ********//

    public static void shareMyApp(Context context) {
    
        String link = "فروشگاه آنلاین " + ConstantValues.APP_NAME + " : \n" + ConstantValues.WOOCOMMERCE_URL;
        
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, link);
        context.startActivity(Intent.createChooser(sharingIntent, "اشتراک گذاری با ..."));
    }



    //*********** Shares the Product with its Image and Url ********//

    public static void shareProduct(Context context, String subject, ImageView imageView, String url) {

        Uri bmpUri = getLocalBitmapUri(context, imageView);

        if (bmpUri != null) {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, url);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

            context.startActivity(Intent.createChooser(shareIntent, "اشتراک گذاری با ..."));

        } else {
            Toast.makeText(context, "Null bmpUri", Toast.LENGTH_SHORT).show();
        }
    }



    //*********** Convert Bitmap into Uri ********//

    public static Uri getLocalBitmapUri(Context context, ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap = null;
        
        if (drawable instanceof BitmapDrawable){
            bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }
        else {
            Toast.makeText(context, "drawable isn't instanceof BitmapDrawable", Toast.LENGTH_SHORT).show();
            return null;
        }
        
        // Store image to default external storage directory
        Uri bitmapUri = null;
        
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            
//            bitmapUri = Uri.fromFile(file);
            bitmapUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "IOException"+e, Toast.LENGTH_SHORT).show();
        }
        
        
        return bitmapUri;
    }


    public static void changeLocale(Context context) {
        Configuration config = context.getResources().getConfiguration();
        String lang = "fa";
        if (!(config.locale.getLanguage().equals(lang))) {
            Locale locale = new Locale("fa");
            Locale.setDefault(locale);
            config.locale = locale;
            config.setLayoutDirection(locale);
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

}

