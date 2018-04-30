package com.senception.contextualmanager.wifi.p2p;

/**
 * Copyright Senception Lda
 * Author(s): Miguel Tavares
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2018
 * @author Miguel Tavares (COPELABS/ULHT)
 * @author José Soares (Senception Lda)
 * @version 1.1, 2018
 *
 * @file Used to indentify the peer as a cm node, and to
 * indicate that it will be transmitting the availability,
 * centrality and similarity.
 */
public abstract class Identity {

    public static final String SVC_INSTANCE_TYPE = "cm";
    public static final String AVAILABILITY = "A";
    public static final String CENTRALITY = "C";
    public static final String SIMILARITY = "I";

}
