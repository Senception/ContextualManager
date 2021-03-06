package com.senception.contextualmanager.communication;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.senception.contextualmanager.activities.ContextualManagerMainActivity;
import com.senception.contextualmanager.databases.ContextualManagerDataSource;
import com.senception.contextualmanager.modals.ContextualManagerAP;
import com.senception.contextualmanager.security.MacSecurity;
import com.senception.contextualmanager.services.ContextualManagerService;
import com.senception.contextualmanager.wifi.p2p.Identity;
import com.senception.contextualmanager.wifi.p2p.WifiP2pListener;
import com.senception.contextualmanager.wifi.p2p.WifiP2pListenerManager;

import java.util.Map;


/**
 * Copyright (C) Senception Lda
 * Update to Contextual Manager 2018
 * @author Igor dos Santos - degomosIgor@senception.com
 * @author José Soares - jose.soares@senception.com
 * @version 0.1
 *
 * @file Contains ContextualManagerReceive. This class is used
 * to receive information from contextual managers
 * We send directly the values A (availability), C (Centrality) and I (similarity)
 */
public class ContextualManagerReceive implements WifiP2pListener.TxtRecordAvailable {

    private final String TAG = ContextualManagerReceive.class.getSimpleName();
    private ContextualManagerDataSource dataSource;
    private Context mContext;

    /**
     * Constructs a contextualManagerReceive that will be attempting to
     * receive the availability and centrality from other peers that are
     * also running the Contextual Manager.
     * @param context
     */
    public ContextualManagerReceive(Context context) {
        mContext = context;
        dataSource = new ContextualManagerDataSource(mContext);
        dataSource.openDB(true);
        WifiP2pListenerManager.registerListener(this);
    }

    @Override
    public void onTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
          if(txtRecordMap != null && txtRecordMap.size() != 0) {
            //Log.d(TAG, "txtRecord: " + txtRecordMap.toString());
            //Log.d(TAG, "received from" + srcDevice.deviceName);
            String a = txtRecordMap.get(Identity.AVAILABILITY);
            String c = txtRecordMap.get(Identity.CENTRALITY);
            Log.d(TAG, "Received a: " + a + "\t c : " + c);

            double A = 0;
            double C = 0;

            if(a != null && c != null ) {
                A = Double.parseDouble(a);
                C = Double.parseDouble(c);
            }

            String hashSrcDeviceHashedMac = MacSecurity.md5Hash(srcDevice.deviceAddress);

            //if it's the first time we see this peer we save it
            if (!dataSource.hasPeer(hashSrcDeviceHashedMac, ContextualManagerService.checkWeek("peers"))) {
                ContextualManagerAP peer = new ContextualManagerAP();
                peer.setSSID(srcDevice.deviceName);
                peer.setHashedMac(hashSrcDeviceHashedMac);
                //TODO peer.setLatitude(latitude);
                //TODO peer.setLongitude(longitude);
                peer.setAvailability(A);
                peer.setCentrality(C);
                peer.setNumEncounters(1);
                //time in seconds System.currentTimeMillis()/1000
                peer.setStartEncounter((int)(System.currentTimeMillis()/1000));
                dataSource.registerNewPeers(peer, ContextualManagerService.checkWeek("peers"));
            } else {
                ContextualManagerAP peer = dataSource.getPeer(hashSrcDeviceHashedMac, ContextualManagerService.checkWeek("peers"));
                peer.setSSID(srcDevice.deviceName);
                peer.setHashedMac(hashSrcDeviceHashedMac);
                peer.setAvailability(A);
                peer.setCentrality(C);
                //TODO peer.setLatitude(latitude);
                //TODO peer.setLongitude(longitude);
                dataSource.updatePeer(peer, ContextualManagerService.checkWeek("peers"));
            }
            ContextualManagerMainActivity.backupDB(mContext);
        }
        else{
            Log.d(TAG, "The txt Record was null or empty");
        }

    }

}
