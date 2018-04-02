/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/8.
 * Class is part of the NSense application.
 */

package com.senception.contextualmanager.wifi.p2p;


import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;

import java.util.Map;

/**
 * 2017 COPELABS/ULHT
 * Update to Contextual Manager 2018 Senception Lda
 * @author Miguel Tavares (COPELABS/ULHT)
 * @author José Soares (Senception Lda) - jose.soares@senception.com
 * @version 1.1, 2018
 *
 * @file interface that allows the communication between the wifi p2p features and your class
 */
public interface WifiP2pListener {

    interface ServiceAvailable extends WifiP2pListener {
        void onServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice);
    }

    interface TxtRecordAvailable extends WifiP2pListener {
        void onTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice);
    }

    interface PeersAvailable extends WifiP2pListener {
        void onPeersAvailable(WifiP2pDeviceList peers);
    }

    interface WifiP2pConnectionStatus extends WifiP2pListener {
        void onConnected(Intent intent);
        void onDisconnected(Intent intent);
    }

    interface GoIpAvailable extends WifiP2pListener {
        void onIamGo();
        void onGoIpAddressAvailable(String ipAddress);
    }

}
