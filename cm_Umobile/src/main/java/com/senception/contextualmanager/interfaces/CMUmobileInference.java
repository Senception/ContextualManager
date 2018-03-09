package com.senception.contextualmanager.interfaces;

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
 * @file Contains the Inference interface. Every class that implements
 * this interface will have to implement the methods declared in it.
 */
public interface CMUmobileInference {

    /**
     * Method to calculate U - availability
     * @return the availability per hour in the given instant.
     */
    public ArrayList<Integer> getA();

    /**
     * Method to calculate C - centrality
     * @return the centrality per hour in the given instant.
     */
    public ArrayList<Integer> getC();

    /**
     * Method to calculate I - similarity
     * @return the similarity per hour in the given instant.
     */
    public ArrayList<Integer> getI();

    /**
     * Gets a list with the availability, centrality and similarity.
     * @return list of all three parameters.
     */
    public ArrayList<ArrayList<Integer>> getAll();
}
