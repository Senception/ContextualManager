package com.senception.contextualmanager.interfaces;

import com.senception.contextualmanager.modals.ContextualManagerAP;

import java.util.List;

/**
 * Copyright (C) 2016 Senception Lda
 *  Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
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