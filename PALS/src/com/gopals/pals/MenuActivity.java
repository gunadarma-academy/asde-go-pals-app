package com.gopals.pals;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

@SuppressLint("NewApi")
public class MenuActivity extends Activity implements
		LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {
	
	private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 3;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    
	LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    
    Typeface bariol;
    private static final int REQUEST_CODE_PERMISSION = 123;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		
		TextView atmLbl = (TextView)findViewById(R.id.atmLbl);
		TextView gasStationLbl = (TextView)findViewById(R.id.gasStationLbl);
		TextView repairShopLbl = (TextView)findViewById(R.id.repairShopLbl);
		atmLbl.setTypeface(bariol);
		gasStationLbl.setTypeface(bariol);
		repairShopLbl.setTypeface(bariol);
		
		/*
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = this.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
		}
		*/
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a1a1a")));
		bar.setTitle("");
		
		String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, 
				Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};

		if(!hasPermissions(this, PERMISSIONS)){
		    ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSION);
		} else {
			createLocationRequest();
			showIntroDialog();
		}
		
		/*
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
				!= PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				// Display UI and wait for user interaction
			} else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
	                                     REQUEST_CODE_PERMISSION);
			}
		} else {
			// permission has been granted, continue as usual
	        createLocationRequest();
	        showIntroDialog();
		}
		*/
		
        LinearLayout btnATM = (LinearLayout)findViewById(R.id.findATM);
        btnATM.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(checkLocation()){
					Intent findATM = new Intent(MenuActivity.this, FindATM.class);
					startActivity(findATM);
				} else showDialog();
			}
		});
        
        LinearLayout btnGasStation = (LinearLayout)findViewById(R.id.findGasStation);
        btnGasStation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(checkLocation()){
					Intent findGasStation = new Intent(MenuActivity.this, FindGasStation.class);
					startActivity(findGasStation);
				} else showDialog();
			}
		});
        
        LinearLayout btnRepairShop = (LinearLayout)findViewById(R.id.findRepairShop);
        btnRepairShop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(checkLocation()){
					Intent findRepairShop = new Intent(MenuActivity.this, FindRepairShop.class);
					startActivity(findRepairShop);
				} else showDialog();
			}
		});
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_CODE_PERMISSION) {
            for( int i = 0; i < permissions.length; i++ ) {
                if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                    Log.d( "Permissions", "Permission Granted: " + permissions[i] );
                } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                    Log.d( "Permissions", "Permission Denied: " + permissions[i] );
                }
            }
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				createLocationRequest();
				showIntroDialog();
			} else {
				showPermissionRejectedDialog();
			}
        }
		
		/*
		if (requestCode == REQUEST_CODE_PERMISSION) {
			if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// success!
				createLocationRequest();
				showIntroDialog();
			} else {
				showPermissionRejectedDialog();
			}
		}
		*/
	}
	
	@Override
    public void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
        Log.d(TAG, "onStart fired ..............");
    }
	
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        if(mGoogleApiClient != null){
        	mGoogleApiClient.disconnect();
        	Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
        }
    }
	
    @Override
    public void onResume() {
        super.onResume();
        if(mGoogleApiClient != null){
        	if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
                Log.d(TAG, "Location update resumed .....................");
            }
        }
    }
    /*
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {}

	@Override
	public void onConnected(Bundle bundle) {
		startLocationUpdates();
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
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_help:
        	Intent help = new Intent(MenuActivity.this, Help.class);
        	startActivity(help);
        	break;
        case R.id.action_about:
        	Intent about = new Intent(MenuActivity.this, About.class);
        	startActivity(about);
        	break;
        case R.id.action_add_location:
        	Intent categoryAddLocation = new Intent(MenuActivity.this, CategoryAddLocation.class);
        	startActivity(categoryAddLocation);
        	break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
	
	protected void createLocationRequest() {
		if (!isGooglePlayServicesAvailable()) {
            finish();
        }
		
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        
        mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addApi(LocationServices.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
        
        mGoogleApiClient.connect();
    }
	
	protected void startLocationUpdates() {
	    LocationServices.FusedLocationApi.requestLocationUpdates(
	            mGoogleApiClient, mLocationRequest, this);
	}
	
	protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
	
	private boolean checkLocation(){
		LocationManager locationManager = null;
		boolean gps_enabled = false, network_enabled = false;
		if(locationManager==null)
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    try{
	    	gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    }catch(Exception ex){}
	    try{
	    	network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	    }catch(Exception ex){}
	    
	    if(!gps_enabled && !network_enabled) return false;
	    
	    return true;
	}
	
	private void showDialog(){
		final AlertDialog.Builder dialog = new AlertDialog.Builder(MenuActivity.this);
        dialog.setMessage("Location service is disabled");
        dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	dialog.cancel();
            }
        });
 
        dialog.setNegativeButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });
        dialog.show();
	}
	
	private void showIntroDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.dialog_intro);
		
		TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialogTitle);
		TextView notice1 = (TextView) dialog.findViewById(R.id.noticeContent1);
		TextView notice2 = (TextView) dialog.findViewById(R.id.noticeContent2);
		TextView notice3 = (TextView) dialog.findViewById(R.id.noticeContent3);
		dialogTitle.setTypeface(bariol, Typeface.BOLD);
		notice1.setTypeface(bariol);
		notice2.setTypeface(bariol);
		notice3.setTypeface(bariol);
		
		Button close = (Button) dialog.findViewById(R.id.btn_close);
		Button okay = (Button) dialog.findViewById(R.id.btn_okay);
		okay.setTypeface(bariol);
		
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		okay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	private void showPermissionRejectedDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Permission Rejected");
		alertDialog.setMessage("Permission to access location is rejected, application will close automatically");
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		            finish();
		        }
		    });
		alertDialog.show();
	}
	
	public static boolean hasPermissions(Context context, String... permissions) {
	    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
	        for (String permission : permissions) {
	            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
	                return false;
	            }
	        }
	    }
	    return true;
	}
}
