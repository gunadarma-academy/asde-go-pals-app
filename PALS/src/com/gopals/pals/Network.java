package com.gopals.pals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Network {
	
	public boolean isNetworkConnected(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) return false;
		if (!ni.isConnected()) return false;
		if (!ni.isAvailable()) return false;
		return true;
	}
	
	public String getDirectionsUrl(LatLng origin,LatLng dest){
	    String str_origin = "origin="+origin.latitude+","+origin.longitude;
	    String str_dest = "destination="+dest.latitude+","+dest.longitude;
	    String sensor = "sensor=false";
	    String avoid = "avoid=highway|tolls";
	    String mode = "mode=driving";
	    
	    // Building the parameters to the web service
	    String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+avoid+"&"+mode;
	    String output = "json";
	    
	    // Building the url to the web service
	    String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
	    return url;
	}
	
	/** A method to download json data from url */
    public String downloadUrl(String strUrl) throws IOException{
    	String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect(); // Connecting to url
            iStream = urlConnection.getInputStream(); // Reading data from url
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
