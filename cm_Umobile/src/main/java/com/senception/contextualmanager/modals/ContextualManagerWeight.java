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
 * save the weights  A (Availability) and C (Centrality).
 *
 */
public class ContextualManagerWeight {

    private int id;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    private String dateTime;
    private double A;
    private double C;
    private int dayOfTheWeek;

    /**
     * Contextual Manager Weight Constructor.
     */
    public ContextualManagerWeight() { super();}

    /**
     * Contextual Manager Weight Constructor.
     * @param dayOfTheWeek the date time.
     */
    public ContextualManagerWeight(int dayOfTheWeek) {
        A = -1;
        C = -1;
        this.dayOfTheWeek = dayOfTheWeek;
        dateTime = dateFormat.format(System.currentTimeMillis());

    }

    /**
     * Get the id of this weight
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Set he id of this weight.
     * @param id the id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the date time.
     * @return the dateTime.
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * Updates the date time.
     */
    public void updateDateTime() {
        this.dateTime =  dateFormat.format(System.currentTimeMillis());
    }

    /**
     * Get availability.
     * @return the availability.
     */
    public double getA() {
        return A;
    }

    /**
     * Set the availability.
     * @param A the availability to be set as double.
     */
    public void setA(double A){
        this.A = A;
    }

    /**
     * Set the availability.
     * @param A the availability to be set as string.
     */
    public void setA(String A){
        this.A = Double.parseDouble(A);
    }

    /**
     * Get the centrality.
     * @return the centrality
     */
    public double getC() {
        return C;
    }

    /**
     * Set the centrality.
     * @param C the centrality to set as double.
     */
    public void setC(double C){
        this.C = C;
    }

    /**
     * Set the centrality.
     * @param C the centrality to set as string.
     */
    public void setC(String C){
        this.C = Double.parseDouble(C);
    }

    /**
     * Get the day of the week.
     * @return the day of the week.
     */
    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    /**
     * Set the day of the week.
     * @param dayOfTheWeek The day of the week to set as integer.
     */
    public void setDayOfTheWeek(int dayOfTheWeek){
        this.dayOfTheWeek = dayOfTheWeek;
    }

    /**
     * Set the day of the week.
     * @param dayOfTheWeek the day of the week to set as string.
     */
    public void setDayOfTheWeek(String dayOfTheWeek) {
        this.dayOfTheWeek = Integer.parseInt(dayOfTheWeek);
    }
}
