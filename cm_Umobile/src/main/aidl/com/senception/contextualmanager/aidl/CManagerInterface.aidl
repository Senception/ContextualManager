package com.senception.contextualmanager.aidl;

//import com.senception.cmumobile.inference.MyObject;

/*
 * This aidl file contains all the methods that contextual manager
 * provides to clients.
 * */
interface CManagerInterface {

    double [] getAvailability(in String [] peerList);
    double [] getCentrality(in String [] peerList);

    //ContextualManagerSimilarity
    //int [] getI(TYPE, NODE);

    //    List<MyObject> getAll(in int[] peerList);

    //List<String> getAll(in);
    //"A.C"
}
