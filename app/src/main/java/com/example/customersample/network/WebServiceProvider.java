package com.example.customersample.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class WebServiceProvider {
    private static final String TAG = WebServiceProvider.class.getSimpleName();

    private RequestQueue requestQueue;

    public static WebServiceProvider instance;

    private WebServiceProvider(Context context){
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
    }

    public static WebServiceProvider getInstance(Context context) {
        if(instance==null){
            instance = new WebServiceProvider(context);
        }
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> request){
        requestQueue.add(request);
    }

    public void clearCache(){
        if(requestQueue!=null){
            requestQueue.getCache().clear();
            Log.i("VolleyDemo","Clearing the cache");
        }
    }
}
