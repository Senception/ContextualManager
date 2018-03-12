package com.senception.contextualmanager.modals;

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
 * @file Contains ContextualManagerAppUsage. This class handles the
 * the capture and persistence of the resource usage of all apps in the device.
 *
 */
public class ContextualManagerAppUsage {

    private final int HOURLY = 23;
    private final int SECONDLY = 59;

    private String appName;
    private String appCategory;
    private ArrayList<Integer> usagePerHour = new ArrayList<>();
    private int dayOfTheWeek;

    public ContextualManagerAppUsage(){super();}

    public ContextualManagerAppUsage(String appName, String appCategory) {

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

    public void setUsagePerHour(String usagePerHour) {
        String [] items = usagePerHour.split("\\.");
        for (String s : items ) {
            this.usagePerHour.add(Integer.parseInt(s));
        }
    }

    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(String dayOfTheWeek) {
        this.dayOfTheWeek = Integer.parseInt(dayOfTheWeek);
    }

    @Override
    public String toString(){
        return "\n\t" + this.getAppName() + "\n\t" + this.getUsagePerHour() + "\n\t" + this.getAppCategory() + "\n\t" + this.getDayOfTheWeek();
    }
}
