/**
 * Copyright (C) Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@senception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017/2018
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerWifiP2P. This class provides an BroadcastReceiver that notifies of important wifi p2p events
 * 
 */

package com.senception.contextualmanager.pipelines;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import com.senception.contextualmanager.modals.ContextualManagerAP;
import com.senception.contextualmanager.services.ContextualManagerService;
import com.senception.contextualmanager.wifi.p2p.WifiP2pListener;
import com.senception.contextualmanager.wifi.p2p.WifiP2pListenerManager;
//import android.util.Log;

@SuppressLint("NewApi")
public class ContextualManagerWifiP2P extends BroadcastReceiver implements WifiP2pListener.TxtRecordAvailable {

	public static final String TAG = ContextualManagerWifiP2P.class.getSimpleName();
	private static WifiP2pManager manager;
	private static Channel channel;
	private ContextualManagerService service;
	ArrayList<ContextualManagerAP> peersList = new ArrayList<ContextualManagerAP>();
	static Context mContext = null;
	@SuppressWarnings("static-access")

    /**
     * Constructor
     */
    public ContextualManagerWifiP2P(WifiP2pManager manager, Channel channel,
									final ContextualManagerService service) {
		super();
		this.manager = manager;
		this.channel = channel;
		this.service = service;
		WifiP2pListenerManager.registerListener(this);
		discoverPeer();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		String action = intent.getAction();

		if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			// request available peers from the wifi p2p manager. This is an
			// asynchronous call and the calling activity is notified with a
			// callback on PeerListListener.onPeersAvailable()

            if (manager != null) {
				manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {

					@Override
					public void onPeersAvailable(WifiP2pDeviceList peers) {
						peersList.clear();
						for(WifiP2pDevice dev : peers.getDeviceList()){
							ContextualManagerAP peerfound = new ContextualManagerAP();
							peerfound.setSSID(dev.deviceName);

							peerfound.setHashedMac(dev.deviceAddress);

							peersList.add(peerfound);
						}

						//wifiDataExchange = new WifiDataExchangeRead(mContext, peersList);
						service.notifyOnPeersFound(peersList);
						service.discoveredPeers(peersList);
					}
				});
			}
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
				//Log.d(TAG, "ONSUCESS");
			}

			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				//Log.d(TAG, "ONFAILURE");
			}
		});
	}

	@Override
	public void onTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {

	}

}
