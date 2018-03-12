package com.senception.contextualmanager.communication;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.senception.contextualmanager.wifi.p2p.WifiP2pListener;
import com.senception.contextualmanager.wifi.p2p.WifiP2pListenerManager;

import java.util.Map;


/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerReceive. This class is used
 * to receive information from contextual managers (A,C)
 */
public class ContextualManagerReceive implements WifiP2pListener.TxtRecordAvailable {

    private final String TAG = ContextualManagerReceive.class.getSimpleName();
    private Context mContext;

    public ContextualManagerReceive(Context context) {
        mContext = context;
        WifiP2pListenerManager.registerListener(this);
    }

    @Override
    public void onTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        Log.i(TAG, fullDomainName + " " + txtRecordMap + " " + srcDevice.deviceName);

        txtRecordMap.values();

    }
}
