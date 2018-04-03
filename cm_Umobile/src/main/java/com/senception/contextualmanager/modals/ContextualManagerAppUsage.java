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

    //Todo private final int HOURLY = 23;
    private final int SECONDLY = 59;

    private String appName;
    private String appCategory;
    private ArrayList<Integer> usagePerHour = new ArrayList<>();
    private int dayOfTheWeek;

    /**
     * Contextual Manager App Usage Constructor.
     */
    public ContextualManagerAppUsage(){super();}

    /**
     * Contextual Manager App Usage Constructor.
     * @param appName the name of the app
     * @param appCategory the app's category -> Todo category of an app.
     */
    public ContextualManagerAppUsage(String appName, String appCategory) {

        this.appName = appName;
        this.appCategory = appCategory;
        this.usagePerHour = new ArrayList<>(SECONDLY);
        for (int i = 0; i <= SECONDLY; i++){
            usagePerHour.add(i, -1);
        }

        Calendar day = Calendar.getInstance();
        dayOfTheWeek = day.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Gets the name of this app
     * @return the name
     */
    public String getAppName(){
        return this.appName;
    }

    /**
     * Set the name of this app
     * @param appName the name to set.
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * Get the category of this app.
     * @return the category
     */
    public String getAppCategory() {
        return this.appCategory;
    }

    /**
     * Set the category of this app.
     * @param appCategory the category to set.
     */
    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    /**
     * Get the usage per hour of this app
     * @return the usgPerHour
     */
    public ArrayList<Integer> getUsagePerHour() {
        return this.usagePerHour;
    }

    /**
     * Set the usage per hour of this app.
     * @param usagePerHour the usagePerHour to set.
     */
    public void setUsagePerHour(String usagePerHour) {
        String [] items = usagePerHour.split("\\.");
        for (String s : items ) {
            this.usagePerHour.add(Integer.parseInt(s));
        }
    }

    /**
     * Get the day of the week this app was built
     * @return the day of the week.
     */
    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    /**
     * Set the day of the week.
     * @param dayOfTheWeek the day to set.
     */
    public void setDayOfTheWeek(String dayOfTheWeek) {
        this.dayOfTheWeek = Integer.parseInt(dayOfTheWeek);
    }

    @Override
    public String toString(){
        return "\n\t" + this.getAppName() + "\n\t" + this.getUsagePerHour() + "\n\t" + this.getAppCategory() + "\n\t" + this.getDayOfTheWeek();
    }
}
