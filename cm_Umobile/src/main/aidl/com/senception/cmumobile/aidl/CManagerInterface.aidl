// IinferenceHandler.aidl
package com.senception.cmumobile.aidl;
//import com.senception.cmumobile.Availability;
//import com.senception.cmumobile.Centrality;

// Declare any non-default types here with import statements

interface CManagerInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    //int[] mIntArray = mList.toArray(mList.size());

    int test();
    int [] getAvailability(in int[] peerList);
    //Availability getAvailability(in int[] peerList);
    //Centrality getCentrality(in int[] peerList);
    //int[] getAll(in int[] peerList);

}
