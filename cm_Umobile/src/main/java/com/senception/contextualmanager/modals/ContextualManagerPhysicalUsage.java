package com.senception.contextualmanager.modals;

import com.senception.contextualmanager.physical_usage.ContextualManagerPhysicalResourceType;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Copyright (C) Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@senception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017/2018
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerPhysicalUsage. This class represents an object that will
 * save the type of a physical resource being used, its usage per hour, and day of the week.
 *
 */
public class ContextualManagerPhysicalUsage {

    // todo private final int HOURLY = 23;
    private final int SECONDLY = 59;

    private ContextualManagerPhysicalResourceType resourceType;
    private ArrayList<Double> usagePerHour = new ArrayList<>();
    private int dayOfTheWeek;

    /**
     * Contextual Manager Physical Usage Constructor
     */
    public ContextualManagerPhysicalUsage() {
        super();
    }

    /**
     * Contextual Manager Physical Usage Constructor
     * @param resourceType the type of the resource.
     */
    public ContextualManagerPhysicalUsage(ContextualManagerPhysicalResourceType resourceType) {

        this.resourceType = resourceType;

        this.usagePerHour = new ArrayList<>(SECONDLY);
        for (int i = 0; i <= SECONDLY; i++){
            usagePerHour.add(i, -1d);
        }

        Calendar day = Calendar.getInstance();
        dayOfTheWeek = day.get(Calendar.DAY_OF_WEEK);

    }

    /**
     * Get the type of the resource.
     * @return the resource type.
     */
    public ContextualManagerPhysicalResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Set the type of the resource.
     * @param resourceType the resource type to set.
     */
    public void setResourceType(String resourceType) {
        this.resourceType = ContextualManagerPhysicalResourceType.valueOf(resourceType);
    }

    /**
     * Get the usage per hour of this resource.
     * @return the usage per hour.
     */
    public ArrayList<Double> getUsagePerHour() {
        return usagePerHour;
    }

    /**
     * Set the usage per hour of this resource.
     * @param usagePerHour the usage per hour to set.
     */
    public void setUsagePerHour(String usagePerHour) {
        String [] items = usagePerHour.split("\\.");
        for (String s : items ) {
            this.usagePerHour.add(Double.parseDouble(s));
        }
    }

    /**
     * Get the day of the week this resource was build
     * @return the day of the week.
     */
    public int getDayOfTheWeek(){return dayOfTheWeek;}

    /**
     * Set the day of the week.
     * @param day the day of the week to set.
     */
    public void setDayOfTheWeek(String day){dayOfTheWeek = Integer.parseInt(day);}

    @Override
    public String toString(){
        return "\n\t" + this.getResourceType() + "\n\t" + this.getUsagePerHour() + "\n\t" + this.getDayOfTheWeek();
    }
}
