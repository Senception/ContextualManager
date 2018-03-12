package com.senception.contextualmanager.modals;

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
 * @file Contains ContextualManagerWeight. This class represents an object that will
 * save the weights A (ContextualManagerCentrality) and U (Availability).
 *
 */
public class ContextualManagerWeight {
    private final int HOURLY = 23;
    private final int SECONDLY = 59;

    private int id;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    private String dateTime;
    private ArrayList<Integer> A = new ArrayList<>();
    private ArrayList<Integer> C = new ArrayList<>();
    private int dayOfTheWeek;


    public ContextualManagerWeight() { super();}

    public ContextualManagerWeight(String dateTime) {
        A = new ArrayList<>(SECONDLY);
        C = new ArrayList<>(SECONDLY);
        //fill both A and C with -1 (24 of them, since we're capturing this every hour-> 24h a day)
        for (int i = 0; i <= SECONDLY; i++){
            A.add(i, -1);

            C.add(i, -1);
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

    public void setA(ArrayList<Integer> A){
        this.A = A;
    }

    public void setA(String A) {
        String [] items = A.split("\\.");
        for (String s : items ) {
            this.A.add(Integer.parseInt(s));
        }
    }

    public ArrayList<Integer> getC() {
        return C;
    }

    public void setC(ArrayList<Integer> C){
        this.C = C;
    }

    public void setC(String C) {
        String [] items = C.split("\\.");
        for (String s : items ) {
            this.C.add(Integer.parseInt(s));
        }
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
