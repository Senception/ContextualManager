package com.senception.contextualmanager.aidl;

/*
 * Copyright Senception Lda
 * @author Jose Soares (jose.soares@senception.com)
 * This aidl file contains all the methods that contextual manager
 * provides to clients.
 * */
interface CManagerInterface {

    Map getAvailability(in List<String> peerList);
    Map getCentrality(in List<String> peerList);
    Map getSimilarity(in List<String> peerList);


}