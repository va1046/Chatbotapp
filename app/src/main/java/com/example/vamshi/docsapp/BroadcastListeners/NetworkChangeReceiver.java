package com.example.vamshi.docsapp.BroadcastListeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.example.vamshi.docsapp.Util.NetworkUtil;

/**
 * Created by vamshi on 20-12-2016.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    NetworkChangeInterface networkChangeInterface = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.networkChangeInterface = (NetworkChangeInterface) context;
        boolean status = NetworkUtil.getConnectivityStatusString(context);
        Log.d("network reciever", "network reciever");
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if(status){
                Log.d("network reciever 1 ", "network reciever");
                networkChangeInterface.networkavailablelistener();
            }else{
                Log.d("network reciever 2 ", "network reciever");
                networkChangeInterface.networknotavailablelistener();
            }
        }
    }

    public interface NetworkChangeInterface{
        void networkavailablelistener();
        void networknotavailablelistener();
    }
}