/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains PerSenseFusedLocation. This class provides access to google fused location api to obtain coordinates.
 * 
 */

package com.senception.contextualmanager.pipelines;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.senception.contextualmanager.services.CMUmobileService;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
//import android.util.Log;

public class CMUmobileFusedLocation implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	//private static final String TAG = "FUSED LOCATION --->";
	private static final long INTERVAL = 1000 * 10;
	private static final long FASTEST_INTERVAL = 500 * 10;
	LocationRequest mLocationRequest;
	GoogleApiClient mGoogleApiClient;
	public Location mCurrentLocation;
	private final Context mContext;
	CMUmobileService service;

	public CMUmobileFusedLocation(Context context){
		this.mContext = context;
		service = new CMUmobileService();

		if(!isGooglePlayServicesAvailable()){
			//finish();
		}
		createLocationRequest();
		if(mGoogleApiClient == null){
			mGoogleApiClient = new GoogleApiClient.Builder(context)
			.addApi(LocationServices.API)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.build();	
		}
		mGoogleApiClient.connect();
	}
	protected void createLocationRequest(){
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		//THE NORMAL STATE FOR PML USAGE
		//mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

		//FOR MAURO RESEARCH
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	@SuppressWarnings("deprecation")
	private boolean isGooglePlayServicesAvailable(){
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
		if(ConnectionResult.SUCCESS == status){
			return true;
		}else{
			//GooglePlayServicesUtil.getErrorDialog(status, mContext, 0).show();
			return false;
		}
	}

	protected void startLocationUpdates(){
		if (mGoogleApiClient.isConnected()) {
	           LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,  mLocationRequest, this);	
		}
	}
	public void stopLocationUpdates(){
		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		//Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
		if (!result.hasResolution()) {
			// show the localized error dialog.
			return;
		}
	}
	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		startLocationUpdates();
	}
	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(location != null){
			mCurrentLocation = location;
			
			String recvBssid = CMUmobileService.getFusedBssid();
			String recvSsid = CMUmobileService.getFusedSsid();
			int recvNoCoor = CMUmobileService.getNoCoordinate();
			if(recvNoCoor == 1 && recvBssid != null && recvSsid != null){
				double latitude = mCurrentLocation.getLatitude();
				double longitude = mCurrentLocation.getLongitude();
				service.actulizaCoordenadas(recvBssid,recvSsid, latitude, longitude);
			}
			//Log.d(TAG, "NO COORDINATE YET!!!SCANNING FUSED LOCATION...");
		}
	}
}
