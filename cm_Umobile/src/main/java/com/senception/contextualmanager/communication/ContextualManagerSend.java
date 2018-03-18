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
        Log.d("Communication", "A TENTAR ENVIAR:");

        /*double A;
        double C;

        ContextualManagerDataSource dataSource = new ContextualManagerDataSource(mContext);
        dataSource.openDB(true);
        if (!dataSource.isTableEmpty(ContextualManagerSQLiteHelper.TABLE_WEIGHTS)) {

            ContextualManagerWeight weight = dataSource.getWeight();

            A = weight.getA();
            C = weight.getC();
            Log.d("Communication", "A: " + A);
            Log.d("Communication", "C: " + C);

            //TODO PASSAR DE INTEIRO PARA DOUBLE

            WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, String.valueOf(A));
            Log.d("Communication", "O valor enviado de A é: " + String.valueOf(A)); // A com valor 1.3872E7 significa 1.3872 * 10^7, notação cientifica
            WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, String.valueOf(C));
            WifiP2pTxtRecord.setRecord(mContext, Identity.SIMILARITY, "10");
        }
        else{
            Log.d("Communication", "A TABELA AINDA ESTA VAZIA");
            WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, "");
            WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, "");
            WifiP2pTxtRecord.setRecord(mContext, Identity.SIMILARITY, "");
        }*/


        WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, "20.5");
        WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, "9.7");
        WifiP2pTxtRecord.setRecord(mContext, Identity.SIMILARITY, "10");

    }
}
