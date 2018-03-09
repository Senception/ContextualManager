package com.senception.contextualmanager;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.senception.contextualmanager.wifi.p2p.Identity;
import com.senception.contextualmanager.wifi.p2p.WifiP2pListener;
import com.senception.contextualmanager.wifi.p2p.WifiP2pListenerManager;
import com.senception.contextualmanager.wifi.p2p.WifiP2pTxtRecord;

import java.util.Map;



public class Receive implements WifiP2pListener.TxtRecordAvailable {

    private static final String TAG = Receive.class.getSimpleName();
    private Context mContext;

    public Receive(Context context) {
        mContext = context;
        WifiP2pListenerManager.registerListener(this);
    }

    @Override
    public void onTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        Log.i(TAG, fullDomainName + " " + txtRecordMap + " " + srcDevice.deviceName);
    }
}
