package com.senception.contextualmanager.inference;

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
 * @file Contains ContextualManagerAvailability. Class that handles the inference of the availability.
 */
public class ContextualManagerAvailability {

    /*Formulas:
     * 1) A(i) = Σ r(i) / T
     * 2) R(i) = B(i) * B(i) * CPU(i) * MEM(i) * STORAGE(i),     r(i) ⊂ [0,1]
    */

    /**
     * Calculates a number that measures if a device is viable to conduct communication with in a given time.
     * @param rList list of all R's calculated so far.
     * @return U the array
     */
    public static ArrayList<Double> calculateA(ArrayList<ArrayList<Integer>> rList){
        ArrayList<Double> A = new ArrayList<>();

        for (int j = 0; j < rList.get(0).size(); j++) {
            A.add((double)rList.get(0).get(j)); //if there's only 1 R -> A = that R converted to double
        }
        if( rList.size() == 1){
            return A;
        }
        else { //if there's more then 1 R -> A = A + R1 + R2...RN
            for (int i = 1; i < rList.size(); i++) {
                A = sumArrays(A, rList.get(i)); //A = R0 //A = A + R2 + R3 --> R1+R2
            }
        }

        return A;
    }

    /**
     * This method is an auxiliar method that sums array lists and for each sum,
     * divides the result for the instant time.
     * E.G.: (1st elem of list1 + 1st elem of list2 )/ Time ... (nth elem of list1 + nth elem of list2 )/ Time
     * @param usagePerHour1 list of usage 1 (list1)
     * @param usagePerHour2 list of usage 2 (list2)
     * @return the resulting list with the sum's result in each index.
     */
    private static ArrayList<Double> sumArrays(ArrayList<Double> usagePerHour1, ArrayList<Integer> usagePerHour2){

        ArrayList<Double> res = new ArrayList<>();
        for (int i = 0; i < usagePerHour1.size(); i++) {
            //-1 + -1 = -1
            if(usagePerHour1.get(i) == -1 && usagePerHour2.get(i) == -1){
                res.add(usagePerHour1.get(i));
            }
            //-1+30 = 30 --> 30/currentHour
            else if ( usagePerHour1.get(i) == -1 && usagePerHour2.get(i) != -1){
                res.add((double) usagePerHour2.get(i) / System.currentTimeMillis()/1000);
            }
            //30+-1 = 30 --> 30/currentHour
            else if (usagePerHour2.get(i) == -1 && usagePerHour1.get(i) != -1){
                res.add(usagePerHour1.get(i) / System.currentTimeMillis()/1000);
            }
            //30+30 = 60 --> 60/currentHour
            else{
                res.add((usagePerHour1.get(i) + usagePerHour2.get(i)) / System.currentTimeMillis()/1000); //todo optimize without dividing for the hour in the sumArrays method
            }
        }
        return res;
    }

    /**
     * This method calculates an array of numbers that will be used to measure
     * whether or not a device is good to establish connection to.
     * @param e the physical resource usage energy
     * @param cpu the physical resource usage cpu
     * @param mem the physical resource usage memory
     * @param storage the physical resource usage storage
     * @return e * e * cpu * mem * storage (e*e to give more value to the batery status)
     */
    public static ArrayList<Integer> calculateR(ArrayList<Integer> e, ArrayList<Integer>
            cpu, ArrayList<Integer> mem, ArrayList<Integer> storage){
        ArrayList<Integer> e2 = multiplyArrays(e, e); // e square
        ArrayList<Integer> e2Cpu = multiplyArrays(e2, cpu); // e2 * cpu
        ArrayList<Integer> memStor = multiplyArrays(mem, storage); // mem * storage
        ArrayList<Integer> r = multiplyArrays(e2Cpu, memStor);
        return r;
    }

    /**
     * This method is an auxiliar method that multiplies array lists
     * 1st elem of list1 * 1st elem of list2 ... nth elem of list1 * nth elem of list2
     * @param usagePerHour1 list of usage (list1)
     * @param usagePerHour2 list of usage (list2)
     * @return the resulting list with the multiplication's result in each index.
     */
    private static ArrayList<Integer> multiplyArrays(ArrayList<Integer> usagePerHour1, ArrayList<Integer> usagePerHour2){
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < usagePerHour1.size(); i++) {
            //-1 * -1 = -1
            if(usagePerHour1.get(i) == -1 && usagePerHour2.get(i) == -1){
                res.add(usagePerHour1.get(i));
            }
            //-1*30 = 30
            else if ( usagePerHour1.get(i) == -1 && usagePerHour2.get(i) != -1){
                res.add(usagePerHour2.get(i));
            }
            //30*-1 = 30
            else if (usagePerHour1.get(i) != -1 && usagePerHour2.get(i) == -1 ){
                res.add(usagePerHour1.get(i));
            }
            //30*30 = 900
            else {
                res.add(usagePerHour1.get(i) * usagePerHour2.get(i));
            }
        }
        return res;
    }
}
