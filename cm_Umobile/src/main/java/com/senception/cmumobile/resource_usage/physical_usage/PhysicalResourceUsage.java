package com.senception.cmumobile.resource_usage.physical_usage;

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
 * @file Contains PhysicalResourceUsage. This class represents an object that will
 * save the type of a physical resource being used and its usage per hour.
 *
 */
public class PhysicalResourceUsage {

    private final int HOURLY = 23;
    private final int SECONDLY = 59;

    private PhysicalResourceType resourceType;
    private ArrayList<Integer> usagePerHour;
    private int dayOfTheWeek;

    public PhysicalResourceUsage(PhysicalResourceType resourceType) {

        this.resourceType = resourceType;

        this.usagePerHour = new ArrayList<>(SECONDLY);
        for (int i = 0; i <= SECONDLY; i++){
            usagePerHour.add(i, -1);
        }

        Calendar day = Calendar.getInstance();
        dayOfTheWeek = day.get(Calendar.DAY_OF_WEEK);

    }

    public PhysicalResourceType getResourceType() {
        return resourceType;
    }

    public ArrayList<Integer> getUsagePerHour() {
        return usagePerHour;
    }

    public int getDayOfTheWeek(){return dayOfTheWeek;}

    public void setDayOfTheWeek(int day){dayOfTheWeek = day;}
}
