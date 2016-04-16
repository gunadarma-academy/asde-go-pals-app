package com.gopals.pals;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FormRepairShop extends Activity implements
		AdapterView.OnItemSelectedListener{
	
	Typeface bariol;
	String repairShopName, vehicleType, brand, repairShopAddress, 
			repairShopPhone, repairShopLat, repairShopLong;
	
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	
	private static final String INSERT_REPAIR_SHOP_URL = 
			"http://gopals.esy.es/insert_bengkel.php";
	public static final String TAG_SUCCESS = "success";
	public static final String TAG_MESSAGE = "message";
	
	Spinner spinnerVehicle, spinnerBrand;
    
    String[] vehicleTypeSpr;
    HashMap<String, String []> hash_brand = new HashMap<String, String []>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_repair_shop);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a1a1a")));
		bar.setTitle("");
		
		repairShopLat = String.valueOf(getIntent().getDoubleExtra("latitude", 0));
		repairShopLong = String.valueOf(getIntent().getDoubleExtra("longitude", 0));
		
		bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		 
		vehicleTypeSpr= new String[] { "Select Vehicle Type", "Car", "Motorcycle" };
		generateBrand();
        
        MySpinnerAdapter adapterVehicle = new MySpinnerAdapter(
        		this, android.R.layout.simple_spinner_dropdown_item, vehicleTypeSpr);
        spinnerVehicle = (Spinner) findViewById(R.id.newVehicleSpr);
        spinnerBrand = (Spinner) findViewById(R.id.newBrandSpr);
        spinnerVehicle.setAdapter(adapterVehicle);
        
        spinnerVehicle.setOnItemSelectedListener(this);
        
        TextView repairShopLbl = (TextView)findViewById(R.id.newRepairShopLbl);
        TextView repairShopNameLbl = (TextView)findViewById(R.id.newRepairShopNameLbl);
        TextView vehicleLbl = (TextView)findViewById(R.id.newVehicleLbl);
        TextView brandLbl = (TextView)findViewById(R.id.newBrandLbl);
        TextView repairShopAddressLbl = (TextView)findViewById(R.id.newRepairShopAddressLbl);
        TextView repairShopPhoneLbl = (TextView)findViewById(R.id.newRepairShopPhoneLbl);
        
        final EditText repairShopNameText = (EditText)findViewById(R.id.repairShopNameText);
        final EditText repairShopAddressText = (EditText)findViewById(R.id.repairShopAddressText);
        final EditText repairShopPhoneText = (EditText)findViewById(R.id.repairShopPhoneText);
        
        Button submitButton = (Button)findViewById(R.id.repairShopSubmitButton);
        
        repairShopLbl.setTypeface(bariol, Typeface.BOLD);
        repairShopNameLbl.setTypeface(bariol);
        vehicleLbl.setTypeface(bariol);
        brandLbl.setTypeface(bariol);
        repairShopAddressLbl.setTypeface(bariol);
        repairShopPhoneLbl.setTypeface(bariol);
        repairShopNameText.setTypeface(bariol);
        repairShopAddressText.setTypeface(bariol);
        repairShopPhoneText.setTypeface(bariol);
        submitButton.setTypeface(bariol);
        
        submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				repairShopName = (String) repairShopNameText.getText().toString().trim();
				vehicleType = (String) spinnerVehicle.getSelectedItem();
				brand = (String) spinnerBrand.getSelectedItem();
				repairShopAddress = (String) repairShopAddressText.getText().toString().trim();
				repairShopPhone = (String) repairShopPhoneText.getText().toString().trim();
				
				Network ic = new Network();
				if (ic.isNetworkConnected(getApplicationContext())) {
					if(repairShopName.equals("") || vehicleType.equals("Select Vehicle Type") || 
							brand.equals("Select Brand") || repairShopAddress.equals("")){
						Toast.makeText(FormRepairShop.this, "Field Cannot be Empty", 
								Toast.LENGTH_SHORT).show();
					} else {
						new InsertRepairShop().execute();	
					} 
				} else 	Toast.makeText(getApplicationContext(), "No Internet Connection", 
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	class InsertRepairShop extends AsyncTask<Void, Void, String> {

		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FormRepairShop.this);
            pDialog.setMessage("Sending Data to Server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(Void... args) {
			HashMap<String, String> params = new HashMap<String, String>();
            params.put("bengkel_name", repairShopName);
			params.put("vehicle_type", vehicleType);
			params.put("brand", brand);
			params.put("bengkel_address", repairShopAddress);
			params.put("bengkel_phone", repairShopPhone);
			params.put("latitude", repairShopLat);
			params.put("longitude", repairShopLong);
			
			Log.d("request!", "starting");
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.makeHttpRequest(INSERT_REPAIR_SHOP_URL, params);
      
			Log.d("Insert attempt", json.toString());
            
            try{
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                	Log.d("User Input Gas Station Added!", json.toString());    
                }else{
                	Log.d("Insert Failure!", json.getString(TAG_MESSAGE));
                }
            	return json.getString(TAG_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
		}
		
		protected void onPostExecute(String result) {
            pDialog.dismiss();
            if (result != null){
            	AlertDialog alertDialog = new AlertDialog.Builder(FormRepairShop.this).create();
            	alertDialog.setTitle("Message");
            	alertDialog.setMessage(result);
            	alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
            	    new DialogInterface.OnClickListener() {
            	        public void onClick(DialogInterface dialog, int which) {
            	            dialog.dismiss();
            	            Intent in = new Intent(FormRepairShop.this, CategoryAddLocation.class);
            	            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	            startActivity(in);
            	        }
            	    });
            	alertDialog.show();
            }
        }
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
		spinnerBrand.setAdapter(adapterBrand);
	}

}
