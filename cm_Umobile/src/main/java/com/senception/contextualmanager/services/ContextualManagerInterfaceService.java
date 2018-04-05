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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final String TAG = ContextualManagerInterfaceService.class.getSimpleName();
    private static ContextualManagerDataSource dataSource;

    final CManagerInterface.Stub mBinder = new CManagerInterface.Stub(){

        /*@Override
        public double[] getAvailability(String[] peerList) throws RemoteException {
            double [] availability = new double [peerList.length];

            for (int i = 0; i < peerList.length; i++){

                if(dataSource.hasPeer(peerList[i], ContextualManagerService.checkWeek("peers"))){
                    ContextualManagerAP peer = dataSource.getPeer(peerList[i], ContextualManagerService.checkWeek("peers"));
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
                if(dataSource.hasPeer(String.valueOf(peerList[i]), ContextualManagerService.checkWeek("peers"))){
                    ContextualManagerAP peer = dataSource.getPeer(String.valueOf(peerList[i]), ContextualManagerService.checkWeek("peers"));
                    centrality[i] = peer.getCentrality();
                }
                else
                    centrality[i] = -1; //if the peer id given was not found on the db then we can't provide it's centrality
            }
            return centrality;
        }

        @Override
        public Map getA(String [] peerList){
            HashMap<String, Double> hashMapAvailability = new HashMap<>();
            for (int i = 0; i < peerList.length; i++){

                if(dataSource.hasPeer(peerList[i], ContextualManagerService.checkWeek("peers"))){
                    ContextualManagerAP peer = dataSource.getPeer(peerList[i], ContextualManagerService.checkWeek("peers"));
                    hashMapAvailability.put(peerList[i], peer.getAvailability());
                }
                else
                    hashMapAvailability.put(peerList[i], null); //if the peer id given was not found on the db then we can't provide it's availability
            }
            return hashMapAvailability;
        }*/

        @Override
        public Map getAvailability(List<String> peerList){
            HashMap<String, Double> hashMapAvailability = new HashMap<>();
            for (int i = 0; i < peerList.size(); i++){

                if(dataSource.hasPeer(peerList.get(i), ContextualManagerService.checkWeek("peers"))){
                    ContextualManagerAP peer = dataSource.getPeer(peerList.get(i), ContextualManagerService.checkWeek("peers"));
                    hashMapAvailability.put(peerList.get(i), peer.getAvailability());
                }
                else
                    hashMapAvailability.put(peerList.get(i), null); //if the peer id given was not found on the db then we can't provide it's availability
            }
            return hashMapAvailability;
        }

        @Override
        public Map getCentrality(List<String> peerList){
            HashMap<String, Double> hashMapCentrality = new HashMap<>();

            for (int i = 0; i < peerList.size(); i++){
                //peerList[i] == peerId
                if(dataSource.hasPeer(String.valueOf(peerList.get(i)), ContextualManagerService.checkWeek("peers"))){
                    ContextualManagerAP peer = dataSource.getPeer(String.valueOf(peerList.get(i)), ContextualManagerService.checkWeek("peers"));
                    hashMapCentrality.put(peerList.get(i), peer.getCentrality());
                }
                else
                    hashMapCentrality.put(peerList.get(i), null); //if the peer id given was not found on the db then we can't provide it's centrality
            }
            return hashMapCentrality;
        }
    };

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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
