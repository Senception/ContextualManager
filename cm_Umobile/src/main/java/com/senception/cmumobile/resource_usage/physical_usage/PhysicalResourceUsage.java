package com.senception.cmumobile.resource_usage.physical_usage;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
 * save the type of a physical resource being used, its usage per hour, and day of the week.
 *
 */
public class PhysicalResourceUsage {

    private final int HOURLY = 23;
    private final int SECONDLY = 59;

    private PhysicalResourceType resourceType;
    private ArrayList<Integer> usagePerHour = new ArrayList<>();
    private int dayOfTheWeek;

    public PhysicalResourceUsage() {
        super();
    }

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

    public void setDayOfTheWeek(String day){dayOfTheWeek = Integer.parseInt(day);}

    public void setResourceType(String resourceType) {
        this.resourceType = PhysicalResourceType.valueOf(resourceType);
    }

    public void setUsagePerHour(String usagePerHour) {
        String [] items = usagePerHour.split("\\.");
        for (String s : items ) {
            this.usagePerHour.add(Integer.parseInt(s));
        }
    }

    @Override
    public String toString(){
        return "\n\t" + this.getResourceType() + "\n\t" + this.getUsagePerHour() + "\n\t" + this.getDayOfTheWeek();
    }
}
