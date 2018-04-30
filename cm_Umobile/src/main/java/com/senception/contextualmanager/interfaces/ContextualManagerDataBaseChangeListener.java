package com.senception.contextualmanager.interfaces;

import com.senception.contextualmanager.modals.ContextualManagerAP;

import java.util.List;

/**
 * Copyright (C)  Senception Lda
 *  Author(s): Igor dos Santos - degomosIgor@senception.com
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017/2018
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerDataBaseChangeListener. This class provides an interface that listen to DataBase
 *
 */
public interface ContextualManagerDataBaseChangeListener {
	void onDataChange(List<ContextualManagerAP> apEntries);
	void onStatusMessageChange(String newMessage);
}