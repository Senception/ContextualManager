/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains CMUmobileVisit class. This class represents what MTracker considers a Visit.
 * The information kept in this object are SSID, BSSID, start and end time of the connection,
 * the day of the week, and the hour of the day
 */ 

package com.senception.contextualmanager.modals;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;

@SuppressLint("SimpleDateFormat")
public class CMUmobileVisit {

	private String SSID;
	private String BSSID;
	private Long startTime;
	private Long endTime;
	private int dayOfTheWeek;
	private int hourOfTheDay;
	private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
	private SimpleDateFormat periodFormat = new SimpleDateFormat("HH:mm:ss");
	
	/**
	 * Get ssid of this AP
	 * @return the SSID
	 */
	public String getSSID() {
		return SSID;
	}
	/**
	 * Set the ssid of this AP
	 * @param sSID the ssid to set
	 */
	public void setSSID(String sSID) {
		SSID = sSID;
	}
	/**
	 * Get bssid of the AP
	 * @return the BSSID
	 */
	public String getBSSID() {
		return BSSID;
	}
	/**
	 * Set the bssid of the AP
	 * @param bSSID the bssid to set
	 */
	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}
	/**
	 * Get start time of the AP
	 * @return the startTime
	 */
	public Long getStartTime() {
		return startTime;
	}
	/**
	 * Set the start time of the AP
	 * @param startTime the start time to set
	 */
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	/**
	 * Get the end time of this AP
	 * @return endTime
	 */
	public Long getEndTime() {
		return endTime;
	}
	/**
	 * Set the end time of the AP
	 * @param endTime the end time to set
	 */
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	/**
	 * Get the day of the week of this AP
	 * @return dayOfTheWeek
	 */
	public int getDayOfTheWeek() {
		return dayOfTheWeek;
	}
	/**
	 * Set the day of the week
	 * @param dayOfTheWeek the day of the week to set
	 */
	public void setDayOfTheWeek(int dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}
	/**
	 * Get hour of the day of this AP
	 * @return hourOfTheDay
	 */
	public int getHourOfTheDay() {
		return hourOfTheDay;
	}
	/**
	 * Set the hour of the day of this AP
	 * @param hourOfTheDay the hour of the day of this set
	 */
	public void setHourOfTheDay(int hourOfTheDay) {
		this.hourOfTheDay = hourOfTheDay;
	}
	/**
	 * TKiddoVisit AP constructor
	 */
	public CMUmobileVisit(){
		super();
	}
	/**
	 * Set default start time and end time of the AP
	 */
	public void setToDefault(){
		this.startTime = null;
		this.endTime = null;
	}
	/**
	 * Update the ssid, bssid, start time and end time of the AP
	 * @param SSID the ssid to set
	 * @param BSSID the bssid to set
	 * @param startTime the start time to set
	 * @param endTime the end time to set
	 */
	public void update(String SSID, String BSSID, Long startTime, Long endTime){
		this.SSID = SSID;
		this.BSSID = BSSID;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	/**
	 *@return sb.toString() Return a string containing the start time, end time, and total time of the AP
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Start: " + dataFormat.format(this.startTime) + "\n");
		sb.append("End: " + dataFormat.format(this.endTime) + "\n");
		sb.append("Total: " + periodFormat.format(this.endTime - this.startTime) + "\n");

		return sb.toString();
	}
}