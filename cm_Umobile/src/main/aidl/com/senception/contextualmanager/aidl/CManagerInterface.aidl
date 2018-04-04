package com.senception.contextualmanager.aidl;

/*
 * This aidl file contains all the methods that contextual manager
 * provides to clients.
 * */
interface CManagerInterface {

    double [] getAvailability(in String [] peerList);
    double [] getCentrality(in String [] peerList);

    //ContextualManagerSimilarity
    //double [] getI(TYPE, NODE);

    //List<String> getAll(in);
    //"A.C.I"
}