package com.senception.cmumobile.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.senception.cmumobile.aidl.CManagerInterface;
import com.senception.cmumobile.inference.MyObject;
import com.senception.cmumobile.modals.CMUmobileAP;

import android.net.wifi.p2p.WifiP2pManager.Channel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Senception on 20/02/2018.
 */
public class aidlService extends Service {

    private static WifiP2pManager manager;
    ArrayList<CMUmobileAP> peersList = new ArrayList<CMUmobileAP>();
    private Channel channel;

    final CManagerInterface.Stub mBinder = new CManagerInterface.Stub(){

        public int getAvailability(int [] peerList) throws RemoteException {

            //int [] availability = new int[60];
            int availability = 13513;
            return availability;
        }

        @Override
        public int[] getCentrality(int[] peerList) throws RemoteException {
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
        public aidlService getService(){
            return aidlService.this;
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
