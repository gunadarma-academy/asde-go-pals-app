package com.gopals.pals;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("InflateParams")
public class ListResultActivity extends ListActivity{
	
	public static final String TAG_NAME = "placeName";
    public static final String TAG_ADDRESS = "placeAddress";
    public static final String TAG_PHONE = "placePhone";
    public static final String TAG_LAT = "placeLat";
    public static final String TAG_LONG = "placeLong";
    public static final String TAG_RADIUS = "placeRadius";
    ArrayList<HashMap<String, String>> placeList; 
    String[] arrPlaceName, arrPlaceAddress, arrPlacePhone, arrPlaceLat, arrPlaceLong, arrPlaceRadius;
    String company, bankName, vehicleType, brand;
    String placeName, placeAddress, placePhone, placeLat, placeLong, placeRadius;
	private String category;
	
	ListView lv;
	Typeface bariol;
	
	String[] from;
	int[] to;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_result);
		RelativeLayout noData = (RelativeLayout)findViewById(R.id.no_data_layout);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a1a1a")));
		bar.setTitle("");
		
		lv = getListView();
		
		bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		TextView resultLbl = (TextView)findViewById(R.id.resultLbl);
		TextView noDataLbl = (TextView)findViewById(R.id.label_no_data);
		resultLbl.setTypeface(bariol, Typeface.BOLD);
		noDataLbl.setTypeface(bariol, Typeface.BOLD);
		
		placeList = new ArrayList<HashMap<String, String>>();
				
		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();
		if(bundle==null){
			noData.setVisibility(View.VISIBLE);
			Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
		} else {
			if (bundle.getString("category").equals("atm")){
				category = bundle.getString("category");
				arrPlaceName = bundle.getStringArray("atm_name");
				bankName = bundle.getString("bank_name");
				arrPlaceAddress = bundle.getStringArray("atm_address");
				arrPlaceLat = bundle.getStringArray("atm_lat");
				arrPlaceLong = bundle.getStringArray("atm_long");
				arrPlaceRadius = bundle.getStringArray("atm_radius");
				setAdapterATMGasStation();
			} else if (bundle.getString("category").equals("gas_station")){
				category = bundle.getString("category");
				arrPlaceName = bundle.getStringArray("spbu_name");
				company = bundle.getString("spbu_company");
				arrPlaceAddress = bundle.getStringArray("spbu_address");
				arrPlaceLat = bundle.getStringArray("spbu_lat");
				arrPlaceLong = bundle.getStringArray("spbu_long");
				arrPlaceRadius = bundle.getStringArray("spbu_radius");
				setAdapterATMGasStation();
			} else if (bundle.getString("category").equals("repair_shop")){
				category = bundle.getString("category");
				arrPlaceName = bundle.getStringArray("bengkel_name");
				vehicleType = bundle.getString("vehicle_type");
				brand = bundle.getString("brand");
				arrPlaceAddress = bundle.getStringArray("bengkel_address");
				arrPlacePhone = bundle.getStringArray("bengkel_phone");
				arrPlaceLat = bundle.getStringArray("bengkel_lat");
				arrPlaceLong = bundle.getStringArray("bengkel_long");
				arrPlaceRadius = bundle.getStringArray("bengkel_radius");
				setAdapterRepairShop();
			}
		}	
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				Network ic = new Network();
				if (ic.isNetworkConnected(getApplicationContext())) {
					placeName = ((TextView) view.findViewById(R.id.placeName)).getText().toString();
					placeAddress = ((TextView) view.findViewById(R.id.placeAddress)).getText().toString();
					placePhone = ((TextView) view.findViewById(R.id.placePhone)).getText().toString();
					placeLat = ((TextView) view.findViewById(R.id.placeLat)).getText().toString();
					placeLong = ((TextView) view.findViewById(R.id.placeLong)).getText().toString();
					placeRadius = ((TextView)view.findViewById(R.id.placeRadius)).getText().toString();
					
					Intent mapsActivity = new Intent(getApplicationContext(), MapsActivity.class);
					if(category.equals("repair_shop")) mapsActivity.putExtra("place_phone", placePhone);
					mapsActivity.putExtra("place_name", placeName);
					mapsActivity.putExtra("place_address", placeAddress);
					mapsActivity.putExtra("place_lat", placeLat);
					mapsActivity.putExtra("place_long", placeLong);
					mapsActivity.putExtra("place_radius", placeRadius);
					mapsActivity.putExtra("category", category);
					startActivity(mapsActivity);
					
				} else 	Toast.makeText(getApplicationContext(), "No Internet Connection", 
							Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	private void setAdapterATMGasStation(){
		from = new String[] {TAG_NAME, TAG_ADDRESS, TAG_RADIUS, TAG_LAT, TAG_LONG};
		to = new int[]{R.id.placeName, R.id.placeAddress, R.id.placeRadius, R.id.placeLat, R.id.placeLong};
		
		int x = 0;
		for(int j=0; j<arrPlaceName.length; j++){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(from[x], arrPlaceName[j]);
			map.put(from[x+1], arrPlaceAddress[j]);
			map.put(from[x+2], arrPlaceRadius[j] + " km");
			map.put(from[x+3], arrPlaceLat[j]);
			map.put(from[x+4], arrPlaceLong[j]);
			placeList.add(map);
		}
		
		if(placeList.size()>0){	
			runOnUiThread(new Runnable() {
	            public void run() {
	            	ListAdapter adapter = new SimpleAdapter(getApplicationContext(), placeList,
	        				R.layout.list_item, from, to){
	            		
						@Override
	                    public View getView(int position, View convertView, ViewGroup parent){
							if(convertView== null){
				                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				                convertView=vi.inflate(R.layout.list_item, null);
				            }
	                        TextView nameLbl = (TextView)convertView.findViewById(R.id.placeName);
			        		TextView addressLbl = (TextView)convertView.findViewById(R.id.placeAddress);
			        		TextView latLbl = (TextView)convertView.findViewById(R.id.placeLat);
			        		TextView longLbl = (TextView)convertView.findViewById(R.id.placeLong);
			        		TextView radiusLbl = (TextView)convertView.findViewById(R.id.placeRadius);
			        		
			        		nameLbl.setText(placeList.get(position).get(TAG_NAME));
			        		addressLbl.setText(placeList.get(position).get(TAG_ADDRESS));
			        		latLbl.setText(placeList.get(position).get(TAG_LAT));
			        		longLbl.setText(placeList.get(position).get(TAG_LONG));
			        		radiusLbl.setText(placeList.get(position).get(TAG_RADIUS));
			        		
			        		nameLbl.setTypeface(bariol, Typeface.BOLD);
			        		addressLbl.setTypeface(bariol);
			        		radiusLbl.setTypeface(bariol);
	                        return convertView;
	                    }
	            	};
	            	setListAdapter(adapter);
	            }
			});
		}
	}
	
	private void setAdapterRepairShop(){
		from = new String[] {TAG_NAME, TAG_ADDRESS, TAG_RADIUS, TAG_PHONE, TAG_LAT, TAG_LONG};
		to = new int[]{R.id.placeName, R.id.placeAddress, R.id.placeRadius, R.id.placePhone, 
				R.id.placeLat, R.id.placeLong};
		
		int x = 0;
		for(int j=0; j<arrPlaceName.length; j++){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(from[x], arrPlaceName[j]);
			map.put(from[x+1], arrPlaceAddress[j]);
			map.put(from[x+2], arrPlaceRadius[j] + " km");
			map.put(from[x+3], arrPlacePhone[j]);
			map.put(from[x+4], arrPlaceLat[j]);
			map.put(from[x+5], arrPlaceLong[j]);
			placeList.add(map);
		}
		
		if(placeList.size()>0){	
			runOnUiThread(new Runnable() {
	            public void run() {
	            	ListAdapter adapter = new SimpleAdapter(getApplicationContext(), placeList,
	        				R.layout.list_item, from, to){
	            		
						@Override
	                    public View getView(int position, View convertView, ViewGroup parent){
							if(convertView== null){
				                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				                convertView=vi.inflate(R.layout.list_item, null);
				            }
	                        TextView nameLbl = (TextView)convertView.findViewById(R.id.placeName);
			        		TextView addressLbl = (TextView)convertView.findViewById(R.id.placeAddress);
			        		TextView phoneLbl = (TextView)convertView.findViewById(R.id.placePhone);
			        		TextView latLbl = (TextView)convertView.findViewById(R.id.placeLat);
			        		TextView longLbl = (TextView)convertView.findViewById(R.id.placeLong);
			        		TextView radiusLbl = (TextView)convertView.findViewById(R.id.placeRadius);
			        		
			        		nameLbl.setText(placeList.get(position).get(TAG_NAME));
			        		addressLbl.setText(placeList.get(position).get(TAG_ADDRESS));
			        		phoneLbl.setText(placeList.get(position).get(TAG_PHONE));
			        		latLbl.setText(placeList.get(position).get(TAG_LAT));
			        		longLbl.setText(placeList.get(position).get(TAG_LONG));
			        		radiusLbl.setText(placeList.get(position).get(TAG_RADIUS));
			        		
			        		nameLbl.setTypeface(bariol, Typeface.BOLD);
			        		addressLbl.setTypeface(bariol);
			        		radiusLbl.setTypeface(bariol);
	                        return convertView;
	                    }
	            	};
	            	setListAdapter(adapter);
	            }
			});
		}
	}
	
}
