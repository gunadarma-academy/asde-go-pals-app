package com.gopals.pals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {

	public static final String TAG = MapsActivity.class.getSimpleName();
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private GoogleMap gMap;
	Polyline line;

	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	String placeName, placeAddress, placePhone, placeRadius, placeDistance;
	Double placeLat, placeLong;
	final Context context = this;
	private String category;
	private LatLng origin;
	Network network = new Network();
	String distance, duration;
	Typeface bariol;
	ArrayList<LatLng> points;
	PolylineOptions lineOptions;
	Marker markerCurrentLoc;
	Circle mapCircle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a1a1a")));
		bar.setTitle("");

		category = getIntent().getStringExtra("category");
		placeName = getIntent().getStringExtra("place_name");
		placeAddress = getIntent().getStringExtra("place_address");
		placeLat = Double.valueOf(getIntent().getStringExtra("place_lat"));
		placeLong = Double.valueOf(getIntent().getStringExtra("place_long"));
		
		if(category.equals("repair_shop")) {
			placePhone = getIntent().getStringExtra("place_phone");
		}

		setUpMap();

		final Typeface bariol = Typeface.createFromAsset(getAssets(),
				"fonts/bariol.ttf");

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();

		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(3 * 1000) // 3 seconds, in milliseconds
				.setFastestInterval(1 * 1000); // 1 second, in milliseconds

		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			TextView tvPhoneLbl, tvPhone;
			Button phone;

			@Override
			public boolean onMarkerClick(Marker marker) {

				final Dialog dialog = new Dialog(context);
				dialog.setTitle("Information");
				if (category.equals("repair_shop")) {
					dialog.setContentView(R.layout.dialog_info_location2);
					phone = (Button) dialog.findViewById(R.id.btn_call);
					tvPhoneLbl = (TextView) dialog
							.findViewById(R.id.tv_phone_lbl);
					tvPhone = (TextView) dialog.findViewById(R.id.tv_phone);

					if (placePhone.equals("")) {
						tvPhone.setText("-");
					} else {
						tvPhone.setText(placePhone);
					}

					phone.setTypeface(bariol);
					tvPhoneLbl.setTypeface(bariol);
					tvPhone.setTypeface(bariol);
					
					phone.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (placePhone != "-")
								CallConfirmationDialog();
							else
								Toast.makeText(MapsActivity.this, "Repair Shop Doesn't have telephone number",
										Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					dialog.setContentView(R.layout.dialog_info_location);
				}

				Button direction = (Button) dialog
						.findViewById(R.id.btn_get_direction);
				Button close = (Button) dialog.findViewById(R.id.btn_close);
				TextView tvNameLbl = (TextView) dialog
						.findViewById(R.id.tv_name_lbl);
				TextView tvAddressLbl = (TextView) dialog
						.findViewById(R.id.tv_address_lbl);
				TextView tvDistanceLbl = (TextView) dialog
						.findViewById(R.id.tv_distance_lbl);
				TextView tvName = (TextView) dialog.findViewById(R.id.tv_name);
				TextView tvAddress = (TextView) dialog
						.findViewById(R.id.tv_address);
				TextView tvDistance = (TextView) dialog
						.findViewById(R.id.tv_distance);

				tvNameLbl.setTypeface(bariol);
				tvAddressLbl.setTypeface(bariol);
				tvDistanceLbl.setTypeface(bariol);
				tvName.setTypeface(bariol);
				tvAddress.setTypeface(bariol);
				tvDistance.setTypeface(bariol);
				direction.setTypeface(bariol);
				close.setTypeface(bariol);

				tvName.setText(placeName);
				tvAddress.setText(placeAddress);
				tvDistance.setText(distance);

				direction.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (lineOptions != null) {
							gMap.addPolyline(lineOptions);
						} else {
							Toast.makeText(getApplicationContext(),
									"Please wait a second, and try again",
									Toast.LENGTH_SHORT).show();
						}
						dialog.dismiss();

					}
				});

				close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				if (!marker.getTitle().equals("Your Position")) {
					dialog.show();
				}
				return true;
			}
		});

	}
	
	@SuppressWarnings("deprecation")
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_maps, menu);
        if(category.equals("atm")){
			menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.icbutton_atm));
		} else if(category.equals("gas_station")){
			menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.icbutton_spbu));
		} else if(category.equals("repair_shop")) {
			menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.icbutton_bengkel));
		}
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		CameraUpdate cameraUpdate;
        switch (item.getItemId()) {    
        case R.id.gotoCurrentLocation:
        	cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					origin, 15);
			gMap.animateCamera(cameraUpdate);
        	break;
        case R.id.gotoDestination:
        	cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					new LatLng(placeLat, placeLong), 15);
			gMap.animateCamera(cameraUpdate);
        	break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
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
		Log.d(TAG,
				"isConnected ...............: "
						+ mGoogleApiClient.isConnected());
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mGoogleApiClient.isConnected()) {
			LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, mLocationRequest, this);
			Log.d(TAG, "Location update resumed .....................");
		}
	}

	private void setUpMap() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (gMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			gMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			BitmapDescriptor bitmap = null;
			// Check if we were successful in obtaining the map.
			if (gMap != null) {
				if(category.equals("atm")){
					 bitmap = BitmapDescriptorFactory
							.fromResource(R.drawable.marker_atm);
				}else if(category.equals("gas_station")){
					bitmap = BitmapDescriptorFactory
							.fromResource(R.drawable.marker_spbu);
				}else if(category.equals("repair_shop")){
					bitmap = BitmapDescriptorFactory
							.fromResource(R.drawable.marker_bengkel);
				}
				gMap.addMarker(new MarkerOptions().position(
						new LatLng(placeLat, placeLong)).title(placeName).icon(bitmap));
				gMap.setMyLocationEnabled(false);
				gMap.getUiSettings().setRotateGesturesEnabled(false);
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
						new LatLng(placeLat, placeLong), 14);
				gMap.animateCamera(cameraUpdate);
			}
		}
	}

	private void handleNewLocation(Location location) {
		Log.d(TAG, location.toString());
		if (markerCurrentLoc != null) {
			markerCurrentLoc.remove();
		}
		
		if (mapCircle != null) {
			mapCircle.remove();
		}

		double currentLatitude = location.getLatitude();
		double currentLongitude = location.getLongitude();
		LatLng latLng = new LatLng(currentLatitude, currentLongitude);
		origin = latLng;

		MarkerOptions options = new MarkerOptions().position(latLng).title(
				"Your Position").icon(BitmapDescriptorFactory
						.fromResource(R.drawable.marker_current));
		markerCurrentLoc = gMap.addMarker(options);
		drawCircle(latLng);
	}

	private void drawCircle(LatLng point) {

		// Instantiating CircleOptions to draw a circle around the marker
		CircleOptions circleOptions = new CircleOptions();

		// Specifying the center of the circle
		circleOptions.center(point);

		// Radius of the circle
		circleOptions.radius(100);

		// Border color of the circle
		circleOptions.strokeColor(0xFF3d94cd);

		// Fill color of the circle
		circleOptions.fillColor(0x303d94cd);

		// Border width of the circle
		circleOptions.strokeWidth(2);

		// Adding the circle to the GoogleMap
		mapCircle = gMap.addCircle(circleOptions);

	}

	@Override
	public void onConnected(Bundle bundle) {
		Location location = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);
		if (location == null) {
			LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, mLocationRequest, this);
		} else {
			handleNewLocation(location);
			if (network.isNetworkConnected(getApplicationContext())) {
				try {
					String url = network.getDirectionsUrl(origin, new LatLng(
							placeLat, placeLong));
					DownloadTask downloadTask = new DownloadTask();
					downloadTask.execute(url);
				} catch (Exception e) {
					Log.e("error:", e.toString());
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"No Internet Connection", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			Log.i(TAG, "Location services connection failed with code "
					+ connectionResult.getErrorCode());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {
			String data = "";
			try {
				data = network.downloadUrl(url[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			ParserTask parserTask = new ParserTask();
			parserTask.execute(result);
		}
	}

	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			points = null;
			lineOptions = null;

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					if (j == 0) { // Get distance from the list
						distance = (String) point.get("distance");
						continue;
					} else if (j == 1) { // Get duration from the list
						duration = (String) point.get("duration");
						continue;
					}

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(4);
				lineOptions.color(Color.RED);
			}

			// Drawing polyline in the Google Map for the i-th route
			// gMap.addPolyline(lineOptions);

		}
	}

	private void CallConfirmationDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MapsActivity.this);

		alertDialog.setTitle("Confirm Phone...");
		alertDialog
				.setMessage("Are you sure you want to call this repair shop?");

		alertDialog.setPositiveButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialog.setNegativeButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						call(placePhone);
					}
				});

		alertDialog.show();
	}

	private void call(String number) {
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + number));
			startActivity(callIntent);
		} catch (Exception e) {
			Toast.makeText(MapsActivity.this, "Can not make a phone call",
					Toast.LENGTH_LONG).show();
		}
	}

}
