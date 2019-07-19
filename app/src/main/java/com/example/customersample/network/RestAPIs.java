package com.example.customersample.network;


import com.example.customersample.Config.AppConfig;

public class RestAPIs {
    public static String getBaseUrl(){
        if(AppConfig.selectedEndPoint== AppConfig.API_ENDPOINTS.localhost){
            return AppRestAPI.baseLocalHostUrl;
        }else if (AppConfig.selectedEndPoint== AppConfig.API_ENDPOINTS.remote){
            return AppRestAPI.baseRemoteUrl;
        }else {
            return AppRestAPI.baseRemoteUrl;
        }
    }
}
