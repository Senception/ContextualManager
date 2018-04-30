package com.senception.contextualmanager.communication;

import android.content.Context;
import android.util.Log;

import com.senception.contextualmanager.databases.ContextualManagerDataSource;
import com.senception.contextualmanager.modals.ContextualManagerAP;
import com.senception.contextualmanager.security.MacSecurity;
import com.senception.contextualmanager.services.ContextualManagerService;
import com.senception.contextualmanager.wifi.p2p.Identity;
import com.senception.contextualmanager.wifi.p2p.WifiP2pTxtRecord;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (C) Senception Lda
 * Update to Contextual Manager 2017
 * @author Igor dos Santos - degomosIgor@senception.com
 * @author José Soares - jose.soares@senception.com
 * @author Rute Sofia
 * @version 0.1
 *
 * @file Contains ContextualManagerSend. This class is used
 * to send information to other contextual managers, namely values A (Availability, C (Centrality) and I (similarity cost to a peer)
 * Check UMOBILE Deliverable D4.5 for details on the cost computation
 */
public class ContextualManagerSend {

    private final String TAG = ContextualManagerSend.class.getSimpleName();
    private Context mContext;

    /**
     * /**
     * Constructs a contextualManagerSend that will be attempting to
     * send the Availability and Centrality to other peers that are
     * also running the Contextual Manager.
     * This is done directly, via Wi-Fi Direct (or via any other direct connection)
     * @param context
     */
    public ContextualManagerSend(Context context) {
        mContext = context;

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ContextualManagerDataSource dataSource = new ContextualManagerDataSource(mContext);
                dataSource.openDB(true);
                if (dataSource.hasPeer(MacSecurity.md5Hash("self"), ContextualManagerService.checkWeek("peers"))) {
                    ContextualManagerAP self = dataSource.getPeer(MacSecurity.md5Hash("self"), ContextualManagerService.checkWeek("peers"));
                    double A = self.getAvailability();
                    double C = self.getCentrality();
                    Log.d(TAG, "Sending A: " + A + "\t C: " + C);
                    String AToSend = String.valueOf(A);
                    String CToSend = String.valueOf(C);
                    WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, AToSend);
                    WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, CToSend);
                    Log.d(TAG, "Sent A and C");
                }
                else{
                    Log.d(TAG, "Table is still empty so we can't send the availability and centrality.");
                }
                // TODO adjust the time the data is being sent. Currently it is being sent every 10 seconds for demo purpose only.
            }
        }, 0, 10*1000);
    }
}
