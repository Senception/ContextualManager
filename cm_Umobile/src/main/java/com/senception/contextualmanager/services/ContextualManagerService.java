/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerService. This class contains the core functionalities of the application.
 * The ContextualManagerService will run in background, getting WI-FI parameters and storing the
 * required information in the database.
 *
 */

package com.senception.contextualmanager.services;

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
import com.senception.contextualmanager.communication.ContextualManagerReceive;
import com.senception.contextualmanager.activities.ContextualManagerMainActivity;
import com.senception.contextualmanager.communication.ContextualManagerSend;
import com.senception.contextualmanager.databases.ContextualManagerDataSource;
import com.senception.contextualmanager.databases.ContextualManagerSQLiteHelper;
import com.senception.contextualmanager.modals.ContextualManagerAP;
import com.senception.contextualmanager.pipelines.ContextualManagerFusedLocation;
import com.senception.contextualmanager.pipelines.ContextualManagerWifiManager;
import com.senception.contextualmanager.R;
import com.senception.contextualmanager.interfaces.ContextualManagerDataBaseChangeListener;
import com.senception.contextualmanager.interfaces.ContextualManagerWifiChangeListener;
import com.senception.contextualmanager.interfaces.ContextualManagerWifiP2PChangeListener;
import com.senception.contextualmanager.pipelines.ContextualManagerWifiP2P;
import com.senception.contextualmanager.wifi.Wifi;
import com.senception.contextualmanager.security.MacSecurity;

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
public class ContextualManagerService extends Service{

	private static final String TAG ="SERVICE --->";
	private int NOTIFICATION_ID = 1338;
	private WifiP2pManager manager;
	private Channel channel;
	ContextualManagerWifiP2P receiver;
	private IntentFilter intentFilter;
	private static double dispositionalTrust = 1.0;
	private static String fusedBssid = "";
	private static String fusedSsid = "";
	private static int noCoordinate = 0;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	ContextualManagerFusedLocation fusedLocation;
	CountDownTimer cdt ,cdtSave;
	private static SimpleDateFormat dataFormat = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss");
	private ContextualManagerWifiManager wifiManager;
	private PerSenseServiceWifiListener wifiListener;
	double latitude = 0.0;
	double longitude = 0.0;
	TimePickerDialog timePickerDialog;
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	String str_preferences = "";

	private Wifi wifi;

	AlarmReceiver mReceiver = new AlarmReceiver();
	static ContextualManagerDataSource dataSource;
	private ArrayList<ContextualManagerDataBaseChangeListener> listeners = new ArrayList<ContextualManagerDataBaseChangeListener>();
	private ArrayList<ContextualManagerWifiP2PChangeListener> listenersWifiP2p = new ArrayList<ContextualManagerWifiP2PChangeListener>();
	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder{
		public ContextualManagerService getService(){
			return ContextualManagerService.this;
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

		new ContextualManagerReceive(this);
		new ContextualManagerSend(this);

		wifi = new Wifi(this);
		wifi.start();

		fusedLocation = new ContextualManagerFusedLocation(ContextualManagerService.this);
		dataSource = new ContextualManagerDataSource(this);
		dataSource.openDB(true);

		wifiManager = new ContextualManagerWifiManager(this);

		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);
		receiver = new ContextualManagerWifiP2P(manager, channel, this);

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
		Intent notificationIntent = new Intent(this, ContextualManagerMainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);//there was Intent.FLAG_ACTIVITY_NEW_TASK); instead of flag 0 (last one)
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
	 * @param cmPeerList arraylist of all discovered wifi p2p devices
	 */
	public void discoveredPeers(ArrayList<ContextualManagerAP> cmPeerList){
        if(fusedLocation.mCurrentLocation != null){
			latitude = fusedLocation.mCurrentLocation.getLatitude();
			longitude = fusedLocation.mCurrentLocation.getLongitude();
			long contactTime = 0;

            ArrayList<ContextualManagerAP> allPeersOnDB = new ArrayList<>();

            //GET A LIST WITH ALL THE PEERS ON THE DATABASE*/
            if(!dataSource.isTableEmpty(checkWeek("peers"))) {
                allPeersOnDB = dataSource.getAllPeers(checkWeek("peers"));
            }

            /*CHECKS IF WE LOST CONNECTION WITH ANY PEER*/
            //if any peer on the db is not on the peers list found in this scan, then the peer was disconnected
            boolean connectionLost = false;
            for (int i = 0; i < allPeersOnDB.size(); i++) {
                ContextualManagerAP peerOnDB = allPeersOnDB.get(i);
                Log.d("teste", "Peer na bd: " + peerOnDB.getSSID());

                for (int j = 0; j < cmPeerList.size(); j++) {
                    if(peerOnDB.getBSSID().equals(MacSecurity.MD5hash(cmPeerList.get(j).getBSSID()))){
                        connectionLost = false;
                        break;
                    }
                    else {
                        connectionLost = true;
                    }
                }
                //We lost connection of a peer
                if( connectionLost &&  allPeersOnDB.get(i).getIsConnected() == 1 ) {
                    Log.d("teste", "peerConnection was lost, endEncounter from " + peerOnDB.getSSID() + " updated on db");
                    peerOnDB.setEndEncounter((int)(System.currentTimeMillis() / 1000));
                    peerOnDB.setIsConnected(0);

                    /*AVG ENCOUNTER CALCULATION*/
                    double peerOnDBAvgEncounterDuration = peerOnDB.getAvgEncounterDuration();
                    int peerOnDBEndEncounter = peerOnDB.getEndEncounter();
                    int peerOnDBStartEncounter = peerOnDB.getStartEncounter();
                    double count = (peerOnDBAvgEncounterDuration + (peerOnDBEndEncounter-peerOnDBStartEncounter))/ (double) (System.currentTimeMillis() / 1000);
                    Log.d("teste", "avgduration: " + count);
                    Log.d("teste", "sum: " + (peerOnDBAvgEncounterDuration + (peerOnDBEndEncounter-peerOnDBStartEncounter)));
                    Log.d("teste", "tempoactual: " + System.currentTimeMillis());
                    //todo check if avgEnv is saved on the db and produce C after it's finished
                    peerOnDB.setAvgEncounterDuration((peerOnDBAvgEncounterDuration + (peerOnDBEndEncounter-peerOnDBStartEncounter))/ (double) (System.currentTimeMillis() / 1000));
                    dataSource.updatePeer(peerOnDB, checkWeek("peers"));
                }
            }

			for(ContextualManagerAP item: cmPeerList){

                /*CHECKS IF ANY PEER IS ON THE DB*/
                String hashBSSID = MacSecurity.MD5hash(item.getBSSID());
				ContextualManagerAP ap = new ContextualManagerAP();
				if(!dataSource.hasPeer(hashBSSID, checkWeek("peers"))){
					ap.setSSID(item.getSSID());
					ap.setBSSID(hashBSSID);
					ap.setLatitude(latitude);
					ap.setLongitude(longitude);
                    ap.setAvailability(0.0);
                    ap.setCentrality(0.0);
                    ap.setNumEncounters(1);
                    ap.setStartEncounter((int)(System.currentTimeMillis()/1000)); //time in seconds System.currentTimeMillis()/1000
                    ap.setEndEncounter((int)(System.currentTimeMillis()/1000));
                    ap.setAvgEncounterDuration(0);
                    ap.setIsConnected(1);
					dataSource.registerNewPeers(ap, checkWeek("peers"));
                    Log.d("teste", "SAVED " + ap.getSSID() + "ON DB (1st time): " + item.getSSID());
				}
				else{
					ContextualManagerAP peer = dataSource.getPeer(hashBSSID, checkWeek("peers"));
					peer.setSSID(item.getSSID());
					peer.setBSSID(hashBSSID);
					peer.setLatitude(latitude);
					peer.setLongitude(longitude);
					peer.setNumEncounters(peer.getNumEncounters()+1);
                    peer.setEndEncounter((int)(System.currentTimeMillis()/1000));
                    if(peer.getIsConnected() == 0) {
                        peer.setIsConnected(1); // if a peer was disconnected, and reconnected
                        peer.setStartEncounter((int)(System.currentTimeMillis()/1000));
                    }
					dataSource.updatePeer(peer, checkWeek("peers"));
                    Log.d("teste", "UPDATED " + peer.getSSID() + " ON DB: " + item.getSSID());
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
					ContextualManagerAP ap = new ContextualManagerAP();
					if(latitude != 0.0 && longitude != 0.0 ){
						//String hashSSID = MD5hash(scan.SSID);
						ap.setSSID(scan.SSID);
                        String hashBSSID = MacSecurity.MD5hash(scan.BSSID);
                        //ap.setBSSID(scan.BSSID);
                        ap.setBSSID(hashBSSID);
						ap.setDayOfWeek(sdf.format(date));
						if(scan.SSID == "" || scan.SSID == null || scan.SSID.isEmpty()){
							ap.setSSID("Unknown");
						}
						else{
							//ap.setSSID(hashSSID);
                            ap.setSSID(scan.SSID);
						}
						ap.setDateTime(dataFormat.format(System.currentTimeMillis()));
						ap.setLatitude(latitude);
						ap.setLongitude(longitude);
						dataSource.registerNewAP(ap, checkWeek("ap"));
					}
				}
				else{
					if(latitude != 0.0 && longitude != 0.0){
						String hashSSID = MacSecurity.MD5hash(scan.SSID);
						ContextualManagerAP ap = dataSource.getAP(scan.BSSID, checkWeek("ap"));
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
			tableName = (peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_MONDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_MONDAY;
		}
		else if(dw.equalsIgnoreCase("Tue") || dw.equalsIgnoreCase("Ter")){
			tableName = (peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_TUESDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_TUESDAY;
		}
		else if(dw.equalsIgnoreCase("Wed") || dw.equalsIgnoreCase("Qua")){
			tableName =(peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_WEDNESDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_WEDNESDAY;
		}
		else if(dw.equalsIgnoreCase("Thu") || dw.equalsIgnoreCase("Qui")){
			tableName = (peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_THURSDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_THURSDAY;
		}
		else if(dw.equalsIgnoreCase("Fri") || dw.equalsIgnoreCase("Sex")){
			tableName = (peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_FRIDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_FRIDAY;
		}
		else if(dw.equalsIgnoreCase("Sat") || dw.equalsIgnoreCase("Sáb")){
			tableName = (peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_SATURDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_SATURDAY;
		}
		else if(dw.equalsIgnoreCase("Sun") || dw.equalsIgnoreCase("Dom")){
			tableName = (peerON.equalsIgnoreCase("peers")) ? ContextualManagerSQLiteHelper.TABLE_SUNDAY_PEERS : ContextualManagerSQLiteHelper.TABLE_SUNDAY;
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
				tableName = ContextualManagerSQLiteHelper.TABLE_MONDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = ContextualManagerSQLiteHelper.TABLE_MONDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = ContextualManagerSQLiteHelper.TABLE_VISITS;
			}
		}
		else if(dw.equalsIgnoreCase("Tue") || dw.equalsIgnoreCase("Ter")){
			if(option.equalsIgnoreCase("ap")){
				tableName = ContextualManagerSQLiteHelper.TABLE_TUESDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = ContextualManagerSQLiteHelper.TABLE_TUESDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = ContextualManagerSQLiteHelper.TABLE_VISITS;
			}
		}
		else if(dw.equalsIgnoreCase("Wed") || dw.equalsIgnoreCase("Qua")){
			if(option.equalsIgnoreCase("ap")){
				tableName = ContextualManagerSQLiteHelper.TABLE_WEDNESDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = ContextualManagerSQLiteHelper.TABLE_WEDNESDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = ContextualManagerSQLiteHelper.TABLE_VISITS;
			}

		}
		else if(dw.equalsIgnoreCase("Thu") || dw.equalsIgnoreCase("Qui")){
			if(option.equalsIgnoreCase("ap")){
				tableName = ContextualManagerSQLiteHelper.TABLE_THURSDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = ContextualManagerSQLiteHelper.TABLE_THURSDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = ContextualManagerSQLiteHelper.TABLE_VISITS;
			}
		}
		else if(dw.equalsIgnoreCase("Fri") || dw.equalsIgnoreCase("Sex")){
			if(option.equalsIgnoreCase("ap")){
				tableName = ContextualManagerSQLiteHelper.TABLE_FRIDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = ContextualManagerSQLiteHelper.TABLE_FRIDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = ContextualManagerSQLiteHelper.TABLE_VISITS;
			}
		}
		else if(dw.equalsIgnoreCase("Sat") || dw.equalsIgnoreCase("Sáb")){
			if(option.equalsIgnoreCase("ap")){
				tableName = ContextualManagerSQLiteHelper.TABLE_SATURDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = ContextualManagerSQLiteHelper.TABLE_SATURDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = ContextualManagerSQLiteHelper.TABLE_VISITS;
			}
		}
		else if(dw.equalsIgnoreCase("Sun") || dw.equalsIgnoreCase("Dom")){
			if(option.equalsIgnoreCase("ap")){
				tableName = ContextualManagerSQLiteHelper.TABLE_SUNDAY;
			}
			else if(option.equalsIgnoreCase("peer")){
				tableName = ContextualManagerSQLiteHelper.TABLE_SUNDAY_PEERS;
			}
			else if(option.equalsIgnoreCase("visit")){
				tableName = ContextualManagerSQLiteHelper.TABLE_VISITS;
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
	public static ArrayList<ContextualManagerAP> getSingleDayOrWeek(String dw, String peerON){

		if(dataSource != null){
			ArrayList<ContextualManagerAP> data = null;
			if(dw.equalsIgnoreCase("Mon") || dw.equalsIgnoreCase("Seg")){
				if(peerON.equalsIgnoreCase("peers")){
					String mon = ContextualManagerSQLiteHelper.TABLE_MONDAY_PEERS;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekPeers(mon).values());
				}
				else{
					String mon = ContextualManagerSQLiteHelper.TABLE_MONDAY;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekAP(mon).values());
				}
			}
			if(dw.equalsIgnoreCase("Tue") || dw.equalsIgnoreCase("Ter")){
				if(peerON.equalsIgnoreCase("peers")){
					String tue = ContextualManagerSQLiteHelper.TABLE_TUESDAY_PEERS;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekPeers(tue).values());
				}
				else{
					String tue = ContextualManagerSQLiteHelper.TABLE_TUESDAY;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekAP(tue).values());
				}
			}
			if(dw.equalsIgnoreCase("Wed") || dw.equalsIgnoreCase("Qua")){
				if(peerON.equalsIgnoreCase("peers")){
					String wed = ContextualManagerSQLiteHelper.TABLE_WEDNESDAY_PEERS;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekPeers(wed).values());
				}
				else{
					String wed = ContextualManagerSQLiteHelper.TABLE_WEDNESDAY;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekAP(wed).values());
				}
			}
			if(dw.equalsIgnoreCase("Thu") || dw.equalsIgnoreCase("Qui")){
				if(peerON.equalsIgnoreCase("peers")){
					String thu = ContextualManagerSQLiteHelper.TABLE_THURSDAY_PEERS;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekPeers(thu).values());
				}
				else{
					String thu = ContextualManagerSQLiteHelper.TABLE_THURSDAY;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekAP(thu).values());
				}
			}
			if(dw.equalsIgnoreCase("Fri") || dw.equalsIgnoreCase("Sex")){
				if(peerON.equalsIgnoreCase("peers")){
					String fri = ContextualManagerSQLiteHelper.TABLE_FRIDAY_PEERS;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekPeers(fri).values());
				}
				else{
					String fri = ContextualManagerSQLiteHelper.TABLE_FRIDAY;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekAP(fri).values());
				}
			}
			if(dw.equalsIgnoreCase("Sat") || dw.equalsIgnoreCase("Sáb")){
				if(peerON.equalsIgnoreCase("peers")){
					String sat = ContextualManagerSQLiteHelper.TABLE_SATURDAY_PEERS;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekPeers(sat).values());
				}
				else{
					String sat = ContextualManagerSQLiteHelper.TABLE_SATURDAY;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekAP(sat).values());
				}
			}
			if(dw.equalsIgnoreCase("Sun") || dw.equalsIgnoreCase("Dom")){
				if(peerON.equalsIgnoreCase("peers")){
					String sun = ContextualManagerSQLiteHelper.TABLE_SUNDAY_PEERS;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekPeers(sun).values());
				}
				else{
					String sun = ContextualManagerSQLiteHelper.TABLE_SUNDAY;
					data = new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekAP(sun).values());
				}
			}

			return data;
		}
		else
			return null;
	}

	@Override
	public void onDestroy(){

		wifi.close();

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
			ContextualManagerAP ap = dataSource.getAP(bssid, checkWeek("ap"));
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
	public void addListener(ContextualManagerWifiP2PChangeListener listener){
		listenersWifiP2p.add(listener);
	}

	public void notifyOnFoundPersence(ArrayList<ContextualManagerAP> disc, ArrayList<ContextualManagerAP> reg){
		for(ContextualManagerWifiP2PChangeListener listener: listenersWifiP2p){
			listener.onFoundPersenceGroup(disc, reg);
		}
	}

	public void notifyOnPeersFound(ArrayList<ContextualManagerAP> disc){
		for(ContextualManagerWifiP2PChangeListener listener: listenersWifiP2p){
			listener.onPeersFound(disc);
		}
	}

	public void setOnStateChangeListener(ContextualManagerDataBaseChangeListener listener){
		listeners.add(listener);
	}
	public void clearOnStateChangeListeners(){
		listeners.clear();
	}
	//Notifies a dataBase change to the listeners
	private void notifyDataBaseChange(){
		for(ContextualManagerDataBaseChangeListener listener : listeners){
			listener.onDataChange(new ArrayList<ContextualManagerAP>(dataSource.getDayOrWeekAP(checkWeek("ap")).values()));

		}
	}
	//Notifies a new message to the listeners
	//param newMessage the message to be notified
	private void notifyPredictedMoveChange(String newMessage){
		for(ContextualManagerDataBaseChangeListener listener : listeners){
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

	class PerSenseServiceWifiListener implements ContextualManagerWifiChangeListener {
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

					ContextualManagerAP ap = new ContextualManagerAP();
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
						ContextualManagerAP ap = new ContextualManagerAP();
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
						ContextualManagerAP ap = dataSource.getAP(bssid, checkWeek("ap"));
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

			ContextualManagerAP bestAp =dataSource.getBestAP(results, checkWeek("ap"));
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
				Toast.makeText(ContextualManagerService.this, "Export successful!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ContextualManagerService.this, "Export failed", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
