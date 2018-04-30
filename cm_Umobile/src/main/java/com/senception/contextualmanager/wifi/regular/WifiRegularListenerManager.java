package com.senception.contextualmanager.wifi.regular;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) Senception Lda
 * Update to Contextual Manager 2017-2018
 * @author José Soares - jose.soares@senception.com
 * @version 0.1
 *
 * @file holds WifiRegular, to set the Wi-Fi Listener
 */


public class WifiRegularListenerManager {

    private static final String TAG = WifiRegularListenerManager.class.getSimpleName();

    /** This list contains all registered listeners */
    private static List<WifiRegularListener> listeners = new ArrayList<>();

    /**
     * This method registers a listener
     * @param wifiRegularListener listener to be registered
     */
    public static void registerListener(WifiRegularListener wifiRegularListener) {
        Log.i(TAG, "Registering a listener");
        listeners.add(wifiRegularListener);
    }

    /**
     * This method unregisters a listener
     * @param wifiRegularListener listener to be unregistered
     */
    public static void unregisterListener(WifiRegularListener wifiRegularListener) {
        Log.i(TAG, "Unregistering a listener");
        listeners.remove(wifiRegularListener);
    }

    static void notifyConnected() {
        Log.i(TAG, "notifyConnected");
        for(WifiRegularListener listener : listeners) {
            listener.onConnected();
        }
    }

    static void notifyDisconnected() {
        Log.i(TAG, "notifyDisconnected");
        for(WifiRegularListener listener : listeners) {
            listener.onDisconnected();
        }
    }
}
