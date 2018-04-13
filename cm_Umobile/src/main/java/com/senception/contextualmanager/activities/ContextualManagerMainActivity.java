package com.senception.contextualmanager.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.senception.contextualmanager.fragments.ContextualManagerLocationDialogF;
import com.senception.contextualmanager.fragments.ContextualManagerUsageStatsDialogF;
import com.senception.contextualmanager.modals.ContextualManagerAP;
import com.senception.contextualmanager.permissions.ContextualManagerPermissions;
import com.senception.contextualmanager.R;
import com.senception.contextualmanager.interfaces.ContextualManagerDataBaseChangeListener;
import com.senception.contextualmanager.services.ContextualManagerCaptureService;
import com.senception.contextualmanager.services.ContextualManagerService;
import com.senception.contextualmanager.services.ContextualManagerInterfaceService;

/**
 * Copyright (C) 2016 Senception Lda
 * Update to Contextual Manager 2017
 * @author Igor dos Santos - degomosIgor@sen-ception.com
 * @author José Soares - jose.soares@senception.com
 * @version 0.1
 *
 * @file Contains ContextualManagerMainActivity.
 * This class is the Main Activity class for the
 * android application
 */
public class ContextualManagerMainActivity extends Activity {

	private static final String TAG = ContextualManagerMainActivity.class.getSimpleName();
	int backButtonCount = 0;
	private ContextualManagerService reportBoundService;
	private ContextualManagerCaptureService resUsgBoundService;
	private boolean resUsgServIsBound = false;
	private boolean mIsServiceBound = false;

	/**
	 * The Connection to the PerSense Light service. 
	 */
	private ServiceConnection reportConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			reportBoundService = ((ContextualManagerService.LocalBinder)service).getService();
			mIsServiceBound = true;

			reportBoundService.setOnStateChangeListener(
					new ContextualManagerDataBaseChangeListener() {
						public void onDataChange(List<ContextualManagerAP> apEntries) {

						}
						public void onStatusMessageChange(String newMessage)  {

						}
					}
			);
		}

		public void onServiceDisconnected(ComponentName className) {
			reportBoundService.clearOnStateChangeListeners ();
			reportBoundService = null;
			mIsServiceBound = false;
		}
	};

	/**
	 * The Connection to the Contextual Manager service.
	 */
	private ServiceConnection resourceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			resUsgBoundService = ((ContextualManagerCaptureService.LocalBinder)service).getService();
			resUsgServIsBound = true;
		}

		public void onServiceDisconnected(ComponentName className) {
			resUsgBoundService = null;
			resUsgServIsBound = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cmumobile_ma_layout);

        FragmentManager manager = getFragmentManager();

		//Asks user for permission to get usage stats
		if(!ContextualManagerPermissions.usageStatsPermission(getApplicationContext())) {
			ContextualManagerUsageStatsDialogF usageDialog = ContextualManagerUsageStatsDialogF.newInstance(getString(R.string.usage_stats), getString(R.string.usage_stats_msg));
			usageDialog.show(manager, "Dialog");
		}

		startService(new Intent(ContextualManagerMainActivity.this, ContextualManagerCaptureService.class));
		doBindResourceService();

		Log.d(TAG, "ContextualManagerCaptureService started.");

		//Asks user for permission to get device location
		if(!ContextualManagerPermissions.isLocationEnabled(getApplicationContext())){
			ContextualManagerLocationDialogF locationDialog = ContextualManagerLocationDialogF.newInstance(getString(R.string.location), getString(R.string.location_msg));
			locationDialog.show(manager, "Dialog");
		}

		startService(new Intent (ContextualManagerMainActivity.this, ContextualManagerService.class));
		doBindReportService();

		Log.d(TAG, "ContextualManagerService started.");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mIsServiceBound && reportConnection == null){
			doBindReportService();
		}

		if(resUsgServIsBound && resourceConnection == null){
			doBindResourceService();
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
        reportBoundService.stopForeGround();
        doUnbindReportService();
        stopService(new Intent(ContextualManagerMainActivity.this, ContextualManagerService.class));

        resUsgBoundService.stopForeGround();
        doUnbindResourceService();
        stopService(new Intent(ContextualManagerMainActivity.this, ContextualManagerCaptureService.class));

        this.finish();

        return true;
	}
	/**
	 * Bind the report service in case it is not already binded.
	 */
	void doBindReportService() {
		if(!mIsServiceBound){
			bindService(new Intent (ContextualManagerMainActivity.this, ContextualManagerService.class), reportConnection, Context.BIND_AUTO_CREATE);
		}
	}

	/**
	 * Bind the resource service in case it is not already binded.
	 */
	void doBindResourceService() {
		if(!resUsgServIsBound){
			bindService(new Intent (ContextualManagerMainActivity.this, ContextualManagerCaptureService.class), resourceConnection, Context.BIND_AUTO_CREATE);
		}
	}

	/**
	 * Un-bind the report service in case it is binded.
	 */
	void doUnbindReportService() {
		if (mIsServiceBound && reportConnection != null) {
			unbindService(reportConnection);
		}
	}

	/**
	 * Un-bind the resource service in case it is binded.
	 */
	void doUnbindResourceService() {
		if (resUsgServIsBound && resourceConnection != null) {
			unbindService(resourceConnection);
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

	@Override
	protected void onDestroy() {
        backupDB(this); //todo eliminate the backup function
		super.onDestroy();
	}

    /**
     * Since the database on the device is only visible through root, to check the
     * database tables this method creates a backup in a place that the database is visible.
     * (main folder of the android device)
     */
    public static void backupDB(Context context) {

        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String DB_PATH = context.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator;
                String currentDBPath = "contextualmanager.db";
                String backupDBPath = "contextualmanagerbackup.db";
                File currentDB = new File(DB_PATH, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}