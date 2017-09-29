package com.senception.cmumobile.ResourceUsage;

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
    private PhysicalResourceType resourceType;
    private ArrayList usagePerHour;

    public PhysicalResourceUsage(PhysicalResourceType resourceType, ArrayList usagePerHour) {
        this.resourceType = resourceType;
        this.usagePerHour = usagePerHour;
    }

    public PhysicalResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(PhysicalResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ArrayList getUsagePerHour() {
        return usagePerHour;
    }

    public void setUsagePerHour(ArrayList usagePerHour) {
        this.usagePerHour = usagePerHour;
    }
}
