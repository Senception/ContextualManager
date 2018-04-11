package com.senception.contextualmanager.aidl;

/*
 * This aidl file contains all the methods that contextual manager
 * provides to clients.
 * */
interface CManagerInterface {

    Map getAvailability(in List<String> peerList);
    Map getCentrality(in List<String> peerList);
    Map getSimilarity(in List<String> peerList);

    //ContextualManagerSimilarity
    //double [] getI(TYPE, NODE);

    //List<String> getAll(in);
    //"A.C.I"
}