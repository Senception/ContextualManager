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
import com.senception.contextualmanager.modals.ContextualManagerAP;

import android.net.wifi.p2p.WifiP2pManager.Channel;

import java.util.ArrayList;

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

    private static WifiP2pManager manager;
    ArrayList<ContextualManagerAP> peersList = new ArrayList<ContextualManagerAP>();
    private Channel channel;

    final CManagerInterface.Stub mBinder = new CManagerInterface.Stub(){

        public int getAvailability() throws RemoteException {

            //int [] availability = new int[60];
            int availability = 13513;
            return availability;
        }

        @Override
        public int[] getCentrality() throws RemoteException {
            return new int[0];
        }

        /*@Override
        public List<MyObject> getAll(int[] peerList) throws RemoteException {
            return null;
        }

        @Override
        public CMPeerInferenceTest getAll(int[] peerList) throws RemoteException {
            return new CMPeerInferenceTest();
        }*/
    };


    public class LocalBinder extends Binder {
        public ContextualManagerInterfaceService getService(){
            return ContextualManagerInterfaceService.this;
        }
    }

    @Override
    public void onCreate() {

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

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

}
