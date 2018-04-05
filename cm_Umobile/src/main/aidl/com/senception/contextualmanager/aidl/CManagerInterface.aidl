package com.senception.contextualmanager.aidl;

/*
 * This aidl file contains all the methods that contextual manager
 * provides to clients.
 * */
interface CManagerInterface {

    //double [] getAvailability(in String [] peerList);
    //double [] getCentrality(in String [] peerList);
    //Map getA(in String [] peerList); //String [] to ArrayList<String>
    //Map getC(in String [] peerList); //String [] to ArrayList<String>
    Map getAvailability(in List<String> peerList);
    Map getCentrality(in List<String> peerList);

    //ContextualManagerSimilarity
    //double [] getI(TYPE, NODE);

    //List<String> getAll(in);
    //"A.C.I"
}