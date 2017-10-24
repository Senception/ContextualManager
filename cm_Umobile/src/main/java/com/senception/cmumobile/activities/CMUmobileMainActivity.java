/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains CMUmobileMainActivity. This class is the Main Activity class for the
 * android application
 *
 */

package com.senception.cmumobile.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.senception.cmumobile.fragments.LocationDialogFragment;
import com.senception.cmumobile.fragments.UsageStatsDialogFragment;
import com.senception.cmumobile.permissions.Permissions;
import com.senception.cmumobile.R;
import com.senception.cmumobile.interfaces.CMUmobileDataBaseChangeListener;
import com.senception.cmumobile.modals.CMUmobileAP;
import com.senception.cmumobile.resource_usage.ResourceUsageHandler;
import com.senception.cmumobile.services.CMUmobileService;

@SuppressLint("Recycle")
public class CMUmobileMainActivity extends Activity {

	private static final String TAG ="MAIN ACTIVITY--->";
	static final int PERMISSION_REQUEST = 1;  // The request code
	int backButtonCount = 0;
	private CMUmobileService mBoundService;
	private boolean mIsServiceBound = false;
	final Context context = this;

	/**
	 * The Connection to the PerSense Light service. 
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBoundService = ((CMUmobileService.LocalBinder)service).getService();
			mIsServiceBound = true;

			mBoundService.setOnStateChangeListener(
					new CMUmobileDataBaseChangeListener() {
						public void onDataChange(List<CMUmobileAP> apEntries) {

						}
						public void onStatusMessageChange(String newMessage)  {

						}
					}
			);
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService.clearOnStateChangeListeners ();
			mBoundService = null;
			mIsServiceBound = false;
		}
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cmumobile_ma_layout);

		FragmentManager manager = getFragmentManager();

		//Log.d(TAG, "MAIN ACTIVITY");
		//Asks user for permission to get usage stats

		if(!Permissions.usageStatsPermission(getApplicationContext())){
			UsageStatsDialogFragment usageDialog = UsageStatsDialogFragment.newInstance(getString(R.string.usage_stats), getString(R.string.usage_stats_msg));
			usageDialog.show(manager, "Dialog");
		}

		if(!Permissions.isLocationEnabled(getApplicationContext())){
			LocationDialogFragment locationDialog = LocationDialogFragment.newInstance(getString(R.string.location), getString(R.string.location_msg));
			locationDialog.show(manager, "Dialog");
		}

		Log.d(TAG, "ENTROU NO ONCREATE");

		startService(new Intent (CMUmobileMainActivity.this, CMUmobileService.class));
		doBindService();

		setHourlyAlarm();

	}

	public void setHourlyAlarm(){

		//To start the alarm 5min after the current time.
		Long timeStart = new GregorianCalendar().getTimeInMillis();
		//Toast.makeText(this, "Alarm Scheduled for 2 seconds", Toast.LENGTH_LONG).show();

		//Set the class that will execute when alarm triggers
		Intent intentAlarm = new Intent(this, ResourceUsageHandler.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

		//Create the object
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		//Set the alarm to trigger hourly
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeStart, 60*1000 /**AlarmManager.INTERVAL_HOUR*/, pendingIntent);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mIsServiceBound && mConnection == null){
			doBindService();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cmumobile_secondary_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
			case R.id.stopservice:
				mBoundService.stopForeGround();
				doUnbindService();
				stopService(new Intent(CMUmobileMainActivity.this, CMUmobileService.class));
				this.finish();

				return true;
			case R.id.sendreport:

				File report_dir = new File(Environment.getExternalStorageDirectory(), "PerSense_mobile_light_Report");

				if (!report_dir.exists()) {
					report_dir.mkdirs();
				}
				if(mBoundService.haveNetworkConnection()){
					new Send_report().execute();
				}else{
					Toast.makeText(getApplicationContext(), getString(R.string.nointernet),Toast.LENGTH_LONG).show();
				}
				//send_report(get_report());

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	/**
	 * Bind the service in case it is not already binded.
	 */
	void doBindService() {
		if(!mIsServiceBound){
			bindService(new Intent (CMUmobileMainActivity.this, CMUmobileService.class), mConnection, Context.BIND_AUTO_CREATE);
		}
	}
	/**
	 * Un-bind the service in case it is binded.
	 */
	void doUnbindService() {
		if (mIsServiceBound && mConnection != null) {
			unbindService(mConnection);
		}
	}
	@Override
	public void onBackPressed(){
		if(backButtonCount >= 1)
		{
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			this.finish();
		}
		else
		{
			Toast.makeText(this, getString(R.string.mainback), Toast.LENGTH_SHORT).show();
			backButtonCount++;
		}
	}

	public String zipFolder(String inputFolderPath) {
		String output = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PerSense_mobile_light_Report/"+"pml_report-"+mBoundService.fileDate()+".zip";

		try {
			FileOutputStream fos = new FileOutputStream(output);
			ZipOutputStream zos = new ZipOutputStream(fos);
			File srcFile = new File(inputFolderPath);
			File[] files = srcFile.listFiles();
			Log.d("", "Zip directory: " + srcFile.getName());
			for (int i = 0; i < files.length; i++) {
				Log.d("", "Adding file: " + files[i].getName());
				byte[] buffer = new byte[1024];
				FileInputStream fis = new FileInputStream(files[i]);
				zos.putNextEntry(new ZipEntry(files[i].getName()));
				int length;
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
		} catch (IOException ioe) {
			Log.e("", ioe.getMessage());
		}
		return output;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public class Send_report extends AsyncTask<String, Void, String> {

		protected String doInBackground(final String... args){
			String input = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PerSense_mobile_light";
			String output = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PerSense_mobile_light_Report/"+"pml_report-"+mBoundService.fileDate()+".zip";

			try {
				FileOutputStream fos = new FileOutputStream(output);
				ZipOutputStream zos = new ZipOutputStream(fos);
				File srcFile = new File(input);
				File[] files = srcFile.listFiles();
				Log.d("", "Zip directory: " + srcFile.getName());
				for (int i = 0; i < files.length; i++) {
					Log.d("", "Adding file: " + files[i].getName());
					byte[] buffer = new byte[1024];
					FileInputStream fis = new FileInputStream(files[i]);
					zos.putNextEntry(new ZipEntry(files[i].getName()));
					int length;
					while ((length = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
					zos.closeEntry();
					fis.close();
				}
				zos.close();
			} catch (IOException ioe) {
				Log.e("", ioe.getMessage());
			}

			return output;
		}
		protected void onPostExecute(final String success){
				File root = new File(success);
				try{
					Intent emailIntent = new Intent(Intent.ACTION_SEND);
					emailIntent.setType("text/plain");
					emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {""});
					emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hi, Here is your PML Report!");
					emailIntent.putExtra(Intent.EXTRA_TEXT, "In Attach is the PerSense Mobile Light Report for the: "+mBoundService.fileDate());
					Uri uri = Uri.fromFile(root);
					emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
					startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));

				}catch(Exception e) {
					Log.d(TAG, getString(R.string.emailnotsent) + e);
				}
		}

	}
}