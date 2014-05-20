package com.doridori.lib.io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * <p>A receiver for checking network status changes.</p>
 *
 * <p>Make sure to set <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> in your manifest</p>
 *
 * <p>This can be useful to reg in a base activity for your networked app if you are using in multiple places</p>
 *
 * <ul>
 *     <li>in onStart() - registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));</li>
 *     <li>in onStop() - unregisterReceiver(mConnectivityReceiver);</li>
 * </ul>
 *
 * <p>Also in your base activity you could for example show croutons for network state and have this behaviour enabled / disabled
 * by your subclasses setting a flag </p>
 *
 * <p><b>make sure to read the doc for {@link #register(android.content.Context)} also before using</b></p>
 *
 * User: doriancussen
 * Date: 31/10/2012
 */
public class ConnectivityReceiver extends BroadcastReceiver{

    private final ConnectivityListener mConnectivityListener;

    public ConnectivityReceiver(ConnectivityListener connectivityListener){
        mConnectivityListener = connectivityListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            mConnectivityListener.onConnectionAvailable();
        }else{
            mConnectivityListener.onConnectionUnavailable();
        }
    }


    /**
     * You should get a callback pretty quickly after registering as the broadcasts are STICKY. This is not documented however
     * so just in case this is made NON-STICKY in the future (which I doubt will happen) you should use the callbacks to inform the user
     * but maybe not performing blocking behaviour (or just be aware this may change and if do perform a manual network state check when this method is called)
     *
     * @param context
     */
    public void register(Context context){
        context.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void unregister(Context context){
        context.unregisterReceiver(this);
    }

    public interface ConnectivityListener {

        /**
         * Called when a data connection has been established. Can use to
         * trigger any waiting behaviour
         */
        public void onConnectionAvailable();
        public void onConnectionUnavailable();
    }
}