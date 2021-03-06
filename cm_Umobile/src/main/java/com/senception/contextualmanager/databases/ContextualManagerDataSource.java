/**
 * Copyright (C) Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@senception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2018
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerDataSource.
 *
 * 
 */

package com.senception.contextualmanager.databases;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
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
import com.senception.contextualmanager.modals.ContextualManagerAppUsage;
import com.senception.contextualmanager.modals.ContextualManagerPhysicalUsage;


/**
 * Copyright (C) Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@senception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2018
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
	}
	
	/**
	 * Close the predefined database.
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
            ContextualManagerSQLiteHelper.COLUMN_SIMILARITY,
            ContextualManagerSQLiteHelper.COLUMN_NUM_ENCOUNTERS,
            ContextualManagerSQLiteHelper.COLUMN_START_ENCOUNTER,
            ContextualManagerSQLiteHelper.COLUMN_END_ENCOUNTER,
            ContextualManagerSQLiteHelper.COLUMN_AVG_ENCOUNTER_DURATION,
            ContextualManagerSQLiteHelper.COLUMN_IS_CONNECTED,
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
		ap.setHashedMac(cursor.getString(1));
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
		ap.setId(cursor.getInt(0)); //id
		ap.setSSID(cursor.getString(1)); // ssid
		ap.setHashedMac(cursor.getString(2)); // HashedMAC
		ap.setLatitude(cursor.getDouble(3)); // latitude
		ap.setLongitude(cursor.getDouble(4)); // longitude
        ap.setAvailability(cursor.getDouble(5)); // a
        ap.setCentrality(cursor.getDouble(6)); // c
        ap.setSimilarity(cursor.getDouble(7)); // i
        ap.setNumEncounters(cursor.getInt(8)); // num enc
        ap.setStartEncounter(cursor.getInt(9)); // start enc
        ap.setEndEncounter(cursor.getInt(10)); // end enc
        ap.setAvgEncounterDuration(cursor.getDouble(11)); // avg encounter duration
        ap.setIsConnected(cursor.getInt(12)); // is connected
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
	 * Function registerNewAP
	 * Register a new AP in the application. It creates a new record on the AP table, with the information passed as ContextualManagerAP.
	 * @param ap AP information.
	 * @param tableName name of the table on the database
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerNewAP (ContextualManagerAP ap, String tableName) {
		ContentValues values = new ContentValues();
	    values.put(ContextualManagerSQLiteHelper.COLUMN_BSSID, ap.getHashedMac());
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
	    values.put(ContextualManagerSQLiteHelper.COLUMN_BSSID, ap.getHashedMac());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_LATITUDE, ap.getLatitude());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_LONGITUDE, ap.getLongitude());
		values.put(ContextualManagerSQLiteHelper.COLUMN_AVAILABILITY, ap.getAvailability());
		values.put(ContextualManagerSQLiteHelper.COLUMN_CENTRALITY, ap.getCentrality());
		values.put(ContextualManagerSQLiteHelper.COLUMN_SIMILARITY, ap.getSimilarity());
        values.put(ContextualManagerSQLiteHelper.COLUMN_NUM_ENCOUNTERS, ap.getNumEncounters());
        values.put(ContextualManagerSQLiteHelper.COLUMN_START_ENCOUNTER, ap.getStartEncounter());
        values.put(ContextualManagerSQLiteHelper.COLUMN_END_ENCOUNTER, ap.getEndEncounter());
        values.put(ContextualManagerSQLiteHelper.COLUMN_AVG_ENCOUNTER_DURATION, ap.getAvgEncounterDuration());
        values.put(ContextualManagerSQLiteHelper.COLUMN_IS_CONNECTED, ap.getIsConnected());
	    long rows = db.insert(tableName, null, values);
        return rows;
	}

	/**
	 * Function registerNewResourceUsage
	 * Register a new Resource Usage in the application. It creates a new record on the ResourceUsage table, with the information passed as PhysicalResourceUSage.
	 * @param resUsg resource usage
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerNewResourceUsage(ContextualManagerPhysicalUsage resUsg) {
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
		return db.insert(ContextualManagerSQLiteHelper.TABLE_RESOURCE_USAGE, null, values);
	}

	/**
	 * Function registerNewAppsUsage
	 * Register a new Apps Usage in the application. It creates a new record on the AppsUsage table, with the information passed as ContextualManagerAppUsage.
	 * @param appUsg resource usage
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerNewAppUsage(ContextualManagerAppUsage appUsg){
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
		return db.insertOrThrow(ContextualManagerSQLiteHelper.TABLE_APPS_USAGE, null, values);
	}

	/**
	 * Function updateAP
	 * Update an AP already registered by the application. This modifies the corresponding record to the AP in the AP table.
	 * @param ap Access point information.
	 * @param tableName name of the table on the database
	 * @return true, if successful.
	 */
	public boolean updateAP(ContextualManagerAP ap, String tableName) {
		String identifier = ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + ap.getHashedMac() + "'"+" COLLATE NOCASE ";
		ContentValues values = new ContentValues();
		values.put(ContextualManagerSQLiteHelper.COLUMN_BSSID, ap.getHashedMac());
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
		String identifier = ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + ap.getHashedMac() + "'"+" COLLATE NOCASE ";

		ContentValues values = new ContentValues();
		values.put(ContextualManagerSQLiteHelper.COLUMN_SSID, ap.getSSID());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_BSSID, ap.getHashedMac());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_LATITUDE, ap.getLatitude());
	    values.put(ContextualManagerSQLiteHelper.COLUMN_LONGITUDE, ap.getLongitude());
		values.put(ContextualManagerSQLiteHelper.COLUMN_AVAILABILITY, ap.getAvailability());
		values.put(ContextualManagerSQLiteHelper.COLUMN_CENTRALITY, ap.getCentrality());
        values.put(ContextualManagerSQLiteHelper.COLUMN_SIMILARITY, ap.getSimilarity());
        values.put(ContextualManagerSQLiteHelper.COLUMN_NUM_ENCOUNTERS, ap.getNumEncounters());
        values.put(ContextualManagerSQLiteHelper.COLUMN_START_ENCOUNTER, ap.getStartEncounter());
        values.put(ContextualManagerSQLiteHelper.COLUMN_END_ENCOUNTER, ap.getEndEncounter());
        values.put(ContextualManagerSQLiteHelper.COLUMN_AVG_ENCOUNTER_DURATION, ap.getAvgEncounterDuration());
        values.put(ContextualManagerSQLiteHelper.COLUMN_IS_CONNECTED, ap.getIsConnected());
	    int rows = db.update(tableName, values, identifier, null);
		
	    return ((rows != 0)? true : false);
	}

	/**
	 * Function updateResourceUsage
	 * Update a resource usage already registered by the application.
	 * This modifies the corresponding averageUsage and day of the week in the resource usage table.
	 * @param pru physical resource usage.
	 * @return true, if successful.
	 */
	public boolean updateResourceUsage(ContextualManagerPhysicalUsage pru){
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

		int rows = db.update(ContextualManagerSQLiteHelper.TABLE_RESOURCE_USAGE, values, identifier, null);

		return rows != 0 ? true : false;
	}

    /**
     * Function updateAppUsage
     * Update an app usage already registered by the application.
     * @param appUsg resource usage.
     * @return true, if successful.
     */
    public boolean updateAppUsage(ContextualManagerAppUsage appUsg){
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
        int rows =  db.update(ContextualManagerSQLiteHelper.TABLE_APPS_USAGE, values, identifier, null);

        return rows != 0 ? true : false;
    }

	/**
	 * Function getAP
	 * Gets an AP already registered by the application. 
	 * @param HashedMAC The ssid of the AP which information should be returned
	 * @param tableName name of the table on the database
	 * @return the ContextualManagerAP object, null if not found.
	 */
	public ContextualManagerAP getAP(String HashedMAC, String tableName) {
		ContextualManagerAP ap;
		Cursor cursor = db.query(tableName, allColumns, ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + HashedMAC + "'", null, null, null, null);
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
	 * @param HashedMAC The ssid of the Peer which information should be returned
	 * @param tableName name of the table on the database
	 * @return the ContextualManagerAP object, null if not found.
	 */
	public ContextualManagerAP getPeer(String HashedMAC, String tableName) {
		ContextualManagerAP ap;
		Cursor cursor = db.query(tableName, allColumnsPeers, ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + HashedMAC + "'"+ " COLLATE NOCASE ", null, null, null, null);
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
	 * @return pru the physical resource usage
	 */
	public ContextualManagerPhysicalUsage getResourceUsage(String type){
		ContextualManagerPhysicalUsage pru = null;
		Cursor cursor = db.query(ContextualManagerSQLiteHelper.TABLE_RESOURCE_USAGE, allColumnsResourceUsage, ContextualManagerSQLiteHelper.COLUMN_TYPE_OF_RESOURCE + "='" + type + "'"+ " COLLATE NOCASE ", null, null, null, null);
		if (cursor.moveToFirst()){
			pru = cursorResourceUsage(cursor);
		}
		return pru;
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
	 * @return A map with the AP objects, and the HashedMAC as key.
	 */
	public Map<String, ContextualManagerAP> getDayOrWeekAP(String tableName) {
		Map<String, ContextualManagerAP> apMap = new TreeMap<String, ContextualManagerAP>();

		Cursor cursor = db.query(tableName, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			ContextualManagerAP ap = cursorToAP(cursor);
			apMap.put(ap.getHashedMac(), ap);
			cursor.moveToNext();
		}

	    cursor.close();
	    return apMap;
	}

	/**
	 * Function getDayOrWeekPeers
	 * Gets the all the Peers recorded by the application on the day or week table.
	 * @param tableName name of the table on the database
	 * @return A map with the AP objects, and the HashedMAC as key.
	 */
	public Map<String, ContextualManagerAP> getDayOrWeekPeers(String tableName) {
		Map<String, ContextualManagerAP> apMap = new TreeMap<String, ContextualManagerAP>();

		Cursor cursor = db.query(tableName, allColumnsPeers, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			ContextualManagerAP ap = cursorPeers(cursor);
			apMap.put(ap.getHashedMac(), ap);
			cursor.moveToNext();
		}

	    cursor.close();
	    return apMap;
	}

	/**
	 * Function getAllAP
	 * Gets the all the AP recorded by the application on the AP table.
	 * @param tableName name of the table on the database
	 * @return A map with the AP objects, and the HashedMAC as key.
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
			if (scanUniques.contains(ap.getHashedMac())) {
				apMap.put(ap.getHashedMac(), ap);
			}
			cursor.moveToNext();
		}

	    cursor.close();
	    return apMap;
	}

	/**
	 * Function hasAP
	 * Checks if a given AP has already been registered by the application.
	 * @param HashedMAC The ssid of the AP
	 * @param tableName name of the table on the database
	 * @return true, if AP has already been registered by the application, false otherwise.
	 */
	public boolean hasAP (String HashedMAC, String tableName) {
        return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + tableName + " WHERE " + ContextualManagerSQLiteHelper.COLUMN_BSSID + " = '" + HashedMAC + "'"+" COLLATE NOCASE ", null) == 0)? false : true;
	}

	/**
	 * Function hasPeer
	 * Checks if a given Peer has already been registered by the application.
	 * @param HashedMAC The ssid of the Peer
	 * @param tableName name of the table on the database
	 * @return true, if Peer has already been registered by the application, false otherwise.
	 */
	public boolean hasPeer(String HashedMAC, String tableName) {
        return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + tableName + " WHERE " + ContextualManagerSQLiteHelper.COLUMN_BSSID + " = '" + HashedMAC + "'"+ " COLLATE NOCASE ", null) == 0)? false : true;
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
     * Function getStationaryTime
     * Computes the Stationary Time for a given AP.
     * @param ap The ContextualManagerAP whose Stationary Time is to be computed.
     * @return The stationary time for the given AP.
     */
	public long getStationaryTime(ContextualManagerAP ap) {
		String HashedMAC = ap.getHashedMac();
		long sationaryTime = 0;
		long count = 0;
		long startTime = 0;
		long endTime = 0;
		Cursor cursor = db.query(ContextualManagerSQLiteHelper.TABLE_VISITS, allColumnsVisit, ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + HashedMAC + "'", null, null, null, null);
		
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
		String HashedMAC = ap.getHashedMac();
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + ContextualManagerSQLiteHelper.TABLE_VISITS + " WHERE " + ContextualManagerSQLiteHelper.COLUMN_BSSID + "='" + HashedMAC + "'", null);
	}

	/**
     * Function getRank
     * Computes the Rank of this node towards a given AP. The Rank is computed as
     * @param ap The ContextualManagerAP whose Stationary Time is to be computed.
     * @return The number of visits.
     */
	public double getRank (ContextualManagerAP ap) {
		return ap.getAttractiveness() * getStationaryTime(ap) * countVisits(ap);
	}
	
	 /**
     * Function registerNewVisit
     * Register a new visit into the database.
     * @param SSID SSID
     * @param HashedMAC HashedMAC
     * @param startTime Time at which the connection started.
     * @param endTime Time at which the connection ended.
     * @return id of the created record, -1 if an error occurs.
     */
	public long registerNewVisit (String SSID, String HashedMAC, Long startTime, Long endTime) {
		cal.setTimeInMillis(startTime);
		ContentValues values = new ContentValues();
	    values.put(ContextualManagerSQLiteHelper.COLUMN_SSID, SSID);
	    values.put(ContextualManagerSQLiteHelper.COLUMN_BSSID, HashedMAC);
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
     * @param HashedMAC HashedMAC
     * @param startTime Time at which the connection started.
     * @param endTime Time at which the connection ended.
     * @return id of the created record, -1 if an error occurs.
     */
	public boolean updateVisit (long _id, String SSID, String HashedMAC, Long startTime, Long endTime) {
		String identifier = ContextualManagerSQLiteHelper.COLUMN_ID + "=" + _id;
		
		ContentValues values = new ContentValues();
		
		if (SSID != null)
			values.put(ContextualManagerSQLiteHelper.COLUMN_SSID, SSID);
		
		if (SSID != null)
			values.put(ContextualManagerSQLiteHelper.COLUMN_BSSID, HashedMAC);
	    
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

		if(cursor.getCount() <= 0){
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}
}