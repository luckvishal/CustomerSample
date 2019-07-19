package com.example.customersample;

import com.example.customersample.Pojo.CustomerListPojo;

import java.util.ArrayList;
import java.util.Collections;

public class CustomerSorter {
    ArrayList<CustomerListPojo> customerList = new ArrayList<>();
    public CustomerSorter(ArrayList<CustomerListPojo> customerList) {
        this.customerList = customerList;
    }

    public ArrayList<CustomerListPojo> getSortedJobCustomerByName() {
        Collections.sort(customerList, CustomerListPojo.nameComparator);
        return customerList;
    }
}
