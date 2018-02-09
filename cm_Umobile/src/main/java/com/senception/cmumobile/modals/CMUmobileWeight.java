package com.senception.cmumobile.modals;

import android.util.Log;

import java.text.SimpleDateFormat;
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
 * @file Contains CMUmobileWeight. This class represents an object that will
 * save the weights A (Centrality) and U (Availability).
 *
 */
public class CMUmobileWeight {
    private final int HOURLY = 23;
    private final int SECONDLY = 59;

    private int id;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    private String dateTime;
    private ArrayList<Integer> A = new ArrayList<>();
    private ArrayList<Integer> U = new ArrayList<>();
    private int dayOfTheWeek;


    public CMUmobileWeight() { super();}

    public CMUmobileWeight(String dateTime) {
        A = new ArrayList<>(SECONDLY);
        U = new ArrayList<>(SECONDLY);
        //fill both A and U with -1 (24 of them, since we're capturing this every hour-> 24h a day)
        for (int i = 0; i <= SECONDLY; i++){
            A.add(i, -1);

            U.add(i, -1);
        }
        Calendar day = Calendar.getInstance();
        dayOfTheWeek = day.get(Calendar.DAY_OF_WEEK);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Integer> getA() {
        return A;
    }

    public void setA(String A) {
        String [] items = A.split("\\.");
        for (String s : items ) {
            this.A.add(Integer.parseInt(s));
        }
    }

    public void setU(ArrayList<Integer> U){
        this.U = U;
    }

    public void setU(String U) {
        String [] items = U.split("\\.");
        for (String s : items ) {
            this.U.add(Integer.parseInt(s));
        }
    }

    public ArrayList<Integer> getU() {
        return U;
    }

    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(String dayOfTheWeek) {
        this.dayOfTheWeek = Integer.parseInt(dayOfTheWeek);
    }

    public void setDayOfTheWeek(int dayOfTheWeek){
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void updateDateTime() {
        this.dateTime =  dateFormat.format(System.currentTimeMillis());
    }
}
