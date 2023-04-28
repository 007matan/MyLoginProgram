package com.example.myloginprogram;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

public class LightDetector {

    public interface CallBack_light {
        void updateUI_light();
    }


    private SensorManager sensorManager;
    private Sensor sensorLight;

    private CallBack_light callBack_lights;


    public LightDetector(Context context, CallBack_light callBack_lights) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        this.callBack_lights = callBack_lights;
    }

    SensorEventListener sensorEventListenerLight = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.values[0] < 30){
                Log.d("ptttw", "LIGHT: " + event.values[0]);
                callBack_lights.updateUI_light();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    public void start() {
        sensorManager.registerListener(sensorEventListenerLight, sensorLight, SensorManager.SENSOR_DELAY_FASTEST);

    }

    /**
     * unregister to the sensors
     */
    public void stop() {
        sensorManager.unregisterListener(sensorEventListenerLight);
    }
}
