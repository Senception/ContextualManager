package com.senception.contextualmanager.inference;

import com.senception.contextualmanager.databases.ContextualManagerDataSource;
import com.senception.contextualmanager.modals.ContextualManagerAP;

import java.util.ArrayList;

import static com.senception.contextualmanager.services.ContextualManagerService.checkWeek;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerSimilarity. Class that handles the inference of the similarity.
 *
 */
// TODO I: similarity
public class ContextualManagerSimilarity {

    //I = numEnc*AvgDuration
    public static double calculateI(ContextualManagerDataSource dataSource) {
        double similarity = 0;
        //get peer list
        ArrayList<ContextualManagerAP> peerList;
        if(!dataSource.isTableEmpty(checkWeek("peers"))) {
            peerList = dataSource.getAllPeers(checkWeek("peers"));

            for (ContextualManagerAP peer : peerList) {
                double numEncounters = peer.getNumEncounters();
                double avgEncDur = peer.getAvgEncounterDuration();
                similarity = numEncounters*avgEncDur;
            }
        }
        return similarity;
    }
}
