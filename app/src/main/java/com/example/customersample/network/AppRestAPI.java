package com.example.customersample.network;
public interface AppRestAPI {
    String baseLocalHostUrl = "http://10.0.2.2:8080/dotolist/webapi";
    String baseRemoteUrl = "https://api.birdeye.com/resources/v1/customer/";
    String getCustomerUrl = "all?businessId=154297123507363&api_key=uu7qrdki2HkQ7H7yf3diI92dkQ6uy7Hd";
    String addCustomerUrl = "checkin?bid=154297123507363&api_key=uu7qrdki2HkQ7H7yf3diI92dkQ6uy7Hd";
    String delCustomerUrl = "?api_key=uu7qrdki2HkQ7H7yf3diI92dkQ6uy7Hd";

}
