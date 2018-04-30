package com.senception.contextualmanager.modals;

/**
 * Copyright (C) Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@senception.com
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017/2018
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerAP. This class holds a generic object for a device stored by the CM
 *
 */
public class ContextualManagerAP {

	private int id;
	private String SSID; //Name of the Device.
    private String HashedMac; //MAC address of the device
	private double attractiveness; //TODO since it's not used in this vertion, delete.
	private  String dateTime;
	private String DayOfWeek;
	private double latitude;
	private double longitude;
	private double Availability;
	private double Centrality;
    private double Similarity;
    private int startEncounter;
    private int endEncounter;
    private int numEncounters;
    private double avgEncounterDuration;
    private int isConnected; //0 - not connected | 1 - connected

    /*The gets and sets for an ap */

    /**
     * Contextual Manager AP Constructor
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
     *Get the HashedMac of this AP
     *@return the HashedMac
     */
    public String getHashedMac() {
        return HashedMac;
    }

    /**
     *Set the HashedMac of this AP
     *@param HashedMAC the HashedMac to set
     */
    public void setHashedMac(String HashedMAC) {
        HashedMac = HashedMAC;
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

    /**
     * Gets the similarity of this peer.
     * @return the similarity
     */
    public double getSimilarity(){return this.Similarity;}

    /**
     * Set the similarity of this peer.
     * @rparam similarity the similarity to set.
     */
    public void setSimilarity(double similarity){
        this.Similarity = similarity;
    }

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
     * Get the start encounter of this peer.
     * @return the startEncounter
     */
    public int getStartEncounter() {
        return startEncounter;
    }

    /**
     * Set the start encounter of this peer.
     * @param startEncounter the startEncounter to set.
     */
    public void setStartEncounter(int startEncounter) {
        this.startEncounter = startEncounter;
    }

    /**
     * Get the end encounter of this peer.
     * @return the endEncounter.
     */
    public int getEndEncounter() {
        return endEncounter;
    }

    /**
     * Set the end encounter of this peer.
     * @param endEncounter the endEncounter to set.
     */
    public void setEndEncounter(int endEncounter) {
        this.endEncounter = endEncounter;
    }

    /**
     * Get the average encounter duration of this peer.
     * @return the avgEncounterDuration.
     */
    public double getAvgEncounterDuration() {
        return avgEncounterDuration;
    }

    /**
     * Set the average encounter duration of this peer.
     * @param avgEncounterDuration the avgEncounterDuration to set.
     */
    public void setAvgEncounterDuration(double avgEncounterDuration) {
        this.avgEncounterDuration = avgEncounterDuration;
    }

    /**
     * Get the variable that indicates if this peer is connected or not.
     * @return isConnected
     */
    public int getIsConnected() {
        return isConnected;
    }

    /**
     * Set the variable that indicates if this peer is connected or not.
     * @param isConnected the value of the variable that's going to be set.
     */
    public void setIsConnected(int isConnected) {
        this.isConnected = isConnected;
    }

}
