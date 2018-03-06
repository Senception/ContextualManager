/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains CMUmobileService. This class contains the core functionalities of the application.
 * The CMUmobileService will run in background, getting WI-FI parameters and storing the
 * required information in the database.
 *
 */

package com.senception.cmumobile.services;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.opencsv.CSVWriter;
import com.senception.cmumobile.aidl.CManagerInterface;
import com.senception.cmumobile.databases.CMUmobileDataSource;
import com.senception.cmumobile.pipelines.CMUmobileFusedLocation;
import com.senception.cmumobile.activities.CMUmobileMainActivity;
import com.senception.cmumobile.pipelines.CMUmobileWifiManager;
import com.senception.cmumobile.R;
import com.senception.cmumobile.databases.CMUmobileSQLiteHelper;
import com.senception.cmumobile.interfaces.CMUmobileDataBaseChangeListener;
import com.senception.cmumobile.interfaces.CMUmobileWifiChangeListener;
import com.senception.cmumobile.interfaces.CMUmobileWifiP2PChangeListener;
import com.senception.cmumobile.modals.CMUmobileAP;
import com.senception.cmumobile.pipelines.CMUmobileWifiP2P;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
//import android.util.Log;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class CMUmobileService extends Service{

	private static final String TAG ="SERVICE --->";
	private int NOTIFICATION_ID = 1338;
	private WifiP2pManager manager;
	private Channel channel;
	CMUmobileWifiP2P receiver;
	private IntentFilter intentFilter;
	private static double dispositionalTrust = 1.0;
	private static String fusedBssid = "";
	private static String fusedSsid = "";
	private static int noCoordinate = 0;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	CMUmobileFusedLocation fusedLocation;
	CountDownTimer cdt ,cdtSave;
	private static SimpleDateFormat dataFormat = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");
	private CMUmobileWifiManager wifiManager;
	private PerSenseServiceWifiListener wifiListener;
	double latitude = 0.0;
	double longitude = 0.0;
	TimePickerDialog timePickerDialog;
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	String str_preferences = "";

	AlarmReceiver mReceiver = new AlarmReceiver();
	static CMUmobileDataSource dataSource;
	private ArrayList<CMUmobileDataBaseChangeListener> listeners = new ArrayList<CMUmobileDataBaseChangeListener>();
	private ArrayList<CMUmobileWifiP2PChangeListener> listenersWifiP2p = new ArrayList<CMUmobileWifiP2PChangeListener>();
	private final IBinder mBinder = new LocalBinder();

	public ArrayList<CMUmobileAP> getPeersList() {
		return peersList;
	}

	private ArrayList<CMUmobileAP> peersList;

	public class LocalBinder extends Binder{
		public CMUmobileService getService(){
			return CMUmobileService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
			return mBinder;
	}

	@Override
	public void onCreate(){
		Log.d("Resource", "ENTROU NO UMSERVICE");
		super.onCreate();

		fusedLocation = new CMUmobileFusedLocation(CMUmobileService.this);
		dataSource = new CMUmobileDataSource(this);
		dataSource.openDB(true);

		wifiManager = new CMUmobileWifiManager(this);

		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);
		receiver = new CMUmobileWifiP2P(manager, channel, this);

		intentFilter = new IntentFilter();
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		registerReceiver(receiver, intentFilter);
		registerReceiver(mReceiver, new IntentFilter("com.example.report"));

		wifiListener = new PerSenseServiceWifiListener();
		wifiManager.setOnWifiChangeListener(wifiListener);
		wifiManager.noteOngoingConnection();

		sharedPreferences = getApplicationContext().getSharedPreferences("pml_sharedpreferences", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		str_preferences = sharedPreferences.getString("shared", "");

		if(str_preferences.equalsIgnoreCase("")){
			openTimePickerDialog(false);
		}

		cdt = new CountDownTimer(20000, 20000) {

			public void onTick(long millisUntilFinished) {

			}

			public void onFinish() {
				wifiManager.startPeriodicScanning();
				apScanResult();
				cdt.start(); // Call Again After 20 seconds
			}
		}.start();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		runAsForeground();
		return START_STICKY;
	}

	public void runAsForeground(){
		Intent notificationIntent = new Intent(this, CMUmobileMainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.cmumobilelight)
				.setContentText(getString(R.string.app_name))
				.setContentIntent(pendingIntent).build();

		startForeground(NOTIFICATION_ID, notification);

	}

	public void stopForeGround(){
		stopForeground(true);
	}

	/**
	 * Function discoveredPeers
	 * Register or Update discovered wifi p2p devices
	 * @param peerPersence arraylist of all discovered wifi p2p devices
	 */
	public void discoveredPeers(ArrayList<CMUmobileAP> peerPersence){
        /*Log.d("Resource", "DISCOVEREDPEERS LIST: ");
        for(CMUmobileAP a : peerPersence){
            Log.d("Resource", "SSID: " + a.getSSID());
        }*/

		this.peersList = peerPersence;

		if(fusedLocation.mCurrentLocation != null){
			latitude = fusedLocation.mCurrentLocation.getLatitude();
			longitude = fusedLocation.mCurrentLocation.getLongitude();
			long startTime = System.currentTimeMillis();
			long endTime = System.currentTimeMillis();

			for(CMUmobileAP item: peerPersence){
				CMUmobileAP ap = new CMUmobileAP();
				if(!dataSource.hasPeer(item.getBSSID(), checkWeek("peers"))){
					ap.setSSID(item.getSSID());
					ap.setBSSID(item.getBSSID());
					ap.setDateTime(dataFormat.format(System.currentTimeMillis()));
					ap.setLatitude(latitude);
					ap.setLongitude(longitude);
					//ap.setContactTime(0);
					ap.setNumEncounters(1);
					//ap.set
					dataSource.registerNewPeers(ap, checkWeek("peers"));
				}
				else{
					CMUmobileAP peer = dataSource.getPeer(item.getBSSID(), checkWeek("peers"));
					ap.setSSID(item.getSSID());
					ap.setBSSID(item.getBSSID());
					ap.setDateTime(dataFormat.format(System.currentTimeMillis()));
					ap.setLatitude(latitude);
					ap.setLongitude(longitude);
					//ap.setContactTime(peer.getContactTime());
					//get from db last numEncounters
					//ap.setNumEncounters(datasource.getNumEncounters()+1);
					dataSource.updatePeer(ap, checkWeek("peers"));
				}
			}
		}
		else{
			//Log.d(TAG, "[*] DISCOVERED PEERS --> Wait for coordinates");
		}
	}
	/**
	 * Function apScanResult
	 * Scan for available Access Point
	 */
	protected void apScanResult(){

		SimpleDateFormat sdf = new SimpleDateFormat("EEE");
		Date date = new Date();

		if(fusedLocation.mCurrentLocation != null){

			latitude = fusedLocation.mCurrentLocation.getLatitude();
			longitude = fusedLocation.mCurrentLocation.getLongitude();

			List<ScanResult> ar = wifiManager.getLastScanResults();
			for(ScanResult scan: ar){
				//scan.
				if(!dataSource.hasAP(scan.BSSID, checkWeek("ap"))){
					CMUmobileAP ap = new CMUmobileAP();
					if(latitude != 0.0 && longitude != 0.0 ){
						String hashSSID = MD5hash(scan.SSID);
						ap.setBSSID(scan.BSSID);
						ap.setDayOfWeek(sdf.format(date));
						if(scan.SSID == "" || scan.SSID == null || scan.SSID.isEmpty()){
							ap.setSSID("Unknown");
						}
						else{
							ap.setSSID(hashSSID);
						}
						ap.setDateTime(dataFormat.format(System.currentTimeMillis()));
						ap.setLatitude(latitude);
						ap.setLongitude(longitude);
						dataSource.registerNewAP(ap, checkWeek("ap"));
					}
				}
				else{
					if(latitude != 0.0 && longitude != 0.0){
						String hashSSID = MD5hash(scan.SSID);
						CMUmobileAP ap = dataSource.getAP(scan.BSSID, checkWeek("ap"));
						ap.setBSSID(scan.BSSID);
						ap.setDayOfWeek(sdf.format(date));
						ap.setSSID(hashSSID);
						ap.setDateTime(dataFormat.format(System.currentTimeMillis()));
						ap.setLatitude(latitude);
						ap.setLongitude(longitude);
						dataSource.updateAP(ap, checkWeek("ap"));
					}
				}
			}
		}
		else{
			//Log.d(TAG, "[*] APSCANRESULT --> Waiting for coordinate");
		}
	}

    /**
     * Hashes the given string using MD5
     * @param strToHash
     * @return hashed string
     */
    public String MD5hash(String strToHash) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(strToHash.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }


	/**
	 * Funtion checkWeek
	 * Check if it is peer or an ap, and what day 
	 * @param peerON value to check if is peer or not
	 * @return tableName current AP or PEERS 
	 */
	public static String checkWeek(String peerON){

		SimpleDateFormat sdf = new SimpleDateFormat("EEE");
		Date date = new Date();
		String dw = sdf.format(date);
		String tableName = "";

		if(dw.equalsIgnoreCase("Mon") || dw.equalsIgnoreCase("Seg")){
			tableName = (peerON.equalsIgnoreCase("peers")) ? CMUmobileSQLiteHelper.TABLE_MONDAY_PEERS : CMUmobileSQLiteHelper.TABLE_MONDAY;
		}
		else if(dw.equalsIgnoreCase("Tue") || dw.equalsIgnoreCase("Ter")){
			tableName = (peerON.equalsIgnoreCase("peers")) ? CMUmobileSQLiteHelper.TABLE_TUESDAY_PEERS :CMUmobileSQLiteHelper.TABLE_TUESDAY;
		}
		else if(dw.equalsIgnoreCase("Wed") || dw.equalsIgnoreCase("Qua")){
			tableName =(peerON.equalsIgnoreCase("peers")) ? CMUmobileSQLiteHelper.TABLE_WEDNESDAY_PEERS : CMUmobileSQLiteHelper.TABLE_WEDNESDAY;
		}
		else if(dw.equalsIgnoreCase("Thu") || dw.equalsIgnoreCase("Qui")){
			tableName = (peerON.equalsIgnoreCase("peers")) ? CMUmobileSQLiteHelper.TABLE_THURSDAY_PEERS : CMUmobileSQLiteHelper.TABLE_THURSDAY;
		}
		else if(dw.equalsIgnoreCase("Fri") || dw.equalsIgnoreCase("Sex")){
			tableName = (peerON.equalsIgnoreCase("peers")) ? CMUmobileSQLiteHelper.TABLE_FRIDAY_PEERS : CMUmobileSQLiteHelper.TABLE_FRIDAY;
		}
		else if(dw.equalsIgnoreCase("Sat") || dw.equalsIgnoreCase("Sáb")){
			tableName = (peerON.equalsIgnoreCase("peers")) ? CMUmobileSQLiteHelper.TABLE_SATURDAY_PEERS : CMUmobileSQLiteHelper.TABLE_SATURDAY;
		}
		else if(dw.equalsIgnoreCase("Sun") || dw.equalsIgnoreCase("Dom")){
			tableName = (peerON.equalsIgnoreCase("peers")) ? CMUmobileSQLiteHelper.TABLE_SUNDAY_PEERS : CMUmobileSQLiteHelper.TABLE_SUNDAY;
		}
		return tableName;
	}

	public static String check_day(String option){

		SimpleDateFormat sdf = new SimpleDateFormat("EEE");
		Date date = new Date();
		String dw = sdf.format(date);
		String tableName = "";

		if(dw.equalsIgnoreCase("Mon") || dw.equalsIgnoreCase("Seg")){
			if(option.equalsIgnoreCase("ap")){
				tableName = CMUmobileSQLiteHelper.TABLE_MONDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = CMUmobileSQLiteHelper.TABLE_MONDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = CMUmobileSQLiteHelper.TABLE_VISITS;
			}
		}
		else if(dw.equalsIgnoreCase("Tue") || dw.equalsIgnoreCase("Ter")){
			if(option.equalsIgnoreCase("ap")){
				tableName = CMUmobileSQLiteHelper.TABLE_TUESDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = CMUmobileSQLiteHelper.TABLE_TUESDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = CMUmobileSQLiteHelper.TABLE_VISITS;
			}
		}
		else if(dw.equalsIgnoreCase("Wed") || dw.equalsIgnoreCase("Qua")){
			if(option.equalsIgnoreCase("ap")){
				tableName = CMUmobileSQLiteHelper.TABLE_WEDNESDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = CMUmobileSQLiteHelper.TABLE_WEDNESDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = CMUmobileSQLiteHelper.TABLE_VISITS;
			}

		}
		else if(dw.equalsIgnoreCase("Thu") || dw.equalsIgnoreCase("Qui")){
			if(option.equalsIgnoreCase("ap")){
				tableName = CMUmobileSQLiteHelper.TABLE_THURSDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = CMUmobileSQLiteHelper.TABLE_THURSDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = CMUmobileSQLiteHelper.TABLE_VISITS;
			}
		}
		else if(dw.equalsIgnoreCase("Fri") || dw.equalsIgnoreCase("Sex")){
			if(option.equalsIgnoreCase("ap")){
				tableName = CMUmobileSQLiteHelper.TABLE_FRIDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = CMUmobileSQLiteHelper.TABLE_FRIDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = CMUmobileSQLiteHelper.TABLE_VISITS;
			}
		}
		else if(dw.equalsIgnoreCase("Sat") || dw.equalsIgnoreCase("Sáb")){
			if(option.equalsIgnoreCase("ap")){
				tableName = CMUmobileSQLiteHelper.TABLE_SATURDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = CMUmobileSQLiteHelper.TABLE_SATURDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = CMUmobileSQLiteHelper.TABLE_VISITS;
			}
		}
		else if(dw.equalsIgnoreCase("Sun") || dw.equalsIgnoreCase("Dom")){
			if(option.equalsIgnoreCase("ap")){
				tableName = CMUmobileSQLiteHelper.TABLE_SUNDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = CMUmobileSQLiteHelper.TABLE_SUNDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = CMUmobileSQLiteHelper.TABLE_VISITS;
			}
		}
		return tableName;
	}

	/**
	 * Function getSingleDayOrWeek
	 * Gets all AP or Peers in the day or week
	 * @param dw day of the week
	 * @param peerON value to check if is peer or not
	 * @return arraylist with Peers or AP
	 */
	public static ArrayList<CMUmobileAP> getSingleDayOrWeek(String dw, String peerON){

		if(dataSource != null){
			ArrayList<CMUmobileAP> data = null;
			if(dw.equalsIgnoreCase("Mon") || dw.equalsIgnoreCase("Seg")){
				if(peerON.equalsIgnoreCase("peers")){
					String mon = CMUmobileSQLiteHelper.TABLE_MONDAY_PEERS;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekPeers(mon).values());
				}
				else{
					String mon = CMUmobileSQLiteHelper.TABLE_MONDAY;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekAP(mon).values());
				}
			}
			if(dw.equalsIgnoreCase("Tue") || dw.equalsIgnoreCase("Ter")){
				if(peerON.equalsIgnoreCase("peers")){
					String tue = CMUmobileSQLiteHelper.TABLE_TUESDAY_PEERS;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekPeers(tue).values());
				}
				else{
					String tue = CMUmobileSQLiteHelper.TABLE_TUESDAY;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekAP(tue).values());
				}
			}
			if(dw.equalsIgnoreCase("Wed") || dw.equalsIgnoreCase("Qua")){
				if(peerON.equalsIgnoreCase("peers")){
					String wed = CMUmobileSQLiteHelper.TABLE_WEDNESDAY_PEERS;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekPeers(wed).values());
				}
				else{
					String wed = CMUmobileSQLiteHelper.TABLE_WEDNESDAY;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekAP(wed).values());
				}
			}
			if(dw.equalsIgnoreCase("Thu") || dw.equalsIgnoreCase("Qui")){
				if(peerON.equalsIgnoreCase("peers")){
					String thu = CMUmobileSQLiteHelper.TABLE_THURSDAY_PEERS;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekPeers(thu).values());
				}
				else{
					String thu = CMUmobileSQLiteHelper.TABLE_THURSDAY;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekAP(thu).values());
				}
			}
			if(dw.equalsIgnoreCase("Fri") || dw.equalsIgnoreCase("Sex")){
				if(peerON.equalsIgnoreCase("peers")){
					String fri = CMUmobileSQLiteHelper.TABLE_FRIDAY_PEERS;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekPeers(fri).values());
				}
				else{
					String fri = CMUmobileSQLiteHelper.TABLE_FRIDAY;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekAP(fri).values());
				}
			}
			if(dw.equalsIgnoreCase("Sat") || dw.equalsIgnoreCase("Sáb")){
				if(peerON.equalsIgnoreCase("peers")){
					String sat = CMUmobileSQLiteHelper.TABLE_SATURDAY_PEERS;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekPeers(sat).values());
				}
				else{
					String sat = CMUmobileSQLiteHelper.TABLE_SATURDAY;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekAP(sat).values());
				}
			}
			if(dw.equalsIgnoreCase("Sun") || dw.equalsIgnoreCase("Dom")){
				if(peerON.equalsIgnoreCase("peers")){
					String sun = CMUmobileSQLiteHelper.TABLE_SUNDAY_PEERS;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekPeers(sun).values());
				}
				else{
					String sun = CMUmobileSQLiteHelper.TABLE_SUNDAY;
					data = new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekAP(sun).values());
				}
			}

			return data;
		}
		else
			return null;
	}

	@Override
	public void onDestroy(){
		alarmManager.cancel(pendingIntent);
		unregisterReceiver(receiver);
		unregisterReceiver(mReceiver);

		wifiManager.close(this);
		dataSource.closeDB();
		fusedLocation.stopLocationUpdates();
		cdt.cancel();
	}

	private void openTimePickerDialog(boolean is24r){
		Calendar calendar = Calendar.getInstance();

		timePickerDialog = new TimePickerDialog(
				getApplicationContext(),
				onTimeSetListener,
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE),
				is24r);
		timePickerDialog.setTitle(getString(R.string.report_time));
		timePickerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		timePickerDialog.show();

	}
	TimePickerDialog.OnTimeSetListener onTimeSetListener
			= new TimePickerDialog.OnTimeSetListener(){

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			Calendar calNow = Calendar.getInstance();
			Calendar calSet = (Calendar) calNow.clone();

			calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calSet.set(Calendar.MINUTE, minute);
			calSet.set(Calendar.SECOND, 0);
			calSet.set(Calendar.MILLISECOND, 0);

			if(calSet.compareTo(calNow) <= 0){
				//Today Set time passed, count to tomorrow
				calSet.add(Calendar.DATE, 1);
			}

			setWeekAlarm(calSet);
			editor.putString("shared", "registered");
			editor.commit();
		}};

	/**
	 * Function setAlarm
	 * Sets an alarm for every day 23:50:00
	 */
	private void setWeekAlarm(Calendar calendar){
		pendingIntent = PendingIntent.getBroadcast(this,
				0, new Intent("com.example.report"), 0);
		/*Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 50);
		calendar.set(Calendar.SECOND, 00);*/

		// Schedule the alarm!
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
	}

	/**
	 * Function saveCleanDay
	 * Save DB to to server and Clean it after
	 */
	public void saveCleanDay(){
		Log.d(TAG, "NO SAVECLEANDARY");
		SimpleDateFormat sdf = new SimpleDateFormat("EEE");
		Date date = new Date();
		final String dw = sdf.format(date);

		new ExportDatabaseCSVTask().execute(check_day("ap"), "ap");
		new ExportDatabaseCSVTask().execute(check_day("peer"), "peer");
		new ExportDatabaseCSVTask().execute(check_day("visit"), "visit");
		if(haveNetworkConnection()){
			exportDB();
			if(dw.equalsIgnoreCase("Sun") || dw.equalsIgnoreCase("Dom")) {
				dataSource.deleteDBRow();
			}
		}
		else{
			cdtSave = new CountDownTimer(360000, 360000) {

				public void onTick(long millisUntilFinished) {
				}

				public void onFinish() {
					if(haveNetworkConnection()){
						exportDB();
						if(dw.equalsIgnoreCase("Sun") || dw.equalsIgnoreCase("Dom")) {
							dataSource.deleteDBRow();
						}
						cdtSave.cancel();
					}
					else{
						cdtSave.start();
					}
				}
			}.start();
		}
	}

	/**
	 * Function exportDB
	 * Export DB to to server
	 */
	public void exportDB(){
		Thread t = new Thread(){

			@SuppressWarnings("resource")
			@Override
			public void run() {
				try {
					String DATABASE_PATH = "/data/com.senception.CMUmobileight/databases/CMUmobileight.db";
					File data = Environment.getDataDirectory();
					File currentDB = new File(data, DATABASE_PATH);

					Socket s = new Socket("193.137.75.151", 9999);
					DataOutputStream dos = new DataOutputStream(s.getOutputStream());
					FileInputStream inputStream = new FileInputStream(currentDB);
					byte[] bufferWrite = new byte[1024];
					int len;
					dos.writeChars(getDeviceName().trim()+"-"+macAddress()+"/"+"##"+fileDate()+"-CMUmobileight.db");
					dos.flush();

					while((len = inputStream.read(bufferWrite)) != -1){
						dos.write(bufferWrite, 0, len);
					}

					dos.close();
					s.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	/**
	 * Function havaNetworkConnection
	 * Check if there is internet connection using wifi or mobile
	 * @return true is the is network connection and false if there is not
	 */
	public boolean haveNetworkConnection(){
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
		for(NetworkInfo ni : netInfo){
			if(ni.getTypeName().equalsIgnoreCase("WIFI")){
				if(ni.isConnected()) {
					haveConnectedWifi = true;
				}
			}
			if(ni.getTypeName().equalsIgnoreCase("MOBILE")){
				if(ni.isConnected()){
					haveConnectedMobile = true;
				}
			}
		}

		return haveConnectedWifi || haveConnectedMobile;
	}
	/**
	 * Function saveCleanDay
	 * @return the device name
	 */
	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		}
		return capitalize(manufacturer) + model;
	}
	/**
	 * Function capitalize
	 * @param str
	 * @return the device name in capital letters
	 */
	private static String capitalize(String str) {
		if (TextUtils.isEmpty(str)) {
			return str;
		}
		char[] arr = str.toCharArray();
		boolean capitalizeNext = true;
		String phrase = "";
		for (char c : arr) {
			if (capitalizeNext && Character.isLetter(c)) {
				phrase += Character.toUpperCase(c);
				capitalizeNext = false;
				continue;
			} else if (Character.isWhitespace(c)) {
				capitalizeNext = true;
			}
			phrase += c;
		}
		return phrase;
	}
	/**
	 * Function fileDate
	 * @return the device date
	 */
	public String fileDate(){
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		String formattedDate = format.format(date);

		return formattedDate;
	}
	/**
	 * Function macAddress
	 * @return the device mac address
	 */
	public String macAddress(){

		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface ntwInterface : interfaces) {

				if (ntwInterface.getName().equalsIgnoreCase("p2p0")) {
					byte[] byteMac = ntwInterface.getHardwareAddress();
					if (byteMac==null){
						return null;
					}
					StringBuilder strBuilder = new StringBuilder();
					for (int i=0; i<byteMac.length; i++) {
						strBuilder.append(String.format("%02X:", byteMac[i]));
					}

					if (strBuilder.length()>0){
						strBuilder.deleteCharAt(strBuilder.length()-1);
					}

					return strBuilder.toString().toLowerCase().replace(":", "");
				}

			}
		} catch (Exception e) {
			Log.e(TAG,e.getMessage());
		}
		return null;
	}

	public static String getFusedBssid(){
		return fusedBssid;
	}
	public static String getFusedSsid(){
		return fusedSsid;
	}
	public static int getNoCoordinate(){
		return noCoordinate;
	}
	public void actulizaCoordenadas(String bssid ,String ssid, double latitude, double longitude){
		SimpleDateFormat sdf = new SimpleDateFormat("EEE");
		Date date = new Date();

		if(latitude != 0.0 && longitude != 0.0){
			CMUmobileAP ap = dataSource.getAP(bssid, checkWeek("ap"));
			ap.setBSSID(bssid);
			ap.setDayOfWeek(sdf.format(date));
			ap.setSSID(ssid);
			ap.setAttractiveness(dispositionalTrust);
			ap.setDateTime(dataFormat.format(System.currentTimeMillis()));
			ap.setLatitude(latitude);
			ap.setLongitude(longitude);
			dataSource.updateAP(ap, checkWeek("ap"));
			noCoordinate = 0;
			notifyDataBaseChange();
		}
	}
	/*@Override
	public IBinder onBind(Intent intent){
		return mBinder;

	}*/
	public void addListener(CMUmobileWifiP2PChangeListener listener){
		listenersWifiP2p.add(listener);
	}

	public void notifyOnFoundPersence(ArrayList<CMUmobileAP> disc, ArrayList<CMUmobileAP> reg){
		for(CMUmobileWifiP2PChangeListener listener: listenersWifiP2p){
			listener.onFoundPersenceGroup(disc, reg);
		}
	}

	public void notifyOnPeersFound(ArrayList<CMUmobileAP> disc){
		for(CMUmobileWifiP2PChangeListener listener: listenersWifiP2p){
			listener.onPeersFound(disc);
		}
	}

	public void setOnStateChangeListener(CMUmobileDataBaseChangeListener listener){
		listeners.add(listener);
	}
	public void clearOnStateChangeListeners(){
		listeners.clear();
	}
	//Notifies a dataBase change to the listeners
	private void notifyDataBaseChange(){
		for(CMUmobileDataBaseChangeListener listener : listeners){
			listener.onDataChange(new ArrayList<CMUmobileAP>(dataSource.getDayOrWeekAP(checkWeek("ap")).values()));

		}
	}
	//Notifies a new message to the listeners
	//param newMessage the message to be notified
	private void notifyPredictedMoveChange(String newMessage){
		for(CMUmobileDataBaseChangeListener listener : listeners){
			listener.onStatusMessageChange(newMessage);
		}
	}
	//Set the dispositional trust, which is the default attractiveness
	//param dt dispositional trust
	//return true id dt id valid [0,1] , false otherwise
	@SuppressWarnings("static-access")
	public boolean setDispositionalTrust(double dt){
		if(dt >= 0.0 && dt <= 1.0 ){
			this.dispositionalTrust = dt;
			return true;
		}
		else{
			return false;
		}
	}

	class PerSenseServiceWifiListener implements CMUmobileWifiChangeListener {
		public void onWifiStateDisabled(boolean valid, String bssid, String ssid, long visitId, long connectionStart, long connectionEnd){
			notifyPredictedMoveChange(getString(R.string.wifiOff));
			if(valid){
				dataSource.updateVisit(visitId, null,null,null, connectionEnd);
				notifyDataBaseChange();
			}
		}
		public void onWifiStateEnabled(String ssid){
			notifyPredictedMoveChange(getString(R.string.wifiOn));
			notifyPredictedMoveChange(getString(R.string.bestAp)+" "+"(" + ssid + ")");

		}
		public void onWifiConnectionDown(boolean valid, String bssid, String ssid, long visitId, long connectionStart, long connectionEnd){
			notifyPredictedMoveChange(getString(R.string.wifiDown));
			if(valid){
				dataSource.updateVisit(visitId, null, null, null, connectionEnd);
				notifyDataBaseChange();
			}
		}
		public long onWifiConnectionUp(String bssid, String ssid, List<ScanResult> lastScanResults){
			notifyPredictedMoveChange(getString(R.string.wifiUp) +"(" + ssid + ")");
			SimpleDateFormat sdf = new SimpleDateFormat("EEE");
			Date date = new Date();

			if(fusedLocation.mCurrentLocation == null){

				if(!dataSource.hasAP(bssid, checkWeek("ap"))){

					CMUmobileAP ap = new CMUmobileAP();
					ap.setBSSID(bssid);
					ap.setDayOfWeek(sdf.format(date));
					ap.setSSID(ssid);
					ap.setAttractiveness(dispositionalTrust);
					ap.setDateTime(dataFormat.format(System.currentTimeMillis()));
					ap.setLatitude(latitude);
					ap.setLongitude(longitude);
					dataSource.registerNewAP(ap, checkWeek("ap"));

					fusedBssid = bssid;
					fusedSsid = ssid;
					noCoordinate = 1;
				}
			}

			if(fusedLocation.mCurrentLocation != null){
				latitude = fusedLocation.mCurrentLocation.getLatitude();
				longitude = fusedLocation.mCurrentLocation.getLongitude();


				if(!dataSource.hasAP(bssid, checkWeek("ap"))){
					if(latitude != 0.0 && longitude != 0.0){
						CMUmobileAP ap = new CMUmobileAP();
						ap.setBSSID(bssid);
						ap.setDayOfWeek(sdf.format(date));
						ap.setSSID(ssid);
						ap.setAttractiveness(dispositionalTrust);
						ap.setDateTime(dataFormat.format(System.currentTimeMillis()));
						ap.setLatitude(latitude);
						ap.setLongitude(longitude);
						dataSource.registerNewAP(ap, checkWeek("ap"));
					}
				}
				else{
					if(latitude != 0.0 && longitude != 0.0){
						CMUmobileAP ap = dataSource.getAP(bssid, checkWeek("ap"));
						ap.setBSSID(bssid);
						ap.setDayOfWeek(sdf.format(date));
						ap.setSSID(ssid);
						ap.setAttractiveness(dispositionalTrust);
						ap.setDateTime(dataFormat.format(System.currentTimeMillis()));
						ap.setLatitude(latitude);
						ap.setLongitude(longitude);
						dataSource.updateAP(ap, checkWeek("ap"));
					}
				}
			}

			computeBestAp(bssid, lastScanResults);
			notifyDataBaseChange();

			return dataSource.registerNewVisit(ssid, bssid, wifiManager.wifiCurrentAPStart, wifiManager.wifiCurrentAPStart);
		}

		public void onWifiAvailableNetworksChange(String bssid, List<ScanResult> results){
			computeBestAp(bssid, results);
		}
		@SuppressWarnings("unused")
		private void computeBestAp(String bssid, List<ScanResult> results){

			CMUmobileAP bestAp =dataSource.getBestAP(results, checkWeek("ap"));
			if(bestAp != null){
				if(bestAp.getBSSID().equals(bssid)){
					long timeToMove = dataSource.getStationaryTime(bestAp) - (System.currentTimeMillis() - wifiManager.wifiCurrentAPStart)/1000;
					notifyPredictedMoveChange(getString(R.string.bestAp)+" "+"(" + bestAp.getSSID() + ")");
				}
				else{
					long timeToMove = dataSource.getStationaryTime(dataSource.getAP(bssid, checkWeek("ap"))) - (System.currentTimeMillis() - wifiManager.wifiCurrentAPStart)/1000;
					if(timeToMove >= 0){
						notifyPredictedMoveChange(getString(R.string.handover)+" " + bestAp.getSSID()+" " + getString(R.string.expected)+" " + timeToMove + "s");
					}
					else {
						notifyPredictedMoveChange(getString(R.string.handover)+" " + bestAp.getSSID()+" " + getString(R.string.expectedd)+" " + (0-timeToMove) + "s ago");
					}
				}
			}
			else{
				notifyPredictedMoveChange(getString(R.string.noAp));
			}
		}
	}

	/*Alarm BroadcastReceiver*/
	public class AlarmReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			saveCleanDay();
		}
	}

	public class ExportDatabaseCSVTask extends AsyncTask<String, String , Boolean> {

		protected Boolean doInBackground(final String... args) {
			String file_name = fileDate()+"-"+args[1]+"-"+macAddress()+"-pml.csv";
			File exportDir = new File(Environment.getExternalStorageDirectory(), "cm_umobile");

			if (!exportDir.exists()) {
				exportDir.mkdirs();
			}

			File file = new File(exportDir.getAbsolutePath(),file_name);
			try {
				file.createNewFile();
				CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
				Cursor curCSV = dataSource.db.rawQuery("select * from " + args[0],null);
				csvWrite.writeNext(curCSV.getColumnNames());
				while(curCSV.moveToNext()) {
					String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
					for(int i=0;i<curCSV.getColumnNames().length;i++)
					{
						mySecondStringArray[i] =curCSV.getString(i);
					}
					csvWrite.writeNext(mySecondStringArray);
				}
				curCSV.close();
				csvWrite.close();
				return true;
			} catch (IOException e) {
				Log.e("PerSense Mobile Light", e.getMessage(), e);
				return false;
			}
		}

		protected void onPostExecute(final Boolean success) {
			if (success) {
				Toast.makeText(CMUmobileService.this, "Export successful!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(CMUmobileService.this, "Export failed", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
