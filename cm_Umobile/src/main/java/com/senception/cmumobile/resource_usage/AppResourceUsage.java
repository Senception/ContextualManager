package com.senception.cmumobile.resource_usage;

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

    private int app_id;
    private double TotalUsageDuration;
    private double CPUConsumption;
    private double connectivityConsumption;
    private String CategoryPreferences;

    public AppResourceUsage(int app_id, double totalUsageDuration, double CPUConsumption, double connectivityConsumption, String categoryPreferences) {
        this.app_id = app_id;
        TotalUsageDuration = totalUsageDuration;
        this.CPUConsumption = CPUConsumption;
        this.connectivityConsumption = connectivityConsumption;
        CategoryPreferences = categoryPreferences;
    }

    public int getApp_id() {
        return app_id;
    }

    public void setApp_id(int app_id) {
        this.app_id = app_id;
    }

    public double getTotalUsageDuration() {
        return TotalUsageDuration;
    }

    public void setTotalUsageDuration(double totalUsageDuration) {
        TotalUsageDuration = totalUsageDuration;
    }

    public double getCPUConsumption() {
        return CPUConsumption;
    }

    public void setCPUConsumption(double CPUConsumption) {
        this.CPUConsumption = CPUConsumption;
    }

    public double getConnectivityConsumption() {
        return connectivityConsumption;
    }

    public void setConnectivityConsumption(double connectivityConsumption) {
        this.connectivityConsumption = connectivityConsumption;
    }

    public String getCategoryPreferences() {
        return CategoryPreferences;
    }

    public void setCategoryPreferences(String categoryPreferences) {
        CategoryPreferences = categoryPreferences;
    }
}
