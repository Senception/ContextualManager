package com.senception.cmumobile.inference;

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
 * @file Contains Centrality. Class that handles the inference of the centrality.
 */
// (A/C) Affinity/Centrality
public class Centrality {

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

    public static ArrayList<Integer> calculateA(String encounter, double encounterDuration, String d){
        //Todo: Calculate A
        return null;
    }


}
