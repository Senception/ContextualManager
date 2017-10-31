package com.senception.cmumobile.resource_usage;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.senception.cmumobile.resource_usage.physical.BatteryUsage;
import com.senception.cmumobile.resource_usage.physical.CPUUsage;
import com.senception.cmumobile.resource_usage.physical.MemoryUsage;
import com.senception.cmumobile.resource_usage.physical.PhysicalResourceType;
import com.senception.cmumobile.resource_usage.physical.PhysicalResourceUsage;
import com.senception.cmumobile.resource_usage.physical.StorageUsage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
    private static PhysicalResourceUsage storage;
    private static PhysicalResourceUsage memory;
    private final IBinder mBinder = new LocalBinder();
    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntent;
    AlarmReceiver alarmReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        energy = new PhysicalResourceUsage(PhysicalResourceType.ENERGY);
        cpu = new PhysicalResourceUsage(PhysicalResourceType.CPU);
        memory = new PhysicalResourceUsage(PhysicalResourceType.MEMORY);
        storage = new PhysicalResourceUsage(PhysicalResourceType.STORAGE);

        alarmReceiver = new AlarmReceiver();
        registerReceiver(alarmReceiver, new IntentFilter("com.example.resource_usage"));
        setHourlyAlarm();
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
        alarmManager.cancel(pendingIntent);
        unregisterReceiver(alarmReceiver);
        //super.onDestroy();
    }

    public void setHourlyAlarm(){
        //To start at the current time.
        Long timeStart = new GregorianCalendar().getTimeInMillis();
        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent("com.example.resource_usage"), 0);
        // Schedule the alarm!
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeStart, 60000/*AlarmManager.INTERVAL_HOUR*/, pendingIntent);
    }

    /*Alarm BroadcastReceiver*/
    public class AlarmReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            CaptureUsage(energy);
            CaptureUsage(cpu);
            CaptureUsage(memory);
            CaptureUsage(storage);
        }
    }

    public void CaptureUsage(PhysicalResourceUsage pru) {

        //get current time
        Calendar currentTime = Calendar.getInstance();

        //get current hour
        //int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentSecond = currentTime.get(Calendar.SECOND);

        printCalendar(currentTime);

        switch(pru.getResourceType()){
            case ENERGY:
                Log.d(TAG, "BATTERY: ");
                int level = BatteryUsage.getEnergyLevel(this);
                energy.getUsagePerHour().set(currentSecond, level);
                Log.d(TAG, pru.getUsagePerHour().toString());
                break;
            case CPU:
                Log.d(TAG, "CPU: ");
                int cpuUsage = CPUUsage.getCpuUsageStatistic();
                //Log.d(TAG, String.valueOf(cpuUsage));
                pru.getUsagePerHour().set(currentSecond, cpuUsage);
                Log.d(TAG, pru.getUsagePerHour().toString());
                break;
            case MEMORY:
                Log.d(TAG, "MEMORY: ");
                int mem = MemoryUsage.getCurrentRam(this);
                //Log.d(TAG, String.valueOf(mem));
                pru.getUsagePerHour().set(currentSecond, mem);
                Log.d(TAG, pru.getUsagePerHour().toString());
                break;
            case STORAGE:
                Log.d(TAG, "STORAGE: ");
                int storageUsg = StorageUsage.getCurrentStorage(this);
                //Log.d(TAG, String.valueOf(storageUsg) + "%");
                pru.getUsagePerHour().set(currentSecond, storageUsg);
                Log.d(TAG, pru.getUsagePerHour().toString());
                break;
            default:
                Log.d(TAG, "THAT RESOURCE ISN'T RECOGNIZED.");
                break;
        }
    }

    public void addToDataBase(PhysicalResourceUsage pru) {
        switch(pru.getResourceType()){
            case ENERGY:
                break;
            case STORAGE:
                break;
            case CPU:
                break;
            case MEMORY:
                break;
            default:
                break;
        }
    }


    @SuppressLint("NewApi")
    private static void checkAppsUsage(Context context) {

        //Map<String, UsageStats> statsMap = usm.queryAndAggregateUsageStats(start, end);

        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        //begin date
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -8);
        printCalendar(startDate);
        long start = startDate.getTimeInMillis();

        //end date
        Calendar endDate = Calendar.getInstance();
        printCalendar(endDate);
        long end = endDate.getTimeInMillis();

        List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);


        //so far it only prints the apps that ran on beetween the given date
        for (UsageStats stat: stats) {
            if(!usm.isAppInactive(stat.getPackageName())){
                DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date netDate = (new Date(stat.getFirstTimeStamp()));
                //Log.d(TAG, "" + sdf.format(netDate));
            }
        }
    }

    public static void printCalendar(Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d'/'MM'/'yyyy 'at' h:mm:s a");
        String currentDate = format.format(calendar.getTime());
        Log.d(TAG, currentDate);
    }

}
