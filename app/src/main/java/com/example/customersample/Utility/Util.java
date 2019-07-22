package com.example.customersample.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import java.io.Serializable;

/**
 * Created by anildeshpande on 2/21/18.
 */

public class Util {

    public static boolean isAppOnLine(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    public static String getName(String firstName,String lastName, String emailId) {
        String fullName ="";
        if(isNullOrEmpty(firstName) && isNullOrEmpty(lastName)){
            return retrunName(emailId);
        }
        else {
            if(!isNullOrEmpty(firstName) && isNullOrEmpty(lastName))
                return firstName;
            else if(isNullOrEmpty(firstName) && !isNullOrEmpty(lastName))
                return lastName;
            else
                return firstName+" "+lastName;
        }
    }
    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    public static String retrunName(String email){
        int index = email.indexOf('@');
        return email.substring(0,index);

    }

    public static String getPhone(String phone) {
        if(phone != null && !phone.isEmpty())
            return phone;
        return "N/A";
    }
}
