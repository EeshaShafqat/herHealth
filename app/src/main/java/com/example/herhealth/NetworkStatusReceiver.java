package com.example.herhealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatusReceiver extends BroadcastReceiver {

    private static NetworkStatusListener listener;

    public interface NetworkStatusListener {
        void onNetworkStatusChanged(Status status);
    }

    public enum Status {
        CONNECTED,
        DISCONNECTED
    }

    public static void setListener(NetworkStatusListener networkStatusListener) {
        listener = networkStatusListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener != null) {
            if (isConnected(context)) {
                listener.onNetworkStatusChanged(Status.CONNECTED);
            } else {
                listener.onNetworkStatusChanged(Status.DISCONNECTED);
            }
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
