package com.senception.contextualmanager.communication;

import android.content.Context;
import android.util.Log;

import com.senception.contextualmanager.databases.ContextualManagerDataSource;
import com.senception.contextualmanager.databases.ContextualManagerSQLiteHelper;
import com.senception.contextualmanager.modals.ContextualManagerWeight;
import com.senception.contextualmanager.wifi.p2p.Identity;
import com.senception.contextualmanager.wifi.p2p.WifiP2pTxtRecord;

import java.util.ArrayList;
import java.util.Calendar;
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
                if (!dataSource.isTableEmpty(ContextualManagerSQLiteHelper.TABLE_WEIGHTS)) {

                    ContextualManagerWeight weight = dataSource.getWeight();
                    A = weight.getA();
                    C = weight.getC();

                    WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, String.valueOf(A));
                    WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, String.valueOf(C));
                    //Todo WifiP2pTxtRecord.setRecord(mContext, Identity.SIMILARITY, "0");
                }
                else{
                    Log.d(TAG, "Table is still empty so we can't send the availability and centrality.");
                }
            }
        }, 0, 1*60*1000); //5 em 5 min
    }
}
