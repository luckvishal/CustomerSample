package com.example.customersample.Pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class CustomerList implements Serializable {
    private ArrayList<CustomerListPojo> customResponseList;
    public ArrayList<CustomerListPojo> getCustomResponseList() {
        return customResponseList;
    }
    public void setCustomResponseList(ArrayList<CustomerListPojo> customResponseList) {
        this.customResponseList = customResponseList;
    }

}
