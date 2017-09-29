/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains CMUmobileDataSource. This class provides access to google fused location api to obtain coordinates.
 * 
 */

package com.senception.cmumobile;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import android.net.wifi.ScanResult;
import android.content.ContentValues;
import android.content.Context;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;

import com.senception.cmumobile.databases.CMUmobileSQLiteHelper;
import com.senception.cmumobile.modals.CMUmobileAP;
import com.senception.cmumobile.modals.CMUmobileVisit;

/**
 * This class provides methods to insert, update and
 * query the application database. It also provide
 * methods to compute certain values, like the
 * Rank and the Stationary Time, among others.
 *
 */
public class CMUmobileDataSource {
	
	public SQLiteDatabase db;
	private CMUmobileSQLiteHelper dbHelper;
	private boolean isDbOpen;
	private GregorianCalendar cal;
	
	/**
	 * Constructor that takes Android Context as input.
	 * @param context class context
	 */
	public CMUmobileDataSource (Context context) {
		dbHelper = new CMUmobileSQLiteHelper(context);
		isDbOpen = false;
		cal = new GregorianCalendar();
	}
	/**
	 * Opens the predefined MTracker database.
	 * @param writable
	 * @throws SQLException
	 */
	public void openDB(boolean writable) throws SQLException {
		if (!isDbOpen) {
			if (writable)
				db = dbHelper.getWritableDatabase();
			else
				db = dbHelper.getReadableDatabase();
		}
	}
	/**
	 * Function deleteDBRow
	 * Deletes data in the database
	 */
	public void deleteDBRow(){
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_VISITS);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_MONDAY);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_TUESDAY);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_WEDNESDAY);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_THURSDAY);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_FRIDAY);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_SATURDAY);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_SUNDAY);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_MONDAY_PEERS);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_TUESDAY_PEERS);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_WEDNESDAY_PEERS);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_THURSDAY_PEERS);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_FRIDAY_PEERS);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_SATURDAY_PEERS);
		db.execSQL("DELETE FROM "+ CMUmobileSQLiteHelper.TABLE_SUNDAY_PEERS);
	}
	
	/**
	 * Close the predefined PerSense Light database.
	 */
	public void closeDB() {
		dbHelper.close();
		isDbOpen = false;
	}
	/**
	 * List of all columns on the AP TABLE
	 */
	private String[] allColumns = {
			CMUmobileSQLiteHelper.COLUMN_ID,
			CMUmobileSQLiteHelper.COLUMN_BSSID,
			CMUmobileSQLiteHelper.COLUMN_DAYOFTHEWEEK,
			CMUmobileSQLiteHelper.COLUMN_SSID,
			CMUmobileSQLiteHelper.COLUMN_ATTRACTIVENESS,
			CMUmobileSQLiteHelper.COLUMN_DATETIME,
			CMUmobileSQLiteHelper.COLUMN_LATITUDE,
			CMUmobileSQLiteHelper.COLUMN_LONGITUDE
	};
	/**
	 * List of all columns on the PEERS table.
	 */
	private String[] allColumnsPeers = { 
			CMUmobileSQLiteHelper.COLUMN_ID,
			CMUmobileSQLiteHelper.COLUMN_SSID,
			CMUmobileSQLiteHelper.COLUMN_BSSID,
			CMUmobileSQLiteHelper.COLUMN_DATETIME,
			CMUmobileSQLiteHelper.COLUMN_LATITUDE,
			CMUmobileSQLiteHelper.COLUMN_LONGITUDE
	};
	/**
	 * Function cursorAP
	 * Converts a cursor pointing to a record in the AP table to a TKiddoAP object.
	 * @param cursor Cursor pointing to a record of the AP table.
	 * @return the TKiddoAP object
	 */
	private CMUmobileAP cursorToAP(Cursor cursor) {
		CMUmobileAP ap = new CMUmobileAP();
		ap.setId(cursor.getInt(0));
		ap.setBSSID(cursor.getString(1));
		ap.setDayOfWeek(cursor.getString(2));
		ap.setSSID(cursor.getString(3));
		ap.setAttractiveness(cursor.getDouble(4));
		ap.setDateTime(cursor.getString(5));
		ap.setLatitude(cursor.getDouble(6));
		ap.setLongitude(cursor.getDouble(7));
	
		return ap;
	}
	/**
	 * Function cursorPeers
	 * Converts a cursor pointing to a record in the Peers table to a TKiddoAP object.
	 * @param cursor Cursor pointing to a record of the Peers table.
	 * @return the TKiddoAP object
	 */
	private CMUmobileAP cursorPeers(Cursor cursor) {
		CMUmobileAP ap = new CMUmobileAP();
		ap.setId(cursor.getInt(0));
		ap.setSSID(cursor.getString(1));
		ap.setBSSID(cursor.getString(2));
		ap.setDateTime(cursor.getString(3));
		ap.setLatitude(cursor.getDouble(4));
		ap.setLongitude(cursor.getDouble(5));
		return ap;
	}
	/**
	 * Function registerNewAP
	 * Register a new AP in the application. It creates a new record on the AP table, with the information passed as CMUmobileAP.
	 * @param ap AP information.
	 * @param tableName name of the table on the database
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerNewAP (CMUmobileAP ap, String tableName) {
		ContentValues values = new ContentValues();
	    values.put(CMUmobileSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
	    values.put(CMUmobileSQLiteHelper.COLUMN_DAYOFTHEWEEK, ap.getDayOfWeek());
	    values.put(CMUmobileSQLiteHelper.COLUMN_SSID, ap.getSSID());
	    values.put(CMUmobileSQLiteHelper.COLUMN_ATTRACTIVENESS, ap.getAttractiveness());
	    values.put(CMUmobileSQLiteHelper.COLUMN_DATETIME, ap.getDateTime());
	    values.put(CMUmobileSQLiteHelper.COLUMN_LATITUDE, ap.getLatitude());
	    values.put(CMUmobileSQLiteHelper.COLUMN_LONGITUDE, ap.getLongitude());
	    
	    return db.insert(tableName, null, values);
	}
	/**
	 * Function registerNewPeers
	 * Register a new Peers in the application. It creates a new record on the Peers table, with the information passed as CMUmobileAP.
	 * @param ap Peers information.
	 * @param tableName name of the table on the database
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerNewPeers (CMUmobileAP ap, String tableName) {
		ContentValues values = new ContentValues();
		values.put(CMUmobileSQLiteHelper.COLUMN_SSID, ap.getSSID());
	    values.put(CMUmobileSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
	    values.put(CMUmobileSQLiteHelper.COLUMN_DATETIME, ap.getDateTime());
	    values.put(CMUmobileSQLiteHelper.COLUMN_LATITUDE, ap.getLatitude());
	    values.put(CMUmobileSQLiteHelper.COLUMN_LONGITUDE, ap.getLongitude());
	    
	    return db.insert(tableName, null, values);
	}
	/**
	 * Function updateAP
	 * Update an AP already registered by the application. This modifies the corresponding record to the AP in the AP table.
	 * @param ap Access point information.
	 * @param tableName name of the table on the database
	 * @return true, if successful.
	 */
	public boolean updateAP(CMUmobileAP ap, String tableName) {
		String identifier = CMUmobileSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'"+" COLLATE NOCASE ";
		ContentValues values = new ContentValues();
		values.put(CMUmobileSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
		values.put(CMUmobileSQLiteHelper.COLUMN_DAYOFTHEWEEK, ap.getDayOfWeek());
	    values.put(CMUmobileSQLiteHelper.COLUMN_SSID, ap.getSSID());
	    values.put(CMUmobileSQLiteHelper.COLUMN_ATTRACTIVENESS, ap.getAttractiveness());
	    values.put(CMUmobileSQLiteHelper.COLUMN_DATETIME, ap.getDateTime());
	    values.put(CMUmobileSQLiteHelper.COLUMN_LATITUDE, ap.getLatitude());
	    values.put(CMUmobileSQLiteHelper.COLUMN_LONGITUDE, ap.getLongitude());
	    
	    int rows = db.update(tableName, values, identifier, null);
		
	    return ((rows != 0)? true : false);
	}
	/**
	 * Function updatePeer
	 * Update an Peer already registered by the application. This modifies the corresponding record to the Peer in the Peer table.
	 * @param ap Peer point information.
	 * @param tableName name of the table on the database
	 * @return true, if successful.
	 */
	public boolean updatePeer(CMUmobileAP ap, String tableName) {
		String identifier = CMUmobileSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'"+" COLLATE NOCASE ";
		ContentValues values = new ContentValues();
		values.put(CMUmobileSQLiteHelper.COLUMN_SSID, ap.getSSID());
	    values.put(CMUmobileSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
	    values.put(CMUmobileSQLiteHelper.COLUMN_DATETIME, ap.getDateTime());
	    values.put(CMUmobileSQLiteHelper.COLUMN_LATITUDE, ap.getLatitude());
	    values.put(CMUmobileSQLiteHelper.COLUMN_LONGITUDE, ap.getLongitude());
	    
	    int rows = db.update(tableName, values, identifier, null);
		
	    return ((rows != 0)? true : false);
	}
	/**
	 * Function getAP
	 * Gets an AP already registered by the application. 
	 * @param bssid The ssid of the AP which information should be returned
	 * @param tableName name of the table on the database
	 * @return the CMUmobileAP object, null if not found.
	 */
	public CMUmobileAP getAP(String bssid, String tableName) {
		CMUmobileAP ap;
		Cursor cursor = db.query(tableName, allColumns, CMUmobileSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null, null, null, null);
		if (cursor.moveToFirst())
			ap = cursorToAP(cursor);
		else
			ap = null;	
		
		cursor.close();
		return ap;
	}
	/**
	 * Function getPeer
	 * Gets an Peer already registered by the application. 
	 * @param bssid The ssid of the Peer which information should be returned
	 * @param tableName name of the table on the database
	 * @return the CMUmobileAP object, null if not found.
	 */
	public CMUmobileAP getPeer(String bssid, String tableName) {
		CMUmobileAP ap;
		Cursor cursor = db.query(tableName, allColumnsPeers, CMUmobileSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'"+ " COLLATE NOCASE ", null, null, null, null);
		if (cursor.moveToFirst())
			ap = cursorPeers(cursor);
		else
			ap = null;	
		
		cursor.close();
		return ap;
	}
	/**
	 * Function getDayOrWeekAP
	 * Gets the all the AP recorded by the application on the day or week table.
	 * @param tableName name of the table on the database
	 * @return A map with the AP objects, and the bssid as key.
	 */
	public Map<String, CMUmobileAP> getDayOrWeekAP(String tableName) {
		Map<String, CMUmobileAP> apMap = new TreeMap<String, CMUmobileAP>();

		Cursor cursor = db.query(tableName, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			CMUmobileAP ap = cursorToAP(cursor);
			apMap.put(ap.getBSSID(), ap);
			cursor.moveToNext();
		}

	    cursor.close();
	    return apMap;
	}
	/**
	 * Function getDayOrWeekPeers
	 * Gets the all the Peers recorded by the application on the day or week table.
	 * @param tableName name of the table on the database
	 * @return A map with the AP objects, and the bssid as key.
	 */
	public Map<String, CMUmobileAP> getDayOrWeekPeers(String tableName) {
		Map<String, CMUmobileAP> apMap = new TreeMap<String, CMUmobileAP>();

		Cursor cursor = db.query(tableName, allColumnsPeers, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			CMUmobileAP ap = cursorPeers(cursor);
			apMap.put(ap.getBSSID(), ap);
			cursor.moveToNext();
		}

	    cursor.close();
	    return apMap;
	}
	/**
	 * Function getAllAP
	 * Gets the all the AP recorded by the application on the AP table.
	 * @param tableName name of the table on the database
	 * @return A map with the AP objects, and the bssid as key.
	 */
	public Map<String, CMUmobileAP> getAllAP(List <ScanResult> availableAP, String tableName) {
		Map<String, CMUmobileAP> apMap = new TreeMap<String, CMUmobileAP>();
		Set<String> scanUniques = new LinkedHashSet<String>();
    	
		for (ScanResult result : availableAP) {
        	scanUniques.add(result.BSSID);
    	}
	
		Cursor cursor = db.query(tableName,
			allColumns, null, null, null, null, null);
	
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			CMUmobileAP ap = cursorToAP(cursor);
			if (scanUniques.contains(ap.getBSSID())) {
				apMap.put(ap.getBSSID(), ap);
			}
			cursor.moveToNext();
		}

	    cursor.close();
	    return apMap;
	}
	/**
	 * Function hasAP
	 * Checks if a given AP has already been registered by the application.
	 * @param bssid The ssid of the AP
	 * @param tableName name of the table on the database
	 * @return true, if AP has already been registered by the application, false otherwise.
	 */
	public boolean hasAP (String bssid, String tableName) {
        return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + tableName + " WHERE " + CMUmobileSQLiteHelper.COLUMN_BSSID + " = '" + bssid + "'"+" COLLATE NOCASE ", null) == 0)? false : true;
	}
	/**
	 * Function hasPeer
	 * Checks if a given Peer has already been registered by the application.
	 * @param bssid The ssid of the Peer
	 * @param tableName name of the table on the database
	 * @return true, if Peer has already been registered by the application, false otherwise.
	 */
	public boolean hasPeer(String bssid, String tableName) {
        return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + tableName + " WHERE " + CMUmobileSQLiteHelper.COLUMN_BSSID + " = '" + bssid + "'"+ " COLLATE NOCASE ", null) == 0)? false : true;
	}
	/**
	 * Function getBestAP
	 * Checks all the AP registered by the application and return the one with the highest Rank.
	 * @param tableName name of the table on the database
	 * @return the best AP registered by the application.
	 */
	public CMUmobileAP getBestAP(String tableName) {
		Map<String, CMUmobileAP> aps = getDayOrWeekAP(tableName);
		if (!aps.isEmpty()) {
			CMUmobileAP bestAp = new CMUmobileAP();
			double bestRank = -1.0;
			double rank;
			for (CMUmobileAP ap : aps.values()) {
				rank = getRank(ap);
				if (rank > bestRank) {
					bestRank = rank;
					bestAp = ap;
				}
			}
			return bestAp;
		} else {
			return null;
		}
	}
	/**
	 * Function getBestAP
	 * Checks the APs registered by the application and available in the List of ScanResult, and the return the one with the highest rank.
	 * @param availableAP list of available AP
	 * @param tableName name of the table on the database
	 * @return the best AP registered by the application.
	 */
	public CMUmobileAP getBestAP(List <ScanResult> availableAP, String tableName) {
		Map<String, CMUmobileAP> aps = getAllAP(availableAP, tableName);
		if (!aps.isEmpty()) {
			CMUmobileAP bestAp = new CMUmobileAP();
			double bestRank = -1.0;
			double rank;
			for (CMUmobileAP ap : aps.values()) {
				rank = getRank(ap);
				if (rank > bestRank) {
					bestRank = rank;
					bestAp = ap;
				}
			}
			return bestAp;
		} else {
			return null;
		}
	}
	/**
	 * List of all columns on the VISIT table.
	 */
	private String[] allColumnsVisit = { 
			CMUmobileSQLiteHelper.COLUMN_SSID,
			CMUmobileSQLiteHelper.COLUMN_BSSID,
			CMUmobileSQLiteHelper.COLUMN_TIMEON,
			CMUmobileSQLiteHelper.COLUMN_TIMEOUT,
			CMUmobileSQLiteHelper.COLUMN_DAYOFTHEWEEK,
			CMUmobileSQLiteHelper.COLUMN_HOUR
	};
	/**
	 * Function cursorToVisit
	 * Converts a cursor pointing to a record in the Visit table to a TKiddoAP object.
	 * @param cursor Cursor pointing to a record of the Visit table.
	 * @return the CMUmobileAP object
	 */
	private CMUmobileVisit cursorToVisit(Cursor cursor) {
		CMUmobileVisit visit = new CMUmobileVisit();
		visit.setSSID(cursor.getString(0));
		visit.setBSSID(cursor.getString(1));
		visit.setStartTime(cursor.getLong(2));
		visit.setEndTime(cursor.getLong(3));
		visit.setDayOfTheWeek(cursor.getInt(2));
		visit.setHourOfTheDay(cursor.getInt(3));
		return visit;
	}
	/**
     * Function getStationaryTime
     * Computes the Stationary Time for a given AP.
     * @param ap The CMUmobileAP whose Stationary Time is to be computed.
     * @return The stationary time for the given AP.
     */
	public long getStationaryTime(CMUmobileAP ap) {
		String bssid = ap.getBSSID();
		long sationaryTime = 0;
		long count = 0;
		long startTime = 0;
		long endTime = 0;
		Cursor cursor = db.query(CMUmobileSQLiteHelper.TABLE_VISITS, allColumnsVisit, CMUmobileSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null, null, null, null);
		
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				startTime = cursor.getLong(2);
				endTime = cursor.getLong(3);
				if ((endTime - startTime) > 0) {
					sationaryTime = sationaryTime + (endTime - startTime);
					count++;
				}
				cursor.moveToNext();
			}
			cursor.close();
			
			if (count > 0)
				sationaryTime = sationaryTime/count;
	
		}
		else {
			cursor.close();
		}
		
		return sationaryTime/1000;
	}
	/**
     * Function getStationaryTimeByMoment
     * Computes the Stationary Time for a given AP, only taking into consideration records for a given Day of the Week.
     * @param ap The CMUmobileAP whose Stationary Time is to be computed.
     * @param dayOfTheWeek Day of the week that will restrict the computation of the stationary time.
     * @return The stationary time for the given AP.
     */
	public long getStationaryTimeByMoment (CMUmobileAP ap, int dayOfTheWeek) {
		String bssid = ap.getBSSID();
		long sationaryTime = 0;
		long count = 0;
		long startTime = 0;
		long endTime = 0;
		Cursor cursor = db.query(CMUmobileSQLiteHelper.TABLE_VISITS, allColumnsVisit, CMUmobileSQLiteHelper.COLUMN_BSSID + "='" + bssid + "' AND " + CMUmobileSQLiteHelper.COLUMN_DAYOFTHEWEEK + "=" + dayOfTheWeek, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				startTime = cursor.getLong(2);
				endTime = cursor.getLong(3);
				if ((endTime - startTime) > 0) {
					sationaryTime = sationaryTime + (endTime - startTime);
					count++;
				}
				cursor.moveToNext();
			}
			cursor.close();
			
			if (count > 0)
				sationaryTime = sationaryTime/count;
	
		}
		else {
			cursor.close();
		}
		
		return sationaryTime/1000;
	}
	/**
     * Function countVisits
     * Computes the Number of visits that the node has done to a given AP.
     * @param ap The CMUmobileAP whose Stationary Time is to be computed.
     * @return The number of visits.
     */
	public long countVisits(CMUmobileAP ap) {
		String bssid = ap.getBSSID();
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + CMUmobileSQLiteHelper.TABLE_VISITS + " WHERE " + CMUmobileSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null);
	}
	/**
     * Function getRank
     * Computes the Rank of this node towards a given AP. The Rank is computed as
     * @param ap The CMUmobileAP whose Stationary Time is to be computed.
     * @return The number of visits.
     */
	public double getRank (CMUmobileAP ap)
	{		 
		return ap.getAttractiveness() * getStationaryTime(ap) * countVisits(ap);
	}
	/**
     * Function getInstantaneousRank
     * Test Method to compute the Rank of this node towards a given AP, taking into consideration the current
     * visit time.
     * @param ap The CMUmobileAP whose Stationary Time is to be computed.
     * @param currentDuration current connection time.
     * @return The number of visits.
     */
	public double getInstantaneousRank(CMUmobileAP ap, Long currentDuration) {
		 if (ap == null) {
			 return currentDuration;
		 }
		 else {
			 return (0.3*getStationaryTime(ap) + 0.7*currentDuration) * (countVisits(ap) + 1) * ap.getAttractiveness();
		 }
	}
	 /**
     * Function registerNewVisit
     * Register a new visit into the database.
     * @param SSID SSID
     * @param BSSID BSSID
     * @param startTime Time at which the connection started.
     * @param endTime Time at which the connection ended.
     * @return id of the created record, -1 if an error occurs.
     */
	public long registerNewVisit (String SSID, String BSSID, Long startTime, Long endTime) {
		cal.setTimeInMillis(startTime);
		ContentValues values = new ContentValues();
	    values.put(CMUmobileSQLiteHelper.COLUMN_SSID, SSID);
	    values.put(CMUmobileSQLiteHelper.COLUMN_BSSID, BSSID);
	    values.put(CMUmobileSQLiteHelper.COLUMN_TIMEON, startTime);
	    values.put(CMUmobileSQLiteHelper.COLUMN_TIMEOUT, endTime);
	    
	    try {
	    	int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
	    	values.put(CMUmobileSQLiteHelper.COLUMN_DAYOFTHEWEEK, dayOfTheWeek);
	    } catch (Exception e) {
	    	//STORE DEFAULT AND WRITE TO LOG
	    }
	    
	    try {
	    	int hourOfTheDay = cal.get(Calendar.HOUR_OF_DAY);
	    	values.put(CMUmobileSQLiteHelper.COLUMN_HOUR, hourOfTheDay);
	    } catch (Exception e) {
	    	//STORE DEFAULT AND WRITE TO LOG
	    }
	    
	    return db.insert(CMUmobileSQLiteHelper.TABLE_VISITS, null, values);
	}
	/**
     * Funtion updateVisit
     * Updates an existing visit in the database.
     * @param _id id of the record to update
     * @param SSID SSID
     * @param BSSID BSSID
     * @param startTime Time at which the connection started.
     * @param endTime Time at which the connection ended.
     * @return id of the created record, -1 if an error occurs.
     */
	public boolean updateVisit (long _id, String SSID, String BSSID, Long startTime, Long endTime) {
		String identifier = CMUmobileSQLiteHelper.COLUMN_ID + "=" + _id;
		
		ContentValues values = new ContentValues();
		
		if (SSID != null)
			values.put(CMUmobileSQLiteHelper.COLUMN_SSID, SSID);
		
		if (SSID != null)
			values.put(CMUmobileSQLiteHelper.COLUMN_BSSID, BSSID);
	    
		if (startTime != null) {
			values.put(CMUmobileSQLiteHelper.COLUMN_TIMEON, startTime);
			
			cal.setTimeInMillis(startTime);
		    
			try {
		    	int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
		    	values.put(CMUmobileSQLiteHelper.COLUMN_DAYOFTHEWEEK, dayOfTheWeek);
		    } catch (Exception e) {
		    	//STORE DEFAULT AND WRITE TO LOG
		    }
		    
		    try {
		    	int hourOfTheDay = cal.get(Calendar.HOUR_OF_DAY);
		    	values.put(CMUmobileSQLiteHelper.COLUMN_HOUR, hourOfTheDay);
		    } catch (Exception e) {
		    	//STORE DEFAULT AND WRITE TO LOG
		    }
		}
		
		if (endTime != null)
			values.put(CMUmobileSQLiteHelper.COLUMN_TIMEOUT, endTime);
	    	    
		int rows;
		
		if (values.size() > 0)
	    	rows = db.update(CMUmobileSQLiteHelper.TABLE_VISITS, values, identifier, null);
		else
			rows = 0;
		
	    return ((rows != 0)? true : false);
	}
	/**
     * Function getAllVisits
     * Get a List with all the visit objects stored in the database.
     */
	public List<CMUmobileVisit> getAllVisits() {
		List<CMUmobileVisit> visitList = new LinkedList<CMUmobileVisit>();
	
		Cursor cursor = db.query(CMUmobileSQLiteHelper.TABLE_VISITS,
			allColumnsVisit, null, null, null, null, null);
	
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			CMUmobileVisit visit = cursorToVisit(cursor);
			visitList.add(visit);
			cursor.moveToNext();
		}

	    cursor.close();
	    return visitList;
	}
	/**
     * Function getAllVisitsString
	 * Gets the all the visit recorded by the application on the visit table.
	 * @param tableName name of the table on the database
	 * @return A map with the AP objects, and the bssid as key.
	 */ 
	public List<String> getAllVisitsString(CMUmobileAP ap) {
		List<String> visitList = new LinkedList<String>();
	
		Cursor cursor = db.query(CMUmobileSQLiteHelper.TABLE_VISITS, allColumnsVisit, CMUmobileSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'", null, null, null, null);
	
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			CMUmobileVisit visit = cursorToVisit(cursor);
			visitList.add(visit.toString());
			cursor.moveToNext();
		}

	    cursor.close();
	    return visitList;
	}
	/**
     * Function getNumVisits
     * Get the number of visits registered in the database.
     */
	public long getNumVisits(){
		return DatabaseUtils.queryNumEntries(db, CMUmobileSQLiteHelper.TABLE_VISITS);
	}
}