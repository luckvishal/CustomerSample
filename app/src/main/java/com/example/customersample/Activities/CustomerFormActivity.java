package com.example.customersample.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.customersample.Adapters.CustomerRecyclerAdapter;
import com.example.customersample.Config.GsonSingleton;
import com.example.customersample.Helper.DatabaseManager;
import com.example.customersample.Pojo.CustomerListPojo;
import com.example.customersample.R;
import com.example.customersample.Utility.Util;
import com.example.customersample.network.AppRestAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerFormActivity extends BaseActivity {
    EditText first_name, middle_name, last_name, customer_phone, customer_email;
    Button chekin_btn;
    private String requestBody;
    DatabaseManager mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_form);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_form);
        //initializing the database manager object
        mDatabase = new DatabaseManager(CustomerFormActivity.this);

        first_name = findViewById(R.id.first_name);
        middle_name = findViewById(R.id.middle_name);
        last_name = findViewById(R.id.last_name);
        customer_phone = findViewById(R.id.customer_phone);
        customer_email = findViewById(R.id.customer_email);

        chekin_btn = findViewById(R.id.chekin_btn);
        chekin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.isNullOrEmpty(customer_email.getText().toString())){
                    if(Util.isAppOnLine(CustomerFormActivity.this))
                    addCustomerData();
                }
            }
        });

    }

    private void addCustomerData() {
        showBusyDialog("Loading");
        requestBody = ""+addCustomerJsonRequest();
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, AppRestAPI.baseRemoteUrl + AppRestAPI.addCustomerUrl, addCustomerJsonRequest(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (!Util.isNullOrEmpty(response.get("customerid").toString())) {
                        if(mDatabase.addCustomer(Integer.parseInt(response.get("customerid").toString()),first_name.getText().toString(), middle_name.getText().toString(), last_name.getText().toString(),customer_email.getText().toString(),customer_phone.getText().toString()));
                        Toast.makeText(CustomerFormActivity.this, "Customer added successfully!", Toast.LENGTH_SHORT).show();
                        dismissBusyDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissBusyDialog();
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //Log.i("Error", errorMessage);
                Toast.makeText(getApplicationContext(), "Error",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){

            //here we set the parsing method
            @Override
            public String getBodyContentType() {
                return "application/application/json; charset=UTF-8";
            }
            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("content-type","application/json");
                headers.put("accept","application/json");
                return headers;
            }

        };

//Set time out for volley api calling
        jor.setRetryPolicy(new

                DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GsonSingleton.getInstance(getApplicationContext()).addToRequestQueue(jor);

    }

    //Method to create json object and send it to api to get the json data
    private JSONObject addCustomerJsonRequest() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name", Util.getName(first_name.getText().toString(),last_name.getText().toString(),customer_email.getText().toString()));
            jsonObject.put("emailId", customer_email.getText().toString());
            jsonObject.put("phone", customer_phone.getText().toString());
            jsonObject.put("smsEnabled", 1);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        onBackPressed();
        super.onBackPressed();
    }
}
