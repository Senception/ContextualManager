package com.senception.contextualmanager.interfaces;

import java.util.List;
import android.net.wifi.ScanResult;

/**
 * Copyright (C) Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@senception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017-2018
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains PerSenseWifiChangeListener. This class provides an interface that listens to the Wi-Fi connection
 *
 */
public interface ContextualManagerWifiChangeListener {
	void onWifiStateDisabled(boolean valid, String bssid, String ssid, long visitId, long connectionStart, long connectionEnd);
	void onWifiStateEnabled(String ssid);
	void onWifiConnectionDown(boolean valid, String bssid, String ssid, long visitId, long connectionStart, long connectionEnd);
	long onWifiConnectionUp(String bssid,String ssid, List<ScanResult> lastScanResults);
	void onWifiAvailableNetworksChange(String bssid, List<ScanResult> results);

}
