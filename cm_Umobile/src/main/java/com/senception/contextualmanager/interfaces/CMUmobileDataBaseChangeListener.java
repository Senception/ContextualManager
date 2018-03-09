/**
 * Copyright (C) 2016 Senception Lda
 *  Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains CMUmobileDataBaseChangeListener. This class provides an interface that listen to DataBase
 *
 */

package com.senception.contextualmanager.interfaces;

import com.senception.contextualmanager.modals.CMUmobileAP;

import java.util.List;

public interface CMUmobileDataBaseChangeListener {
	void onDataChange(List<CMUmobileAP> apEntries);
	void onStatusMessageChange(String newMessage);
}