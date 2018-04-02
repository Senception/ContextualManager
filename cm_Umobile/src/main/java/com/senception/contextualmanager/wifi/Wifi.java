/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/7.
 * Class is part of the NSense application.
 */

package com.senception.contextualmanager.wifi;

import android.content.Context;
import android.util.Log;

import com.senception.contextualmanager.wifi.p2p.WifiP2p;
import com.senception.contextualmanager.wifi.regular.WifiRegular;

/**
 * 2017 COPELABS/ULHT
 * Update to Contextual Manager 2018 Senception Lda
 * @author Miguel Tavares (COPELABS/ULHT)
 * @author José Soares (Senception Lda) - jose.soares@senception.com
 * @version 1.1, 2018
 *
 * @file This class is responsible for discover wifi p2p devices, services
 * and also text records which are being announced.
 */
public class Wifi {

    /** This variable is used to debug Wifi class */
    private static final String TAG = "Wifi";

    /** This object is used to instantiate the wifi p2p features */
    private WifiP2p mWifiP2p;

    private WifiRegular mWifiRegular;

    public Wifi(Context context) {
        mWifiP2p = new WifiP2p(context);
        mWifiRegular = new WifiRegular(context);
    }

    public void start() {
        mWifiP2p.start();
        mWifiRegular.start();
    }

    /**
     * This method closes all features related with this class
     */
    public void close() {
        Log.i(TAG, "Closing WifiRegular");
        mWifiP2p.stop();
        mWifiRegular.stop();
    }
}
