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
 * @file Contains ContextualManagerCentrality. Class that handles the inference of the centrality.
 */
public class ContextualManagerCentrality {

    //Formula: C(i) = 1/λ Σj A(j) * p(j)

    /*
     p(j)= (encounter * average_encounter_duration)/ (d(i,j) + 1)
     encounter: encounter between a pair
     average_encounter_duration: avg duration of an encounter between a pair
     d(i,j):
     A(j) = adjacency's matrix (vector of 1s e 0s) -- considered allways as 1. 1-connected | 0-disconnected

     λ ∈ [0,1]
     A(j): adjancency
     d(i,j) ∈ [0,100]
    */

    /**
     * Method that calculates the centrality of a device, using
     * it's peer's average encounter duration and the number of encounters.
     * (Eigenvector)
     * @param dataSource
     * @return centrality - the centrality of the device
     */
    //NOTE: In the beggining the calculated C will present often as 0.0
    public static double calculateC(ContextualManagerDataSource dataSource){
        //double degree; --> to use later
        int numEncounters;
        double avgEncDur;
        double distance = 1; // we're not taking into consideration the distance in this implementation -> only in the future
        double lambda = 0.8; // should be the max of a lambda vector.

        //get peer list
        ArrayList<ContextualManagerAP> peerList;
        double centrality = 0;
        if(!dataSource.isTableEmpty(checkWeek("peers"))) {
            peerList = dataSource.getAllPeers(checkWeek("peers"));

            for (ContextualManagerAP peer : peerList) {
                //if peer is connected (future) --> to use later
                numEncounters = peer.getNumEncounters();
                avgEncDur = peer.getAvgEncounterDuration();
                centrality += numEncounters*avgEncDur/distance;
            }
        }
        centrality = centrality * (1/lambda);
        return centrality;
    }
}
