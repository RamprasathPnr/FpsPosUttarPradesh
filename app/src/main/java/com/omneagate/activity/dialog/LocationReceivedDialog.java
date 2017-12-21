package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.Util.Util;
import com.omneagate.activity.AdminActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * This dialog will appear on the time of user logout
 */
public class LocationReceivedDialog extends Dialog implements
        View.OnClickListener {
    private final Activity context;  //    Context from the user

    Location location;

    /*Constructor class for this dialog*/
    public LocationReceivedDialog(Activity _context, Location location) {
        super(_context);
        context = _context;
        this.location = location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_location_received);


        Util.LoggingQueue(context, "LocationReceivedDialog", "onCreate() called ");

        setCancelable(false);
        Button yesButton = (Button) findViewById(R.id.buttonYes);
        Button noButton = (Button) findViewById(R.id.buttonNo);

        String locationDetailsLng, locationDetailsLat, continueString, fpsLocStr, warningTxt;
        if(GlobalAppState.language.equalsIgnoreCase("hi")) {
            locationDetailsLng = "देशान्तर  : " + location.getLongitude();
            locationDetailsLat = "अक्षांश  : " + location.getLatitude();
            Util.setTamilText(yesButton, "हाँ");
            Util.setTamilText(noButton, "नहीं");
            continueString = "क्या आप जारी रखना चाहते हैं ?";
            fpsLocStr = "यह आपके एफपीएस स्थान के रूप में तय हो जाएगा";
            warningTxt = "स्थान";
        }
        else {
            locationDetailsLng = "Longitude : " + location.getLongitude();
            locationDetailsLat = "Latitude  : " + location.getLatitude();
            Util.setTamilText(yesButton, "Yes");
            Util.setTamilText(noButton, "No");
            continueString = "Do you want to continue ?";
            fpsLocStr = "This location will be set FPS store location";
            warningTxt = "Location";
        }
        Util.setTamilText(((TextView) findViewById(R.id.tvNextDay)), locationDetailsLng);
        Util.setTamilText(((TextView) findViewById(R.id.tvLat)), locationDetailsLat);
        Util.setTamilText(((TextView) findViewById(R.id.tvContinue)), continueString);
        Util.setTamilText(((TextView) findViewById(R.id.tvloginBack)), fpsLocStr);
        Util.setTamilText(((TextView) findViewById(R.id.tvWaring)), warningTxt);


        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes:
                dismiss();
                //getLocationAddress();
                //((TextView) findViewById(R.id.tvNextDay)).setText(""+getLocationAddress());
                sendLocation();
                break;
            case R.id.buttonNo:
                dismiss();
                break;
        }
    }


    public String getLocationAddress() {

        //if (isLocationAvailable) {
        Util.LoggingQueue(context, "LocationReceivedDialog", "getLocationAddress() called ");

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            // Get the current location from the input parameter list
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
				/*
				 * Return 1 address.
				 */
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e1) {
                e1.printStackTrace();

                Util.LoggingQueue(context, "LocationReceivedDialog", "IO Exception trying to get address:" + e1);

                return ("IO Exception trying to get address:" + e1);
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments "
                        + Double.toString(location.getLatitude()) + " , "
                        + Double.toString(location.getLongitude())
                        + " passed to address service";
                e2.printStackTrace();
                Util.LoggingQueue(context, "LocationReceivedDialog", "errorString = "+errorString);

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


                Util.LoggingQueue(context, "LocationReceivedDialog", "addressText = "+addressText);

                // Return the text
                return addressText;
            } else {
                Util.LoggingQueue(context, "LocationReceivedDialog", "No address found by the service: Note to the developers," +
                        " If no address is found by google itself, there is nothing you can do about it. :(");

                return "No address found by the service: Note to the developers, If no address is found by google itself, there is nothing you can do about it. :(";
            }
        /*} else {
            return "Location Not available";
        }*/

    }


    private void sendLocation() {
        ((AdminActivity) context).sendLocation(location);
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, String id) {
        textName.setText(id);
    }


}