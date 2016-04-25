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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FindRepairShop extends Activity implements
		LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		AdapterView.OnItemSelectedListener {
	private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 3;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    
    private ProgressDialog pDialog;
    private static final String GET_BENGKEL = 
			"http://gopals.esy.es/get_bengkel.php";
    public static final String TAG_SUCCESS = "success";
	public static final String TAG_MESSAGE = "message";
	public static final String TAG_REPAIR_SHOP = "bengkel";
	public static final String TAG_ID = "id_bengkel";
    public static final String TAG_NAME = "bengkel_name";
    public static final String TAG_ADDRESS = "bengkel_address";
    public static final String TAG_PHONE = "telephone";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    
    private JSONArray bengkelJSON = null;
    private ArrayList<String> bengkelNameList, bengkelAddressList, bengkelPhoneList,
    		bengkelLatList,	bengkelLongList; 
    private ArrayList<Double> bengkelRadiusList;
    private ArrayList<Location> locationList;
    String vehicleType, brand, radiusStr;
    Location currentLocation;
    
	LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    
    Spinner SpinnerVehicle, SpinnerBrand, SpinnerRadius;
    
    String[] vehicleTypeSpr, radiusSpr;
    String[] radiusAmount=null;
    HashMap<String, String []> hash_brand = new HashMap<String, String []>();
    	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		generateBrand();
        
		setContentView(R.layout.find_repair_shop);
		
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
        
        vehicleTypeSpr= new String[] { "Select Vehicle Type", "Car", "Motorcycle" };
        radiusSpr=null;
        
        radiusSpr = getResources().getStringArray(R.array.radius_display);
        radiusAmount = getResources().getStringArray(R.array.radius);
        
        MySpinnerAdapter adapterVehicle = new MySpinnerAdapter(
        		this, android.R.layout.simple_spinner_dropdown_item, vehicleTypeSpr);
        MySpinnerAdapter adapterRadius = new MySpinnerAdapter(
        		this, android.R.layout.simple_spinner_dropdown_item, radiusSpr);
        
        SpinnerVehicle = (Spinner) findViewById(R.id.spr_vehicle);
        SpinnerBrand = (Spinner) findViewById(R.id.spr_brand);
        SpinnerRadius = (Spinner) findViewById(R.id.spr_radius);
        SpinnerVehicle.setAdapter(adapterVehicle);
        SpinnerRadius.setAdapter(adapterRadius);
        
        SpinnerVehicle.setOnItemSelectedListener(this);
        
        TextView repairShopLbl = (TextView)findViewById(R.id.repairShopLbl);
        Button findButton = (Button)findViewById(R.id.repairShopFindButton);
        repairShopLbl.setTypeface(bariol, Typeface.BOLD);
        findButton.setTypeface(bariol);
        
        findButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vehicleType = (String) SpinnerVehicle.getSelectedItem();
				brand = (String) SpinnerBrand.getSelectedItem();
				radiusStr = radiusAmount[SpinnerRadius.getSelectedItemPosition()];
				Network ic = new Network();
				if (ic.isNetworkConnected(getApplicationContext())) {
					if(vehicleType.equals("Select Vehicle Type") || brand.equals("Select Brand") ||
							radiusStr.equals("Select Radius")){
						Toast.makeText(FindRepairShop.this, "Please Select Vehicle Type, Brand and Radius", 
								Toast.LENGTH_SHORT).show();
					} else {
						if (currentLocation!=null){
							new GetRepairShop().execute();
						} else {
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

	public void onItemSelected(AdapterView<?> parent, View v, int position, long id){
		fillSpinnerBrand(vehicleTypeSpr[position]);
	}
	
	public void onNothingSelected(AdapterView<?> parent){}
	
	private void generateBrand(){ 
		hash_brand.put("Select Vehicle Type", new String[] {"Select Brand"});
		hash_brand.put("Car", new String[] {"Select Brand", "Toyota", "Honda", 
				"Daihatsu", "Suzuki"});
		hash_brand.put("Motorcycle", new String[] {"Select Brand", "Honda", "Yamaha", 
				"Suzuki", "Kawasaki"});
	}
	
	private void fillSpinnerBrand(String vehicle){
		String[] brandSpr=null;
        MySpinnerAdapter adapterBrand = null;
		try {
			brandSpr = hash_brand.get(vehicle);
			adapterBrand = new MySpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, 
					brandSpr);
		} catch (NullPointerException e){
			Log.d("error", e.toString());
		}
		SpinnerBrand.setAdapter(adapterBrand);
	}

	public class GetRepairShop extends AsyncTask<Void, Void, Boolean> {
		String error;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FindRepairShop.this);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			bengkelNameList = new ArrayList<String>();
			bengkelAddressList = new ArrayList<String>();
			bengkelPhoneList = new ArrayList<String>();
			bengkelLatList = new ArrayList<String>();
			bengkelLongList = new ArrayList<String>();
			locationList = new ArrayList<Location>();
			bengkelRadiusList = new ArrayList<Double>();
			Double rad;
			HashMap<String, String> params = new HashMap<String, String>();
            params.put("vehicle_type", vehicleType);
            params.put("brand", brand);
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.makeHttpRequest(GET_BENGKEL, params);
	        Log.d("All Data: ", json.toString());
			try {
				CalculateRadius cr = new CalculateRadius();
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					bengkelJSON = json.getJSONArray(TAG_REPAIR_SHOP);
					if(radiusStr.equals("Display All")){
						for (int i = 0; i < bengkelJSON.length(); i++) {
							JSONObject c = bengkelJSON.getJSONObject(i);
							String bengkelName = c.getString(TAG_NAME);
							String bengkelAddress = c.getString(TAG_ADDRESS);
							String bengkelPhone = c.getString(TAG_PHONE);
							Double bengkelLatitude = c.getDouble(TAG_LATITUDE);
							Double bengkelLongitude = c.getDouble(TAG_LONGITUDE);
							Location bengkelLocation = new Location("bengkel location");
							bengkelLocation.setLatitude(bengkelLatitude);
							bengkelLocation.setLongitude(bengkelLongitude);
							rad = cr.calculateRadius(currentLocation, bengkelLocation);
							
						    bengkelNameList.add(bengkelName);
						    bengkelAddressList.add(bengkelAddress);
						    bengkelPhoneList.add(bengkelPhone);
						    bengkelLatList.add(String.valueOf(bengkelLatitude));
						    bengkelLongList.add(String.valueOf(bengkelLongitude));   
						    locationList.add(bengkelLocation); 
						    bengkelRadiusList.add(rad);
						}
					} else {
						double radius = Double.valueOf(radiusStr);
						for (int i = 0; i < bengkelJSON.length(); i++) {
							JSONObject c = bengkelJSON.getJSONObject(i);
							String bengkelName = c.getString(TAG_NAME);
							String bengkelAddress = c.getString(TAG_ADDRESS);
							String bengkelPhone = c.getString(TAG_PHONE);
							Double bengkelLatitude = c.getDouble(TAG_LATITUDE);
							Double bengkelLongitude = c.getDouble(TAG_LONGITUDE);
							Location bengkelLocation = new Location("bengkel location");
							bengkelLocation.setLatitude(bengkelLatitude);
							bengkelLocation.setLongitude(bengkelLongitude);
							rad = cr.calculateRadius(currentLocation, bengkelLocation);
							
							if(rad<radius){
								bengkelNameList.add(bengkelName);
							    bengkelAddressList.add(bengkelAddress);
							    bengkelPhoneList.add(bengkelPhone);
							    bengkelLatList.add(String.valueOf(bengkelLatitude));
							    bengkelLongList.add(String.valueOf(bengkelLongitude));   
							    locationList.add(bengkelLocation); 
							    bengkelRadiusList.add(rad);
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
				String[] arrayName = new String[bengkelNameList.size()];
				String[] arrayAddress = new String[bengkelAddressList.size()];
				String[] arrayPhone = new String[bengkelPhoneList.size()];
				String[] arrayLat = new String[bengkelLatList.size()];
				String[] arrayLong = new String[bengkelLongList.size()];
				Double[] arrayRadius = new Double[bengkelRadiusList.size()];
							
				arrayName = bengkelNameList.toArray(arrayName);
				arrayAddress = bengkelAddressList.toArray(arrayAddress);
				arrayPhone = bengkelPhoneList.toArray(arrayPhone);
				arrayLat = bengkelLatList.toArray(arrayLat);
				arrayLong = bengkelLongList.toArray(arrayLong);
				arrayRadius = bengkelRadiusList.toArray(arrayRadius);
				String[] sortedName = new String[arrayName.length];
				String[] sortedAddress = new String[arrayAddress.length];
				String[] sortedPhone = new String[arrayPhone.length];
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
						sortedPhone[i] = arrayPhone[idx[i]];
						sortedLat[i] = arrayLat[idx[i]];
						sortedLong[i] = arrayLong[idx[i]];
						sortedRadius[i] = doubleRadius[i].toString();
					}
				}
				
				pDialog.dismiss();
				
				Intent listResult = new Intent(FindRepairShop.this, ListResultActivity.class);
				if(arrayName.length>0){
					listResult.putExtra("category", "repair_shop");
					listResult.putExtra("bengkel_name", sortedName);
					listResult.putExtra("bengkel_address", sortedAddress);
					listResult.putExtra("bengkel_phone", sortedPhone);
					listResult.putExtra("vehicle_type", vehicleType);
					listResult.putExtra("brand", brand);
					listResult.putExtra("bengkel_lat", sortedLat);
					listResult.putExtra("bengkel_long", sortedLong);
					listResult.putExtra("bengkel_radius", sortedRadius);
				}
				startActivity(listResult);
			} else {
				Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
