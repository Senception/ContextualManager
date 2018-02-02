package com.senception.cmumobile.inference;

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
     λ ∈ [0,1]
     A(j): adjancency
     d(i,j) ∈ [0,100]
    */
}
