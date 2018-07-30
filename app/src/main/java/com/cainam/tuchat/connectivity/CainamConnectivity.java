package com.cainam.tuchat.connectivity;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by cainam on 9/6/17.
 */

public class CainamConnectivity extends AppCompatActivity {

    ConnectivityManager cm;
    Context ctx;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cm = (ConnectivityManager) getSystemService(ctx.CONNECTIVITY_SERVICE);
        networkInfo = cm.getActiveNetworkInfo();

    }

    public boolean isConnectivityUp(Context context){

        this.ctx = context;

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){

            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileData = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobileData != null && networkInfo.isConnectedOrConnecting()) || (wifi != null && networkInfo.isConnectedOrConnecting())){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    public AlertDialog.Builder buildAlertDialog(Context context){

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("No network connection");
        dialog.setMessage("You need a mobile data or WIFI Connection to run TuChat APP. Press OK to EXIT");

        dialog.setPositiveButton("Open Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        return dialog;
    }
}
