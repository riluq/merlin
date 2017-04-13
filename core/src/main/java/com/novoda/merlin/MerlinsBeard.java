package com.novoda.merlin;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import com.novoda.merlin.service.AndroidVersion;

/**
 * This class provides a mechanism for retrieving the current
 * state of a network connection given an application context.
 */
public class MerlinsBeard {

    private final ConnectivityManager connectivityManager;
    private final AndroidVersion androidVersion;

    /**
     * Use this method to create a MerlinsBeard object, this is how you can retrieve the current network state.
     *
     * @param context pass any context application or activity.
     * @return MerlinsBeard.
     */
    public static MerlinsBeard from(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        AndroidVersion androidVersion = new AndroidVersion();
        return new MerlinsBeard(connectivityManager, androidVersion);
    }

    MerlinsBeard(ConnectivityManager connectivityManager, AndroidVersion androidVersion) {
        this.connectivityManager = connectivityManager;
        this.androidVersion = androidVersion;
    }

    /**
     * Provides a boolean representing whether a network connection has been established.
     * NOTE: Therefore available does not necessarily mean that an internet connection
     * is available.
     *
     * @return boolean true if a network connection is available.
     */
    public boolean isConnected() {
        NetworkInfo activeNetworkInfo = getNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private NetworkInfo getNetworkInfo() {
        return connectivityManager.getActiveNetworkInfo();
    }

    /**
     * Provides a boolean representing whether a mobile network connection has been established and is active.
     * NOTE: Therefore available does not necessarily mean that an internet connection
     * is available. Also, there can be only one network connection at a time, so this would return false if
     * the active connection is the Wi-Fi one, even if there is a (inactive) mobile network connection established.
     *
     * @return boolean true if a mobile network connection is available.
     */
    public boolean isConnectedToMobileNetwork() {
        return isConnectedTo(ConnectivityManager.TYPE_MOBILE);
    }

    private boolean isConnectedTo(int networkType) {
        if (androidVersion.isMarshmallowOrHigher()) {
            return connectedToNetworkTypeForMarshmallow(networkType);
        } else {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(networkType);
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    /**
     * Provides a boolean representing whether a Wi-Fi network connection has been established.
     * <p/>
     * NOTE: Therefore available does not necessarily mean that an internet connection
     * is available.
     *
     * @return boolean true if a Wi-Fi network connection is available.
     */
    public boolean isConnectedToWifi() {
        return isConnectedTo(ConnectivityManager.TYPE_WIFI);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean connectedToNetworkTypeForMarshmallow(int networkType) {
        Network[] networks = connectivityManager.getAllNetworks();

        for (Network network : networks) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);

            if (networkInfo.getType() == networkType) {
                return networkInfo.getState() == NetworkInfo.State.CONNECTED;
            }

        }

        return false;
    }

    /**
     * Provides a human-readable String describing the network subtype (e.g. UMTS, LTE) when connected to a mobile network.
     *
     * @return network subtype name, or empty string if not connected to a mobile network.
     */
    public String getMobileNetworkSubtypeName() {
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            return "";
        }
        return networkInfo.getSubtypeName();
    }

}
