package com.senception.contextualmanager.communication;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.senception.contextualmanager.databases.ContextualManagerDataSource;
import com.senception.contextualmanager.databases.ContextualManagerSQLiteHelper;
import com.senception.contextualmanager.modals.ContextualManagerAP;
import com.senception.contextualmanager.security.MacSecurity;
import com.senception.contextualmanager.wifi.p2p.Identity;
import com.senception.contextualmanager.wifi.p2p.WifiP2pListener;
import com.senception.contextualmanager.wifi.p2p.WifiP2pListenerManager;
import com.senception.contextualmanager.wifi.p2p.WifiP2pTxtRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
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
 * @file Contains ContextualManagerReceive. This class is used
 * to receive information from contextual managers (A,C)
 */
public class ContextualManagerReceive implements WifiP2pListener.TxtRecordAvailable {

    private static SimpleDateFormat dataFormat = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");
    private final String TAG = ContextualManagerReceive.class.getSimpleName();
    private ContextualManagerDataSource dataSource;
    private Context mContext;

    public ContextualManagerReceive(Context context) {
        mContext = context;
        dataSource = new ContextualManagerDataSource(mContext);
        dataSource.openDB(true);
        WifiP2pListenerManager.registerListener(this);

        /*Log.d("Communication", "A TENTAR ENVIAR:");

        WifiP2pTxtRecord.setRecord(mContext, Identity.AVAILABILITY, "40.5");
        WifiP2pTxtRecord.setRecord(mContext, Identity.CENTRALITY, "49.7");
        WifiP2pTxtRecord.setRecord(mContext, Identity.SIMILARITY, "410");*/

    }

    @Override
    public void onTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        /* fullDomainName: uuidcm (cm - service on identify)
        *
        *   txtRecordMap:
        * Availability --> 4716456285
        * Centrality --> -1
        * Similarity --> 10
        *
        * srcDevice.deviceName: SAMSUNG NEO
        */
        Log.d("teste", "A TENTAR RECEBER:");

        Log.i("teste", fullDomainName + " " + txtRecordMap + " " + srcDevice.deviceName);

        if(txtRecordMap != null && txtRecordMap.size() != 0) {
            double A = Double.parseDouble(txtRecordMap.get(Identity.AVAILABILITY));
            Log.d("teste", "A recebido: " + A);
            double C = Double.parseDouble(txtRecordMap.get(Identity.CENTRALITY));
            Log.d("teste", "C recebido: " + C);

            Log.i(TAG, fullDomainName + " " + txtRecordMap + " " + srcDevice.deviceName);
            //deviceAddress - Mac (BSSID) | deviceName - Device name (SSID)

            String hashSrcDeviceBSSID = MacSecurity.MD5hash(srcDevice.deviceAddress);
            Log.d("teste", "hash of the device mac: " + hashSrcDeviceBSSID);
            if (!dataSource.hasPeer(hashSrcDeviceBSSID, checkWeek("peers"))) {
                ContextualManagerAP peer = new ContextualManagerAP();
                peer.setSSID(srcDevice.deviceName);
                peer.setBSSID(hashSrcDeviceBSSID);
                //TODO peer.setLatitude(latitude);
                //TODO peer.setLongitude(longitude);
                peer.setAvailability(A);
                peer.setCentrality(C);
                peer.setNumEncounters(1);
                peer.setStartEncounter(String.valueOf(System.currentTimeMillis()/1000)); //time in seconds System.currentTimeMillis()/1000
                dataSource.registerNewPeers(peer, checkWeek("peers"));
                Log.d("teste", "New peer device registered on DB (1st time)");
            } else {
                ContextualManagerAP peer = dataSource.getPeer(hashSrcDeviceBSSID, checkWeek("peers"));
                peer.setSSID(srcDevice.deviceName);
                peer.setBSSID(hashSrcDeviceBSSID);
                peer.setAvailability(A);
                peer.setCentrality(C);
                //TODO peer.setLatitude(latitude);
                //TODO peer.setLongitude(longitude);
                peer.setNumEncounters(peer.getNumEncounters()+1);
                Log.d("teste", "numEncounters: " + peer.getNumEncounters());
                dataSource.updatePeer(peer, checkWeek("peers"));
                Log.d("teste", "New peer device updated on the DB (2nd+ time).");
            }
            //txtRecordMap.values();
        }
        else{
            Log.d("teste", "The txt Record was null or empty");
        }

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
