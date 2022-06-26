package com.orsoot.spoton;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "MainActivity";
    public static Location myCurrentLocation;
    public static Context context;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 7;
    private static final int PERMISSION_REQUEST_CODE_SMS = 1;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 2;

    AppSettings settings;

    // Android widgets
    TextView unlockCountText;

    SeekBar AttemptsCountBar;

//    ToggleButton systemAdminSwitch;

    SwitchCompat systemAdminSwitch;
    TextView deviceAdminStatusText;
    EditText[] contactText = new EditText[3];
    EditText contactsMessageText;
    Button contactsUpdateButton;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        // Read settings
        settings = new AppSettings(getApplicationContext());


        // Assign widgets -- Assigns widgets to widget variables
        AssignWidgets();

        // Status
        CheckStatusValues();

        // Settings
        layoutValuesFromSettings();

        getMyCurrentLocation();
    }

    private void getMyCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.d(TAG, "onSuccess: location: "+ location.toString());
                            myCurrentLocation = location;
                        } else
                            Log.d(TAG, "onSuccess: location is null");
                    }
                });


    }

    @Override
    protected void onResume() {
        super.onResume();
//        CheckStatusValues();

    }

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.CAMERA
        };

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Allow location ")
                        .setMessage("Click on OK button to continue")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Allow SMS ")
                        .setMessage("Click on OK button to continue")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.SEND_SMS},
                                        PERMISSION_REQUEST_CODE_SMS);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        PERMISSION_REQUEST_CODE_SMS);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Allow Camera")
                        .setMessage("Click on OK button to continue")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        PERMISSION_REQUEST_CODE_CAMERA);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CODE_CAMERA);
            }
            return false;
        } else {
            return true;
        }
    }

    public void layoutValuesFromSettings() {
        // Setting values to existing preferences
        int attemptsCount = settings.getAttemptsCount();

        unlockCountText.setText("failed attempts before capture : " + attemptsCount);

        AttemptsCountBar.setProgress(attemptsCount - 1);// -1 because it starts
        // from 0
        AttemptsCountBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // Contacts and message
        for (int i = 1; i <= 3; i++) {
            String contactNumber = settings.getPrefs("contact" + i);
            if (contactNumber.compareTo("") != 0)
                contactText[i - 1].setText(contactNumber);
        }
        String contactsMessage = settings.getPrefs("message");
        if (contactsMessage.compareTo("") != 0)
            contactsMessageText.setText(contactsMessage);

        contactsUpdateButton.setOnClickListener(contactsUpdateButtonListener);

    }

    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    public void CheckStatusValues() {
        // Device Admin check //
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, DeviceAdmnRcvr.class);
        boolean isActive = devicePolicyManager.isAdminActive(componentName);

        if (isActive) {
            systemAdminSwitch.setChecked(true);
            deviceAdminStatusText.setText("Device admin enabled!");
        } else {
            systemAdminSwitch.setChecked(false);
            deviceAdminStatusText.setText("Device admin not enabled");
        }
        systemAdminSwitch.setOnCheckedChangeListener(systemAdminSwitchChangeListener);

    }

    private void AssignWidgets() {
        unlockCountText = findViewById(R.id.unlockCountText);
        AttemptsCountBar = findViewById(R.id.failedAttemptsCountBar);
        systemAdminSwitch = findViewById(R.id.deviceAdminSwitch);
        deviceAdminStatusText = findViewById(R.id.deviceAdminStatusText);
        contactText[0] = findViewById(R.id.contact1);
        contactText[1] = findViewById(R.id.contact2);
        contactText[2] = findViewById(R.id.contact3);
        contactsMessageText = findViewById(R.id.contactsMessage);
        contactsUpdateButton = findViewById(R.id.contactsButton);
    }

    private final OnClickListener contactsUpdateButtonListener = new OnClickListener() {

        @Override
        public void onClick(View dropboxAuthButton) {
            /*if(!checkLocationPermission()){
                Toast.makeText(MainActivity.this, "Please allow location permission", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!checkSmsPermission()){
                Toast.makeText(MainActivity.this, "Please allow SMS permission", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!checkCameraPermission()){
                Toast.makeText(MainActivity.this, "Please allow Camera permission", Toast.LENGTH_SHORT).show();
                return;
            }*/
            if (!hasPermissions(context, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);
            }
            String[] contactDetails = new String[4];
            contactDetails[0] = contactsMessageText.getText().toString();
            for (int i = 1; i <=3; i++) {
                contactDetails[i] = contactText[i - 1].getText().toString().trim();
            }
            settings.savePrefs(contactDetails);
            Toast.makeText(MainActivity.this, "Values Saved", Toast.LENGTH_SHORT).show();
        }
    };

    private final OnCheckedChangeListener systemAdminSwitchChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton adminStatusSwitch, boolean checked) {
//            adminStatusSwitch.setChecked(value);
            if (checked) {
                systemAdminSwitch.setChecked(true);
                deviceAdminStatusText.setText("Device admin enabled!");
                openAdminIntent();
            } else {
                systemAdminSwitch.setChecked(false);
                deviceAdminStatusText.setText("Device admin disabled!");
                devicePolicyManager.removeActiveAdmin(componentName);
            }
        }
    };

    private final OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar AttemptsCountBar) {
            int progressValue = AttemptsCountBar.getProgress() + 1;

            TextView unlockCountText = findViewById(R.id.unlockCountText);
            unlockCountText.setText("Password Wrong Attempt Count : "+ progressValue);

            // Saving settings
            settings = new AppSettings(getApplicationContext());
            settings.saveAttemptsCount(progressValue);
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(SeekBar AttemptsCountBar, int value,
                                      boolean arg2) {

            TextView unlockCountText = findViewById(R.id.unlockCountText);
            unlockCountText.setText("Password Wrong Attempt Count : "+ (value + 1));


        }
    };

    public void openAdminIntent() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        ComponentName deviceAdminComponentName = new ComponentName(this, DeviceAdmnRcvr.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "We need this to monitor failed unlock attempts only");
        startActivityForResult(intent, 5);
    }

}