package com.senception.contextualmanager.interfaces;

import com.senception.contextualmanager.modals.ContextualManagerAP;

import java.util.ArrayList;

/**
 * Copyright (C) Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@senception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017-2018
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerWifiP2PChangeListener. This class provides an interface that listen to Peer Availability
 *
 */
public interface ContextualManagerWifiP2PChangeListener {
		void onFoundGroup(ArrayList<ContextualManagerAP> disc, ArrayList<ContextualManagerAP> reg);
		void onPeersFound(ArrayList<ContextualManagerAP> disc);
}
