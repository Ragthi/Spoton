package com.orsoot.spoton;


import static com.orsoot.spoton.MainActivity.myCurrentLocation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class sendMessages {
    private String Location = "";
    private String Lat = "";
    private String Lng = "";
    private Context context = null;

    public sendMessages(Context contextReceived) {
        context = contextReceived;

        AppSettings appSettings = new AppSettings(context);
        getAddress(null);

        // Get preferences and send message
        String prefMessage = appSettings.getPrefs("message");
        if (prefMessage.compareTo("") == 0)
            prefMessage = "Someone else from this address is trying to look in to your phone.";
        String message = prefMessage + "\n" + "Location Detail :" + Location;


        for (int i = 1; i < 6; i++) {
            String contactNumber = appSettings.getPrefs("contact" + i);
            if (contactNumber.compareTo("") != 0)
                sendSMS(contactNumber, message);
        }

        /*UserLocation.LocationResult locationResult = new UserLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                // Got the location!
                AppSettings appSettings = new AppSettings(context);
                getAddress(location);

                // Get preferences and send message
                String prefMessage = appSettings.getPrefs("message");
                if (prefMessage.compareTo("") == 0)
                    prefMessage = "Someone else from this address is trying to look in to your phone.";
                String message = prefMessage + "\n" + "Location Detail :" + Location;


                for (int i = 1; i < 6; i++) {
                    String contactNumber = appSettings.getPrefs("contact" + i);
                    if (contactNumber.compareTo("") != 0)
                        sendSMS(contactNumber, message);
                }

            }
        };*/
//        UserLocation myLocation = new UserLocation();
//        myLocation.getLocation(context, locationResult);
    }

    public void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(message);
            sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

        }

    }

    public void getAddress(Location location) {
        if (myCurrentLocation != null) {
            Log.d("TAG", "getAddress: "+ myCurrentLocation.toString());
            Lat = myCurrentLocation.getLatitude() + "";
            Lng = myCurrentLocation.getLongitude() + "";
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(
                        myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude(), 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    Location = listAddresses.get(0).getAddressLine(0);
//                        + "\n"
//                        + listAddresses.get(0).getAddressLine(1)
//                        + "\n"
//                        + listAddresses.get(0).getAddressLine(2)
//                        + "\n"
//                        + "[ Latitude,Longitude ] : "+"[ "+Lat+","+ Lng+" ]"
//                        + "\n"
//                        + "From : AppName "
//                        + "Company web link"
//                        +"Date :"+SimpleDateFormat.getDateInstance().format(new Date(System.currentTimeMillis()))
//                        +"Time :"+SimpleDateFormat.getTimeInstance().format(new Date(System.currentTimeMillis()))
//                +"\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
                Location = "Address fetching failed due to No Internet connection";
//                    + "[ Latitude,Longitude ] : "+"[ "+Lat+","+ Lng+" ]"
//                    + "\n"
//                    + "From : AppName "
//                    + "Company web link"
//                    +"Date :"+SimpleDateFormat.getDateInstance().format(new Date(System.currentTimeMillis()))
//                    +"Time :"+SimpleDateFormat.getTimeInstance().format(new Date(System.currentTimeMillis()))
//                    +"\n";
            }
        }
    }
}
