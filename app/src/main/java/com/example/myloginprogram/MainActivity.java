package com.example.myloginprogram;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Button main_BTN_enter;
    private Button main_BTN_textPass;
    private LottieAnimationView animation_view;
    private LottieAnimationView animation_view_2;
    private LinearLayout main_LL_passOption;
    private  LinearLayout main_LL_passOption2;
    private Button main_MTR_BTN_textOption;
    private Button main_MTR_BTN_sensorsOption;
    private Button main_MTR_BTN_backOption;

    private EditText main_EDT_TXT_textPass;


    private IntentFilter ifilter;

    private Intent batteryStatus;

    private RelativeLayout nisayon;

    private DirectionDetector directionDetector;

    private LightDetector lightDetector;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;

    private final Handler handler = new Handler();
    private final int delay = 5000; // 1000 milliseconds == 1 second

    private final Handler handler2 = new Handler();
    private final int delay2 = 500; // 1000 milliseconds == 1 second
    private static boolean pass = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        setContentView(R.layout.activity_main);


        findViews();
        initViews();


        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = MainActivity.this.registerReceiver(null, ifilter);

        directionDetector = new DirectionDetector(this, callBack_direction);

        lightDetector = new LightDetector(this, callBack_light);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);



    }

    private void validatePasswordByWIFIName(){
        if(!pass) {
            wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                String ssid = wifiInfo.getSSID().toString();

                //findSSIDForWifiInfo(wifiManager, wifiInfo);
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

                switch (permissionCheck(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    case 1:
                        if (ssid.contains("yafa")) {
                            Log.d("pttq", "WIFI name: " + ssid);
                            Toast.makeText(MainActivity.this, "validate Password By WIFI name: " + ssid, Toast.LENGTH_SHORT).show();
                            updateUI();
                        }
                    case 2:
                        validatePasswordByWIFIName();
                    case 3:
                        Toast.makeText(MainActivity.this, "You need to give location permission manually", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
    /*
    public String findSSIDForWifiInfo(WifiManager manager, WifiInfo wifiInfo) {

        requestPermissionLauncher.launch(Manifest.permission.CHANGE_WIFI_STATE);

        switch(permissionCheck(Manifest.permission.CHANGE_WIFI_STATE)){
            case 1:
                @SuppressLint("MissingPermission") List<WifiConfiguration> listOfConfigurations = manager.getConfiguredNetworks();

                for (int index = 0; index < listOfConfigurations.size(); index++) {
                    WifiConfiguration configuration = listOfConfigurations.get(index);
                    if (configuration.networkId == wifiInfo.getNetworkId()) {
                        return configuration.SSID;
                    }
                }

        }
        return null;

    }
     */
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            });
    private int permissionCheck(String prm) {
        //checking permission
        boolean granted = ContextCompat.checkSelfPermission(this, prm) == PackageManager.PERMISSION_GRANTED;
        if(granted){
            return 1;
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, prm)){
                Toast.makeText(MainActivity.this, "Be aware, its the last time you have this option from here", Toast.LENGTH_LONG).show();
                return 2;
            }
            return  3;
        }
    }

    void validatePasswordByCharging(){
        if(!pass) {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            if (isCharging == true) {
                Log.d("ptte", "battery status: " + isCharging);
                Toast.makeText(MainActivity.this, "validate Password By Is Charging Parameter: ", Toast.LENGTH_SHORT).show();
                updateUI();
            }
        }
    }
    private void validatePasswordByInputText(){
        if(!pass) {
            String _pass = main_EDT_TXT_textPass.getText().toString();
            int _batteryPercentage;
            try {
                _batteryPercentage = Integer.parseInt(_pass);
            } catch (Exception e) {
                _batteryPercentage = 0;
            }

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level * 100 / (float) scale;
            if (batteryPct == _batteryPercentage) {
                Log.d("pttr", "validatePasswordByInputText: ");
                Toast.makeText(MainActivity.this, "validate Password By Battery Level Parameter: ", Toast.LENGTH_SHORT).show();
                updateUI();
            }
        }
    }

    private DirectionDetector.CallBack_direction callBack_direction = new DirectionDetector.CallBack_direction() {
        @Override
        public void updateUI_direction() {
            updateUI();
            Toast.makeText(MainActivity.this, "validate Password By Direction Parameter: ",Toast.LENGTH_SHORT).show();
        }
    };
    private LightDetector.CallBack_light callBack_light = new LightDetector.CallBack_light() {
        @Override
        public void updateUI_light() {
            updateUI();
            Toast.makeText(MainActivity.this, "validate Password By Light Parameter: ",Toast.LENGTH_SHORT).show();
        }
    };

    private void validatePasswordBySensors() {
        directionDetector.start();
        lightDetector.start();
    }

    private void updateUI(){
        nisayon.setBackgroundColor(Color.rgb(17, 17, 17));
        animation_view_2.setVisibility(View.VISIBLE);
        main_LL_passOption.setVisibility(View.INVISIBLE);
        directionDetector.stop();
        lightDetector.stop();
        pass = true;
    }

    private void findViews() {
        main_BTN_enter = findViewById(R.id.main_BTN_enter);
        animation_view = findViewById(R.id.animation_view);
        main_LL_passOption = findViewById(R.id.main_LL_passOption);
        main_MTR_BTN_textOption = findViewById(R.id.main_MTR_BTN_textOption);
        main_MTR_BTN_sensorsOption = findViewById(R.id.main_MTR_BTN_sensorsOption);
        main_MTR_BTN_backOption = findViewById(R.id.main_MTR_BTN_backOption);
        main_EDT_TXT_textPass = findViewById(R.id.main_EDT_TXT_textPass);
        main_BTN_textPass = findViewById(R.id.main_BTN_textPass);
        main_LL_passOption2 = findViewById(R.id.main_LL_passOption2);
        animation_view_2 = findViewById(R.id.animation_view_2);
        nisayon = findViewById(R.id.nisayon);
    }

    private void initViews() {
        main_BTN_enter.setOnClickListener(v -> openPassOptionsLL());
        main_MTR_BTN_backOption.setOnClickListener(v->removePassOptionLL());
        main_MTR_BTN_textOption.setOnClickListener(v->inputTextOption());
        main_MTR_BTN_sensorsOption.setOnClickListener(v->validatePasswordBySensors());
        main_BTN_textPass.setOnClickListener(v -> validatePasswordByInputText());//need to call rapidly like rows 244-245
        //in inputTextOption
    }

    private void inputTextOption() {
        if(main_LL_passOption2.getVisibility()==View.INVISIBLE) {
            main_LL_passOption2.setVisibility(View.VISIBLE);
            nisayon.setBackgroundColor(Color.rgb(232, 154, 38));
            handler2.postDelayed(new Runnable() {
                public void run() {
                    Log.d("ptttm", "run: m "+System.currentTimeMillis());
                    validatePasswordByInputText();
                    handler2.postDelayed(this, delay2);
                }
            }, delay2);
        }
        else
            main_LL_passOption2.setVisibility(View.INVISIBLE);
    }

    private void openPassOptionsLL() {
        main_LL_passOption.setVisibility(View.VISIBLE);
        nisayon.setBackgroundColor(Color.rgb(232, 154, 38));
        animation_view.setVisibility(View.INVISIBLE);
        main_BTN_enter.setVisibility(View.INVISIBLE);
        validatePasswordByCharging(); // need to be reapeted
        validatePasswordByWIFIName(); //need to be call rapidly
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.d("pttto", "run: O "+System.currentTimeMillis());
                validatePasswordByCharging(); // need to be reapeted
                validatePasswordByWIFIName(); //need to be call rapidly
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void removePassOptionLL() {
        main_LL_passOption.setVisibility(View.INVISIBLE);
        animation_view.setVisibility(View.VISIBLE);
        main_BTN_enter.setVisibility(View.VISIBLE);
        main_LL_passOption2.setVisibility(View.INVISIBLE);
        nisayon.setBackgroundColor(Color.rgb(252, 250, 247));
    }


     /*
    private MCT5.CycleTicker tickerCycle = new MCT5.CycleTicker() {
        @Override
        public void periodic(int repeatsRemaining) {
            validatePasswordByInputText();
        }

        @Override
        public void done() {}
    };
     */

    /*
    private boolean validatePasswordBySensorsPermission() {
        //check if sensor enabled
        boolean isLocationServiceOn = isLocationEnabled(this);
        if(isLocationServiceOn) {
            Toast.makeText(MainActivity.this, "location Service On", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(MainActivity.this, "location Service Off", Toast.LENGTH_SHORT).show();

        int isGotPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (isGotPermission == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "have Permission for coarse location", Toast.LENGTH_SHORT).show();
            //
        } else {
            Toast.makeText(MainActivity.this, "Permission denied for coarse location", Toast.LENGTH_SHORT).show();
            //*** need to check if it is the second time
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        return false;
    }

    public static Boolean isLocationEnabled(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is a new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            // This was deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    permissionDenied();
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Toast.makeText(MainActivity.this, "You must give me permission to continue", Toast.LENGTH_SHORT).show();
                }
                validatePasswordBySensors();
            });

    private void permissionDenied() {
        Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();


        // permission denied, boo! Disable the
        // functionality that depends on this permission.
    }



    private void startRecording() {
        MCT5.get().cycle(tickerCycle, MCT5.CONTINUOUSLY_REPEATS, 2500);
    }


    private void stopRecording() {
        MCT5.get().remove(tickerCycle);
    }
*/
}