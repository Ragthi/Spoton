package com.orsoot.spoton;

import static com.orsoot.spoton.MainActivity.context;
import static com.orsoot.spoton.MainActivity.myCurrentLocation;

import android.annotation.SuppressLint;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.orsoot.spoton.controller.CameraController;
import com.orsoot.spoton.mail.SendMailTask;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class DeviceAdmnRcvr extends DeviceAdminReceiver {
    static final String TAG = "bishal";
    @SuppressLint("StaticFieldLeak")
    static Context OnPswdFailedContext = null;
    private final String DEBUG_TAG = "SavePhoto";
    private static int callBackTester = 0;
    private String Location = "";
    private String Lat = "";
    private String Lng = "";

    /**
     * Called when this application is approved to be a device administrator.
     */
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, "Device admin enabled", Toast.LENGTH_LONG)
                .show();
    }

    /**
     * Called when this application is no longer the device administrator.
     */
    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context, "Device admin disabled", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);

        Log.d(TAG, "onPasswordFailed: called");
        OnPswdFailedContext = context;

        // Get failed attempts from system
        DevicePolicyManager DPM = getManager(OnPswdFailedContext);
        int failCount = DPM.getCurrentFailedPasswordAttempts();
        System.out.println("Fail count : " + failCount);

        // Get failed attempts preference
        AppSettings settings = new AppSettings(OnPswdFailedContext);
        int failCountPref = settings.getAttemptsCount();

        if (failCount >= failCountPref) {
//            GetLoc();
            TakePicture();
            getAddress(null);

            System.gc();
            if (settings.getMessagingState()) {
                // Location obtained and messages sent by just instantiating
                @SuppressWarnings("unused")
                sendMessages smsWithLocation = new sendMessages(
                        OnPswdFailedContext);
                SendMail();
            }

        } else {
            System.out.println("Waiting for counts match !");
            System.out.println("Failed count : " + failCount + " Pref count : "
                    + failCountPref);
        }
    }

    private void GetLoc() {
        UserLocation.LocationResult locationResult = new UserLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                getAddress(location);
            }
        };
//        UserLocation myLocation = new UserLocation();
//        myLocation.getLocation(OnPswdFailedContext, locationResult);
    }

    private void SendMail() {
        if (Location.equals("")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Location.equals("")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AppSettings appSettings = new AppSettings(OnPswdFailedContext);
                                String email = appSettings.getPrefs("contact3");

                                /*new SendMailTask(OnPswdFailedContext).execute("appt7790@gmail.com", "App@123456", email, "Security Alert",
                                        "<h1>Find Your Phone, Someone is Trying to Look into it</h1>" +
                                                "<h2> Location= " + Location + "</h2>"

                                );*/
                                new SendMailTask(OnPswdFailedContext).execute("appt7790@gmail.com", "App@123456", email, "Security Alert",
                                        attachImage()

                                );
                            }
                        }, 4000);
                    } else {
                        AppSettings appSettings = new AppSettings(OnPswdFailedContext);
                        String email = appSettings.getPrefs("contact3");

                        new SendMailTask(OnPswdFailedContext).execute("appt7790@gmail.com", "App@123456", email, "Security Alert",
                                attachImage()

                        );
                    }
                }
            }, 2000);
        } else {
            AppSettings appSettings = new AppSettings(OnPswdFailedContext);
            String email = appSettings.getPrefs("contact3");

            new SendMailTask(OnPswdFailedContext).execute("appt7790@gmail.com", "App@123456", email, "Security Alert",
                    attachImage()

            );
        }
    }

    public void getAddress(Location location) {
        if (myCurrentLocation != null) {
            Log.d(TAG, "getAddress: location: "+ myCurrentLocation.toString());
            Lat = myCurrentLocation.getLatitude() + "";
            Lng = myCurrentLocation.getLongitude() + "";
            Geocoder geocoder = new Geocoder(OnPswdFailedContext, Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(
                        myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude(), 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    Location =
                            listAddresses.get(0).getAddressLine(0)
                                    + listAddresses.get(0).getAddressLine(1)
                                    + listAddresses.get(0).getAddressLine(2)
                                    + "\n"
                                    + "[ Latitude,Longitude ] : " + "[ " + Lat + "," + Lng + " ]"
                                    + "\n"
                                    + "From : AppName "
                                    + "Company web link"
                                    + "Date :" + SimpleDateFormat.getDateInstance().format(new java.sql.Date(System.currentTimeMillis()))
                                    + "\n"
                                    + "Time :" + SimpleDateFormat.getTimeInstance().format(new java.sql.Date(System.currentTimeMillis()))
                    ;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Location = "Address fetching failed due to No Internet connection\n"
                        + "[ Latitude,Longitude ] : " + "[ " + Lat + "," + Lng + " ]"
                        + "\n"
                        + "From : AppName "
                        + "Company web link"
                        + "Date :" + SimpleDateFormat.getDateInstance().format(new java.sql.Date(System.currentTimeMillis()))
                        + "\n"
                        + "Time :" + SimpleDateFormat.getTimeInstance().format(new Date(System.currentTimeMillis()))
                ;
            }
        }
    }

    private void TakePicture() {
        Log.d("123", "TakePicture: called!");
        CameraController cameraController = new CameraController(context);

        cameraController.openCamera();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
        }
        cameraController.takePicture();
    }

    private Multipart attachImage() {
        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

        // Create a multipart message
        Multipart multipart = new MimeMultipart();

        try {
            // Now set the actual message
            messageBodyPart.setText("<h1>Find Your Phone, Someone is Trying to Look into it</h1>" +
                    "<h2> Location= " + Location + "</h2>");
            // Set text message part
            multipart.addBodyPart(messageBodyPart);
            // Part two is attachment
            messageBodyPart = new MimeBodyPart();

            File pictureFileDir = new File(context.getExternalFilesDir("temp").toString());
            if (pictureFileDir.exists() && pictureFileDir != null) {
                String filename = pictureFileDir.getPath() + File.separator + "image.jpg";
                File mainPicture = new File(filename);
                DataSource source = new FileDataSource(mainPicture);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);
            }
        } catch (MessagingException e) {
           e.printStackTrace();
        }

        return multipart;
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);

    }
}