package com.senception.contextualmanager;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;

import com.senception.contextualmanager.wifi.p2p.Identity;
import com.senception.contextualmanager.wifi.p2p.WifiP2pListener;
import com.senception.contextualmanager.wifi.p2p.WifiP2pListenerManager;
import com.senception.contextualmanager.wifi.p2p.WifiP2pTxtRecord;

import java.util.Map;

/**
 * Created by Senception on 09/03/2018.
 */


public class Send {

    private static final String TAG = Receive.class.getSimpleName();
    private Context mContext;

    public Send(Context context) {
        mContext = context;

        WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, "20.5");
        WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, "9.7");
        WifiP2pTxtRecord.setRecord(mContext, Identity.SIMILARITY, "10");

    }
}
