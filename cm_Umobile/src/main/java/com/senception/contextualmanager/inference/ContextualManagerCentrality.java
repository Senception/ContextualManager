package com.senception.contextualmanager.inference;

import android.util.Log;

import com.senception.contextualmanager.databases.ContextualManagerDataSource;
import com.senception.contextualmanager.modals.ContextualManagerAP;

import java.lang.reflect.Array;
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
// (C) Affinity/ContextualManagerCentrality
public class ContextualManagerCentrality {

    //Formula: C(i) = 1/λ Σj A(j) * p(j)

    /*
     p(j)= (encounter * average_encounter_duration)/ (d(i,j) + 1)
     encounter: encounter between a pair
     average_encounter_duration: avg duration of an encounter between a pair
     d(i,j):

     λ ∈ [0,1]
     A(j): adjancency
     d(i,j) ∈ [0,100]
    */

    private static int numEncounters;
    private static long avg_enc_dur;
    private static long currentDur;

    public static double calculateC(ContextualManagerDataSource dataSource){
        double centrality = -1;

        //Todo: Calculate C
        //get peer list
        ArrayList<ContextualManagerAP> peerList;

        if(!dataSource.isTableEmpty(checkWeek("peers"))) {
            peerList = dataSource.getAllPeers(checkWeek("peers"));

            double totalDuration = 0;
            for (ContextualManagerAP peer : peerList) {
                totalDuration += peer.getAvgEncounterDuration();
            }
            Log.d("teste", "Total duration of avgEnc : " + totalDuration);
        }

        return centrality;
    }


}
