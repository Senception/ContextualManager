package com.senception.contextualmanager.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.senception.contextualmanager.aidl.CManagerInterface;
import com.senception.contextualmanager.databases.ContextualManagerDataSource;
import com.senception.contextualmanager.modals.ContextualManagerAP;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.senception.contextualmanager.services.ContextualManagerService.checkWeek;

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

        @Override
        public Map getAvailability(List<String> peerList){
            HashMap<String, Double> hashMapAvailability = new HashMap<>();
            for (int i = 0; i < peerList.size(); i++){

                if(dataSource.hasPeer(peerList.get(i), checkWeek("peers"))){
                    ContextualManagerAP peer = dataSource.getPeer(peerList.get(i), checkWeek("peers"));
                    hashMapAvailability.put(peerList.get(i), peer.getAvailability());
                    Log.d(TAG, "Calculated the availability of the peer " + peerList.get(i) + "with A = " + peer.getAvailability());
                }
                else {
                    hashMapAvailability.put(peerList.get(i), null); //if the peer id given was not found on the db then we can't provide it's availability
                    Log.d(TAG, "The peer " + peerList.get(i) + "was not found" );
                }
            }
            return hashMapAvailability;
        }

        @Override
        public Map getCentrality(List<String> peerList){
            HashMap<String, Double> hashMapCentrality = new HashMap<>();

            for (int i = 0; i < peerList.size(); i++){
                //peerList[i] == peerId
                if(dataSource.hasPeer(peerList.get(i), checkWeek("peers"))){
                    ContextualManagerAP peer = dataSource.getPeer(String.valueOf(peerList.get(i)), checkWeek("peers"));
                    hashMapCentrality.put(peerList.get(i), peer.getCentrality());
                    Log.d(TAG, "Calculated the centrality of the peer " + peerList.get(i) + "with C = " + peer.getCentrality());
                }
                else {
                    hashMapCentrality.put(peerList.get(i), null); //if the peer id given was not found on the db then we can't provide it's centrality
                    Log.d(TAG, "The peer " + peerList.get(i) + "was not found" );
                }
            }
            return hashMapCentrality;
        }

        @Override
        public Map getSimilarity(List<String> peerList){
            HashMap<String, Double> hashMapSimilarity = new HashMap<>();

            for (int i = 0; i < peerList.size(); i++){
                //peerList[i] == peerId
                if(dataSource.hasPeer(peerList.get(i), checkWeek("peers"))){
                    ContextualManagerAP peer = dataSource.getPeer(peerList.get(i), checkWeek("peers"));
                    hashMapSimilarity.put(peerList.get(i), peer.getSimilarity());
                    Log.d(TAG, "Calculated the similarity of the peer " + peerList.get(i) + " with I = " + peer.getSimilarity());
                }
                else {
                    hashMapSimilarity.put(peerList.get(i), null); //if the peer id given was not found on the db then we can't provide it's centrality
                    Log.d(TAG, "The peer " + peerList.get(i) + "was not found" );
                }
            }
            return hashMapSimilarity;
        }
    };

    @Override
    public void onCreate() {
        dataSource = new ContextualManagerDataSource(this);
        dataSource.openDB(true);
        Log.d(TAG, "Aidl service called");
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
