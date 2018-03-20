/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerSQLiteHelper. This class provides the PerSense Light DataBase
 * 
 */

package com.senception.contextualmanager.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerSQLiteHelper.
 * This class extends the SQLiteOpenHelper android class.
 */
public class ContextualManagerSQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "contextualmanager.db";
	private static final int DATABASE_VERSION = 1;

    //TABLES
	public static final String TABLE_VISITS = "visits";
	public static final String TABLE_MONDAY = "monday";
	public static final String TABLE_TUESDAY = "tuesday";
	public static final String TABLE_WEDNESDAY = "wednesday";
	public static final String TABLE_THURSDAY = "thursday";
	public static final String TABLE_FRIDAY = "friday";
	public static final String TABLE_SATURDAY = "saturday";
	public static final String TABLE_SUNDAY = "sunday";
	public static final String TABLE_MONDAY_PEERS = "mondaypeers";
	public static final String TABLE_TUESDAY_PEERS = "tuesdaypeers";
	public static final String TABLE_WEDNESDAY_PEERS = "wednesdaypeers";
	public static final String TABLE_THURSDAY_PEERS = "thursdaypeers";
	public static final String TABLE_FRIDAY_PEERS = "fridaypeers";
	public static final String TABLE_SATURDAY_PEERS = "saturdaypeers";
	public static final String TABLE_SUNDAY_PEERS = "sundaypeers";
    public static final String TABLE_RESOURCE_USAGE = "resourceusage";
	public static final String TABLE_APPS_USAGE = "appsusage";
	public static final String TABLE_WEIGHTS = "weights";

	// IDENTIFICATION
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SSID = "ssid";
	public static final String COLUMN_BSSID = "mac";
	public static final String COLUMN_GROUPID = "groupid";
    public static final String COLUMN_DAYOFTHEWEEK = "dayoftheweek"; //for peers, resource and apps usage

	// ACCESS POINTS
	public static final String COLUMN_ATTRACTIVENESS = "attractiveness";
	public static final String COLUMN_DATETIME = "dateTime";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_NUM_ENCOUNTERS = "encounters";
    public static final String COLUMN_ENCOUNTER_DURATION = "duration";

	// VISITS
	public static final String COLUMN_TIMEON = "timeon";
	public static final String COLUMN_TIMEOUT = "timeout";
	public static final String COLUMN_HOUR = "hour";

    //RESOURCE USAGE
    public static final String COLUMN_TYPE_OF_RESOURCE = "typeofresource";
    public static final String COLUMN_AVERAGE_USAGE_HOUR = "averageusagehour"; //for resource usage and apps usage

	//APPS USAGE
	public static final String COLUMN_APP_NAME = "appname";
	public static final String COLUMN_APP_CATEGORY = "appcategory";

	//WEIGHTS
	public static final String COLUMN_AVAILABILITY = "a"; //Affinity network level of a node (measures node's centrality/popularity).
	public static final String COLUMN_CENTRALITY = "c"; // Internal Usage weight of a node (measures the availability of the node)

	/* Measures the (eigenvector) similarity between the selected resource of node I and j.
	   For instance, I can provide a measure of battery similarity over time between nodes.
	   Or, it can provide a measure of similarity between category of applications.*/
	//public static final String COLUMN_I = "i"

	private static final String CREATE_MONDAY_TABLE = "create table "
			+ TABLE_MONDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SSID + " text, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";

	private static final String CREATE_TUESDAY_TABLE = "create table "
			+ TABLE_TUESDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SSID + " text, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";

	private static final String CREATE_WEDNESDAY_TABLE = "create table "
			+ TABLE_WEDNESDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SSID + " text, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";

	private static final String CREATE_THURSDAY_TABLE = "create table "
			+ TABLE_THURSDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SSID + " text, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";

	private static final String CREATE_FRIDAY_TABLE = "create table "
			+ TABLE_FRIDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SSID + " text, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";

	private static final String CREATE_SATURDAY_TABLE = "create table "
			+ TABLE_SATURDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SSID + " text, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";

	private static final String CREATE_SUNDAY_TABLE = "create table "
			+ TABLE_SUNDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SSID + " text, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";
	
	private static final String CREATE_MONDAY_PEERS_TABLE = "create table "
			+ TABLE_MONDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer, "
			+ COLUMN_AVAILABILITY + " text, "
			+ COLUMN_CENTRALITY + " text, "
            + COLUMN_NUM_ENCOUNTERS + " integer, "
            + COLUMN_ENCOUNTER_DURATION + " text "
			+ ");";
	
	private static final String CREATE_TUESDAY_PEERS_TABLE = "create table "
			+ TABLE_TUESDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer, "
			+ COLUMN_AVAILABILITY + " text, "
			+ COLUMN_CENTRALITY + " text, "
            + COLUMN_NUM_ENCOUNTERS + " integer, "
            + COLUMN_ENCOUNTER_DURATION + " text "
			+ ");";
	
	private static final String CREATE_WEDNESDAY_PEERS_TABLE = "create table "
			+ TABLE_WEDNESDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer, "
            + COLUMN_AVAILABILITY + " text, "
            + COLUMN_CENTRALITY + " text, "
            + COLUMN_NUM_ENCOUNTERS + " integer, "
            + COLUMN_ENCOUNTER_DURATION + " text "
			+ ");";
	
	private static final String CREATE_THURSDAY_PEERS_TABLE = "create table "
			+ TABLE_THURSDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer, "
			+ COLUMN_AVAILABILITY + " text, "
			+ COLUMN_CENTRALITY + " text, "
            + COLUMN_NUM_ENCOUNTERS + " integer, "
            + COLUMN_ENCOUNTER_DURATION + " text "
			+ ");";
	
	private static final String CREATE_FRIDAY_PEERS_TABLE = "create table "
			+ TABLE_FRIDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer, "
			+ COLUMN_AVAILABILITY + " text, "
			+ COLUMN_CENTRALITY + " text, "
            + COLUMN_NUM_ENCOUNTERS + " integer, "
            + COLUMN_ENCOUNTER_DURATION + " text "
			+ ");";
	
	private static final String CREATE_SATURDAY_PEERS_TABLE = "create table "
			+ TABLE_SATURDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer, "
			+ COLUMN_AVAILABILITY + " text, "
			+ COLUMN_CENTRALITY + " text, "
            + COLUMN_NUM_ENCOUNTERS + " integer, "
            + COLUMN_ENCOUNTER_DURATION + " text "
			+ ");";
	
	private static final String CREATE_SUNDAY_PEERS_TABLE = "create table "
			+ TABLE_SUNDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer, "
			+ COLUMN_AVAILABILITY + " text, "
			+ COLUMN_CENTRALITY + " text, "
            + COLUMN_NUM_ENCOUNTERS + " integer, "
            + COLUMN_ENCOUNTER_DURATION + " text "
			+ ");";
	
	private static final String CREATE_VISITS_TABLE = "create table "
			+ TABLE_VISITS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text not null, "
			+ COLUMN_TIMEON + " integer, "
			+ COLUMN_TIMEOUT + " integer, "
			+ COLUMN_DAYOFTHEWEEK + " integer, "
			+ COLUMN_HOUR + " integer"
			+ ");";

    private static final String CREATE_RESOURCE_USAGE_TABLE = "create table "
            + TABLE_RESOURCE_USAGE + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TYPE_OF_RESOURCE + " text not null, "
            + COLUMN_AVERAGE_USAGE_HOUR + " text not null, "
            + COLUMN_DAYOFTHEWEEK + " integer "
            + ");";

	private static final String CREATE_APPS_USAGE_TABLE = "create table "
			+ TABLE_APPS_USAGE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_APP_NAME + " text not null, "
			+ COLUMN_APP_CATEGORY + " text not null, "
			+ COLUMN_AVERAGE_USAGE_HOUR + " text not null, "
			+ COLUMN_DAYOFTHEWEEK + " integer "
			+ ");";

	private static final String CREATE_WEIGHTS_TABLE = "create table "
			+ TABLE_WEIGHTS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_AVAILABILITY + " text not null, "
			+ COLUMN_CENTRALITY + " text not null, "
			+ COLUMN_DAYOFTHEWEEK + " integer "
			+ ");";
	
	public ContextualManagerSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase dataBase) {
		Log.d("RESOURCE", "ON CREATE");
		dataBase.execSQL(CREATE_VISITS_TABLE);
		dataBase.execSQL(CREATE_MONDAY_TABLE);
		dataBase.execSQL(CREATE_TUESDAY_TABLE);
		dataBase.execSQL(CREATE_WEDNESDAY_TABLE);
		dataBase.execSQL(CREATE_THURSDAY_TABLE);
		dataBase.execSQL(CREATE_FRIDAY_TABLE);
		dataBase.execSQL(CREATE_SATURDAY_TABLE);
		dataBase.execSQL(CREATE_SUNDAY_TABLE);
		dataBase.execSQL(CREATE_MONDAY_PEERS_TABLE);
		dataBase.execSQL(CREATE_TUESDAY_PEERS_TABLE);
		dataBase.execSQL(CREATE_WEDNESDAY_PEERS_TABLE);
		dataBase.execSQL(CREATE_THURSDAY_PEERS_TABLE);
		dataBase.execSQL(CREATE_FRIDAY_PEERS_TABLE);
		dataBase.execSQL(CREATE_SATURDAY_PEERS_TABLE);
		dataBase.execSQL(CREATE_SUNDAY_PEERS_TABLE);
        dataBase.execSQL(CREATE_RESOURCE_USAGE_TABLE);
		dataBase.execSQL(CREATE_APPS_USAGE_TABLE);
		dataBase.execSQL(CREATE_WEIGHTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
		Log.d("RESOURCE", "ON UPDATE");
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_VISITS);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_MONDAY);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_TUESDAY);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_WEDNESDAY);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_THURSDAY);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIDAY);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_SATURDAY);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_SUNDAY);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_MONDAY_PEERS);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_TUESDAY_PEERS);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_WEDNESDAY_PEERS);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_THURSDAY_PEERS);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIDAY_PEERS);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_SATURDAY_PEERS);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_SUNDAY_PEERS);
        dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_RESOURCE_USAGE);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_APPS_USAGE);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHTS);
		onCreate(dataBase);
	}

	@Override
	public void onOpen(SQLiteDatabase database){
		Log.d("RESOURCE", "ON OPEN");
		//I use this to "clear" the resource usage table
		//onUpgrade(database, 1, 1);
	}
}