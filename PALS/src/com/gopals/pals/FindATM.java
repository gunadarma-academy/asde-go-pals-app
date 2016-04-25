package com.gopals.pals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FindATM extends Activity implements
		LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {
	
	private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 3;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    
    private ProgressDialog pDialog;
    private static final String GET_ATM = 
			"http://gopals.esy.es/get_atm.php";
    public static final String TAG_SUCCESS = "success";
	public static final String TAG_MESSAGE = "message";
	public static final String TAG_ATM = "atm";
	public static final String TAG_ID = "id_atm";
    public static final String TAG_NAME = "atm_name";
    public static final String TAG_ADDRESS = "atm_address";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    
    private JSONArray atmJSON = null;
    private ArrayList<String> atmNameList, atmAddressList, atmLatList, 
    				atmLongList; 
    private ArrayList<Double> atmRadiusList;
    private ArrayList<Location> locationList;
    String bankName, radiusStr;
    Location currentLocation;
    
	LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    
    String[] radiusAmount=null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_atm);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a1a1a")));
		bar.setTitle("");
		
		Typeface bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		
		if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addApi(LocationServices.API)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .build();
        
        String[] bankNameSpr=null;
        String[] radiusSpr=null;
        
        bankNameSpr = getResources().getStringArray(R.array.bank_name);
        radiusSpr = getResources().getStringArray(R.array.radius_display);
        radiusAmount = getResources().getStringArray(R.array.radius);
        MySpinnerAdapter adapterBank = new MySpinnerAdapter(
        		this, android.R.layout.simple_spinner_dropdown_item, bankNameSpr);
        MySpinnerAdapter adapterRadius = new MySpinnerAdapter(
        		this, android.R.layout.simple_spinner_dropdown_item, radiusSpr);
        
        final Spinner SpinnerBank = (Spinner) findViewById(R.id.spr_bank);
        final Spinner SpinnerRadius = (Spinner) findViewById(R.id.spr_radius);
        SpinnerBank.setAdapter(adapterBank);
        SpinnerRadius.setAdapter(adapterRadius);
        
        TextView atmLbl = (TextView)findViewById(R.id.atmLbl);
        Button findButton = (Button)findViewById(R.id.atmFindButton);
        atmLbl.setTypeface(bariol, Typeface.BOLD);
        findButton.setTypeface(bariol);
        
        findButton.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
				bankName = (String) SpinnerBank.getSelectedItem();
				radiusStr = radiusAmount[SpinnerRadius.getSelectedItemPosition()];
				Network ic = new Network();
				if (ic.isNetworkConnected(getApplicationContext())) {
					if(bankName.equals("Select Bank") || radiusStr.equals("Select Radius")){
						Toast.makeText(FindATM.this, "Please Select Bank and Radius", 
								Toast.LENGTH_SHORT).show();
					} else {
						if (currentLocation!=null){
							new GetATM().execute();
						}else{
							Toast.makeText(getApplicationContext(), "Cannot Detect Your Location, " +
									"Please Wait and Try Again", Toast.LENGTH_SHORT).show();
						}
					}
				} else 	Toast.makeText(getApplicationContext(), "No Internet Connection", 
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }
	
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }
	
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }
    
    @Override
	public void onConnectionFailed(ConnectionResult connectionResult) {}

	@Override
	public void onConnected(Bundle bundle) {
		startLocationUpdates();
		currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
	}

	@Override
	public void onConnectionSuspended(int i) {}

	@Override
	public void onLocationChanged(Location location) {}
	
	private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
	
	protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
	
	protected void startLocationUpdates() {
	    LocationServices.FusedLocationApi.requestLocationUpdates(
	            mGoogleApiClient, mLocationRequest, this);
	}
	
	public class GetATM extends AsyncTask<Void, Void, Boolean> {
		String error;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FindATM.this);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			atmNameList = new ArrayList<String>();
			atmAddressList = new ArrayList<String>();
			atmLatList = new ArrayList<String>();
			atmLongList = new ArrayList<String>();
			locationList = new ArrayList<Location>();
			atmRadiusList = new ArrayList<Double>();
			Double rad;
			HashMap<String, String> params = new HashMap<String, String>();
            params.put("bank_name", bankName);
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.makeHttpRequest(GET_ATM, params);
			if(json!=null)
				Log.d("All Data: ", json.toString());
			try {
				CalculateRadius cr = new CalculateRadius();
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					atmJSON = json.getJSONArray(TAG_ATM);
					if(radiusStr.equals("Display All")){
						for (int i = 0; i < atmJSON.length(); i++) {
							JSONObject c = atmJSON.getJSONObject(i);
							String spbuName = c.getString(TAG_NAME);
							String spbuAddress = c.getString(TAG_ADDRESS);
							Double spbuLatitude = c.getDouble(TAG_LATITUDE);
							Double spbuLongitude = c.getDouble(TAG_LONGITUDE);
							Location spbuLocation = new Location("spbu location");
							spbuLocation.setLatitude(spbuLatitude);
							spbuLocation.setLongitude(spbuLongitude);
							rad = cr.calculateRadius(currentLocation, spbuLocation);
							
						    atmNameList.add(spbuName);
						    atmAddressList.add(spbuAddress);
						    atmLatList.add(String.valueOf(spbuLatitude));
						    atmLongList.add(String.valueOf(spbuLongitude));   
						    locationList.add(spbuLocation); 
						    atmRadiusList.add(rad);
						}
					} else {
						double radius = Double.valueOf(radiusStr);
						for (int i = 0; i < atmJSON.length(); i++) {
							JSONObject c = atmJSON.getJSONObject(i);
							String atmName = c.getString(TAG_NAME);
							String atmAddress = c.getString(TAG_ADDRESS);
							Double atmLatitude = c.getDouble(TAG_LATITUDE);
							Double atmLongitude = c.getDouble(TAG_LONGITUDE);
							Location atmLocation = new Location("spbu location");
							atmLocation.setLatitude(atmLatitude);
							atmLocation.setLongitude(atmLongitude);
							rad = cr.calculateRadius(currentLocation, atmLocation);
							
							if(rad<radius){
								atmNameList.add(atmName);
							    atmAddressList.add(atmAddress);
							    atmLatList.add(String.valueOf(atmLatitude));
							    atmLongList.add(String.valueOf(atmLongitude));   
							    locationList.add(atmLocation); 
							    atmRadiusList.add(rad);
							}
						}
					}
				} 
				return true;
			} catch (JSONException e) {
				 //e.printStackTrace();
				error = "Slow Internet Connection";
				return false;
			} catch(RuntimeException e){
				//e.printStackTrace();
				error = "Slow Internet Connection";
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if(result){
				String[] arrayName = new String[atmNameList.size()];
				String[] arrayAddress = new String[atmAddressList.size()];
				String[] arrayLat = new String[atmLatList.size()];
				String[] arrayLong = new String[atmLongList.size()];
				Double[] arrayRadius = new Double[atmRadiusList.size()];
							
				arrayName = atmNameList.toArray(arrayName);
				arrayAddress = atmAddressList.toArray(arrayAddress);
				arrayLat = atmLatList.toArray(arrayLat);
				arrayLong = atmLongList.toArray(arrayLong);
				arrayRadius = atmRadiusList.toArray(arrayRadius);
				String[] sortedName = new String[arrayName.length];
				String[] sortedAddress = new String[arrayAddress.length];
				String[] sortedLat = new String[arrayLat.length];
				String[] sortedLong = new String[arrayLong.length];
				String[] sortedRadius = new String[arrayLong.length];
				Double[] doubleRadius = Arrays.copyOf(arrayRadius, arrayRadius.length);
				int[] idx = new int[doubleRadius.length];
				int index=0;
				if(doubleRadius.length>0){
					Arrays.sort(doubleRadius);
					for(int i=0; i<doubleRadius.length; i++){
						for(int j=0; j<arrayRadius.length; j++){
							if(arrayRadius[j]==doubleRadius[i]){
								idx[index] = j;
								index++;
							}
						}
					}
					for(int i=0; i<idx.length; i++){
						sortedName[i] = arrayName[idx[i]];
						sortedAddress[i] = arrayAddress[idx[i]];
						sortedLat[i] = arrayLat[idx[i]];
						sortedLong[i] = arrayLong[idx[i]];
						sortedRadius[i] = doubleRadius[i].toString();
					}
				}
				
				pDialog.dismiss();
				
				Intent listResult = new Intent(FindATM.this, ListResultActivity.class);
				if(arrayName.length>0){
					listResult.putExtra("category", "atm");
					listResult.putExtra("atm_name", sortedName);
					listResult.putExtra("atm_address", sortedAddress);
					listResult.putExtra("bank_name", bankName);
					listResult.putExtra("atm_lat", sortedLat);
					listResult.putExtra("atm_long", sortedLong);
					listResult.putExtra("atm_radius", sortedRadius);
				}
				startActivity(listResult);
			} else {
				pDialog.dismiss();
				Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	
}
