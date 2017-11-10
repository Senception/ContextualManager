/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains CMUmobileSQLiteHelper. This class provides the PerSense Light DataBase
 * 
 */

package com.senception.cmumobile.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class extends the SQLiteOpenHelper android class.
 */
public class CMUmobileSQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "cmumobile.db";
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

	// IDENTIFICATION
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SSID = "uuid";
	public static final String COLUMN_BSSID = "mac";
	public static final String COLUMN_GROUPID = "groupid";
    public static final String COLUMN_DAYOFTHEWEEK = "dayoftheweek";
	
	// ACCESS POINTS
	public static final String COLUMN_ATTRACTIVENESS = "attractiveness";
	public static final String COLUMN_DATETIME = "dateTime";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";

	// VISITS
	public static final String COLUMN_TIMEON = "timeon";
	public static final String COLUMN_TIMEOUT = "timeout";
	public static final String COLUMN_HOUR = "hour";

    //RESOURCE USAGE
    public static final String COLUMN_TYPE_OF_RESOURCE = "typeofresource";
    public static final String COLUMN_AVERAGE_USAGE_HOUR = "averageusagehour";


	private static final String CREATE_MONDAY_TABLE = "create table "
			+ TABLE_MONDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_SSID + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";

	private static final String CREATE_TUESDAY_TABLE = "create table "
			+ TABLE_TUESDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_SSID + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";

	private static final String CREATE_WEDNESDAY_TABLE = "create table "
			+ TABLE_WEDNESDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_SSID + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";

	private static final String CREATE_THURSDAY_TABLE = "create table "
			+ TABLE_THURSDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_SSID + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";

	private static final String CREATE_FRIDAY_TABLE = "create table "
			+ TABLE_FRIDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_SSID + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";

	private static final String CREATE_SATURDAY_TABLE = "create table "
			+ TABLE_SATURDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_SSID + " text, "
			+ COLUMN_ATTRACTIVENESS + " integer not null, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";

	private static final String CREATE_SUNDAY_TABLE = "create table "
			+ TABLE_SUNDAY + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DAYOFTHEWEEK + " text, "
			+ COLUMN_SSID + " text, "
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
			+ COLUMN_LONGITUDE + " integer "
			+ ");";
	
	private static final String CREATE_TUESDAY_PEERS_TABLE = "create table "
			+ TABLE_TUESDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";
	
	private static final String CREATE_WEDNESDAY_PEERS_TABLE = "create table "
			+ TABLE_WEDNESDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";
	
	private static final String CREATE_THURSDAY_PEERS_TABLE = "create table "
			+ TABLE_THURSDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";
	
	private static final String CREATE_FRIDAY_PEERS_TABLE = "create table "
			+ TABLE_FRIDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";
	
	private static final String CREATE_SATURDAY_PEERS_TABLE = "create table "
			+ TABLE_SATURDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
			+ ");";
	
	private static final String CREATE_SUNDAY_PEERS_TABLE = "create table "
			+ TABLE_SUNDAY_PEERS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SSID + " text not null, "
			+ COLUMN_BSSID + " text, "
			+ COLUMN_DATETIME + " text, "
			+ COLUMN_LATITUDE + " integer, "
			+ COLUMN_LONGITUDE + " integer "
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
	
	public CMUmobileSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase dataBase) {
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
	}

	@Override
	public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
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
		onCreate(dataBase);
	}

	@Override
	public void onOpen(SQLiteDatabase database){
		//I use this to "clear" the resource usage table
		//onUpgrade(database, 1, 1);
	}
}