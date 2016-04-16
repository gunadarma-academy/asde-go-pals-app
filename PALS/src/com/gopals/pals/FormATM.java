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

public class FormATM extends Activity {
	
	Typeface bariol;
	String atmName, bankName, atmAddress, atmLat, atmLong;
	
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	
	private static final String INSERT_ATM_URL = 
			"http://gopals.esy.es/insert_atm.php";
	public static final String TAG_SUCCESS = "success";
	public static final String TAG_MESSAGE = "message";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_atm);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a1a1a")));
		bar.setTitle("");
		
		atmLat = String.valueOf(getIntent().getDoubleExtra("latitude", 0));
		atmLong = String.valueOf(getIntent().getDoubleExtra("longitude", 0));
		
		bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		 
		String[] bankNameSpr=null;
        bankNameSpr = getResources().getStringArray(R.array.bank_name);
        
        MySpinnerAdapter adapterBankName = new MySpinnerAdapter(
        		this, android.R.layout.simple_spinner_dropdown_item, bankNameSpr);
        final Spinner spinnerBankName = (Spinner) findViewById(R.id.newBankNameSpr);
        spinnerBankName.setAdapter(adapterBankName);
        
        TextView atmLbl = (TextView)findViewById(R.id.newATMLbl);
        TextView atmNameLbl = (TextView)findViewById(R.id.newATMNameLbl);
        TextView bankNameLbl = (TextView)findViewById(R.id.newBankNameLbl);
        TextView atmAddressLbl = (TextView)findViewById(R.id.newATMAddressLbl);
        
        final EditText atmNameText = (EditText)findViewById(R.id.atmNameText);
        final EditText atmAddressText = (EditText)findViewById(R.id.atmAddressText);
        
        Button submitButton = (Button)findViewById(R.id.atmSubmitButton);
        
        atmLbl.setTypeface(bariol, Typeface.BOLD);
        atmNameLbl.setTypeface(bariol);
        bankNameLbl.setTypeface(bariol);
        atmAddressLbl.setTypeface(bariol);
        atmNameText.setTypeface(bariol);
        atmAddressText.setTypeface(bariol);
        submitButton.setTypeface(bariol);
        
        submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				atmName = (String) atmNameText.getText().toString().trim();
				bankName = (String) spinnerBankName.getSelectedItem();
				atmAddress = (String) atmAddressText.getText().toString().trim();
				
				Network ic = new Network();
				if (ic.isNetworkConnected(getApplicationContext())) {
					if(atmName.equals("") || bankName.equals("Select Bank") || 
							atmAddress.equals("")){
						Toast.makeText(FormATM.this, "Field Cannot be Empty", 
								Toast.LENGTH_SHORT).show();
					} else {
						new InsertATM().execute();	
					} 
				} else 	Toast.makeText(getApplicationContext(), "No Internet Connection", 
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	class InsertATM extends AsyncTask<Void, Void, String> {

		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FormATM.this);
            pDialog.setMessage("Sending Data to Server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(Void... args) {
			HashMap<String, String> params = new HashMap<String, String>();
            params.put("atm_name", atmName);
			params.put("bank_name", bankName);
			params.put("atm_address", atmAddress);
			params.put("latitude", atmLat);
			params.put("longitude", atmLong);
			
			Log.d("request!", "starting");
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.makeHttpRequest(INSERT_ATM_URL, params);
      
			Log.d("Insert attempt", json.toString());
            
            try{
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                	Log.d("User Input ATM Added!", json.toString());    
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
            	AlertDialog alertDialog = new AlertDialog.Builder(FormATM.this).create();
            	alertDialog.setTitle("Message");
            	alertDialog.setMessage(result);
            	alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
            	    new DialogInterface.OnClickListener() {
            	        public void onClick(DialogInterface dialog, int which) {
            	            dialog.dismiss();
            	            Intent in = new Intent(FormATM.this, CategoryAddLocation.class);
            	            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	            startActivity(in);
            	        }
            	    });
            	alertDialog.show();
            }
        }
	}
}
