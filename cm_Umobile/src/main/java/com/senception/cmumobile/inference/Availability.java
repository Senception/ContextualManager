package com.senception.cmumobile.inference;

import android.util.Log;

import com.senception.cmumobile.resource_usage.physical_usage.PhysicalResourceUsage;

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
 * @file Contains Availability. Class that handles the inference of the availability.
 */
//(U) Usage/Availability
public class Availability {

    //Formulas:
    // 1) U(i) = Σ r(i) / T
    // 2) R(i) = B(i) * B(i) * CPU(i) * MEM(i) * STORAGE(i),     r(i) ⊂ [0,1]

    /**
     * Calculates a number that measures if a device is a viable one to conduct communication with.
     * @param rList list of all R's calculated so far.
     * @return U the array
     */
    public static ArrayList<Integer> calculateU(ArrayList<ArrayList<Integer>> rList){
        //1)
        ArrayList<Integer> U = rList.get(0);
        for (int i = 1; i < rList.size(); i++) {
            U = sumArrays(U, rList.get(i)); //U = R1 // U = U + R2 + R3 --> R1+R2
        }

        //2)
        /*ArrayList<Integer> U = rList.get(0);
        for (int i = 1; i < rList.size(); i+=2) {
            for (int j = 0; j < rList.get(i).size(); j++) {
                U.add(rList.get(i).get(j) + rList.get(i+1).get(j));
            }
        }*/
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

        Calendar t = Calendar.getInstance();
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < usagePerHour1.size(); i++) {
            //-1 + -1 = -1
            if(usagePerHour1.get(i) == -1 && usagePerHour2.get(i) == -1){
                res.add(usagePerHour1.get(i));
            }
            //-1+0,3 = 0,3
            else if ( usagePerHour1.get(i) == -1 && usagePerHour2.get(i) != -1){
                res.add(usagePerHour2.get(i) / t.get(Calendar.HOUR_OF_DAY));
            }
            //0,3+-1 = 0,3
            else if (usagePerHour2.get(i) == -1 && usagePerHour1.get(i) != -1){
                res.add(usagePerHour2.get(i) / t.get(Calendar.HOUR_OF_DAY));
            }
            //0,3+0,3 = 0.6
            else
                res.add(usagePerHour1.get(i)+usagePerHour2.get(i) / t.get(Calendar.HOUR_OF_DAY));
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
    public static ArrayList<Integer> calculateR(PhysicalResourceUsage e, PhysicalResourceUsage
            cpu, PhysicalResourceUsage mem, PhysicalResourceUsage storage){

        ArrayList<Integer> b2 = multiplyArrays(e.getUsagePerHour(), e.getUsagePerHour()); // [0,1,2,...24]
        ArrayList<Integer> c = cpu.getUsagePerHour();
        ArrayList<Integer> m = mem.getUsagePerHour();
        ArrayList<Integer> s = storage.getUsagePerHour();

        ArrayList<Integer> r = multiplyArrays(multiplyArrays(b2, c), multiplyArrays(m, s));
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
            else
                res.add(usagePerHour1.get(i)*usagePerHour2.get(i));
        }
        return res;
    }
}
