package com.senception.cmumobile.resource_usage.physical;

import java.util.ArrayList;

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

    private final String TAG = "PHYSICAL RESOURCE";

    public PhysicalResourceUsage(PhysicalResourceType resourceType) {

        this.resourceType = resourceType;

        this.usagePerHour = new ArrayList<>(SECONDLY);
        for (int i = 0; i <= SECONDLY; i++){
            usagePerHour.add(i, -1);
        }
    }

    public PhysicalResourceType getResourceType() {
        return resourceType;
    }

    public ArrayList<Integer> getUsagePerHour() {
        return usagePerHour;
    }

}
