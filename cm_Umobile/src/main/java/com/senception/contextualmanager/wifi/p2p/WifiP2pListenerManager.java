/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/8.
 * Class is part of the NSense application.
 */

package com.senception.contextualmanager.wifi.p2p;


import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 2017 COPELABS/ULHT
 * Update to Contextual Manager 2018 Senception Lda
 * @author Miguel Tavares (COPELABS/ULHT)
 * @author José Soares (Senception Lda) - jose.soares@senception.com
 * @version 1.1, 2018
 *
 * @file responsible for managing the wifi p2p listeners.
 * RegisterResult, unregisterListener and notify them.
 */
public abstract class WifiP2pListenerManager {

    /** This variable is used to debug WifiP2pListenerManager class */
    private static final String TAG = "WifiP2pListenerManager";

    /** This list contains all registered listeners */
    private static List<WifiP2pListener> listeners = new ArrayList<>();

    /**
     * This method registers a listener
     * @param wifiP2pListener listener to be registered
     */
    public static void registerListener(WifiP2pListener wifiP2pListener) {
        Log.i(TAG, "Registering a listener");
        listeners.add(wifiP2pListener);
    }

    /**
     * This method unregisters a listener
     * @param wifiP2pListener listener to be unregistered
     */
    public static void unregisterListener(WifiP2pListener wifiP2pListener) {
        Log.i(TAG, "Unregistering a listener");
        listeners.remove(wifiP2pListener);
    }

    /**
     * This method notifies all ServiceAvailable listeners
     */
    static void notifyServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
        Log.i(TAG, "notifyServiceAvailable");
        for(WifiP2pListener listener : listeners) {
            if(listener instanceof WifiP2pListener.ServiceAvailable)
                ((WifiP2pListener.ServiceAvailable)listener).onServiceAvailable(instanceName, registrationType, srcDevice);
        }
    }

    /**
     * This method notifies all TxtRecordAvailable listeners
     */
    static void notifyTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        Log.i(TAG, "notifyTxtRecordAvailable");
        for(WifiP2pListener listener : listeners) {
            if(listener instanceof WifiP2pListener.TxtRecordAvailable)
                ((WifiP2pListener.TxtRecordAvailable)listener).onTxtRecordAvailable(fullDomainName, txtRecordMap, srcDevice);
        }
    }

    /**
     * This method notifies all PeersAvailable listeners
     */
    static void notifyPeersAvailable(WifiP2pDeviceList peers) {
        Log.i(TAG, "notifyPeersAvailable");
        for(WifiP2pListener listener : listeners) {
            if(listener instanceof  WifiP2pListener.PeersAvailable) {
                ((WifiP2pListener.PeersAvailable)listener).onPeersAvailable(peers);
            }
        }
    }

    static void notifyConnected(Intent intent) {
        Log.i(TAG, "Wi-Fi connection established");
        for(WifiP2pListener listener : listeners) {
            if(listener instanceof WifiP2pListener.WifiP2pConnectionStatus) {
                ((WifiP2pListener.WifiP2pConnectionStatus)listener).onConnected(intent);
            }
        }
    }

    static void notifyDisconnected(Intent intent) {
        Log.i(TAG, "Wi-Fi connection dropped down");
        for(WifiP2pListener listener : listeners) {
            if(listener instanceof WifiP2pListener.WifiP2pConnectionStatus) {
                ((WifiP2pListener.WifiP2pConnectionStatus)listener).onDisconnected(intent);
            }
        }
    }

    static void notifyGoIpAddressAvailable(String ipAddress) {
        Log.i(TAG, "Group Owner ip address is " + ipAddress);
        for(WifiP2pListener listener : listeners) {
            if(listener instanceof WifiP2pListener.GoIpAvailable) {
                ((WifiP2pListener.GoIpAvailable)listener).onGoIpAddressAvailable(ipAddress);
            }
        }
    }

    static void notifyIamGo() {
        Log.i(TAG, "I'm Go!!");
        for(WifiP2pListener listener : listeners) {
            if(listener instanceof WifiP2pListener.GoIpAvailable) {
                ((WifiP2pListener.GoIpAvailable)listener).onIamGo();
            }
        }
    }

}
