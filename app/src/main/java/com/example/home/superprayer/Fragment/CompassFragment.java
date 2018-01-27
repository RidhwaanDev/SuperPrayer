package com.example.home.superprayer.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.home.superprayer.R;

/**
 * Created by Home on 11/5/2017.
 */

public class CompassFragment extends android.support.v4.app.Fragment {


    /**
     *
     *    Author: Viacheslav Iutin
     *    From: https://github.com/iutinvg/compass
     *       modified to use private nested class
     *
     */


    private PrayerCompass mCompass;

    @Override
    public void onStart() {
        super.onStart();
        mCompass.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCompass.stop();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_compass,container,false);

        mCompass = new PrayerCompass(getActivity());
        mCompass.iv_compass = v.findViewById(R.id.compass_image);
        mCompass.tv_compass = v.findViewById(R.id.compass_tv);

        return v;
    }

    private class PrayerCompass implements SensorEventListener {
        private static final String COMPASS_TAG = "my_compass_tag";
        private SensorManager mSensorManager;
        private Sensor gSensor;
        private Sensor mSensor;


        private float[] mGrav = new float[3];
        private float[]mGeoMag = new float[3];

        private float azimuth = 0f;
        private float currentAzimuth = 0f;

        public ImageView iv_compass = null;
        public TextView tv_compass = null;

        public PrayerCompass(Context context ){
                mSensorManager =(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

                gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        public void start(){
            mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(this,gSensor,SensorManager.SENSOR_DELAY_GAME);
        }

        public void stop(){
            mSensorManager.unregisterListener(this);

        }

        private void adjustArrow(){

            if(iv_compass == null){
                Log.d(COMPASS_TAG, "   " + "comapss view is null");
                return;
            }

            Log.d(COMPASS_TAG," AZMIUTH " + "  " + azimuth + "  " + currentAzimuth +"  ");

            Animation an = new RotateAnimation(-currentAzimuth,-azimuth,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            currentAzimuth = azimuth;

            int rounded_azimuth = ((int) currentAzimuth);


            tv_compass.setText(String.valueOf(rounded_azimuth));

            if(rounded_azimuth >= 35 && rounded_azimuth <= 65){
                tv_compass.setTextColor(Color.GREEN);
            } else {
                tv_compass.setTextColor(Color.RED);
            }

            an.setDuration(500);
            an.setRepeatCount(0);
            an.setFillAfter(true);

            iv_compass.startAnimation(an);

        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
                final float alpha = 0.97f;
                synchronized (this){
                    if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                        mGrav[0] = alpha * mGrav[0] + ( 1 - alpha) * sensorEvent.values[0];
                        mGrav[1] = alpha * mGrav[1] + ( 1 - alpha) * sensorEvent.values[1];
                        mGrav[2] = alpha * mGrav[2] + ( 1 - alpha) * sensorEvent.values[2];

                    }

                    if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                        mGeoMag[0] = alpha * mGeoMag[0] + ( 1- alpha) * sensorEvent.values[0];
                        mGeoMag[1] = alpha * mGeoMag[1] + ( 1- alpha) * sensorEvent.values[1];
                        mGeoMag[2] = alpha * mGeoMag[0] + ( 1- alpha) * sensorEvent.values[2];

                    }
                    float R[] = new float[9];
                    float I[] = new float[9];
                    boolean success = SensorManager.getRotationMatrix(R, I, mGrav,
                            mGeoMag);
                    if (success) {
                        float orientation[] = new float[3];
                        SensorManager.getOrientation(R, orientation);
                        azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                        azimuth = (azimuth + 360) % 360;
                        adjustArrow();
                    }

                }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}
