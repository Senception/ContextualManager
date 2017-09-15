 /**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains CMUmobileAP. This class represents objects that PerSense Light considers
 * 
 */

package com.senception.cmumobile;

public class CMUmobileAP {

	private String SSID;
	private String BSSID;
	private double attractiveness;
	private  String dateTime;
	private double latitude;
	private double longitude;
	private String DayOfWeek;
	private int id;

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
	 *Get the longitude of this AP
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
	 *PerSense AP Constructor
	 */
	public CMUmobileAP(){
		super();
	}

}