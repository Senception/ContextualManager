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
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.senception.cmumobile.databases.CMUmobileDataSource;
import com.senception.cmumobile.databases.CMUmobileSQLiteHelper;
import com.senception.cmumobile.resource_usage.physical.BatteryUsage;
import com.senception.cmumobile.resource_usage.physical.CPUUsage;
import com.senception.cmumobile.resource_usage.physical.MemoryUsage;
import com.senception.cmumobile.resource_usage.physical.PhysicalResourceType;
import com.senception.cmumobile.resource_usage.physical.PhysicalResourceUsage;
import com.senception.cmumobile.resource_usage.physical.StorageUsage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
    private static PhysicalResourceUsage memory;
    private static PhysicalResourceUsage storage;
    private final IBinder mBinder = new LocalBinder();
    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntentHourly;
    private static PendingIntent pendingIntentDaily;
    private final String TABLE_RESOURCE_USAGE = CMUmobileSQLiteHelper.TABLE_RESOURCE_USAGE;
    AlarmReceiver alarmReceiverHourly;
    AlarmReceiver alarmReceiverDaily;
    CMUmobileDataSource dataSource;

    @Override
    public void onCreate() {
        super.onCreate();

        dataSource = new CMUmobileDataSource(this);
        dataSource.openDB(true);

        energy = new PhysicalResourceUsage(PhysicalResourceType.ENERGY);
        initializeResourceTable(energy);
        cpu = new PhysicalResourceUsage(PhysicalResourceType.CPU);
        initializeResourceTable(cpu);
        memory = new PhysicalResourceUsage(PhysicalResourceType.MEMORY);
        initializeResourceTable(memory);
        storage = new PhysicalResourceUsage(PhysicalResourceType.STORAGE);
        initializeResourceTable(storage);

        //Schedules the alarm to trigger every hour (from this exact moment)
        alarmReceiverHourly = new AlarmReceiver();
        registerReceiver(alarmReceiverHourly, new IntentFilter("com.example.resource_usage_hourly"));
        //Schedules the alarm to trigger every day (from midnight on)
        alarmReceiverDaily = new AlarmReceiver();
        registerReceiver(alarmReceiverDaily, new IntentFilter("com.example.resource_usage_daily"));
        setAlarm();

    }

    /**
     * Checks if the 4 types of resource are already in the resource usage table, if not, then adds them.
     */
    private void initializeResourceTable(PhysicalResourceUsage pru) {
        if (!dataSource.rowExists(TABLE_RESOURCE_USAGE, pru.getResourceType().toString())) {
            dataSource.registerNewResourceUsage(pru, TABLE_RESOURCE_USAGE);
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

    /*Alarm BroadcastReceiver*/
    public class AlarmReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.example.resource_usage_hourly")) {
                Log.d(TAG, "A CADA minuto ELE ENTRA AQUI"); //mudar para de hora em hora
                CaptureUsage(energy);
                CaptureUsage(cpu);
                CaptureUsage(memory);
                CaptureUsage(storage);
            }
            else{
                Log.d(TAG, "A CADA 2 minutos ELE ENTRA AQUI"); //mudar para diariamente à meia noite
                Calendar day = Calendar.getInstance();
                int newDayOfTheWeek = day.get(Calendar.DAY_OF_WEEK);
                energy.setDayOfTheWeek(newDayOfTheWeek);
                addToDataBase(energy);
                cpu.setDayOfTheWeek(newDayOfTheWeek);
                addToDataBase(cpu);
                memory.setDayOfTheWeek(newDayOfTheWeek);
                addToDataBase(memory);
                storage.setDayOfTheWeek(newDayOfTheWeek);
                addToDataBase(storage);
            }
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
                int level = BatteryUsage.getEnergyLevel(this);
                //Log.d(TAG, "BATTERY: " + String.valueOf(level) + "%");
                pru.getUsagePerHour().set(currentSecond, level);
                //Log.d(TAG, pru.getUsagePerHour().toString());
                break;
            case CPU:
                int cpuUsage = CPUUsage.getCpuUsageStatistic();
                //Log.d(TAG, "CPU: " + String.valueOf(cpuUsage) + "%");
                pru.getUsagePerHour().set(currentSecond, cpuUsage);
                //Log.d(TAG, pru.getUsagePerHour().toString());
                break;
            case MEMORY:
                int mem = MemoryUsage.getCurrentRam(this);
                //Log.d(TAG, "MEMORY: " + String.valueOf(mem) + "%");
                pru.getUsagePerHour().set(currentSecond, mem);
                //Log.d(TAG, pru.getUsagePerHour().toString());
                break;
            case STORAGE:
                int storageUsg = StorageUsage.getCurrentStorage(this);
                //Log.d(TAG, "STORAGE: " + String.valueOf(storageUsg) + "%");
                pru.getUsagePerHour().set(currentSecond, storageUsg);
                //Log.d(TAG, pru.getUsagePerHour().toString());
                break;
            default:
                Log.d(TAG, "THAT RESOURCE ISN'T RECOGNIZED.");
                break;
        }
    }

    public void addToDataBase(PhysicalResourceUsage pru) {
        //get current time
        Calendar currentTime = Calendar.getInstance();
        printCalendar(currentTime);

        Log.d(TAG, pru.getUsagePerHour().toString());

        dataSource.updateResourceUsage(pru, TABLE_RESOURCE_USAGE);

        backupDB();
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

    public static void printCalendar(Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d'/'MM'/'yyyy 'at' h:mm:s a");
        String currentDate = format.format(calendar.getTime());
        Log.d(TAG, currentDate);
    }

}
