package com.senception.contextualmanager.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.senception.contextualmanager.aidl.CManagerInterface;
import com.senception.contextualmanager.databases.ContextualManagerDataSource;
import com.senception.contextualmanager.databases.ContextualManagerSQLiteHelper;
import com.senception.contextualmanager.modals.ContextualManagerAP;

import android.net.wifi.p2p.WifiP2pManager.Channel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerInterfaceService. This service runs in the
 * background, and is used to provide the weights of the surrounded peers to
 * other applications.
 */
public class ContextualManagerInterfaceService extends Service {

    private static ContextualManagerDataSource dataSource;

    final CManagerInterface.Stub mBinder = new CManagerInterface.Stub(){

        @Override
        public double[] getAvailability(String[] peerList) throws RemoteException {
            double [] availability = new double [peerList.length];

            for (int i = 0; i < peerList.length; i++){
                //peerList[i] == peerId
                if(dataSource.hasPeer(peerList[i], checkWeek("peers"))){
                    ContextualManagerAP peer = dataSource.getPeer(peerList[i], checkWeek("peers"));
                    availability[i] = peer.getAvailability();
                }
                else
                    availability[i] = -1; //if the peer id given was not found on the db then we can't provide it's availability
            }
            return availability;
        }

        @Override
        public double[] getCentrality(String[] peerList) throws RemoteException {
            double [] centrality = new double [peerList.length];

            for (int i = 0; i < peerList.length; i++){
                //peerList[i] == peerId
                if(dataSource.hasPeer(String.valueOf(peerList[i]), checkWeek("peers"))){
                    ContextualManagerAP peer = dataSource.getPeer(String.valueOf(peerList[i]), checkWeek("peers"));
                    centrality[i] = peer.getAvailability();
                }
                else
                    centrality[i] = -1; //if the peer id given was not found on the db then we can't provide it's availability
            }
            return new double[0];
        }
    };


    public class LocalBinder extends Binder {
        public ContextualManagerInterfaceService getService(){
            return ContextualManagerInterfaceService.this;
        }
    }

    @Override
    public void onCreate() {

        dataSource = new ContextualManagerDataSource(this);
        dataSource.openDB(true);

        Log.d("Resource", "AIDL SERVICE ENTROU NO ONCREATE");

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void stopForeGround(){
        stopForeground(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Funtion checkWeek
     * Check if it is peer or an ap, and what day
     * @param peerON value to check if is peer or not
     * @return tableName current AP or PEERS
     */
    public static String checkWeek(String peerON){

        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        Date date = new Date();
        String dw = sdf.format(date);
        String tableName = "";

        if(dw.equalsIgnoreCase("Mon") || dw.equalsIgnoreCase("Seg")){
            tableName = (peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_MONDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_MONDAY;
        }
        else if(dw.equalsIgnoreCase("Tue") || dw.equalsIgnoreCase("Ter")){
            tableName = (peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_TUESDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_TUESDAY;
        }
        else if(dw.equalsIgnoreCase("Wed") || dw.equalsIgnoreCase("Qua")){
            tableName =(peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_WEDNESDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_WEDNESDAY;
        }
        else if(dw.equalsIgnoreCase("Thu") || dw.equalsIgnoreCase("Qui")){
            tableName = (peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_THURSDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_THURSDAY;
        }
        else if(dw.equalsIgnoreCase("Fri") || dw.equalsIgnoreCase("Sex")){
            tableName = (peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_FRIDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_FRIDAY;
        }
        else if(dw.equalsIgnoreCase("Sat") || dw.equalsIgnoreCase("Sáb")){
            tableName = (peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_SATURDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_SATURDAY;
        }
        else if(dw.equalsIgnoreCase("Sun") || dw.equalsIgnoreCase("Dom")){
            tableName = (peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_SUNDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_SUNDAY;
        }
        return tableName;
    }
}
