package com.rayanandisheh.isuperynew.utils;

import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;

import com.rayanandisheh.isuperynew.app.App;


/**
 * CheckPermissions can be used to check different Permissions
 **/

public class CheckPermissions {
    
    public static final int PERMISSIONS_REQUEST_CAMERA = 300;
    public static final int PERMISSIONS_REQUEST_LOCATION = 400;

    public static boolean is_STORAGE_PermissionGranted() {
    
        if (Build.VERSION.SDK_INT >= 23) {
            return  (App.getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }
        else {
            // Permission is granted by default
            return true;
        }
        
    }
    
}

