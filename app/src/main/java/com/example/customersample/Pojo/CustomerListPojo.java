
package com.example.customersample.Pojo;

import com.example.customersample.Utility.Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;

public class CustomerListPojo implements Serializable {

    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("middleName")
    @Expose
    private String middleName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("emailId")
    @Expose
    private String emailId;
    @SerializedName("phone")
    @Expose
    private Object phone;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public static Comparator<CustomerListPojo> nameComparator = new Comparator<CustomerListPojo>() {
        @Override
        public int compare(CustomerListPojo jc1, CustomerListPojo jc2) {
            return (int) (Util.getName(jc1.getFirstName(),jc1.getLastName(),jc1.getEmailId()).compareTo(Util.getName(jc2.getFirstName(),jc2.getLastName(),jc2.getEmailId())));
        }
    };

}
