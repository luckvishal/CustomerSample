package com.example.customersample.Activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.example.customersample.Dialog.CustomProgressDialog;

public class BaseActivity extends AppCompatActivity {

    DialogFragment dialogFragment;

    public void showBusyDialog(String message){
        if(dialogFragment!=null && dialogFragment.isVisible()){
            dialogFragment.dismiss();
        }
        dialogFragment = new CustomProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putString("message",message);
        dialogFragment.setArguments(bundle);
        dialogFragment.setCancelable(false);
        dialogFragment.show(getFragmentManager(),"busyDialog");
    }

    public  void dismissBusyDialog(){
        if(dialogFragment.isVisible()){
            dialogFragment.dismiss();
        }
    }

    public void toastMessage(String message, int toastTiming){
        Toast.makeText(this,message,toastTiming).show();
    }
}
