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
 * Copyright (C) 2016 Senception Lda
 * Update to Contextual Manager 2017
 * @author Igor dos Santos - degomosIgor@sen-ception.com
 * @author José Soares - jose.soares@senception.com
 * @version 0.1
 *
 * @file Contains ContextualManagerSend. This class is used
 * to send information to other contextual managers (A,C)
 */
public class ContextualManagerSend {

    private final String TAG = ContextualManagerSend.class.getSimpleName();
    private Context mContext;

    /**
     * /**
     * Constructs a contectualManagerSend that will be attempting to
     * send the availability and centrality to other peers that are
     * also running the Contextual Manager.
     * @param context
     */
    public ContextualManagerSend(Context context) {
        mContext = context;

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                double A;
                double C;
                ContextualManagerDataSource dataSource = new ContextualManagerDataSource(mContext);
                dataSource.openDB(true);
                if (dataSource.hasPeer(MacSecurity.md5Hash("self"), ContextualManagerService.checkWeek("peers"))) {

                    ContextualManagerAP self = dataSource.getPeer(MacSecurity.md5Hash("self"), ContextualManagerService.checkWeek("peers"));
                    A = self.getAvailability();
                    C = self.getCentrality();

                    WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, String.valueOf(A));
                    WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, String.valueOf(C));

                    //Todo WifiP2pTxtRecord.setRecord(mContext, Identity.SIMILARITY, "0");
                }
                else{
                    Log.d(TAG, "Table is still empty so we can't send the availability and centrality.");
                }
            }
        }, 0, 1*60*1000); //todo every 5 min
    }
}
