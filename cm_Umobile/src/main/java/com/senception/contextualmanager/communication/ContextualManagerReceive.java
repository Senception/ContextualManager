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
 * Copyright (C) 2016 Senception Lda
 * Update to Contextual Manager 2017
 * @author Igor dos Santos - degomosIgor@sen-ception.com
 * @author José Soares - jose.soares@senception.com
 * @version 0.1
 *
 * @file Contains ContextualManagerReceive. This class is used
 * to receive information from contextual managers (A,C)
 */
public class ContextualManagerReceive implements WifiP2pListener.TxtRecordAvailable {

    private final String TAG = ContextualManagerReceive.class.getSimpleName();
    private ContextualManagerDataSource dataSource;
    private Context mContext;

    /**
     * Constructs a contectualManagerReceive that will be attempting to
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
            Log.d("teste", "txtRecord: " + txtRecordMap.toString());
            String a = txtRecordMap.get(Identity.AVAILABILITY);
            String c = txtRecordMap.get(Identity.CENTRALITY);
            Log.d("teste", "a: " + a + "     c : " + c);
            double A = 0;
            double C = 0;

            //if(a != null && c != null ) {
            A = Double.parseDouble(a);
            C = Double.parseDouble(txtRecordMap.get(Identity.CENTRALITY));

            //else{
            Log.d("teste", "Device Name: " + srcDevice.deviceName);

            //double I = Double.parseDouble(txtRecordMap.get(Identity.SIMILARITY));
            String hashSrcDeviceBSSID = MacSecurity.md5Hash(srcDevice.deviceAddress);

            //if it's the first time we see this peer we save it
            if (!dataSource.hasPeer(hashSrcDeviceBSSID, ContextualManagerService.checkWeek("peers"))) {
                ContextualManagerAP peer = new ContextualManagerAP();
                peer.setSSID(srcDevice.deviceName);
                peer.setBSSID(hashSrcDeviceBSSID);
                //TODO peer.setLatitude(latitude);
                //TODO peer.setLongitude(longitude);
                peer.setAvailability(A);
                peer.setCentrality(C);
                //peer.setSimilarity(I);
                peer.setNumEncounters(1);
                peer.setStartEncounter((int)(System.currentTimeMillis()/1000)); //time in seconds System.currentTimeMillis()/1000
                dataSource.registerNewPeers(peer, ContextualManagerService.checkWeek("peers"));
            } else {
                ContextualManagerAP peer = dataSource.getPeer(hashSrcDeviceBSSID, ContextualManagerService.checkWeek("peers"));
                peer.setSSID(srcDevice.deviceName);
                peer.setBSSID(hashSrcDeviceBSSID);
                peer.setAvailability(A);
                peer.setCentrality(C);
                //peer.setSimilarity(I);
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
