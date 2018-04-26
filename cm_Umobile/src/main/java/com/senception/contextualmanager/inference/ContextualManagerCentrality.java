package com.senception.contextualmanager.inference;

import com.senception.contextualmanager.databases.ContextualManagerDataSource;
import com.senception.contextualmanager.modals.ContextualManagerAP;
import com.senception.contextualmanager.services.ContextualManagerCaptureService;

import java.util.ArrayList;
import static com.senception.contextualmanager.services.ContextualManagerService.checkWeek;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@senception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @author Rute Sofia
 * @version 0.1
 *
 * @file Contains ContextualManagerCentrality. Class that handles the centrality computation.
 */
public class ContextualManagerCentrality {

    //Formula: C(i) = 1/λ Σj A(j) * p(j)

    /*
     p(j)= (encounter * average_encounter_duration)/ (d(i,j) + 1)
     encounter: encounter between a pair
     average_encounter_duration: avg duration of an encounter between a pair
     d(i,j): distance in meters between nodes
     A(j) = adjacency's matrix  1-connected | 0-disconnected

     λ ∈ [0,1]
     A(j): adjacency
     d(i,j) ∈ [0,100]
    */

    /**
     * Method that calculates the centrality of a device, based on an adaptation
     * of Eigenvalue centrality.
     * Formula considers, for node i and neighbors j, the numEncounters(i,j) and the
     * avgEncounterDuration(i,j)
     * it splits the numEncounters*avgEncounterDuration per d, which is the distance in meters
     * @todo use the Haversine formula to compute the distance between the two nodes
     * Currently, we have set distance to always be one.
     * @param dataSource
     * @return centrality - the centrality of the device
     */
    //NOTE: In the beginning the calculated C will present often as 0.0
    public static double calculateC(ContextualManagerDataSource dataSource){

        int numEncounters=0;
        double avgEncDur=0;
        double distance = 1; // we're not taking into consideration the distance in this implementation -> only in the future
        double lambda = 1;
        double i=0;
        /* work on peer list
         * and compute centrality
         */
        ArrayList<ContextualManagerAP> peerList;
        double centrality = 0;
        if(!dataSource.isTableEmpty(checkWeek("peers"))) {
            peerList = dataSource.getAllPeers(checkWeek("peers"));
            i=0;
            for (ContextualManagerAP peer : peerList) {
                if (peer.getIsConnected() == 1) {
                    // @todo distance based on Haversive formula
                    centrality = centrality+(peer.getNumEncounters()*peer.getAvgEncounterDuration())/distance;
                ++i;
                }

            }
        /* simplification of lambda, just the ratio of connected peers against all peers
        * the more connected peers, the higher the centrality
        */
        lambda = i/peerList.size();
        }

        centrality = centrality * 1/lambda;
       // centrality = centrality/(System.currentTimeMillis()- ContextualManagerCaptureService.TIMESTAMP)/60000;
        return centrality;
    }
}
