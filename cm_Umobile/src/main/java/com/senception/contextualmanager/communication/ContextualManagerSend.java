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
        ArrayList<Integer> A;
        ArrayList<Integer> C;

        ContextualManagerDataSource dataSource = new ContextualManagerDataSource(mContext);
        dataSource.openDB(true);
        if (!dataSource.isWeightsEmpty()) {
            ContextualManagerWeight weight = dataSource.getWeight(ContextualManagerSQLiteHelper.TABLE_WEIGHTS);

            A = weight.getA();
            Log.d("Communication", "A: " + A);

            Calendar currentTime = Calendar.getInstance();
            int currentSecond = currentTime.get(Calendar.SECOND);
            //TODO PASSAR DE INTEIRO PARA DOUBLE
            int availability = A.get(currentSecond);
            Log.d("Communication", "Current Second: "+ currentSecond);
            Log.d("Communication", "A: "+ availability);
            C = weight.getC();


            WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, String.valueOf(availability));
            WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, "9.7");
            WifiP2pTxtRecord.setRecord(mContext, Identity.SIMILARITY, "10");
        }
        else{
            Log.d("COMMUNICATION", "A TABELA AINDA ESTA VAZIA");
        }

        /*
        WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, "20.5");
        WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, "9.7");
        WifiP2pTxtRecord.setRecord(mContext, Identity.SIMILARITY, "10");
        */
    }
}
