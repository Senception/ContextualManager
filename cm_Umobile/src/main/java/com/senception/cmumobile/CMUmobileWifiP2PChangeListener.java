/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains CMUmobileWifiP2PChangeListener. This class provides an interface that listen to Peer Availability
 *
 */

package com.senception.cmumobile;

import java.util.ArrayList;

public interface CMUmobileWifiP2PChangeListener {
		void onFoundPersenceGroup(ArrayList<CMUmobileAP> disc, ArrayList<CMUmobileAP> reg);
		void onPeersFound(ArrayList<CMUmobileAP> disc);
}
