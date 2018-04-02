package com.senception.contextualmanager.modals;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerAP. This class represents objects that PerSense Light considers
 *
 */
public class ContextualManagerAP {

	private int id;
	private String SSID; //Name of the WAP
    private String BSSID; //Mac address of the WAP (wireless access point)
	private double attractiveness; //TODO since it's not used in this vertion, delete.
	private  String dateTime;
	private String DayOfWeek;
	private double latitude;
	private double longitude;
	private double Availability;
	private double Centrality;
    private int startEncounter;
    private int endEncounter;
    private int numEncounters;
    private double avgEncounterDuration;
    private int isConnected; //0 - not connected | 1 - connected

    /*The gets and sets for an ap */

    /**
     *PerSense AP Constructor
     */
    public ContextualManagerAP(){
        super();
    }

    /**
     *Get the id of this AP
     *@return the id
     */
    public int getId() {
        return id;
    }

    /**
     *Set the id of this AP
     *@param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *Get the bssid of this AP
     *@return the bssid
     */
    public String getBSSID() {
        return BSSID;
    }

    /**
     *Set the bssid of this AP
     *@param bSSID the bssid to set
     */
    public void setBSSID(String bSSID) {
        BSSID = bSSID;
    }

    /**
     *Get the ssid of this AP
     *@return the SSID
     */
    public String getSSID() {
        return SSID;
    }
    /**
     *Set the ssid of this AP
     *@param sSID the ssid to set
     */
    public void setSSID(String sSID) {
        SSID = sSID;
    }

    /**
     *Get the day of week of this AP
     *@return the dayofweek
     */
    public String getDayOfWeek() {
        return DayOfWeek;
    }

    /**
     *Set the day of the week of this AP
     *@param dayOfWeek the day of the week to set
     */
    public void setDayOfWeek(String dayOfWeek) {
        DayOfWeek = dayOfWeek;
    }

    /**
     *Get the attractiveness of this AP
     *@return the attractiveness
     */
    public double getAttractiveness() {
        return attractiveness;
    }

    /**
     *Set the attractiveness of this AP
     *@param attractiveness the attractiveness to set
     */
    public void setAttractiveness(double attractiveness) {
        this.attractiveness = attractiveness;
    }

    /**
     *Get the date and time of this AP
     *@return the dateTime
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     *Set the date and time of this AP
     *@param dateTime the date and time to set
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     *Get the latitude of this AP
     *@return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     *Set the latitude of this AP
     *@param latitude the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     *Gets the longitude of this AP
     *@return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     *Set the longitude of this AP
     *@param longitude the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /* ------ The gets and sets for a peer ------ */      //Todo separate aps from peers

    /**
     * Get the availability of this peer.
     * @return the availability
     */
    public double getAvailability() {
        return Availability;
    }

    /**
     * Set the availability of this peer.
     * @param availability the availability to set
     */
    public void setAvailability(double availability) {
        Availability = availability;
    }

    /**
     * Gets the centrality of this peer.
     * @return the centrality
     */
    public double getCentrality() {
        return Centrality;
    }

    /**
     * Set the centrality of this peer.
     * @param centrality the centrality to set.
     */
    public void setCentrality(double centrality) {
        Centrality = centrality;
    }

    //TODO finish the javadoc

    /**
     * Get the number of encounters of this peer.
     * @return the numEncounters
     */
    public int getNumEncounters() {
        return numEncounters;
    }

    /**
     * Set the number of encounters of this peer.
     * @param numEncounters the number of encounters to set.
     */
    public void setNumEncounters(int numEncounters) {
        this.numEncounters = numEncounters;
    }

    /**
     *
     * @return
     */
    public int getStartEncounter() {
        return startEncounter;
    }

    /**
     *
     * @param startTime
     */
    public void setStartEncounter(int startTime) {
        this.startEncounter = startTime;
    }

    /**
     *
     * @return
     */
    public int getEndEncounter() {
        return endEncounter;
    }

    /**
     *
     * @param endTime
     */
    public void setEndEncounter(int endTime) {
        this.endEncounter = endTime;
    }

    /**
     *
     * @return
     */
    public double getAvgEncounterDuration() {
        return avgEncounterDuration;
    }

    /**
     *
     * @param avgEncounterDuration
     */
    public void setAvgEncounterDuration(double avgEncounterDuration) {
        this.avgEncounterDuration = avgEncounterDuration;
    }

    /**
     *
     * @return
     */
    public int getIsConnected() {
        return isConnected;
    }

    /**
     *
     * @param isConnected
     */
    public void setIsConnected(int isConnected) {
        this.isConnected = isConnected;
    }

}
