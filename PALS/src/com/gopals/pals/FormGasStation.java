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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FormGasStation extends Activity {
	
	Typeface bariol;
	String gasStationName, company, gasStationAddress, gasStationLat, gasStationLong;
	
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	
	private static final String INSERT_GAS_STATION_URL = 
			"http://gopals.esy.es/insert_spbu.php";
	public static final String TAG_SUCCESS = "success";
	public static final String TAG_MESSAGE = "message";
	
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_gas_station);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a1a1a")));
		bar.setTitle("");
		
		gasStationLat = String.valueOf(getIntent().getDoubleExtra("latitude", 0));
		gasStationLong = String.valueOf(getIntent().getDoubleExtra("longitude", 0));
		
		bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		 
		String[] companySpr=null;
        companySpr = getResources().getStringArray(R.array.company);
        
        MySpinnerAdapter adapterCompany = new MySpinnerAdapter(
        		this, android.R.layout.simple_spinner_dropdown_item, companySpr);
        final Spinner spinnerCompany = (Spinner) findViewById(R.id.newCompanySpr);
        spinnerCompany.setAdapter(adapterCompany);
        
        TextView gasStationLbl = (TextView)findViewById(R.id.newGasStationLbl);
        TextView gasStationNameLbl = (TextView)findViewById(R.id.newGasStationNameLbl);
        TextView gasStationCompanyLbl = (TextView)findViewById(R.id.newCompanyLbl);
        TextView gasStationAddressLbl = (TextView)findViewById(R.id.newGasStationAddressLbl);
        
        final EditText gasStationNameText = (EditText)findViewById(R.id.gasStationNameText);
        final EditText gasStationAddressText = (EditText)findViewById(R.id.gasStationAddressText);
        
        Button submitButton = (Button)findViewById(R.id.gasStationSubmitButton);
        
        gasStationLbl.setTypeface(bariol, Typeface.BOLD);
        gasStationNameLbl.setTypeface(bariol);
        gasStationCompanyLbl.setTypeface(bariol);
        gasStationAddressLbl.setTypeface(bariol);
        gasStationNameText.setTypeface(bariol);
        gasStationAddressText.setTypeface(bariol);
        submitButton.setTypeface(bariol);
        
        submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gasStationName = (String) gasStationNameText.getText().toString().trim();
				company = (String) spinnerCompany.getSelectedItem();
				gasStationAddress = (String) gasStationAddressText.getText().toString().trim();
				
				Network ic = new Network();
				if (ic.isNetworkConnected(getApplicationContext())) {
					if(gasStationName.equals("") || company.equals("Select Company") || 
							gasStationAddress.equals("")){
						Toast.makeText(FormGasStation.this, "Field Cannot be Empty", 
								Toast.LENGTH_SHORT).show();
					} else {
						new InsertGasStation().execute();	
					} 
				} else 	Toast.makeText(getApplicationContext(), "No Internet Connection", 
						Toast.LENGTH_SHORT).show();
			}
		});
        
	}
    
    class InsertGasStation extends AsyncTask<Void, Void, String> {

		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FormGasStation.this);
            pDialog.setMessage("Sending Data to Server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(Void... args) {
			HashMap<String, String> params = new HashMap<String, String>();
            params.put("spbu_name", gasStationName);
			params.put("company", company);
			params.put("spbu_address", gasStationAddress);
			params.put("latitude", gasStationLat);
			params.put("longitude", gasStationLong);
			
			Log.d("request!", "starting");
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.makeHttpRequest(INSERT_GAS_STATION_URL, params);
      
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
            	AlertDialog alertDialog = new AlertDialog.Builder(FormGasStation.this).create();
            	alertDialog.setTitle("Message");
            	alertDialog.setMessage(result);
            	alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
            	    new DialogInterface.OnClickListener() {
            	        public void onClick(DialogInterface dialog, int which) {
            	            dialog.dismiss();
            	            Intent in = new Intent(FormGasStation.this, CategoryAddLocation.class);
            	            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	            startActivity(in);
            	        }
            	    });
            	alertDialog.show();
            }
        }
	}
}
