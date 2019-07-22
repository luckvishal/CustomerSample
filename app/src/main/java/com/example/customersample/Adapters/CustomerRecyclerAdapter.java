package com.example.customersample.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.customersample.Activities.BaseActivity;
import com.example.customersample.Activities.CustomerFormActivity;
import com.example.customersample.Activities.MainActivity;
import com.example.customersample.Config.GsonSingleton;
import com.example.customersample.Helper.DatabaseManager;
import com.example.customersample.Pojo.CustomerListPojo;
import com.example.customersample.R;
import com.example.customersample.Utility.Util;
import com.example.customersample.network.AppRestAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerRecyclerAdapter extends RecyclerView.Adapter<CustomerRecyclerAdapter.MyViewHolder> implements Filterable {
    private ArrayList<CustomerListPojo> customerList = null;
    private Context context;
    private ArrayList<CustomerListPojo> customerListFiltered = null;
    String requestBody = "";
    private final ClickListener listener;
    // Provide a suitable constructor (depends on the kind of dataset)
    public CustomerRecyclerAdapter(MainActivity context, ArrayList<CustomerListPojo> customerList, ClickListener listener) {
        this.customerList = customerList;
        this.context = context;
        this.customerListFiltered = customerList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_adapter_list, parent, false);

        return new MyViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final CustomerListPojo customerListPojo = customerListFiltered.get(position);
        holder.customer_name.setText(Util.getName(customerListPojo.getFirstName(),customerListPojo.getLastName(),customerListPojo.getEmailId()));
        holder.customer_email.setText(customerListPojo.getEmailId());
        holder.customer_phone.setText(Util.getPhone(customerListPojo.getPhone()));
    }

    @Override
    public int getItemCount() {
        return customerListFiltered.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView customer_name, customer_email, customer_phone;
        public ImageView del_button;
        private WeakReference<ClickListener> listenerRef;
        public MyViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            customer_name = (TextView) itemView.findViewById(R.id.customer_name);
            customer_email = (TextView) itemView.findViewById(R.id.customer_email);
            customer_phone = (TextView) itemView.findViewById(R.id.customer_phone);
            del_button = itemView.findViewById(R.id.del_button);
            del_button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == del_button.getId()) {
                Toast.makeText(v.getContext(), "ITEM PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    customerListFiltered = customerList;
                } else {
                    ArrayList<CustomerListPojo> filteredList = new ArrayList<>();
                    for(CustomerListPojo row : customerList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (Util.getName(row.getFirstName(),row.getLastName(),row.getEmailId()).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    if (filteredList != null)
                        customerListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = customerListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                customerListFiltered = (ArrayList<CustomerListPojo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public interface ClickListener {

        void onPositionClicked(int position);
    }

}
