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
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerSend. This class is used
 * to send information to other contextual managers (A,C)
 */
public class ContextualManagerSend {

    private final String TAG = ContextualManagerSend.class.getSimpleName();
    private Context mContext;

    public ContextualManagerSend(Context context) {
        mContext = context;

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("Communication", "A TENTAR ENVIAR:");
                double A;
                double C;

                ContextualManagerDataSource dataSource = new ContextualManagerDataSource(mContext);
                dataSource.openDB(true);
                if (!dataSource.isTableEmpty(ContextualManagerSQLiteHelper.TABLE_WEIGHTS)) {

                    ContextualManagerWeight weight = dataSource.getWeight();

                    A = weight.getA();
                    C = weight.getC();
                    Log.d("Communication", "A: " + A); // A with value 1.3872E7 means 1.3872 * 10^7, cientific notation
                    Log.d("Communication", "C: " + C);

                    //TODO Change A and C from int to double if needed
                    // TODO if txtRecord = {} we shouldn't send it
                    WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, String.valueOf(A));
                    WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, String.valueOf(C));
                    WifiP2pTxtRecord.setRecord(mContext, Identity.SIMILARITY, "10");
                }
                else{
                    Log.d("Communication", "A TABELA AINDA ESTA VAZIA");
                }

                /*
                WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, "20.5");
                WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, "9.7");
                WifiP2pTxtRecord.setRecord(mContext, Identity.SIMILARITY, "10");*/
            }
        }, 0, 1*60000); //5 em 5 min
    }
}
