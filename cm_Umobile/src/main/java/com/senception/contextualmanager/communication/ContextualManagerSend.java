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
        //TODO resolve error: getting txtrecord with a and c null ---> Cannot reproduce error.
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
                    Log.d("teste", "Sending A: " + A + "\t C: " + C);
                    String AToSend = String.valueOf(A);
                    String CToSend = String.valueOf(C);
                    Log.d("teste", "AToSend :" + AToSend + "\t CToSend: " + CToSend);//Todo: Find bug - Find out when can A or C null
                    WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, AToSend);
                    WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, CToSend);
                    Log.d("teste", "Sent A and C");
                }
                else{
                    Log.d(TAG, "Table is still empty so we can't send the availability and centrality.");
                }
            }
        }, 0, 10*1000); //todo every 5 min
    }
}
