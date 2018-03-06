/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains CMUmobileWifiP2P. This class provides an BroadcastReceiver that notifies of important wifi p2p events
 * 
 */

package com.senception.cmumobile.pipelines;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import com.senception.cmumobile.services.CMUmobileService;
import com.senception.cmumobile.modals.CMUmobileAP;
import com.senception.cmumobile.services.aidlService;
//import android.util.Log;

@SuppressLint("NewApi")
public class CMUmobileWifiP2P extends BroadcastReceiver {

	//public static final String TAG = "WIFIP2P ---->";
	private static WifiP2pManager manager;
	private static Channel channel;
	private CMUmobileService service;
	private aidlService serviceAidl;
	ArrayList<CMUmobileAP> peersList = new ArrayList<CMUmobileAP>();
	ArrayList<CMUmobileAP> arrayPersence = new ArrayList<CMUmobileAP>();
	public String peerName;

	static Context mContext = null;
	@SuppressWarnings("static-access")
	public CMUmobileWifiP2P(WifiP2pManager manager, Channel channel,
			final CMUmobileService service) {
		super();
		this.manager = manager;
		this.channel = channel;
		this.service = service;
		discoverPeer();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Receiver", "ENTROU NO ONRECEIVE DO CMUMOBILEWIFIP2P");
		mContext = context;
		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

			} else {

			}
			//Log.d(TAG, "P2P state changed - " + state);
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			// request available peers from the wifi p2p manager. This is an
			// asynchronous call and the calling activity is notified with a
			// callback on PeerListListener.onPeersAvailable()
			if (manager != null) {
				manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {

					@Override
					public void onPeersAvailable(WifiP2pDeviceList peers) {
						// TODO Auto-generated method stub
						//
						peersList.clear();
						for(WifiP2pDevice dev : peers.getDeviceList()){
							//dev.
							CMUmobileAP peerfound = new CMUmobileAP();
							peerfound.setSSID(dev.deviceName);
							peerfound.setBSSID(dev.deviceAddress);

							peersList.add(peerfound);
						}
						
						service.notifyOnPeersFound(peersList);
						service.discoveredPeers(peersList);

						//Log.d(TAG,String.format("PeerListListener: %d peers available, updating device list", peers.getDeviceList().size()));
					}
				});
			}

			//Log.d(TAG, "P2P peers changed");
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {		

		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

		}
	}
	/**
	 * Function discoverPeer
	 * Start wifi p2p discovery
	 */
	public static void discoverPeer(){

		manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub

				//Log.d(TAG, "ONSUCESS");
			}

			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				//Log.d(TAG, "ONFAILURE");
			}
		});
	}
}
