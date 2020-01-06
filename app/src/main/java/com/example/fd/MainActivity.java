package com.example.fd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int RequestPermissionCode = 1;

    SQLiteDatabase myDB;

    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private TextView xText;
    private TextView yText;
    private TextView zText;
    private TextView accReader;
    private TextView lat;
    private TextView lnt;

    private String smsTo;

    Sensor sensor;
    private SensorManager SM;

    double sum;
    boolean min, max;
    int i, count;

    Button setContactBtn, getContactBtn;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);

        //google api client
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //Creating Sensor Manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Accelerometer Sensor
        sensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Assign TextView
        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);
        accReader = (TextView) findViewById(R.id.acreader);
        lat = (TextView) findViewById(R.id.latValueId);
        lnt = (TextView) findViewById(R.id.longValueId);


        setContactBtn = (Button)findViewById(R.id.contactSetBtn);

        setContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setContactView = new Intent(MainActivity.this,settingContact.class);
                startActivity(setContactView);
            }
        });

        getContactBtn = (Button)findViewById(R.id.showCaretakerBtn);

        getContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getContactView = new Intent(MainActivity.this,CaretakersList.class);
                startActivity(getContactView);
            }
        });
    }

    @Override
    public  void onSensorChanged(final SensorEvent event){
        xText.setText("X: " + event.values[0]);
        yText.setText("Y: " + event.values[1]);
        zText.setText("Z: " + event.values[2]);


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            //sum = Math.round(Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2)));

            sum=Math.sqrt(Math.pow(event.values[0],2)+Math.pow(event.values[1],2)+Math.pow(event.values[2],2));
            DecimalFormat precision = new DecimalFormat("0.00");
            double accRound = Double.parseDouble(precision.format(sum));
            accReader.setText("Total: " + accRound);

            if (accRound <= 2.0){
                SM.unregisterListener(this);
                Toast.makeText(this, "Fall detected", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, AlertService.class));
                showTimerDialog();
            }



//            if (sum <= 20.0){
//                min = true;
//            }
//
//            if (min == true){
//                i++;
//                if (sum >= 40.0){
//                    max = true;
//                }
//            }
//
//            if (min == true && max == true){
//                SM.unregisterListener(this);
//                Toast.makeText(this,"Suspect Fall", Toast.LENGTH_SHORT).show();
///////////////////////////////////////////////dialog/////////////////////////////////////////////
//                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
//                alertDialogBuilder.setTitle("Confirmation");
//                alertDialogBuilder.setMessage("")
//                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                final AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();
///////////////////////////////////////////////dialog/////////////////////////////////////////////
//                /////////////////////timer/////////////////////////
//                timer = new CountDownTimer(30000, 1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        int seconds = (int) (millisUntilFinished / 1000);
//                        int minutes = seconds / 60;
//                        seconds = seconds % 60;
//                        alertDialog.setMessage("Times to send message: " + String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
//                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                timer.cancel();
//                                stopService(new Intent(MainActivity.this,AlertService.class));
//                                onResume();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        sendMessage();
//                        //stopService(new Intent(MainActivity.this,AlertService.class));
//                    }
//                }.start();
//                //////////////////timer end///////////////////////
//                //sendMessage();
//                //Intent test = new Intent(MainActivity.this,Fall_test.class);
//                // startActivityForResult(test,2);
//                startService(new Intent(MainActivity.this, AlertService.class));
//
//                min = false;
//                max = false;
//
//            }
        }

    }

    private void showTimerDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Confirmation");
        alertDialogBuilder.setMessage("")
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                /////////////////////timer/////////////////////////
                timer = new CountDownTimer(30000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) (millisUntilFinished / 1000);
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        alertDialog.setMessage("Times to send message: " + String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                timer.cancel();
                                stopService(new Intent(MainActivity.this,AlertService.class));
                                onResume();
                            }
                        });
                    }

                    @Override
                    public void onFinish() {
                        sendMessage();
                        //stopService(new Intent(MainActivity.this,AlertService.class));
                    }
                }.start();
                //////////////////timer end///////////////////////
    }

    private void sendMessage(){
        String strlat = lat.getText().toString();
        String strlnt = lnt.getText().toString();
        String message = "The message sender may have been fallen, click the link to see possible location: https://maps.google.com/?q=" + strlat + strlat;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            myDB = openOrCreateDatabase(DatabaseHelper.DATABASE_NAME, MODE_PRIVATE,null);
            Cursor phoneNumbers = myDB.rawQuery("SELECT * FROM caretaker_table", null);
            if (phoneNumbers.getCount() > 0){
                for (phoneNumbers.moveToFirst(); !phoneNumbers.isAfterLast(); phoneNumbers.moveToNext()){
                    smsTo = phoneNumbers.getString(2).trim();

                    SmsManager smsManager = SmsManager.getDefault();

                    smsManager.sendTextMessage(smsTo,null,message,null,null);
                    Toast.makeText(this,"SMS was sent successfully!", Toast.LENGTH_LONG).show();
                }
            }else{
                Log.d("Number not found!", "null");
                Toast.makeText(this,"Sorry number not found to send sms!", Toast.LENGTH_LONG).show();
            }
            phoneNumbers.close();
        }else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS)){

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 0:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    sendMessage();
                    Toast.makeText(this,"Permission accepted and sms was sent!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this,"You don't have required permission!", Toast.LENGTH_LONG).show();
                }
        }
    }

    //    public void onSensorChanged(SensorEvent event) {
//        xText.setText("X: " + event.values[0]);
//        yText.setText("Y: " + event.values[1]);
//        zText.setText("Z: " + event.values[2]);
//
//        //MyTest
////        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
////            final float valueX = event.values[0];
////            final float valueY = event.values[1];
////            final float valueZ = event.values[2];
////            if (valueZ >= 9 && valueX < 2){
////                startService(new Intent(MainActivity.this, AlertService.class));
////                Toast.makeText(this, "Fall Detection!!", Toast.LENGTH_SHORT).show();
////            }else{
////                stopService(new Intent(MainActivity.this,AlertService.class));
////            }
////        }
//


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Not in use
    }

    @Override
    protected void onResume() {
        super.onResume();

        SM.registerListener(this,sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //SM.unregisterListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("MainActivity", "Map Connection Failed " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("MainActivity", "Map Connection Suspended");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions();

        }else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        lat.setText(String.valueOf(location.getLatitude()));
                        lnt.setText(String.valueOf(location.getLongitude()));
                    }
                }
            });
        }
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION}, RequestPermissionCode);
    }
}