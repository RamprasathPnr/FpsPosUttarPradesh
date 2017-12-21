package com.omneagate.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BaseDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.RoleFeature;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.POSLocationDto;
import com.omneagate.DTO.RollMenuDto;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.GPSService;
import com.omneagate.Util.LocalDbRecoveryProcess;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.LocationReceivedDialog;
import com.omneagate.activity.dialog.LogoutDialog;
import com.omneagate.activity.dialog.fpsRollViewAdpter;
import com.omneagate.service.HttpClientWrapper;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AdminActivity extends BaseActivity {

    final ArrayList<String> fpsRoleName = new ArrayList<>();

    GridView fpsRoleView;

    List<RollMenuDto> roleMenu;

    LocationReceivedDialog locationReceivedDialog;

    LogoutDialog logoutDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_admin);
        Util.LoggingQueue(this, " AdminActivity", "onCreate called");

        roleMenu = new ArrayList<>();
        configureInitialPage();
        rollFeatureView();
        progressBar = new CustomProgressDialog(this);
    }

    public void rollFeatureView() {


        if(SessionId.getInstance().getUserId() == 0 ){



        }

        Set<String> retrieveRoleFeature = FPSDBHelper.getInstance(this).retrieveRolesDataString(SessionId.getInstance().getUserId());
        Util.LoggingQueue(this, " AdminActivity", "getUserId ->" + SessionId.getInstance().getUserId());


        Util.LoggingQueue(this, " AdminActivity", "retrieveRoleFeature ->" + retrieveRoleFeature.toString());

        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.dashboard);
        for (String roleUser : retrieveRoleFeature) {
            try {
                if (roleUser.equals("OPEN_STOCK") || roleUser.equals("GEO_LOCATION") || roleUser.equals("STATISTICS")
                        || roleUser.equals("RETRIEVE_DB") || roleUser.equals("RESTORE_DB") || roleUser.equals("VERSION_UPGRADE")) {
                    RoleFeature roles = RoleFeature.valueOf(roleUser);
                    roleMenu.add(new RollMenuDto(getString(roles.getRollName()), roles.getBackground(), roles.getColorCode(), roles.getDescription()));
                    fpsRoleName.add(roleUser);
                }
            } catch (Exception e) {
                Util.LoggingQueue(this, " AdminActivity", "Excep ->" + e.toString());

            }
        }

        Util.LoggingQueue(this, " AdminActivity", "fpsRoleName ->" + fpsRoleName);

        fpsRoleView = (GridView) findViewById(R.id.fpsroll);
        fpsRoleView.setAdapter(new fpsRollViewAdpter(this, roleMenu));
        fpsRoleView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Util.LoggingQueue(AdminActivity.this, " AdminActivity", "roleMenu.get(i).getClassName() ->" + roleMenu.get(i).getClassName());

                printData(roleMenu.get(i).getClassName());
            }
        });

    }

    private void printData(String funcName) {
        try {
            Method method = getClass().getDeclaredMethod(funcName);
            method.invoke(this, new Object[]{});
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    private void restoreDB() {
        LocalDbRecoveryProcess localDbRecoveryProcess = new LocalDbRecoveryProcess(com.omneagate.activity.AdminActivity.this);
        localDbRecoveryProcess.restoresDb();
    }

    private void getStatistics() {
        startActivity(new Intent(this, StatisticsActivity.class));
        finish();
    }

    private void retrieveDB() {
        LocalDbRecoveryProcess localDbRecoveryPro = new LocalDbRecoveryProcess(com.omneagate.activity.AdminActivity.this);
        localDbRecoveryPro.backupDb(true, "", FPSDBHelper.DATABASE_NAME, "");
    }

    private void openStock() {
        startActivity(new Intent(this, OpenStockActivity.class));
        finish();
    }

    private void versionUpgrade() {
        startActivity(new Intent(this, VersionUpgradeInfo.class));
        finish();
    }

    private void findLocation() {
        Util.LoggingQueue(AdminActivity.this, "AdminActivity", "findLocation() called");

        findGPSLocation();
    }

    private void configureInitialPage() {
        setUpPopUpPageForAdmin();
    }

    private void findGPSLocation() {

        Util.LoggingQueue(AdminActivity.this, "AdminActivity", "findGPSLocation() called");

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Util.LoggingQueue(AdminActivity.this, "AdminActivity", "findGPSLocation() GPS ! enabled");

            turnGPSOn();
        } else {

            Util.LoggingQueue(AdminActivity.this, "AdminActivity", "findGPSLocation() GPS enabled");


           // startService(new Intent(this, GPSService.class));
            GPSService mGPSService = new GPSService(this);
            Location locationB = mGPSService.getLocation();

           // Util.LoggingQueue(AdminActivity.this, "AdminActivity", "findGPSLocation() Location = "+locationB);


            if (locationB != null) {
                locationReceivedDialog = new LocationReceivedDialog(this, locationB);
                locationReceivedDialog.show();
            } else {
                if (GlobalAppState.language.equalsIgnoreCase("ta")) {
                    Toast.makeText(this, "இருப்பிடம் பெற முடியாது", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Location can not be received", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void turnGPSOn() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (enabled) {
            progressBar.show();
            setLocation();
        } else {
            if(GlobalAppState.language.equalsIgnoreCase("hi")) {
                Toast.makeText(this, "இருப்பிடம் அமைப்பை செயல்படுத்தவும்", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please Enable Location Setting", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setLocation() {


        Util.LoggingQueue(AdminActivity.this, "AdminActivity", "setLocation() called");

        final Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null)
                    progressBar.dismiss();
                findGPSLocation();
            }
        }, 5000);
    }


    @Override
    public void onBackPressed() {
        userLogoutResponse();
    }

    //After user give logout this method will call dialog
    private void userLogoutResponse() {
        logoutDialog = new LogoutDialog(this);
        logoutDialog.show();
    }

    public void sendLocation(Location location) {
        try {

            Util.LoggingQueue(AdminActivity.this, "AdminActivity", "sendLocation() Location = "+location);


            getLocationAddress(location);


            httpConnection = new HttpClientWrapper();
            POSLocationDto posLocation = new POSLocationDto();
            posLocation.setLatitude(String.valueOf(location.getLatitude()));
            posLocation.setLongitude(String.valueOf(location.getLongitude()));
            AndroidDeviceProperties device = new AndroidDeviceProperties(this);
            posLocation.setDeviceNumber(device.getDeviceProperties().getSerialNumber());
            String deviceLocation = new Gson().toJson(posLocation);
            StringEntity se = new StringEntity(deviceLocation, HTTP.UTF_8);
            String url = "/remoteLogging/addlocation";
            httpConnection.sendRequest(url, null, ServiceListenerType.DEVICE_STATUS,
                    SyncHandler, RequestType.POST, se, this);
        } catch (Exception e) {
            Util.messageBar(this, getString(R.string.internalError));
            //Util.LoggingQueue(this, "POS location Error", "Locayion Error in Pos");
            //Log.e("error", e.toString(), e);

            Util.LoggingQueue(AdminActivity.this, "AdminActivity", "sendLocation() Exception = "+e);



        }
    }
    public String getLocationAddress(Location location) {

       // if (isLocationAvailable) {

            Geocoder geocoder = new Geocoder(AdminActivity.this, Locale.getDefault());
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
            //    Toast.makeText(AdminActivity.this, "IO Exception trying to get address" , Toast.LENGTH_LONG).show();
                return ("IO Exception trying to get address:" + e1);
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments "
                        + Double.toString(location.getLatitude()) + " , "
                        + Double.toString(location.getLongitude())
                        + " passed to address service";
                e2.printStackTrace();
            //    Toast.makeText(AdminActivity.this, ""+errorString , Toast.LENGTH_LONG).show();

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


             //   Toast.makeText(AdminActivity.this, "" +addressText, Toast.LENGTH_LONG).show();

                // Return the text
                return addressText;
            } else {
              //  Toast.makeText(AdminActivity.this, "No address found by the service" , Toast.LENGTH_LONG).show();

                return "No address found by the service: Note to the developers, If no address is found by google itself, there is nothing you can do about it. :(";
            }
        /*} else {
            return "Location Not available";
        }*/

    }
    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {



        Util.LoggingQueue(AdminActivity.this, "AdminActivity ",
                "processMessage() called message -> " + message + " Type -> " + what);

        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {

        }

        switch (what) {

            case ERROR_MSG:
                Util.messageBar(this, getString(R.string.connectionRefused));

                break;
            case DEVICE_STATUS:
                setStatusCheck(message);
                break;
            default:
                Util.messageBar(this, getString(R.string.serviceNotAvailable));
                break;
        }
    }

    /**
     * status Response from server
     */
    private void setStatusCheck(Bundle message) {
        try {


            Util.LoggingQueue(AdminActivity.this, "AdminActivity", "setStatusCheck() message = "+message);

            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BaseDto base = gson.fromJson(response, BaseDto.class);
            if (base.getStatusCode() == 0) {
                Util.messageBar(this, getString(R.string.successInUpdate));
            } else {
                Util.messageBar(this, getString(R.string.connectionError));
            }





        } catch (Exception e) {
            Util.messageBar(this, getString(R.string.connectionError));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {}
        try {
            if ((locationReceivedDialog != null) && locationReceivedDialog.isShowing()) {
                locationReceivedDialog.dismiss();
            }
        } catch (final Exception e) {}
        finally {
            locationReceivedDialog = null;
        }
        try {
            if ((logoutDialog != null) && logoutDialog.isShowing()) {
                logoutDialog.dismiss();
            }
        } catch (final Exception e) {}
        finally {
            logoutDialog = null;
        }
    }

}
