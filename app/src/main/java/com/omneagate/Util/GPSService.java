package com.omneagate.Util;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSService 
        implements LocationListener {

    // Minimum time fluctuation for next update (in milliseconds)
    private static final long TIME = 1000;
    // Minimum distance fluctuation for next update (in meters)
    private static final long DISTANCE = 1;
    // saving the context for later use
    private final Context mContext;
    // if Location co-ordinates are available using GPS or Network
    public boolean isLocationAvailable = false;
    // Declaring a Location Manager
    protected LocationManager mLocationManager;
    // if GPS is enabled
    boolean isGPSEnabled = false;
    // if Network is enabled
    boolean isNetworkEnabled = false;
    // Location and co-ordinates coordinates
    Location mLocation;
    double mLatitude;
    double mLongitude;




    public GPSService(Context context) {

        this.mContext = context;
        mLocationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
        Util.LoggingQueue(mContext, "GPSService", "GPSService() called ");



        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
       //  locationManager.getBestProvider(criteria, true);


        Util.LoggingQueue(mContext, "GPSService", "GPSService() called "+mLocationManager.getBestProvider(criteria, true));

    }



    /**
     * Returs the Location
     *
     * @return Location or null if no location is found
     */
    public Location getLocation() {
        try {
            Util.LoggingQueue(mContext, "GPSService", "getLocation() called ");

            // Getting GPS status
            isGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);


            Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.GPS_PROVIDER isGPSEnabled =  "+isGPSEnabled);



            // If GPS enabled, get latitude/longitude using GPS Services
            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, TIME, DISTANCE, this);



                Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.GPS_PROVIDER requestLocationUpdates  TIME =  "+TIME);
                Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.GPS_PROVIDER requestLocationUpdates  DISTANCE =  "+DISTANCE);

                if (mLocationManager != null) {

                    Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.GPS_PROVIDER   mLocationManager  =  "+mLocationManager);
                    Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.GPS_PROVIDER   mLocationManager getAllProviders =  "+mLocationManager.getAllProviders());
                    Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.GPS_PROVIDER   mLocationManager  =  "+mLocationManager);

                    mLocation = mLocationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (mLocation != null) {


                        mLatitude = mLocation.getLatitude();
                        mLongitude = mLocation.getLongitude();
                        isLocationAvailable = true;
                        getLocationAddress();
                        Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.GPS_PROVIDER   mLatitude  =  "+mLatitude);
                        Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.GPS_PROVIDER   mLongitude  =  "+mLongitude);


                        return mLocation;
                    }else{
                        Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.GPS_PROVIDER   Location Not Found  =  ");

                    }
                }else{
                    Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.GPS_PROVIDER  !!!  mLocationManager =  "+mLocationManager);

                }
            }else{

            }

            // If we are reaching this part, it means GPS was not able to fetch
            // any location
            // Getting network status
            isNetworkEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.NETWORK_PROVIDER   isNetworkEnabled =  " +isNetworkEnabled);


            if (isNetworkEnabled) {


                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, TIME, DISTANCE, this);

                Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.NETWORK_PROVIDER requestLocationUpdates  TIME =  "+TIME);
                Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.NETWORK_PROVIDER requestLocationUpdates  DISTANCE =  "+DISTANCE);
                if (mLocationManager != null) {

                   /* mLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            2000, 1, this);*/

                    mLocation = mLocationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.NETWORK_PROVIDER   mLocationManager  =  "+mLocationManager);

                    if (mLocation != null) {
                        mLatitude = mLocation.getLatitude();
                        mLongitude = mLocation.getLongitude();
                        isLocationAvailable = true; // setting a flag that
                        // location is available
                        getLocationAddress();
                        Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.NETWORK_PROVIDER   mLatitude  =  "+mLatitude);
                        Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.NETWORK_PROVIDER   mLongitude  =  "+mLongitude);
                        return mLocation;
                    }else{
                        Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.NETWORK_PROVIDER   Location Not Found  =  ");

                    }
                }else{
                    Util.LoggingQueue(mContext, "GPSService", "getLocation() LocationManager.NETWORK_PROVIDER  !!!  mLocationManager =  "+mLocationManager);

                }
            }else{

            }
            // If reaching here means, we were not able to get location neither
            // from GPS not Network,
            if (!isGPSEnabled) {

            }

        } catch (Exception e) {


            Util.LoggingQueue(mContext, "GPSService", "getLocation() Exception =  "+e);

            Log.e("Error in GPS", e.toString(), e);
        }
        // if reaching here means, location was not available, so setting the
        // flag as false
        isLocationAvailable = false;
        return null;
    }

    public String getLocationAddress() {

        if (isLocationAvailable) {

            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
				/*
				 * Return 1 address.
				 */
                addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
            } catch (IOException e1) {
                e1.printStackTrace();
                return ("IO Exception trying to get address:" + e1);
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments "
                        + Double.toString(mLatitude) + " , "
                        + Double.toString(mLongitude)
                        + " passed to address service";
                e2.printStackTrace();
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
				/*
				 * Format the first line of address (if available), city, and
				 * country name.
				 */
                String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ? address
                                .getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());



                // Return the text
                return addressText;
            } else {
                return "No address found by the service: Note to the developers, If no address is found by google itself, there is nothing you can do about it. :(";
            }
        } else {
            return "Location Not available";
        }

    }
    /**
     * get latitude
     *
     * @return latitude in double
     */
    public double getLatitude() {

        Util.LoggingQueue(mContext, "GPSService", "getLatitude()");

        if (mLocation != null) {
            mLatitude = mLocation.getLatitude();
            Util.LoggingQueue(mContext, "GPSService", "getLatitude() mLatitude =  "+mLatitude);

        }
        return mLatitude;
    }

    /**
     * get longitude
     *
     * @return longitude in double
     */
    public double getLongitude() {
        Util.LoggingQueue(mContext, "GPSService", "getLongitude()");

        if (mLocation != null) {
            mLongitude = mLocation.getLongitude();
            Util.LoggingQueue(mContext, "GPSService", "getLongitude() mLongitude =  "+mLongitude);

        }
        return mLongitude;
    }

    /**
     * close GPS to save battery
     */
    public void closeGPS() {
        Util.LoggingQueue(mContext, "GPSService", "closeGPS() ");

        if (mLocationManager != null) {
            Util.LoggingQueue(mContext, "GPSService", "closeGPS() mLocationManager =  "+mLocationManager);

            mLocationManager.removeUpdates(com.omneagate.Util.GPSService.this);
        }
    }


    /**
     * Updating the location when location changes
     */
    @Override
    public void onLocationChanged(Location location) {
//        Util.LoggingQueue(mContext, "GPSService", "onLocationChanged() Location =  "+location);

        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {

        Util.LoggingQueue(mContext, "GPSService", "onProviderDisabled() provider =  "+provider);

    }

    @Override
    public void onProviderEnabled(String provider) {

        Util.LoggingQueue(mContext, "GPSService", "onProviderEnabled() provider =  "+provider);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        Util.LoggingQueue(mContext, "GPSService", "onStatusChanged() provider =  "+provider);
        Util.LoggingQueue(mContext, "GPSService", "onStatusChanged() status =  "+status);
        Util.LoggingQueue(mContext, "GPSService", "onStatusChanged() extras =  "+extras);

    }



}