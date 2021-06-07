package com.rayanandisheh.isuperynew.utils;


import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * ValidateInputs class has different static methods, to validate different types of user Inputs
 **/

public class ValidateInputs {
    
    private static String blockCharacters = "[$&+~;=\\\\?|/'<>^*%!-]";
    
    
    //*********** Validate Email Address ********//
    
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    
    
    //*********** Validate Name Input ********//
    
    public static boolean isValidName(String name) {
        String regExpn = "^([a-zA-Z ]{1,24})+$";
        if (name.equalsIgnoreCase(""))
            return false;
        
        CharSequence inputStr = name;
        Pattern pattern = Pattern.compile(blockCharacters, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        
        return !pattern.matcher(inputStr).find();
    }
    
    
    
    //*********** Validate Password Input ********//
    
    public static boolean isValidPassword(String password) {
        
        String regExpn = "^[a-z0-9_$@.!%*?&]{6,24}$";
        CharSequence inputStr = password;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        
        return matcher.matches();
    }
    
    //*********** Validate Any Input ********//
    
    public static boolean isValidInput(String input) {


        String regExpn = "(.*?)?((?:[a-z][a-z]+))";
        if (input.equalsIgnoreCase(""))
            return false;
        
        CharSequence inputStr = input;
        Pattern pattern = Pattern.compile(blockCharacters, Pattern.CASE_INSENSITIVE);

        return !pattern.matcher(inputStr).find();
    }

    public static boolean hasPersianChar(String input) {
        final char [] persianChar = {'آ' ,'ا','ب' ,'پ' ,'ت' ,'ث' ,'ج' ,'چ' ,'ح' ,'خ' ,'د' ,'ذ' ,'ر' ,'ز' ,'ژ' ,'س' ,'ش' ,'ص' ,'ض','ط' ,'ظ' ,'ع' ,'غ' ,'ف' ,'ق' ,'ک' ,'گ' ,'ل' ,'م' ,'ن' ,'و' ,'ه' ,'ی'};

        for (int i = 0 ; i < persianChar.length; i++){
            if (input.indexOf(persianChar[i]) > -1){
                return true;
            }
        }
        return false;
    }


    
    //*********** Validate Any Input ********//
    
    public static boolean isIfValidInput(String input) {
        CharSequence inputStr = input;
        Pattern pattern = Pattern.compile(blockCharacters, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        
        return !pattern.matcher(inputStr).find();
    }
    
}

