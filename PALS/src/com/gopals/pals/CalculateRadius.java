package com.gopals.pals;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.location.Location;

public class CalculateRadius {
	
	public double calculateRadius(Location curLoc, Location destLoc) {
        int Radius = 6371; // radius of earth in Km
        double curLat = curLoc.getLatitude();
        double destLat = destLoc.getLatitude();
        double curLong = curLoc.getLongitude();
        double destLong = destLoc.getLongitude();
        double dLat = Math.toRadians(destLat - curLat);
        double dLon = Math.toRadians(destLong - curLong);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(curLat))
                * Math.cos(Math.toRadians(destLat)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        NumberFormat newFormat = new DecimalFormat("#.##");
        double result = 2 * Math.asin(Math.sqrt(a));
           
        return Double.valueOf(newFormat.format(Radius * result));
    }
}
