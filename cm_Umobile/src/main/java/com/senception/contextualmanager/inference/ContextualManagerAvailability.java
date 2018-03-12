package com.senception.contextualmanager.inference;

import java.util.ArrayList;
import java.util.Calendar;

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
//(A) Usage/Availability
public class ContextualManagerAvailability {

    //Formulas:
    // 1) U(i) = Σ r(i) / T
    // 2) R(i) = B(i) * B(i) * CPU(i) * MEM(i) * STORAGE(i),     r(i) ⊂ [0,1]

    /**
     * Calculates a number that measures if a device is viable to conduct communication with in a given time.
     * @param rList list of all R's calculated so far.
     * @return U the array
     */
    public static ArrayList<Integer> calculateA(ArrayList<ArrayList<Integer>> rList){
        ArrayList<Integer> U = rList.get(0);
        for (int i = 1; i < rList.size(); i++) {
            U = sumArrays(U, rList.get(i)); //U = R1 // U = U(R1) + R2 + R3 --> R1+R2
        }
        return U;
    }

    /**
     * This method is an auxiliar method that sums array lists and for each sum,
     * divides the result for the instant time.
     * E.G.: (1st elem of list1 + 1st elem of list2 )/ Time ... (nth elem of list1 + nth elem of list2 )/ Time
     * @param usagePerHour1 list of usage 1 (list1)
     * @param usagePerHour2 list of usage 2 (list2)
     * @return the resulting list with the sum's result in each index.
     */
    private static ArrayList<Integer> sumArrays(ArrayList<Integer> usagePerHour1, ArrayList<Integer> usagePerHour2){

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // hour

        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < usagePerHour1.size(); i++) {
            //-1 + -1 = -1
            if(usagePerHour1.get(i) == -1 && usagePerHour2.get(i) == -1){
                res.add(usagePerHour1.get(i));
            }
            //-1+0,3 = 0,3 --> 0,3/currentHour
            else if ( usagePerHour1.get(i) == -1 && usagePerHour2.get(i) != -1){
                res.add(usagePerHour2.get(i) / hour);
            }
            //0,3+-1 = 0,3 --> 0,3/currentHour
            else if (usagePerHour2.get(i) == -1 && usagePerHour1.get(i) != -1){
                res.add(usagePerHour1.get(i) / hour);
            }
            //0,3+0,3 = 0.6 --> 0.6/currentHour
            else{
                res.add((usagePerHour1.get(i) + usagePerHour2.get(i)) / hour);
            }
        }
        return res;
    }

    /**
     * This method calculates a number that will be used to measure
     * whether or not a device is good to establish connection to.
     * @param e the physical resource usage energy
     * @param cpu the physical resource usage cpu
     * @param mem the physical resource usage memory
     * @param storage the physical resource usage storage
     * @return e * e * cpu * mem * storage (e*e to give more value to the batery status)
     */
    public static ArrayList<Integer> calculateR(ArrayList<Integer> e, ArrayList<Integer>
            cpu, ArrayList<Integer> mem, ArrayList<Integer> storage){
        ArrayList<Integer> e2 = multiplyArrays(e, e); // e square ---> working
        ArrayList<Integer> e2Cpu = multiplyArrays(e2, cpu); // e2 * cpu ---> working
        ArrayList<Integer> memStor = multiplyArrays(mem, storage); // mem * storage ---> working

        ArrayList<Integer> r = multiplyArrays(e2Cpu, memStor); // testing
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
            //-1*0,3 = 0,3
            else if ( usagePerHour1.get(i) == -1 && usagePerHour2.get(i) != -1){
                res.add(usagePerHour2.get(i));
            }
            //0,3*-1 = 0,3
            else if (usagePerHour1.get(i) != -1 && usagePerHour2.get(i) == -1 ){
                res.add(usagePerHour1.get(i));
            }
            //0,3*0,3 = 0.09
            else {
                res.add(usagePerHour1.get(i) * usagePerHour2.get(i));
            }
        }
        return res;
    }
}
