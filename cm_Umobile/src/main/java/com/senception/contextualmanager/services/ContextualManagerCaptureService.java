package com.senception.contextualmanager.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.senception.contextualmanager.R;
import com.senception.contextualmanager.activities.ContextualManagerMainActivity;
import com.senception.contextualmanager.databases.ContextualManagerDataSource;
import com.senception.contextualmanager.databases.ContextualManagerSQLiteHelper;
import com.senception.contextualmanager.inference.ContextualManagerCentrality;
import com.senception.contextualmanager.inference.ContextualManagerAvailability;
import com.senception.contextualmanager.modals.ContextualManagerAP;
import com.senception.contextualmanager.modals.ContextualManagerAppUsage;
import com.senception.contextualmanager.modals.ContextualManagerPhysicalUsage;
import com.senception.contextualmanager.physical_usage.ContextualManagerBattery;
import com.senception.contextualmanager.physical_usage.ContextualManagerCPU;
import com.senception.contextualmanager.physical_usage.ContextualManagerMemory;
import com.senception.contextualmanager.physical_usage.ContextualManagerPhysicalResourceType;
import com.senception.contextualmanager.physical_usage.ContextualManagerStorage;
import com.senception.contextualmanager.security.MacSecurity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.senception.contextualmanager.services.ContextualManagerService.checkWeek;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@senception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @author Rute Sofia
 * @version 0.1
 *
 * @file Contains ContextualManagerCaptureService. This service runs in the
 * background, for each hour it gets the 4 resource usages (energy,
 * cpu, memory and storage) and the apps usage, for each day it
 * saves them into the database.
 * for DEMO, it is sampling per MINUTE - @todo change later for hour
 */
public class ContextualManagerCaptureService extends Service {

    private int NOTIFICATION_ID = 1338;
    private static final String TAG = ContextualManagerCaptureService.class.getSimpleName();
    private static ContextualManagerPhysicalUsage energy;
    private static ContextualManagerPhysicalUsage cpu;
    private static ContextualManagerPhysicalUsage memory;
    private static ContextualManagerPhysicalUsage storage;
    private static ArrayList<ArrayList<Double>> rList = new ArrayList();
    //private static ArrayList<Double> availability = new ArrayList<>();
    private static List<UsageStats> ustats;
    private static List<ContextualManagerAppUsage> apps = new ArrayList<>();
    private final IBinder mBinder = new LocalBinder();
    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntentHourly;
    private static PendingIntent pendingIntentDaily;
    private static AlarmReceiver alarmReceiverHourly;
    private static AlarmReceiver alarmReceiverDaily;
    private static ContextualManagerDataSource dataSource;
    public static final long TIMESTAMP = System.currentTimeMillis();

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();
        dataSource = new ContextualManagerDataSource(this);
        dataSource.openDB(true);

        /*
        * Initializes the physical resource usage table in the DB
        */
        energy = new ContextualManagerPhysicalUsage(ContextualManagerPhysicalResourceType.ENERGY);
        initializeResourceTable(energy);
        cpu = new ContextualManagerPhysicalUsage(ContextualManagerPhysicalResourceType.CPU);
        initializeResourceTable(cpu);
        memory = new ContextualManagerPhysicalUsage(ContextualManagerPhysicalResourceType.MEMORY);
        initializeResourceTable(memory);
        storage = new ContextualManagerPhysicalUsage(ContextualManagerPhysicalResourceType.STORAGE);
        initializeResourceTable(storage);

        /**
         * Initializes the apps resource usage table in the DB with all the apps in the device
         */
        ustats = getDeviceAppsList(this);
        for (UsageStats stat : ustats) {
            ContextualManagerAppUsage app = new ContextualManagerAppUsage(stat.getPackageName(), "Category: To-Do");
            apps.add(app);
            initializeAppsTable(app);
        }

    //    Log.d(TAG, "All the apps used by the device were initialized if they weren't already on the DB");

        /*
        * @todo implement category of an app
        * Creates a thread where the categories of all apps in the device will be determined
        * by getting it's category in the google apstore, to make this possible, internet
        * connection is necessary, if there is no internet connection, categories will not be available.
        */
        //getCategory();

        //Schedules the alarm to trigger every hour (from this exact moment)
        alarmReceiverHourly = new AlarmReceiver();
        registerReceiver(alarmReceiverHourly, new IntentFilter("com.example.resource_usage_hourly"));
        //Schedules the alarm to trigger every day (from midnight on)
        alarmReceiverDaily = new AlarmReceiver();
        registerReceiver(alarmReceiverDaily, new IntentFilter("com.example.resource_usage_daily"));

        setAlarm();

   //     Log.d(TAG, "Alarms setted to hourly and daily");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        runAsForeground();
        return START_STICKY;
    }

    /**
     * Makes the service run in the foreground
     */
    public void runAsForeground(){
        Intent notificationIntent = new Intent(this, ContextualManagerMainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cmumobilelight)
                .setContentText(getString(R.string.app_name))
                .setContentIntent(pendingIntent).build();

        startForeground(NOTIFICATION_ID, notification);

    }

    /**
     * Stops the service from running in the foreground.
     */
    public void stopForeGround(){
        stopForeground(true);
    }

    /**
     * Checks if the given app is already in the apps usage table, if not, adds it.
     * @param app the app to initialize.
     */
    private void initializeAppsTable(ContextualManagerAppUsage app) {
        if (!dataSource.rowExists(ContextualManagerSQLiteHelper.TABLE_APPS_USAGE, app.getAppName(), ContextualManagerSQLiteHelper.COLUMN_APP_NAME)) {
            dataSource.registerNewAppUsage(app);
        }
    }

    /**
     * Checks if the given physical resource usage (pru) is already in the resource usage table, if not, adds it.
     * @param pru the physical resource usage to initialize.
     */
    private void initializeResourceTable(ContextualManagerPhysicalUsage pru) {
        if (!dataSource.rowExists(ContextualManagerSQLiteHelper.TABLE_RESOURCE_USAGE, pru.getResourceType().toString(), ContextualManagerSQLiteHelper.COLUMN_TYPE_OF_RESOURCE)) {
            dataSource.registerNewResourceUsage(pru);
            Log.d(TAG, "Physical resource usage with type " + pru.getResourceType() + " initialized.");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * To bind locally
     */
    public class LocalBinder extends Binder {
        public ContextualManagerCaptureService getService(){
            return ContextualManagerCaptureService.this;
        }
    }

    @Override
    public void onDestroy() {
        alarmManager.cancel(pendingIntentHourly);
        alarmManager.cancel(pendingIntentDaily);
        unregisterReceiver(alarmReceiverHourly);
        unregisterReceiver(alarmReceiverDaily);
        dataSource.closeDB();
    }

    /**
     * Sets the alarm to trigger hourly and to trigger daily, each with different intents
     */
    public void setAlarm(){
        //To start at the current time.
        Long timeStartHourly = System.currentTimeMillis();
        //To start at midnight todo it can start earlier
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.MILLISECOND, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.HOUR_OF_DAY, 0);

        /*
         * Todo to start at midnight
         * To use after tests: (to start allways at midnight)
         * midnight.add(Calendar.DAY_OF_MONTH, 1);
        */

        Long timeStartDaily = midnight.getTimeInMillis();

        pendingIntentHourly = PendingIntent.getBroadcast(this, 0, new Intent("com.example.resource_usage_hourly"), 0);
        pendingIntentDaily = PendingIntent.getBroadcast(this, 0, new Intent("com.example.resource_usage_daily"), 0);

        // Schedule the alarms
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //set alarm to start immediately, and repeating for each hour (for tests: every min) todo change to hourly
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeStartHourly, 60*1000/*AlarmManager.INTERVAL_HOUR*/, pendingIntentHourly);
        //set alarm to start at midnight, and repeating for each day (for tests: every 2 min) todo change to daily
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeStartDaily, 2*60*1000/*AlarmManager.INTERVAL_DAY*/, pendingIntentDaily);
    }

    /**
     * Alarm BroadcastReceiver
     * On receiving the broadcast if the broadcast came from and hourly intent
     * then it captures the usage, else it saves them into the database.
     * */
    public class AlarmReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ContextualManagerMainActivity.backupDB(context);
            Calendar day = Calendar.getInstance();
            int newDayOfTheWeek = day.get(Calendar.DAY_OF_WEEK);

            //Captures the usage
            if(intent.getAction().equals("com.example.resource_usage_hourly")) { //If hourly: Captures usage
                Log.d(TAG, "Alarm triggered after 1min");
                //get current time
                Calendar currentTime = Calendar.getInstance();

                /* Captures the 4 physical resource usage */
                capturePhysicalUsage(energy);
                capturePhysicalUsage(cpu);
                capturePhysicalUsage(memory);
                capturePhysicalUsage(storage);

            //    Log.d(TAG, "Captured all the physical resources.");

                /*Availability Calculation:*/
                // Captures the R (b*b*cpu*mem*storage) every hour (for tests: min)
                //rList.add();
                //Log.d(TAG, "RList: " + rList);
                // Calculates the A - availability (sum of all Rs) every hour (for tests: min)
                double Availability = ContextualManagerAvailability.calculateA(ContextualManagerAvailability.calculateR(energy.getUsagePerHour(), cpu.getUsagePerHour(), memory.getUsagePerHour(), storage.getUsagePerHour()));
                //int currentMinute = currentTime.get(Calendar.MINUTE); //todo change to hourly
                // double A = availability.get(currentMinute);
             //   Log.d(TAG, "In CaptureService Calculated A: " + Availability);

                /*Centrality Calculation:*/
                double C = ContextualManagerCentrality.calculateC(dataSource);
               // Log.d(TAG, "In CaptureService Calculated C: " + C);

                /* Saves A and C into the database */
                ContextualManagerAP mySelf = new ContextualManagerAP();
                mySelf.setSSID("self");
                mySelf.setBSSID(MacSecurity.md5Hash("self"));
                mySelf.setAvailability(Availability);
                mySelf.setCentrality(C);
                if(!dataSource.hasPeer(mySelf.getBSSID(), ContextualManagerService.checkWeek("peers"))) {
                    dataSource.registerNewPeers(mySelf, ContextualManagerService.checkWeek("peers"));
                }
                else {
                    dataSource.updatePeer(mySelf, ContextualManagerService.checkWeek("peers"));
                }

                //Log.d(TAG, "A and C saved into the DB on the correspondent peer table of the current day of the week as the peer 'self'");

                /*Similarity computation*/
                //get peer list, and foreach one updates its similarity
                if(!dataSource.isTableEmpty(checkWeek("peers"))) {
                    ArrayList<ContextualManagerAP> peerList = dataSource.getAllPeers(checkWeek("peers"));

                    for (ContextualManagerAP peer : peerList) {
                        double numEncounters = peer.getNumEncounters();
                        double avgEncDur = peer.getAvgEncounterDuration();

                        /* similarity (I) should be computed as:
                         * similarity(a,b) = 1 - abs(a-b)/max(a,b)
                         * a: node's value and b peer value
                         * @todo cosine similarity, for array values (currently A and C are doubles)
                         * for the routing interface, similarity is computed simply based on
                         * the numEncounters and avgEncounter duration.
                         * Similarity A weights similarity between  availability
                         * Similarity C weights similarity between centralities of the node
                         * @todo improve similarity C and A as well as similarity based on node interests (e.g., apps used)
                         */


                      /*  double similarityA = 1-(Math.abs(peer.getAvailability()-mySelf.getAvailability())/Math.max(peer.getAvailability(),mySelf.getAvailability()));
                        double similarityC = 1-(Math.abs(peer.getCentrality()-mySelf.getCentrality())/Math.max(peer.getCentrality(),mySelf.getCentrality()));
                        double similarity = (similarityA+similarityC)/2;
                        */
                      // similarity for the routing
                        double similarity = (numEncounters*avgEncDur);
                        peer.setSimilarity(similarity);
                        dataSource.updatePeer(peer, checkWeek("peers"));
                        Log.d(TAG, "Similarity with peer: " + peer.getSSID() + " is: "+similarity);
                    }
                }

                /* Captures the apps usage */
                captureAppsUsage();
              //  Log.d(TAG, "Captured the apps usage");

                ContextualManagerMainActivity.backupDB(context); //todo eliminate
            }
            else{ // if daily: Saves the usage percentage into the database
             //   Log.d(TAG, "Alarm triggered after 2min");
                /*Saves the 4 physical resource usage into the database*/
                energy.setDayOfTheWeek(String.valueOf(newDayOfTheWeek));
                dataSource.updateResourceUsage(energy);
                cpu.setDayOfTheWeek(String.valueOf(newDayOfTheWeek));
                dataSource.updateResourceUsage(cpu);
                memory.setDayOfTheWeek(String.valueOf(newDayOfTheWeek));
                dataSource.updateResourceUsage(memory);
                storage.setDayOfTheWeek(String.valueOf(newDayOfTheWeek));
                dataSource.updateResourceUsage(storage);

                /*Saves the apps usage into the database*/
                for (ContextualManagerAppUsage app: apps){
                    boolean updated = dataSource.updateAppUsage(app);
                    if (!updated){
                        dataSource.registerNewAppUsage(app);
                    }
                }
                Log.d(TAG, "Saved the physical resources and the apps usage on the DB");
            }
            ContextualManagerMainActivity.backupDB(context);
        }
    }

    /**
     * Given a physical resource usage, this method will capture its usage
     * @param pru the physical resource usage
     */
    public void capturePhysicalUsage(ContextualManagerPhysicalUsage pru) {

        Calendar currentTime = Calendar.getInstance();
        //todo int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        switch(pru.getResourceType()){
            case ENERGY:
                double level = ContextualManagerBattery.getEnergyLevel(this);
                Log.d(TAG, "Captured the physical resource usage " + pru.getResourceType() + "with usage: " + level);
                pru.getUsagePerHour().set(currentMinute, level);
                break;
            case CPU: //100d - because we want not the used but the available
                double cpuUsage = ContextualManagerCPU.getCpuUsageStatistic();
                Log.d(TAG, "Captured the physical resource usage " + pru.getResourceType() + "with usage: " + cpuUsage);
                pru.getUsagePerHour().set(currentMinute, 100d - cpuUsage);
                break;
            case MEMORY:
                double mem = ContextualManagerMemory.getCurrentRam(this);
                Log.d(TAG, "Captured the physical resource usage " + pru.getResourceType() + "with usage: " + mem);
                pru.getUsagePerHour().set(currentMinute, 100d - mem);
                break;
            case STORAGE:
                double storageUsg = ContextualManagerStorage.getCurrentStorage();
                Log.d(TAG, "Captured the physical resource usage " + pru.getResourceType() + "with usage: " + storageUsg);
                pru.getUsagePerHour().set(currentMinute, 100d - storageUsg);
                break;
            default:
                Log.d(TAG, "THAT RESOURCE ISN'T RECOGNIZED.");
                break;
        }
    }

    /**
     * Captures the usage of all the used apps on the device
     */
    @SuppressLint("NewApi")
    private static void captureAppsUsage() {

        int totalTimeSpent = 0;
        for (UsageStats ustat : ustats){
            totalTimeSpent += ustat.getTotalTimeInForeground(); //time in miliseconds
        }

        Calendar currentTime = Calendar.getInstance();
        //todo int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        for (int i = 0; i < ustats.size(); i++){
            long usage = ustats.get(i).getTotalTimeInForeground();
            int percentage = 0;
            if (usage != 0){
                percentage = (int) ((usage/ (double) totalTimeSpent * 100.0) + 0.5);
            }
            apps.get(i).getUsagePerHour().set(currentMinute, percentage);
        }
    }

    /**
     * Gets a list of the apps used by the device in a certain interval of time.
     * @param context
     * @return the apps list
     */
    @SuppressLint("NewApi")
    public static List<UsageStats> getDeviceAppsList(Context context){

        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        //begin date
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        long start = startDate.getTimeInMillis();

        //end date
        Calendar endDate = Calendar.getInstance();
        long end = endDate.getTimeInMillis();

        return usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
    }

    /**
     * @todo category
     * Updates the category of each app in the apps list if there is internet connection
     * and the respective app has a category affiliated in google play store -- To-complete
     * https://stackoverflow.com/questions/10710442/how-to-get-category-for-each-app-on-device-on-android
     */
    private void getCategory() {
        Thread downloadThread = new Thread() {
            public void run() {
                if (isNetworkAvailable()) {
                    for (ContextualManagerAppUsage app : apps) {
                        String GOOGLE_URL = "https://play.google.com/store/apps/details?id=";
                        final String query_url = GOOGLE_URL + app.getAppName() + "&&hl=en";

                        Document doc;
                        try {
                            doc = Jsoup.connect(query_url).get();
                            String category = doc.select("span[itemprop=genre]").first().text();
                            app.setAppCategory(category);
                        } catch (IOException e) {
                            Log.d(TAG, app.getAppName() + " doesn't have a category a affiliated with google play store.");
                            //e.printStackTrace();
                        }
                    }
                }
            }
        };

        downloadThread.start();
    }

    /**
     * Checks if there is currently any network available
     * @return true if there is internet access.
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
