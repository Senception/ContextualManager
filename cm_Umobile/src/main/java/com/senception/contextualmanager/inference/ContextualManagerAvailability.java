package com.senception.contextualmanager.inference;

import android.util.Log;

import com.senception.contextualmanager.activities.ContextualManagerMainActivity;
import com.senception.contextualmanager.services.ContextualManagerCaptureService;

import java.util.ArrayList;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@senception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017/2018
 * @author Igor dos Santos
 * @author José Soares
 * @author Rute Sofia
 * @version 0.1
 *
 * @file Contains ContextualManagerAvailability. Class that handles the inference of the availability.
 */
public class ContextualManagerAvailability {

    /*Formulas:
     * 1) A(i) = Σ r(i) / t, A(i) ⊂ [0,1]
     * 2) R(i) = B(i) * B(i) * CPU(i) * MEM(i) * STORAGE(i),     r(i) ⊂ [0,1]
    */

    /**
     * Calculates a number that measures if a device is viable to conduct communication with in a given time.
     * @param rList list of all R's calculated so far.
     * @return A Availability
     */

    public static double calculateA(ArrayList<Double> rList) {
       double Availability = 0;
        int t=0;
        for (int i =0; i < rList.size(); i++) {
            if (rList.get(i) != -1) {
                ++t;
                Availability = Availability + rList.get(i);
            }
        }
        // Availability is an weighted average of all values of r
        // @todo improve the average accuracy, by using another type of weighted average
        Availability = Availability/t;
        Log.d("teste", "r is:"+rList.toString());
        Log.d("teste", "t is:"+t);
        Log.d("teste", "Availability A  = " + Availability);
     return Availability;
    }

    /**
     * This method is an auxiliar method that sums array lists and for each sum,
     * divides the result for the instant time.
     * E.G.: (1st elem of list1 + 1st elem of list2 )/ Time ... (nth elem of list1 + nth elem of list2 )/ Time
     * @param usagePerHour1 list of usage 1 (list1)
     * @param usagePerHour2 list of usage 2 (list2)
     * @return the resulting list with the sum's result in each index.
     */
    private static ArrayList<Double> sumArrays(ArrayList<Double> usagePerHour1, ArrayList<Double> usagePerHour2){

        ArrayList<Double> res = new ArrayList<>();
        for (int i = 0; i < usagePerHour1.size(); i++) {
            //-1 + -1 = -1
            if(usagePerHour1.get(i) == -1 && usagePerHour2.get(i) == -1){
                res.add(usagePerHour1.get(i));
            }
            //-1+30 = 30 --> 30/currentHour
            else if ( usagePerHour1.get(i) == -1 && usagePerHour2.get(i) != -1){
                res.add(usagePerHour2.get(i) / (System.currentTimeMillis() - ContextualManagerCaptureService.TIMESTAMP)/60*1000);
            }
            //30+-1 = 30 --> 30/currentHour
            else if (usagePerHour2.get(i) == -1 && usagePerHour1.get(i) != -1){
                res.add(usagePerHour1.get(i) / (System.currentTimeMillis() - ContextualManagerCaptureService.TIMESTAMP)/60*1000);
            }
            //30+30 = 60 --> 60/currentHour
            else{ // Maybe we should use the time in hours instead of minutes
                res.add((usagePerHour1.get(i) + usagePerHour2.get(i)) / ( (System.currentTimeMillis() - ContextualManagerCaptureService.TIMESTAMP)/60*1000)); //todo optimize without dividing for the hour in the sumArrays method
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
    public static ArrayList<Double> calculateR(ArrayList<Double> e, ArrayList<Double>
            cpu, ArrayList<Double> mem, ArrayList<Double> storage){
        Log.d("teste", "e = " + e.toString());
        Log.d("teste", "cpu = " + cpu.toString());
        Log.d("teste", "mem = " + mem.toString());
        Log.d("teste", "s = " + storage.toString());
        ArrayList<Double> e2 = multiplyArrays(e, e); // e square
        ArrayList<Double> e2Cpu = multiplyArrays(e2, cpu); // e2 * cpu
        ArrayList<Double> memStor = multiplyArrays(mem, storage); // mem * storage
        ArrayList<Double> r = multiplyArrays(e2Cpu, memStor);
        //todo resources between [0..100], but we need r between [0..1]
        double d = Math.pow(100,5);
        for (int i = 0; i < r.size(); i++) {
            if(r.get(i) != -1){
                r.set(i, r.get(i) / d);
            }
        }
        return r;
    }

    /**
     * This method is an auxiliar method that multiplies array lists
     * 1st elem of list1 * 1st elem of list2 ... nth elem of list1 * nth elem of list2
     * @param usagePerHour1 list of usage (list1)
     * @param usagePerHour2 list of usage (list2)
     * @return the resulting list with the multiplication's result in each index.
     */
    private static ArrayList<Double> multiplyArrays(ArrayList<Double> usagePerHour1, ArrayList<Double> usagePerHour2){
        ArrayList<Double> res = new ArrayList<>();
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
