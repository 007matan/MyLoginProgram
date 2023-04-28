package com.example.myloginprogram;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

public class DirectionDetector {

    public interface CallBack_direction {
        void updateUI_direction();
    }


    private SensorManager sensorManager;
    private Sensor sensorAcceleration;
    private Sensor sensorMagneticField;

    private CallBack_direction callBack_steps;
    private float[] floatGravity = new float[3];
    private float[] floatGeoMagnetic = new float[3];
    private float[] floatOrientation = new float[3];
    private float[] floatRotationMatrix = new float[9];

    public DirectionDetector(Context context, CallBack_direction callBack_steps) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        sensorAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        this.callBack_steps = callBack_steps;
    }

    SensorEventListener sensorEventListenerAccelerometer = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            floatGravity = event.values;

            SensorManager.getRotationMatrix(floatRotationMatrix,null, floatGravity, floatGeoMagnetic);
            SensorManager.getOrientation(floatRotationMatrix, floatOrientation);
            if(floatOrientation[0]>-0.5 && floatOrientation[0]<0.5 && floatOrientation[0]!=0.0) {
                Log.d("pttt", "onSensorChanged1: "+floatOrientation[0]);
                //Toast.makeText(MainActivity.this, "validatePasswordByDirection: ",Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "valid password sensors", Toast.LENGTH_SHORT).show();
                //main_LL_redScreen.setVisibility(View.VISIBLE);
                callBack_steps.updateUI_direction();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    SensorEventListener sensorEventListenerMagneticField = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            floatGeoMagnetic = event.values;

            SensorManager.getRotationMatrix(floatRotationMatrix,null, floatGravity, floatGeoMagnetic);
            SensorManager.getOrientation(floatRotationMatrix, floatOrientation);
            if(floatOrientation[0]>-0.1 && floatOrientation[0]<0.1 && floatOrientation[0]!=0.0) {
                Log.d("pttt", "onSensorChanged2: "+floatOrientation[0]);
                //Toast.makeText(MainActivity.this, "validatePasswordByDirection: ",Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "valid password sensors", Toast.LENGTH_SHORT).show();
                //main_LL_redScreen.setVisibility(View.VISIBLE);
                callBack_steps.updateUI_direction();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void start() {
        sensorManager.registerListener(sensorEventListenerAccelerometer, sensorAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventListenerMagneticField, sensorMagneticField, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * unregister to the sensors
     */
    public void stop() {
        sensorManager.unregisterListener(sensorEventListenerAccelerometer);
        sensorManager.unregisterListener(sensorEventListenerMagneticField);
    }
}
