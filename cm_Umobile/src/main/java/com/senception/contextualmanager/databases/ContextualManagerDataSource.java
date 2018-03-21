/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerDataSource. This class provides access to google fused location api to obtain coordinates.
 * 
 */

package com.senception.contextualmanager.databases;

import java.util.ArrayList;
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

import com.senception.contextualmanager.modals.ContextualManagerAP;
import com.senception.contextualmanager.modals.ContextualManagerVisit;
import com.senception.contextualmanager.modals.ContextualManagerWeight;
import com.senception.contextualmanager.modals.ContextualManagerAppUsage;
import com.senception.contextualmanager.modals.ContextualManagerPhysicalUsage;


/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerDataSource.
 * This class provides methods to insert, update and
 * query the application database. It also provide
 * methods to compute certain values, like the
 * Rank and the Stationary Time, among others.
 *
 */
public class ContextualManagerDataSource {
	
	public SQLiteDatabase db;
	private ContextualManagerSQLiteHelper dbHelper;
	private boolean isDbOpen;
	private GregorianCalendar cal;

	/**
	 * Constructor that takes Android Context as input.
	 * @param context class context
	 */
	public ContextualManagerDataSource(Context context) {
		dbHelper = new ContextualManagerSQLiteHelper(context);
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
			if (writable) {
				db = dbHelper.getWritableDatabase();
			}
			else
				db = dbHelper.getReadableDatabase();
		}
	}
	/**
	 * Function deleteDBRow
	 * Deletes data in the database
	 */
	public void deleteDBRow(){
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_VISITS);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_MONDAY);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_TUESDAY);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_WEDNESDAY);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_THURSDAY);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_FRIDAY);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_SATURDAY);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_SUNDAY);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_MONDAY_PEERS);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_TUESDAY_PEERS);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_WEDNESDAY_PEERS);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_THURSDAY_PEERS);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_FRIDAY_PEERS);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_SATURDAY_PEERS);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_SUNDAY_PEERS);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_RESOURCE_USAGE);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_APPS_USAGE);
		db.execSQL("DELETE FROM "+ ContextualManagerSQLiteHelper.TABLE_WEIGHTS);
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
			ContextualManagerSQLiteHelper.COLUMN_ID,
			ContextualManagerSQLiteHelper.COLUMN_BSSID,
			ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK,
			ContextualManagerSQLiteHelper.COLUMN_SSID,
			ContextualManagerSQLiteHelper.COLUMN_ATTRACTIVENESS,
			ContextualManagerSQLiteHelper.COLUMN_DATETIME,
			ContextualManagerSQLiteHelper.COLUMN_LATITUDE,
			ContextualManagerSQLiteHelper.COLUMN_LONGITUDE
	};

	/**
	 * List of all columns on the PEERS table.
	 */
	private String[] allColumnsPeers = { 
			ContextualManagerSQLiteHelper.COLUMN_ID,
			ContextualManagerSQLiteHelper.COLUMN_SSID,
			ContextualManagerSQLiteHelper.COLUMN_BSSID,
			ContextualManagerSQLiteHelper.COLUMN_LATITUDE,
			ContextualManagerSQLiteHelper.COLUMN_LONGITUDE,
            ContextualManagerSQLiteHelper.COLUMN_AVAILABILITY,
            ContextualManagerSQLiteHelper.COLUMN_CENTRALITY,
            ContextualManagerSQLiteHelper.COLUMN_NUM_ENCOUNTERS,
            ContextualManagerSQLiteHelper.COLUMN_ENCOUNTER_DURATION,
            ContextualManagerSQLiteHelper.COLUMN_START_ENCOUNTER,
            ContextualManagerSQLiteHelper.COLUMN_END_ENCOUNTER,
	};

	/**
	 * List of all columns on the VISIT table.
	 */
	private String[] allColumnsVisit = {
			ContextualManagerSQLiteHelper.COLUMN_SSID,
			ContextualManagerSQLiteHelper.COLUMN_BSSID,
			ContextualManagerSQLiteHelper.COLUMN_TIMEON,
			ContextualManagerSQLiteHelper.COLUMN_TIMEOUT,
			ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK,
			ContextualManagerSQLiteHelper.COLUMN_HOUR
	};

	/**
	 * List of all columns on the ResourceUsage table
	 */
	private String[] allColumnsResourceUsage = {
			ContextualManagerSQLiteHelper.COLUMN_ID,
			ContextualManagerSQLiteHelper.COLUMN_TYPE_OF_RESOURCE,
			ContextualManagerSQLiteHelper.COLUMN_AVERAGE_USAGE_HOUR,
			ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK,
	};

	/**
	 * List of all columns on the AppsUsage table
	 */
	private String[] allColumnsAppsUsage = {
			ContextualManagerSQLiteHelper.COLUMN_ID,
			ContextualManagerSQLiteHelper.COLUMN_APP_NAME,
			ContextualManagerSQLiteHelper.COLUMN_APP_CATEGORY,
			ContextualManagerSQLiteHelper.COLUMN_AVERAGE_USAGE_HOUR,
			ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK,
	};

	/**
	 * List of all columns on the Weights table
	 */
	private String[] allColumnsWeight = {
			ContextualManagerSQLiteHelper.COLUMN_ID,
			ContextualManagerSQLiteHelper.COLUMN_DATETIME,
			ContextualManagerSQLiteHelper.COLUMN_AVAILABILITY,
			ContextualManagerSQLiteHelper.COLUMN_CENTRALITY,
			ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK,
	};

	/**
	 * Function cursorAP
	 * Converts a cursor pointing to a record in the AP table to a TKiddoAP object.
	 * @param cursor Cursor pointing to a record of the AP table.
	 * @return the TKiddoAP object
	 */
	private ContextualManagerAP cursorToAP(Cursor cursor) {
		ContextualManagerAP ap = new ContextualManagerAP();
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
	private ContextualManagerAP cursorPeers(Cursor cursor) {
		ContextualManagerAP ap = new ContextualManagerAP();
		ap.setId(cursor.getInt(0));
		ap.setSSID(cursor.getString(1));
		ap.setBSSID(cursor.getString(2));
		ap.setLatitude(cursor.getDouble(3));
		ap.setLongitude(cursor.getDouble(4));
        ap.setAvailability(cursor.getDouble(5));
        ap.setCentrality(cursor.getDouble(6));
        ap.setNumEncounters(cursor.getInt(7));
        ap.setStartEncounter(cursor.getString(9));
        ap.setEndEncounter(cursor.getString(10));
		return ap;
	}

	/**
	 * Function cursorResourceUsage
	 * Converts a cursor pointing to a record in the ResourceUsage table to a Contextual Manager object.
	 * @param cursor Cursor pointing to a record of the ResourceUsage table.
	 * @return the Contextual Manager object
	 */
	private ContextualManagerPhysicalUsage cursorResourceUsage(Cursor cursor){
		ContextualManagerPhysicalUsage pru = new ContextualManagerPhysicalUsage();
		pru.setResourceType(cursor.getString(1));
		pru.setUsagePerHour(cursor.getString(2));
		pru.setDayOfTheWeek(cursor.getString(3));
		return pru;
	}

	/**
	 * Function cursorAppResourceUsage
	 * Converts a cursor pointing to a record in the AppsResourceUsage table to a Contextual Manager object.
	 * @param cursor Cursor pointing to a record of the AppsResourceUsage table.
	 * @return the Contextual Manager object
	 */
	private ContextualManagerAppUsage cursorAppResourceUsage (Cursor cursor){
		ContextualManagerAppUsage app = new ContextualManagerAppUsage();
		app.setAppName(cursor.getString(1));
		app.setAppCategory(cursor.getString(2));
		app.setUsagePerHour(cursor.getString(3));
		app.setDayOfTheWeek(cursor.getString(4));
		return app;
	}

	/**
	 * Function cursorWeight
	 * Converts a cursor pointing to a record in the Weights table to a Contextual Manager object.
	 * @param cursor Cursor pointing to a record of the Weight table.
	 * @return the Contextual Manager object
	 */
	private ContextualManagerWeight cursorWeight(Cursor cursor){
		ContextualManagerWeight weight = new ContextualManagerWeight();
		weight.setDateTime(cursor.getString(1));
		weight.setA(cursor.getString(2));
		weight.setC(cursor.getString(3));
		weight.setDayOfTheWeek(cursor.getString(4));
		return weight;
	}

	/**
	 * Function registerNewAP
	 * Register a new AP in the application. It creates a new record on the AP table, with the information passed as ContextualManagerAP.
	 * @param ap AP information.
	 * @param tableName name of the table on the database
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerNewAP (ContextualManagerAP ap, String tableName) {
		ContentValues values = new ContentValues();
	    values.put(ContextualManagerSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK, ap.getDayOfWeek());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_SSID, ap.getSSID());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_ATTRACTIVENESS, ap.getAttractiveness());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_DATETIME, ap.getDateTime());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_LATITUDE, ap.getLatitude());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_LONGITUDE, ap.getLongitude());
	    
	    return db.insert(tableName, null, values);
	}
	/**
	 * Function registerNewPeers
	 * Register a new Peers in the application. It creates a new record on the Peers table, with the information passed as ContextualManagerAP.
	 * @param ap Peers information.
	 * @param tableName name of the table on the database
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerNewPeers (ContextualManagerAP ap, String tableName) {
		ContentValues values = new ContentValues();
		values.put(ContextualManagerSQLiteHelper.COLUMN_SSID, ap.getSSID());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_LATITUDE, ap.getLatitude());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_LONGITUDE, ap.getLongitude());
		values.put(ContextualManagerSQLiteHelper.COLUMN_AVAILABILITY, ap.getAvailability());
		values.put(ContextualManagerSQLiteHelper.COLUMN_CENTRALITY, ap.getCentrality());
        values.put(ContextualManagerSQLiteHelper.COLUMN_NUM_ENCOUNTERS, ap.getNumEncounters());
        values.put(ContextualManagerSQLiteHelper.COLUMN_START_ENCOUNTER, ap.getStartEncounter());
        values.put(ContextualManagerSQLiteHelper.COLUMN_END_ENCOUNTER, ap.getEndEncounter());
	    long rows = db.insert(tableName, null, values);
        return rows;
	}

	/**
	 * Function registerNewResourceUsage
	 * Register a new Resource Usage in the application. It creates a new record on the ResourceUsage table, with the information passed as PhysicalResourceUSage.
	 * @param resUsg resource usage
	 * @param tableName name of the table on the database
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerNewResourceUsage(ContextualManagerPhysicalUsage resUsg, String tableName) {
		ContentValues values = new ContentValues();
		values.put(ContextualManagerSQLiteHelper.COLUMN_TYPE_OF_RESOURCE, resUsg.getResourceType().toString());
        StringBuilder arrayToDatabase = new StringBuilder();
        for(int i = 0; i < resUsg.getUsagePerHour().size(); i++){
			arrayToDatabase.append(resUsg.getUsagePerHour().get(i));
			if( i < resUsg.getUsagePerHour().size() - 1 ){
				arrayToDatabase.append(".");
			}
		}
		values.put(ContextualManagerSQLiteHelper.COLUMN_AVERAGE_USAGE_HOUR, arrayToDatabase.toString());
		values.put(ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK, String.valueOf(resUsg.getDayOfTheWeek()));
		return db.insert(tableName, null, values);
	}

	/**
	 * Function registerNewAppsUsage
	 * Register a new Apps Usage in the application. It creates a new record on the AppsUsage table, with the information passed as ContextualManagerAppUsage.
	 * @param appUsg resource usage
	 * @param tableName name of the table on the database
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerNewAppUsage(ContextualManagerAppUsage appUsg, String tableName){
		ContentValues values = new ContentValues();
		values.put(ContextualManagerSQLiteHelper.COLUMN_APP_NAME, appUsg.getAppName());
		values.put(ContextualManagerSQLiteHelper.COLUMN_APP_CATEGORY, appUsg.getAppCategory());
		StringBuilder arrayToDatabase = new StringBuilder();
		for(int i = 0; i < appUsg.getUsagePerHour().size(); i++){
			arrayToDatabase.append(appUsg.getUsagePerHour().get(i));
			if( i < appUsg.getUsagePerHour().size() - 1 ){
				arrayToDatabase.append(".");
			}
		}
		values.put(ContextualManagerSQLiteHelper.COLUMN_AVERAGE_USAGE_HOUR, arrayToDatabase.toString());
        values.put(ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK, appUsg.getDayOfTheWeek());
		return db.insertOrThrow(tableName, null, values);
	}

	/**
	 * Method that registers a new wheight on the wheight table
     * this table will save the wheight of the device (A,C and I)
	 * @param weight - the wheight to be stored
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long registerWeight(ContextualManagerWeight weight){
		ContentValues values = new ContentValues();
		values.put(ContextualManagerSQLiteHelper.COLUMN_DATETIME, weight.getDateTime());
		values.put(ContextualManagerSQLiteHelper.COLUMN_AVAILABILITY, weight.getA());
		values.put(ContextualManagerSQLiteHelper.COLUMN_CENTRALITY, weight.getC());
		values.put(ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK, weight.getDayOfTheWeek());

		return db.insertOrThrow(ContextualManagerSQLiteHelper.TABLE_WEIGHTS, null, values);
	}

	/**
	 * Function updateAP
	 * Update an AP already registered by the application. This modifies the corresponding record to the AP in the AP table.
	 * @param ap Access point information.
	 * @param tableName name of the table on the database
	 * @return true, if successful.
	 */
	public boolean updateAP(ContextualManagerAP ap, String tableName) {
		String identifier = ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'"+" COLLATE NOCASE ";
		ContentValues values = new ContentValues();
		values.put(ContextualManagerSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
		values.put(ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK, ap.getDayOfWeek());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_SSID, ap.getSSID());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_ATTRACTIVENESS, ap.getAttractiveness());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_DATETIME, ap.getDateTime());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_LATITUDE, ap.getLatitude());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_LONGITUDE, ap.getLongitude());

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
	public boolean updatePeer(ContextualManagerAP ap, String tableName) {
		String identifier = ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'"+" COLLATE NOCASE ";

		ContentValues values = new ContentValues();
		values.put(ContextualManagerSQLiteHelper.COLUMN_SSID, ap.getSSID());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_LATITUDE, ap.getLatitude());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_LONGITUDE, ap.getLongitude());
		values.put(ContextualManagerSQLiteHelper.COLUMN_AVAILABILITY, ap.getAvailability());
		values.put(ContextualManagerSQLiteHelper.COLUMN_CENTRALITY, ap.getCentrality());
        values.put(ContextualManagerSQLiteHelper.COLUMN_NUM_ENCOUNTERS, ap.getNumEncounters());
        values.put(ContextualManagerSQLiteHelper.COLUMN_START_ENCOUNTER, ap.getStartEncounter());
        values.put(ContextualManagerSQLiteHelper.COLUMN_END_ENCOUNTER, ap.getEndEncounter());
	    int rows = db.update(tableName, values, identifier, null);
		
	    return ((rows != 0)? true : false);
	}

	/**
	 * Function updateResourceUsage
	 * Update a resource usage already registered by the application.
	 * This modifies the corresponding averageUsage and day of the week in the resource usage table.
	 * @param pru physical resource usage.
	 * @param tableName name of the table on the database
	 * @return true, if successful.
	 */
	public boolean updateResourceUsage(ContextualManagerPhysicalUsage pru, String tableName){
		String identifier = ContextualManagerSQLiteHelper.COLUMN_TYPE_OF_RESOURCE + "='" + pru.getResourceType() + "'" +" COLLATE NOCASE ";
		ContentValues values = new ContentValues();
		StringBuilder arrayToDatabase = new StringBuilder();
		for(int i = 0; i < pru.getUsagePerHour().size(); i++){
			arrayToDatabase.append(pru.getUsagePerHour().get(i));
			if( i < pru.getUsagePerHour().size() - 1 ){
				arrayToDatabase.append(".");
			}
		}

       	values.put(ContextualManagerSQLiteHelper.COLUMN_AVERAGE_USAGE_HOUR, arrayToDatabase.toString());
		values.put(ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK, String.valueOf(pru.getDayOfTheWeek()));

		int rows = db.update(tableName, values, identifier, null);

		return rows != 0 ? true : false;
	}

    /**
     * TODO get the parameter "tablename" out -> unnecessary
     * Function updateAppUsage
     * Update an app usage already registered by the application.
     * @param appUsg resource usage.
     * @param tableName name of the table on the database
     * @return true, if successful.
     */
    public boolean updateAppUsage(ContextualManagerAppUsage appUsg, String tableName){
        int rows = 0;
        String identifier = ContextualManagerSQLiteHelper.COLUMN_APP_NAME + "='" + appUsg.getAppName() + "'" +" COLLATE NOCASE ";
        ContentValues values = new ContentValues();
        StringBuilder arrayToDatabase = new StringBuilder();
        for(int i = 0; i < appUsg.getUsagePerHour().size(); i++){
            arrayToDatabase.append(appUsg.getUsagePerHour().get(i));
            if( i < appUsg.getUsagePerHour().size() - 1 ){
                arrayToDatabase.append(".");
            }
        }

        values.put(ContextualManagerSQLiteHelper.COLUMN_AVERAGE_USAGE_HOUR, arrayToDatabase.toString());
		values.put(ContextualManagerSQLiteHelper.COLUMN_APP_CATEGORY, appUsg.getAppCategory());
        values.put(ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK, String.valueOf(appUsg.getDayOfTheWeek()));

        //try to update the app, if its not in the db then register it.
        if ((rows = db.update(tableName, values, identifier, null)) == 0){
            rows = (int) registerNewAppUsage(appUsg, ContextualManagerSQLiteHelper.TABLE_APPS_USAGE);
        }

        return rows != 0 ? true : false;
    }

	/**
	 * Function that updates the Weights table
	 * Update a weight already registered by the application.
	 * @param weight the weight
	 * @return true, if successful.
	 */
	public boolean updateWeight(ContextualManagerWeight weight){
		int rows;
		String identifier = ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK + "='" + String.valueOf(weight.getDayOfTheWeek()) + "'" +" COLLATE NOCASE ";
		ContentValues values = new ContentValues();
        values.put(ContextualManagerSQLiteHelper.COLUMN_DATETIME, weight.getDateTime());
		values.put(ContextualManagerSQLiteHelper.COLUMN_AVAILABILITY, weight.getA());
        values.put(ContextualManagerSQLiteHelper.COLUMN_CENTRALITY, weight.getC());
		values.put(ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK, String.valueOf(weight.getDayOfTheWeek()));

		//try to update the app, if its not in the db then register it.
		if ((rows = db.update(ContextualManagerSQLiteHelper.TABLE_WEIGHTS, values, identifier, null)) == 0){
			rows = (int) registerWeight(weight);
		}

		return rows != 0 ? true : false;
	}

	/**
	 * Function getAP
	 * Gets an AP already registered by the application. 
	 * @param bssid The ssid of the AP which information should be returned
	 * @param tableName name of the table on the database
	 * @return the ContextualManagerAP object, null if not found.
	 */
	public ContextualManagerAP getAP(String bssid, String tableName) {
		ContextualManagerAP ap;
		Cursor cursor = db.query(tableName, allColumns, ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null, null, null, null);
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
	 * @return the ContextualManagerAP object, null if not found.
	 */
	public ContextualManagerAP getPeer(String bssid, String tableName) {
		ContextualManagerAP ap;
		Cursor cursor = db.query(tableName, allColumnsPeers, ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'"+ " COLLATE NOCASE ", null, null, null, null);
		if (cursor.moveToFirst())
			ap = cursorPeers(cursor);
		else
			ap = null;	
		
		cursor.close();
		return ap;
	}

	/**
	 * Function getResourceUsage
	 * Gets a ContextualManagerPhysicalUsage saved in the database.
	 * @param type the physical resource usage type
	 * @param tableName the name of the table
	 * @return pru the physical resource usage
	 */
	public ContextualManagerPhysicalUsage getResourceUsage(String type, String tableName){
		ContextualManagerPhysicalUsage pru = null;
		Cursor cursor = db.query(tableName, allColumnsResourceUsage, ContextualManagerSQLiteHelper.COLUMN_TYPE_OF_RESOURCE + "='" + type + "'"+ " COLLATE NOCASE ", null, null, null, null);
		if (cursor.moveToFirst()){
			pru = cursorResourceUsage(cursor);
		}
		return pru;
	}

	/**
	 * Function getAppResourceUsage
	 * Gets from database the app resource usage with the given name.
	 * @param name name of the app to get from database
	 * @param tableName the name of the table
	 * @return app the app resource usage
	 */
	public ContextualManagerAppUsage getAppResourceUsage (String name, String tableName){
		ContextualManagerAppUsage app = null;
		Cursor cursor = db.query(tableName, allColumnsAppsUsage, ContextualManagerSQLiteHelper.COLUMN_APP_NAME + "='" + name + "'"+ " COLLATE NOCASE ", null, null, null, null);
		if (cursor.moveToFirst()){
			app = cursorAppResourceUsage(cursor);
		}
		return app;
	}

	/**
	 * Method to get the device's weight
	 * @return weight the weight of the device (A,C,I)
	 */
	public ContextualManagerWeight getWeight (){
		ContextualManagerWeight weight = null;
		//Cursor cursor = db.query(tableName, allColumnsWeight, ContextualManagerSQLiteHelper.COLUMN_DATETIME + "='" + dateTime + "'"+ " COLLATE NOCASE ", null, null, null, null);
		// query to get last row of the weight's table
		String selectQuery = "SELECT * FROM " + ContextualManagerSQLiteHelper.TABLE_WEIGHTS + " ORDER BY " + ContextualManagerSQLiteHelper.COLUMN_DATETIME + " DESC LIMIT 1";
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst() && cursor.getCount()>0) {
            weight = cursorWeight(cursor);
        }
		return weight;
	}

    /**
     * Function getAllAP
     * Gets the all the Peers recorded by the application on the Peers table.
     * @param tableName name of the table on the database
     * @return A list with all the peer objects.
     */
    public ArrayList<ContextualManagerAP> getAllPeers( String tableName) {
        String query = "SELECT * FROM "+ tableName;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<ContextualManagerAP> peerList = new ArrayList<>();

        while(cursor.moveToNext()){
            ContextualManagerAP peer = cursorPeers(cursor);
            peerList.add(peer);
        }

        cursor.close();
        db.close();
        return peerList;
    }

	/**
	 * Method that checks whether a table is empty or not.
	 * @param tableName table to check if is empty
	 * @return true if table with tableName is empty
	 */
	public boolean isTableEmpty(String tableName){
        boolean isEmpty = true;
		String count = "SELECT count(*) FROM " + tableName;
		Cursor mcursor = db.rawQuery(count, null);
		mcursor.moveToFirst();
		int icount = mcursor.getInt(0);
		if(icount>0) {
            isEmpty = false;
        }
        return isEmpty;
	}

	/**
	 * Function getDayOrWeekAP
	 * Gets the all the AP recorded by the application on the day or week table.
	 * @param tableName name of the table on the database
	 * @return A map with the AP objects, and the bssid as key.
	 */
	public Map<String, ContextualManagerAP> getDayOrWeekAP(String tableName) {
		Map<String, ContextualManagerAP> apMap = new TreeMap<String, ContextualManagerAP>();

		Cursor cursor = db.query(tableName, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			ContextualManagerAP ap = cursorToAP(cursor);
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
	public Map<String, ContextualManagerAP> getDayOrWeekPeers(String tableName) {
		Map<String, ContextualManagerAP> apMap = new TreeMap<String, ContextualManagerAP>();

		Cursor cursor = db.query(tableName, allColumnsPeers, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			ContextualManagerAP ap = cursorPeers(cursor);
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
	public Map<String, ContextualManagerAP> getAllAP(List <ScanResult> availableAP, String tableName) {
		Map<String, ContextualManagerAP> apMap = new TreeMap<String, ContextualManagerAP>();
		Set<String> scanUniques = new LinkedHashSet<String>();
    	
		for (ScanResult result : availableAP) {
        	scanUniques.add(result.BSSID);
    	}
	
		Cursor cursor = db.query(tableName,
			allColumns, null, null, null, null, null);
	
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			ContextualManagerAP ap = cursorToAP(cursor);
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
        return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + tableName + " WHERE " + ContextualManagerSQLiteHelper.COLUMN_BSSID + " = '" + bssid + "'"+" COLLATE NOCASE ", null) == 0)? false : true;
	}
	/**
	 * Function hasPeer
	 * Checks if a given Peer has already been registered by the application.
	 * @param bssid The ssid of the Peer
	 * @param tableName name of the table on the database
	 * @return true, if Peer has already been registered by the application, false otherwise.
	 */
	public boolean hasPeer(String bssid, String tableName) {
        return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + tableName + " WHERE " + ContextualManagerSQLiteHelper.COLUMN_BSSID + " = '" + bssid + "'"+ " COLLATE NOCASE ", null) == 0)? false : true;
	}

    public boolean hasWeight(int dayOfTheWeek) {
        return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + ContextualManagerSQLiteHelper.TABLE_WEIGHTS + " WHERE " + ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK + " = '" + dayOfTheWeek + "'", null) == 0)? false : true;
    }

	/**
	 * Function getBestAP
	 * Checks all the AP registered by the application and return the one with the highest Rank.
	 * @param tableName name of the table on the database
	 * @return the best AP registered by the application.
	 */
	public ContextualManagerAP getBestAP(String tableName) {
		Map<String, ContextualManagerAP> aps = getDayOrWeekAP(tableName);
		if (!aps.isEmpty()) {
			ContextualManagerAP bestAp = new ContextualManagerAP();
			double bestRank = -1.0;
			double rank;
			for (ContextualManagerAP ap : aps.values()) {
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
	public ContextualManagerAP getBestAP(List <ScanResult> availableAP, String tableName) {
		Map<String, ContextualManagerAP> aps = getAllAP(availableAP, tableName);
		if (!aps.isEmpty()) {
			ContextualManagerAP bestAp = new ContextualManagerAP();
			double bestRank = -1.0;
			double rank;
			for (ContextualManagerAP ap : aps.values()) {
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
	 * Function cursorToVisit
	 * Converts a cursor pointing to a record in the Visit table to a TKiddoAP object.
	 * @param cursor Cursor pointing to a record of the Visit table.
	 * @return the ContextualManagerAP object
	 */
	private ContextualManagerVisit cursorToVisit(Cursor cursor) {
		ContextualManagerVisit visit = new ContextualManagerVisit();
		visit.setSSID(cursor.getString(0));
		visit.setBSSID(cursor.getString(1));
		visit.setStartTime(cursor.getLong(2));
		visit.setEndTime(cursor.getLong(3));
		visit.setDayOfTheWeek(cursor.getInt(2));
		visit.setHourOfTheDay(cursor.getInt(3));
		return visit;
	}

	/*public long getContactTime(ContextualManagerAP ap){
		String bssid = ap.getBSSID();
		long sationaryTime = 0;
		long count = 0;
		long startTime = 0;
		long endTime = 0;
		Cursor cursor = db.query(ContextualManagerSQLiteHelper., allColumnsVisit, ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null, null, null, null);

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
*/
	/**
     * Function getStationaryTime
     * Computes the Stationary Time for a given AP.
     * @param ap The ContextualManagerAP whose Stationary Time is to be computed.
     * @return The stationary time for the given AP.
     */
	public long getStationaryTime(ContextualManagerAP ap) {
		String bssid = ap.getBSSID();
		long sationaryTime = 0;
		long count = 0;
		long startTime = 0;
		long endTime = 0;
		Cursor cursor = db.query(ContextualManagerSQLiteHelper.TABLE_VISITS, allColumnsVisit, ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null, null, null, null);
		
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
     * @param ap The ContextualManagerAP whose Stationary Time is to be computed.
     * @param dayOfTheWeek Day of the week that will restrict the computation of the stationary time.
     * @return The stationary time for the given AP.
     */
	public long getStationaryTimeByMoment (ContextualManagerAP ap, int dayOfTheWeek) {
		String bssid = ap.getBSSID();
		long sationaryTime = 0;
		long count = 0;
		long startTime = 0;
		long endTime = 0;
		Cursor cursor = db.query(ContextualManagerSQLiteHelper.TABLE_VISITS, allColumnsVisit, ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "' AND " + ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK + "=" + dayOfTheWeek, null, null, null, null);
		
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
     * @param ap The ContextualManagerAP whose Stationary Time is to be computed.
     * @return The number of visits.
     */
	public long countVisits(ContextualManagerAP ap) {
		String bssid = ap.getBSSID();
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + ContextualManagerSQLiteHelper.TABLE_VISITS + " WHERE " + ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null);
	}
	/**
     * Function getRank
     * Computes the Rank of this node towards a given AP. The Rank is computed as
     * @param ap The ContextualManagerAP whose Stationary Time is to be computed.
     * @return The number of visits.
     */
	public double getRank (ContextualManagerAP ap)
	{		 
		return ap.getAttractiveness() * getStationaryTime(ap) * countVisits(ap);
	}
	/**
     * Function getInstantaneousRank
     * Test Method to compute the Rank of this node towards a given AP, taking into consideration the current
     * visit time.
     * @param ap The ContextualManagerAP whose Stationary Time is to be computed.
     * @param currentDuration current connection time.
     * @return The number of visits.
     */
	public double getInstantaneousRank(ContextualManagerAP ap, Long currentDuration) {
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
	    values.put(ContextualManagerSQLiteHelper.COLUMN_SSID, SSID);
	    values.put(ContextualManagerSQLiteHelper.COLUMN_BSSID, BSSID);
	    values.put(ContextualManagerSQLiteHelper.COLUMN_TIMEON, startTime);
	    values.put(ContextualManagerSQLiteHelper.COLUMN_TIMEOUT, endTime);
	    
	    try {
	    	int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
	    	values.put(ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK, dayOfTheWeek);
	    } catch (Exception e) {
	    	//STORE DEFAULT AND WRITE TO LOG
	    }
	    
	    try {
	    	int hourOfTheDay = cal.get(Calendar.HOUR_OF_DAY);
	    	values.put(ContextualManagerSQLiteHelper.COLUMN_HOUR, hourOfTheDay);
	    } catch (Exception e) {
	    	//STORE DEFAULT AND WRITE TO LOG
	    }
	    
	    return db.insert(ContextualManagerSQLiteHelper.TABLE_VISITS, null, values);
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
		String identifier = ContextualManagerSQLiteHelper.COLUMN_ID + "=" + _id;
		
		ContentValues values = new ContentValues();
		
		if (SSID != null)
			values.put(ContextualManagerSQLiteHelper.COLUMN_SSID, SSID);
		
		if (SSID != null)
			values.put(ContextualManagerSQLiteHelper.COLUMN_BSSID, BSSID);
	    
		if (startTime != null) {
			values.put(ContextualManagerSQLiteHelper.COLUMN_TIMEON, startTime);
			
			cal.setTimeInMillis(startTime);
		    
			try {
		    	int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
		    	values.put(ContextualManagerSQLiteHelper.COLUMN_DAYOFTHEWEEK, dayOfTheWeek);
		    } catch (Exception e) {
		    	//STORE DEFAULT AND WRITE TO LOG
		    }
		    
		    try {
		    	int hourOfTheDay = cal.get(Calendar.HOUR_OF_DAY);
		    	values.put(ContextualManagerSQLiteHelper.COLUMN_HOUR, hourOfTheDay);
		    } catch (Exception e) {
		    	//STORE DEFAULT AND WRITE TO LOG
		    }
		}
		
		if (endTime != null)
			values.put(ContextualManagerSQLiteHelper.COLUMN_TIMEOUT, endTime);
	    	    
		int rows;
		
		if (values.size() > 0)
	    	rows = db.update(ContextualManagerSQLiteHelper.TABLE_VISITS, values, identifier, null);
		else
			rows = 0;
		
	    return ((rows != 0)? true : false);
	}

	/**
     * Function getAllVisits
     * Get a List with all the visit objects stored in the database.
     */
	public List<ContextualManagerVisit> getAllVisits() {
		List<ContextualManagerVisit> visitList = new LinkedList<ContextualManagerVisit>();
	
		Cursor cursor = db.query(ContextualManagerSQLiteHelper.TABLE_VISITS,
			allColumnsVisit, null, null, null, null, null);
	
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			ContextualManagerVisit visit = cursorToVisit(cursor);
			visitList.add(visit);
			cursor.moveToNext();
		}

	    cursor.close();
	    return visitList;
	}
	/**
     * Function getAllVisitsString
	 * Gets the all the visit recorded by the application on the visit table.
	 * @param ap (table name) name of the table on the database
	 * @return A map with the AP objects, and the bssid as key.
	 */ 
	public List<String> getAllVisitsString(ContextualManagerAP ap) {
		List<String> visitList = new LinkedList<String>();
	
		Cursor cursor = db.query(ContextualManagerSQLiteHelper.TABLE_VISITS, allColumnsVisit, ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'", null, null, null, null);
	
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			ContextualManagerVisit visit = cursorToVisit(cursor);
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
		return DatabaseUtils.queryNumEntries(db, ContextualManagerSQLiteHelper.TABLE_VISITS);
	}

	/**
	 * Check if there is in the database a row in the given
	 * tableName with the given fieldValue, in the given columnName
	 * @param tableName the name of the table to search on
	 * @param fieldValue the value to search for
	 * @param columnName the name of the column to search on
	 * @return true in case the row exists, false otherwise
	 */
	public boolean rowExists(String tableName, String fieldValue, String columnName) {
		String query = "Select * from " + tableName + " where " + columnName + " = '" + fieldValue + "'";

        Cursor cursor = db.rawQuery(query, null);
		//Log.d("RESOURCE", "" + cursor.getCount());

		if(cursor.getCount() <= 0){
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}


}