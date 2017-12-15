package com.senception.cmumobile.resource_usage.app_usage;

import android.util.Log;

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
 * @file Contains AppResourceUsage. This class represents an object that
 * records all the resource usage of an app.
 *
 */
public class AppResourceUsage {

    private final int HOURLY = 23;
    private final int SECONDLY = 59;

    private String appName;
    private String appCategory;
    private ArrayList<Integer> usagePerHour;
    private int dayOfTheWeek;

    public AppResourceUsage(String appName, String appCategory) {

        this.appName = appName;
        this.appCategory = appCategory;
        this.usagePerHour = new ArrayList<>(SECONDLY);
        for (int i = 0; i <= SECONDLY; i++){
            usagePerHour.add(i, -1);
        }

        //Log.d("Resource", usagePerHour.toString());

        Calendar day = Calendar.getInstance();
        dayOfTheWeek = day.get(Calendar.DAY_OF_WEEK);
    }

    public String getAppName(){
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCategory() {
        return this.appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    public ArrayList<Integer> getUsagePerHour() {
        return this.usagePerHour;
    }

    public void setUsagePerHour(ArrayList<Integer> usagePerHour) {
        this.usagePerHour = usagePerHour;
    }

    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(int dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    @Override
    public String toString(){
        return "\n\t" + this.getAppName() + "\n\t" + this.getUsagePerHour() + "\n\t" + this.getAppCategory() + "\n\t" + this.getDayOfTheWeek();
    }
}
