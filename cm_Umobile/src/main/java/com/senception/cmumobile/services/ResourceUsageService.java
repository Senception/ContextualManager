package com.senception.cmumobile.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.senception.cmumobile.databases.CMUmobileDataSource;
import com.senception.cmumobile.databases.CMUmobileSQLiteHelper;
import com.senception.cmumobile.resource_usage.app_usage.AppResourceUsage;
import com.senception.cmumobile.resource_usage.physical_usage.BatteryUsage;
import com.senception.cmumobile.resource_usage.physical_usage.CPUUsage;
import com.senception.cmumobile.resource_usage.physical_usage.MemoryUsage;
import com.senception.cmumobile.resource_usage.physical_usage.PhysicalResourceType;
import com.senception.cmumobile.resource_usage.physical_usage.PhysicalResourceUsage;
import com.senception.cmumobile.resource_usage.physical_usage.StorageUsage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ResourceUsageService. This class run's in the
 * background, for each hour it gets the 4 resource usages (energy,
 * cpu, memory and storage) and saves them in to the database.
 */
public class ResourceUsageService extends Service{

    private static final String TAG = "RESOURCE USAGE SERVICE";
    private static PhysicalResourceUsage energy;
    private static PhysicalResourceUsage cpu;
    private static PhysicalResourceUsage memory;
    private static PhysicalResourceUsage storage;
    private static List<UsageStats> apps;
    private final IBinder mBinder = new LocalBinder();
    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntentHourly;
    private static PendingIntent pendingIntentDaily;
    AlarmReceiver alarmReceiverHourly;
    AlarmReceiver alarmReceiverDaily;
    CMUmobileDataSource dataSource;

    @Override
    public void onCreate() {
        super.onCreate();
        dataSource = new CMUmobileDataSource(this);
        dataSource.openDB(true);

        /*
        * Initializes the physical resource usage table in the DB
        * */
        energy = new PhysicalResourceUsage(PhysicalResourceType.ENERGY);
        //initializeResourceTable(energy);
        cpu = new PhysicalResourceUsage(PhysicalResourceType.CPU);
        //initializeResourceTable(cpu);
        memory = new PhysicalResourceUsage(PhysicalResourceType.MEMORY);
        //initializeResourceTable(memory);
        storage = new PhysicalResourceUsage(PhysicalResourceType.STORAGE);
        //initializeResourceTable(storage);

        /**
         * Initializes the apps resource usage table in the DB with all the apps in the device
         */
        apps = getDeviceAppsList(this);
        int count = 0;
        for (UsageStats app : apps) {
            Log.d(TAG, app.toString());
            if (count < 1) {
                count++;
                initializeAppsTable(app);
            }
        }


        backupDB();
        Log.d(TAG, "FEZ BACKUP");
/*
        //Schedules the alarm to trigger every hour (from this exact moment)
        alarmReceiverHourly = new AlarmReceiver();
        registerReceiver(alarmReceiverHourly, new IntentFilter("com.example.resource_usage_hourly"));
        //Schedules the alarm to trigger every day (from midnight on)
        alarmReceiverDaily = new AlarmReceiver();
        registerReceiver(alarmReceiverDaily, new IntentFilter("com.example.resource_usage_daily"));
        //setAlarm();
*/
    }

    @SuppressLint("NewApi")
    private void initializeAppsTable(UsageStats app) {
        Log.d(TAG, CMUmobileSQLiteHelper.TABLE_APPS_USAGE);
        Log.d(TAG, app.getPackageName());
        Log.d(TAG, CMUmobileSQLiteHelper.COLUMN_APP_NAME);
        if (!dataSource.rowExists(CMUmobileSQLiteHelper.TABLE_APPS_USAGE, app.getPackageName(), CMUmobileSQLiteHelper.COLUMN_APP_NAME)) {
            Log.d(TAG, "INICIALIZOU APPS");
            AppResourceUsage appUsg = new AppResourceUsage(app.getPackageName(), "Category");
            Log.d(TAG, appUsg.toString());
            dataSource.registerNewAppUsage(appUsg, CMUmobileSQLiteHelper.TABLE_APPS_USAGE);
        }
    }

    /**
     * Checks if the 4 types of resource are already in the resource usage table, if not, then adds them.
     */
    private void initializeResourceTable(PhysicalResourceUsage pru) {
        if (!dataSource.rowExists(CMUmobileSQLiteHelper.TABLE_RESOURCE_USAGE, pru.getResourceType().toString(), CMUmobileSQLiteHelper.COLUMN_TYPE_OF_RESOURCE)) {
            Log.d(TAG, "INICIALIZOU RESOURCE_USAGE");
            dataSource.registerNewResourceUsage(pru, CMUmobileSQLiteHelper.TABLE_RESOURCE_USAGE);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public ResourceUsageService getService(){
            return ResourceUsageService.this;
        }
    }

    @Override
    public void onDestroy() {
        alarmManager.cancel(pendingIntentHourly);
        alarmManager.cancel(pendingIntentDaily);
        unregisterReceiver(alarmReceiverHourly);
        unregisterReceiver(alarmReceiverDaily);
        //super.onDestroy();
    }

    /**
     * Sets the alarm to trigger hourly and to trigger daily, each with diferent intents
     */
    public void setAlarm(){
        //To start at the current time.
        Long timeStartHourly = new GregorianCalendar().getTimeInMillis();
        //To start at midnight
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.MILLISECOND, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        //To use after tests: (to start allways at midnight)
        //midnight.add(Calendar.DAY_OF_MONTH, 1);

        Long timeStartDaily = midnight.getTimeInMillis();

        pendingIntentHourly = PendingIntent.getBroadcast(this, 0, new Intent("com.example.resource_usage_hourly"), 0);
        pendingIntentDaily = PendingIntent.getBroadcast(this, 0, new Intent("com.example.resource_usage_daily"), 0);

        // Schedule the alarms!
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //set alarm to start immediately, and repeating for each hour
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeStartHourly, 60000/*AlarmManager.INTERVAL_HOUR*/, pendingIntentHourly);
        //set alarm to start at midnight, and repeating for each day
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
            //Captures the usage
            if(intent.getAction().equals("com.example.resource_usage_hourly")) {
                //get current time
                Calendar currentTime = Calendar.getInstance();
                printCalendar(currentTime);
                Log.d(TAG, "A CADA MINUTO ELE ENTRA AQUI"); //mudar para de hora em hora

                /* Captures the 4 physical resource usage */
                //capturePhysicalUsage(energy);
                //capturePhysicalUsage(cpu);
                //capturePhysicalUsage(memory);
                //capturePhysicalUsage(storage);

                /* Captures the apps usage */
                //captureAppsUsage(context);
            }
            //Saves the usage percentage into the database
            else{

                Calendar day = Calendar.getInstance();
                int newDayOfTheWeek = day.get(Calendar.DAY_OF_WEEK);
                printCalendar(day);
                Log.d(TAG, "A CADA 2 MINUTOS ELE ENTRA AQUI"); //mudar para diariamente à meia noite

                //Saves the 4 physical resource usage into the database
                energy.setDayOfTheWeek(newDayOfTheWeek);
                //Log.d(TAG, energy.getUsagePerHour().toString());
                //addToDataBase(energy);
                cpu.setDayOfTheWeek(newDayOfTheWeek);
                //Log.d(TAG, cpu.getUsagePerHour().toString());
                //addToDataBase(cpu);
                memory.setDayOfTheWeek(newDayOfTheWeek);
                //Log.d(TAG, memory.getUsagePerHour().toString());
                //addToDataBase(memory);
                storage.setDayOfTheWeek(newDayOfTheWeek);
                //Log.d(TAG, storage.getUsagePerHour().toString());
                //addToDataBase(storage);

            }
        }
    }

    /**
     * Given a physical resource usage, this method will capture its usage
     * @param pru the physical resource usage
     */
    public void capturePhysicalUsage(PhysicalResourceUsage pru) {

        //get current time
        Calendar currentTime = Calendar.getInstance();

        //get current hour
        //int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentSecond = currentTime.get(Calendar.SECOND);

        //printCalendar(currentTime);

        switch(pru.getResourceType()){
            case ENERGY:
                int level = BatteryUsage.getEnergyLevel(this);
                //Log.d(TAG, "BATTERY: " + String.valueOf(level) + "%");
                pru.getUsagePerHour().set(currentSecond, level);
                Log.d(TAG, pru.getUsagePerHour().toString());
                break;
            case CPU:
                int cpuUsage = CPUUsage.getCpuUsageStatistic();
                //Log.d(TAG, "CPU: " + String.valueOf(cpuUsage) + "%");
                pru.getUsagePerHour().set(currentSecond, cpuUsage);
                Log.d(TAG, pru.getUsagePerHour().toString());
                break;
            case MEMORY:
                int mem = MemoryUsage.getCurrentRam(this);
                //Log.d(TAG, "MEMORY: " + String.valueOf(mem) + "%");
                pru.getUsagePerHour().set(currentSecond, mem);
                Log.d(TAG, pru.getUsagePerHour().toString());
                break;
            case STORAGE:
                int storageUsg = StorageUsage.getCurrentStorage(this);
                //Log.d(TAG, "STORAGE: " + String.valueOf(storageUsg) + "%");
                pru.getUsagePerHour().set(currentSecond, storageUsg);
                Log.d(TAG, pru.getUsagePerHour().toString());
                break;
            default:
                Log.d(TAG, "THAT RESOURCE ISN'T RECOGNIZED.");
                break;
        }
    }

    /**
     * captures the usage of the five more used apps on the device
     * @param context the context
     */
    @SuppressLint("NewApi")
    private static void captureAppsUsage(Context context) {

        List<UsageStats> stats = getDeviceAppsList(context);

        //list of the five more used usageStats in the current time
        ArrayList<UsageStats> mostUsed = getMostUsedApps(stats);

        //list of the top five appResourceUsages
        ArrayList<AppResourceUsage> topFiveApps = new ArrayList<AppResourceUsage>(4);

        int id = 0;
        for (UsageStats stat : mostUsed){
            //Log.d(TAG, id+1 + stat.getTotalTimeInForeground()/1000/60 + " minutes of usage:");
            id++;

            AppResourceUsage app = new AppResourceUsage(stat.getPackageName(), "category(To-Do)");
            //Log.d(TAG, app.toString());
            topFiveApps.add(app);
        }

        //Log.d(TAG, topFiveApps.toString());

    }

    /**
     * Updates the given physical resource usage in the resource usage table in the database to
     * @param pru the physical resource usage to update
     */
    public void addToDataBase(PhysicalResourceUsage pru) {

        dataSource.updateResourceUsage(pru, CMUmobileSQLiteHelper.TABLE_RESOURCE_USAGE);

        backupDB();
    }

    /**
     * The next 2 methods are just something we're trying out
     * to get the category of an app, they're not used yet
     *
     * Get the category of the app with the package name given
     * @param packageName the package name of the app
     * @return the category of the app
     */
    private String getCategory(String packageName) {

        String GOOGLE_URL = "https://play.google.com/store/apps/details?id=";
        String ERROR = "Failed to get app category";

        packageName = GOOGLE_URL + packageName;

        boolean network = isNetworkAvailable();
        if (!network) {
            //manage connectivity lost
            return ERROR;
        } else {
            try {
                Document doc = Jsoup.connect(packageName).get();
                Element link = doc.select("span[itemprop=genre]").first();
                return link.text();
            } catch (Exception e) {
                return ERROR;
            }
        }
    }

    // Check all connectivities whether available or not
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Checks for the 5 more used apps in the given list of apps, in the current time
     * @param stats list of UsageStats
     * @return mostUsed ArrayList with 5 elements that are the most used apps from the given list
     */
    @SuppressLint("NewApi")
    public static ArrayList<UsageStats> getMostUsedApps(List<UsageStats> stats){
        ArrayList<UsageStats> mostUsed = new ArrayList<>(4); //gets the 5 more used from the given list
        Map<UsageStats, Long> usageAppsMap = new HashMap<UsageStats, Long>();

        for (UsageStats stat: stats) {
            long totalTimeInForeground = stat.getTotalTimeInForeground()/1000/60; //time in minutes
            usageAppsMap.put(stat, totalTimeInForeground);
        }

        Map<UsageStats, Long> sortedMap = sortByValue(usageAppsMap);

        int id = 0;
        for (UsageStats usageStat: sortedMap.keySet()){

            if(id < 5){
                mostUsed.add(usageStat);
            }
            String key =usageStat.getPackageName();
            String value = usageAppsMap.get(usageStat).toString();
            //Log.d(TAG, id + " - " + key + " " + value);
            id++;
        }
        return mostUsed;
    }

    @SuppressLint("NewApi")
    public static List<UsageStats> getDeviceAppsList(Context context){

        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        //begin date
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        //printCalendar(startDate);
        long start = startDate.getTimeInMillis();

        //end date
        Calendar endDate = Calendar.getInstance();
        //printCalendar(endDate);
        long end = endDate.getTimeInMillis();

        return usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
    }

    /**
     * Since the database on the device is only visible through root, to check the
     * database tables this method creates a backup in a place that the database is visible.
     * (main folder of the android device)
     */
    private void backupDB() {

        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String DB_PATH = this.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator;
                String currentDBPath = "cmumobile.db";
                String backupDBPath = "cmumobilebackup.db";
                File currentDB = new File(DB_PATH, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    //Log.d(TAG, "Backup Done");
                    src.close();
                    dst.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs the calender in a specific format
     * @param calendar the calendar to log
     */
    public static void printCalendar(Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d'/'MM'/'yyyy 'at' h:mm:s a");
        String currentDate = format.format(calendar.getTime());
        Log.d(TAG, currentDate);
    }

    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    /**
     * Sorts the given map by its values in a descendent way
     * @param map map to sort
     * @param <K> data type of the keys
     * @param <V> data type of the values
     * @return result map with the entrys sorted by its values
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}