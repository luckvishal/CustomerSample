package com.example.customersample.Config;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.customersample.network.WebServiceProvider;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class AppConfig extends Application {

    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;

    public static API_ENDPOINTS selectedEndPoint;
    public boolean isEmulator;
    private static Context context;
    private static WebServiceProvider webServiceProvider;
    public static enum API_ENDPOINTS{
        localhost, remote
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("appprefrences.xml",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        context = getApplicationContext();
        webServiceProvider = WebServiceProvider.getInstance(context);
        /**
         * Enables https connections
         */
        handleSSLHandshake();

    }
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
    public static void saveUserName(String username){
        editor.putString("username",username);
        editor.commit();
    }

    public static void saveSuccessfulLoginUser(String jsonString){
        editor.putString("user",jsonString);
        editor.commit();
    }


    public static void saveSessionTokenValue(String token){
        editor.putString("token",token);
        editor.commit();
    }

    public static String getSessionTokenValue(){
        return sharedPreferences.getString("token",null);
    }

    public static  String getSavedUserName(){
        return sharedPreferences.getString("username",null);
    }

    public static void savePassword(String password){
        editor.putString("password",password);
        editor.commit();
    }

    public static void saveToBeDeletedToDoId(long id){
        editor.putLong("tobedeleted",id);
        editor.commit();
    }

    public static long getToBeDeletedToDoId(){
        return sharedPreferences.getLong("tobedeleted",0);
    }

    public static String getSavedPassword(){
        return sharedPreferences.getString("password",null);
    }


    public static String getSessionId(){
        return sharedPreferences.getString("sessionId",null);
    }

    public static void saveSessionId(String sessionId){
        editor.putString("sessionId",sessionId);
        editor.commit();
    }

    public static void setServerEndPointPreference(boolean endPoint){
        editor.putBoolean("endpoint",endPoint);
        editor.commit();
    }

    public static boolean getSeverEndPointPreference(){
        return sharedPreferences.getBoolean("endpoint", true);
    }

    public static Context getContext(){
        return context;
    }
    public static WebServiceProvider getWebServiceProvider(){
        return webServiceProvider;
    }
}