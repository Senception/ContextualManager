package com.senception.contextualmanager.pipelines;

import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.senception.contextualmanager.interfaces.ContextualManagerWifiChangeListener;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerWifiManager.
 * This class provides an BroadcastReceiver that notifies
 * of important wifi events and provides some methods to extend
 * functionality to the android WifiManager.
 */
public class ContextualManagerWifiManager {
	public static long MINIMUM_CONNEXION_TIME = 10;
	public static int SCANNING_INTERVAL = 20000;
	
	public boolean isScanningActive = false;
	public boolean isWaitingScanResults = false;
	
	private WifiManager androidWifiManager;
	
	WifiInfo wifiCurrentAP;
	private long wifiCurrentVisitId;
	public long wifiCurrentAPStart;
	
	private WifiStateChange wifiStateReceiver;
	private WifiConnectionChange wifiConnReceiver;
	private WifiAvailableNetworksChange wifiScanReceiver;
	private ContextualManagerWifiChangeListener listener;

	private Handler mHandler = new Handler();
	
	private Runnable runScan = new Runnable() {
		public void run() {
			if (isScanningActive) {
				if (!isWaitingScanResults && isWifiEnabled()) {
					if (startScan()) {
						isWaitingScanResults = true;
					}
					else {
						//notifyPredictedMoveChange("Scanning rejected");
					}
					
					mHandler.postDelayed(runScan, SCANNING_INTERVAL);
				}
				else if (isWaitingScanResults) {
					//notifyPredictedMoveChange("Still Waiting");
	        		mHandler.postDelayed(runScan, SCANNING_INTERVAL);
	        		isWaitingScanResults = false;
				}
				else {
					// WifiManager not active
				}
			}
			else {
				//notifyPredictedMoveChange("Scanning Stoped");
			}
		}
		
	};
	
	public ContextualManagerWifiManager(Context c) {
		androidWifiManager = (WifiManager)c.getSystemService(Context.WIFI_SERVICE);
		wifiStateReceiver = new WifiStateChange();
    	wifiConnReceiver = new WifiConnectionChange();
    	wifiScanReceiver = new WifiAvailableNetworksChange();
		c.registerReceiver(wifiStateReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    	c.registerReceiver(wifiConnReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    	c.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    
	}
	
	public void close (Context c) {
		this.stopPeriodicScanning();
		c.unregisterReceiver(wifiStateReceiver);
		c.unregisterReceiver(wifiScanReceiver);
		c.unregisterReceiver(wifiConnReceiver);
		
		if (wifiCurrentAP != null) {
			long wifiCurrentAPEnd = System.currentTimeMillis();
			long connectionTime = wifiCurrentAPEnd - wifiCurrentAPStart;
			if (connectionTime > ContextualManagerWifiManager.MINIMUM_CONNEXION_TIME) {
				listener.onWifiConnectionDown(true, wifiCurrentAP.getBSSID(), wifiCurrentAP.getSSID(), wifiCurrentVisitId, wifiCurrentAPStart, wifiCurrentAPEnd);
			}
			else {
				listener.onWifiConnectionDown(false, null, null, -1, -1, -1);
			}
			
			wifiCurrentAP = null;
			wifiCurrentAPStart = 0;
		}
	}
	
	public void startPeriodicScanning () {
		isScanningActive = true;
		mHandler.removeCallbacks(runScan);
		mHandler.post(runScan);
	}
	
	public void stopPeriodicScanning () {
		isScanningActive = false;
		mHandler.removeCallbacks(runScan);
	}
	
    public void setOnWifiChangeListener (ContextualManagerWifiChangeListener listener) {
        this.listener = listener;
    }
    
    public void clearOnWifiChangeListener () {
        this.listener = null;
    }
  
	public void setWifiManager (WifiManager wm) {
		this.androidWifiManager = wm;
	}
	
	public void setWifiManager (Context c) {
		this.androidWifiManager = (WifiManager)c.getSystemService(Context.WIFI_SERVICE);
	}
	
	public boolean isWifiEnabled () {
		return androidWifiManager.isWifiEnabled();
	}
	
	public boolean startScan () {
		return androidWifiManager.startScan();
	}
	
	public List<ScanResult> getLastScanResults () {
		return androidWifiManager.getScanResults();
	}
	
	public int getGatewayIp () {
		return androidWifiManager.getDhcpInfo().gateway;
	}
	
	public void noteOngoingConnection () {
		if (wifiCurrentAP == null && androidWifiManager != null) {
			wifiCurrentAP = androidWifiManager.getConnectionInfo();
			if (wifiCurrentAP != null) {
				if (wifiCurrentAP.getBSSID() != null && wifiCurrentAP.getSSID() != null) {
					System.out.println(wifiCurrentAP.getSSID());
					wifiCurrentAPStart = System.currentTimeMillis();
					wifiCurrentVisitId = listener.onWifiConnectionUp(wifiCurrentAP.getBSSID(), wifiCurrentAP.getSSID(), getLastScanResults());
					
					
				}
			}
		}
	}
		
    class WifiStateChange extends BroadcastReceiver {
        public void onReceive(Context c, Intent i) {
        	int newState = i.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

        	switch (newState) {
        		case WifiManager.WIFI_STATE_DISABLED:
    				if (wifiCurrentAP != null) {
    					long wifiCurrentAPEnd = System.currentTimeMillis();
    					if ((wifiCurrentAPEnd - wifiCurrentAPStart) > ContextualManagerWifiManager.MINIMUM_CONNEXION_TIME) {
    						listener.onWifiStateDisabled(true, wifiCurrentAP.getBSSID(), wifiCurrentAP.getSSID(), wifiCurrentVisitId, wifiCurrentAPStart, wifiCurrentAPEnd);
    					}
    					else {
    						listener.onWifiStateDisabled(false, null, null, -1, -1, -1);
    					}
    					
    					wifiCurrentAP = null;
    					wifiCurrentAPStart = 0;
    				}
    				stopPeriodicScanning();
    				break;
        		case WifiManager.WIFI_STATE_ENABLED:
        			if(wifiCurrentAP != null){
        			listener.onWifiStateEnabled(wifiCurrentAP.getSSID());
        			startPeriodicScanning();
        			}
        			else{
        				startPeriodicScanning();
        			}
        			break;
        		default:
        			break;
        	}
            
        }      
    }
    
    class WifiConnectionChange extends BroadcastReceiver {
        public void onReceive(Context c, Intent i) {
        	NetworkInfo netInf = (NetworkInfo) i.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        	if (netInf != null) {
        		NetworkInfo.State netState = netInf.getState();
        		switch (netState) {
        			case DISCONNECTED:
        				if (wifiCurrentAP != null) {
        					long wifiCurrentAPEnd = System.currentTimeMillis();
            				long connectionTime = wifiCurrentAPEnd - wifiCurrentAPStart;
        					if (connectionTime > ContextualManagerWifiManager.MINIMUM_CONNEXION_TIME) {
        						listener.onWifiConnectionDown(true, wifiCurrentAP.getBSSID(), wifiCurrentAP.getSSID(), wifiCurrentVisitId, wifiCurrentAPStart, wifiCurrentAPEnd);
        					}
        					else {
        						listener.onWifiConnectionDown(false, null, null, -1, -1, -1);
        					}
        					
        					wifiCurrentAP = null;
        					wifiCurrentAPStart = 0;
        				}
        				stopPeriodicScanning();
        				break;
        			case CONNECTED:
        				if (wifiCurrentAP == null) {
        					wifiCurrentAPStart = System.currentTimeMillis();
        					wifiCurrentAP = ((WifiManager)c.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();        				        					
        					wifiCurrentVisitId = listener.onWifiConnectionUp(wifiCurrentAP.getBSSID(), wifiCurrentAP.getSSID(), getLastScanResults());
        					startPeriodicScanning();
        				}
        				break;
        			default:
        				break;
        		}
        	}
        }      
    }
    
    class WifiAvailableNetworksChange extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
        	isWaitingScanResults = false;
            if (wifiCurrentAP != null) {
            	listener.onWifiAvailableNetworksChange(wifiCurrentAP.getBSSID(), getLastScanResults());
            }
            else {
            	// notifyPredictedMoveChange("Not Connected");
            	//PREVIOUS INFO
				//no active connection yet
				//builds initial list of APs, based on scan
				//triggers start of time counting
				//later should be triggered by beacon
            }
        }      
    }
}
