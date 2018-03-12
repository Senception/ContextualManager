package com.senception.contextualmanager.inference;

import com.senception.contextualmanager.databases.ContextualManagerDataSource;

import java.util.ArrayList;

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
     encounter: encontro entre 2 pares
     average_encounter_duration: média de duração entre encontro desses 2 pares
     d(i,j):

     λ ∈ [0,1]
     A(j): adjancency
     d(i,j) ∈ [0,100]
    */

    private static int numEncounters;
    private static long avg_enc_dur;
    private static long currentDur;

    public static ArrayList<Integer> calculateC(ContextualManagerDataSource dataSource){
        ArrayList<Integer> centrality = new ArrayList<>();
        for (int i = 0; i <= 59; i++){
            centrality.add(i, -1);
        }

        //1) Get the encounter duration

        //2) Get the number of encounters
        //3) Get the average encounter duration





        //get peer list
        //ArrayList<ContextualManagerAP> peersList = dataSource.getAllAP(,ContextualManagerService.checkWeek("ap"));

        //get peers number of connections/encounters (list.length)
        //get those encounter durations
        //calculate avg duration
        //Todo: Calculate C


        return centrality;
    }


}
