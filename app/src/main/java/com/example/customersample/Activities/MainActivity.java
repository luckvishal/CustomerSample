package com.example.customersample.Activities;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.customersample.CustomerSorter;
import com.example.customersample.Helper.DatabaseManager;
import com.example.customersample.Pojo.CustomerListPojo;
import com.example.customersample.R;
import com.example.customersample.Utility.Util;
import com.example.customersample.network.AppRestAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity  {
    public String requestBody;
    ArrayList<CustomerListPojo> customerList;
    CustomerRecyclerAdapter customerRecyclerAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchView searchView;
    CustomerSorter customerSorter;
    DatabaseManager mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.toolbar_title);
        recyclerView = findViewById(R.id.customer_recycler);

        mDatabase = new DatabaseManager(MainActivity.this);
        customerList = new ArrayList<CustomerListPojo>();
        customerSorter = new CustomerSorter(customerList);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent customerFormIntent = new Intent(MainActivity.this,CustomerFormActivity.class);
                startActivity(customerFormIntent);
            }
        });
        if(Util.isAppOnLine(MainActivity.this))
        customResponseObject();

    }
    private void sortCustomerList()throws Exception {
        System.out.println("-----Sorted customerSorter by name: Ascending-----");
        ArrayList<CustomerListPojo> sortedJobCandidate = customerSorter.getSortedJobCustomerByName();
        customerList = sortedJobCandidate;
        customerRecyclerAdapter.notifyDataSetChanged();
    }

        private void customResponseObject() {
        showBusyDialog("Loading");
        requestBody = null;
        JsonArrayRequest jor = new JsonArrayRequest(Request.Method.GET, AppRestAPI.baseRemoteUrl + AppRestAPI.getCustomerUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                Type collectionType = new TypeToken<ArrayList<CustomerListPojo>>(){}.getType();
                customerList = gson.fromJson(String.valueOf(response), collectionType);

                customerRecyclerAdapter = new CustomerRecyclerAdapter(MainActivity.this,customerList, new CustomerRecyclerAdapter.ClickListener() {
                    @Override
                    public void onPositionClicked(int position) {
                        if(Util.isAppOnLine(MainActivity.this))
                        deleteCustomer(customerList.get(position));
                    }
                });
                recyclerView.setAdapter(customerRecyclerAdapter);
                customerRecyclerAdapter.notifyDataSetChanged();
                customerSorter = new CustomerSorter(customerList);
                insertCustomer();
                dismissBusyDialog();
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

    private void insertCustomer() {
        SQLiteDatabase db = mDatabase.getWritableDatabase();
        String count = "SELECT count(*) FROM table";
        Cursor mcursor = mDatabase.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getCount();
        if(icount>0)
            saveIntoLocalData();
            //leave
        else
            return;
    }

    private void saveIntoLocalData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                customerRecyclerAdapter.getFilter().filter(query);
                customerRecyclerAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                customerRecyclerAdapter.getFilter().filter(query);
                customerRecyclerAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        if (id == R.id.action_sort) {
            try {
                sortCustomerList();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteCustomer(final CustomerListPojo customerListPojo) {

        requestBody = null;
        showBusyDialog("Loading");
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.DELETE, AppRestAPI.baseRemoteUrl + "id/"+customerListPojo.getNumber()+AppRestAPI.delCustomerUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismissBusyDialog();
                    if (!Util.isNullOrEmpty(response.get("number").toString())) {
                        customerList.remove(customerListPojo);
                        customerRecyclerAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Customer Deleted successfully!", Toast.LENGTH_SHORT).show();
                        //calling the delete method from the database manager instance
                        if (mDatabase.deleteCustomer(customerListPojo.getNumber())){

                        }

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
                Toast.makeText(MainActivity.this, "Error",Toast.LENGTH_SHORT).show();
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
        GsonSingleton.getInstance(MainActivity.this).addToRequestQueue(jor);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


}
