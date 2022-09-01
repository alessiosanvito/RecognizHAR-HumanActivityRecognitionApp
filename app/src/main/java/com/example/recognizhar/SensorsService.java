package com.example.recognizhar;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SensorsService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorGyr;
    private Sensor mSensorLinAcc;

    private float valAccX;
    private float valAccY;
    private float valAccZ;

    private float valGyrX;
    private float valGyrY;
    private float valGyrZ;

    private float valLinAccX;
    private float valLinAccY;
    private float valLinAccZ;

    final static String MY_ACTION = "MY_ACTION";
    Intent intent = new Intent(SensorsService.MY_ACTION);

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorAcc, SensorManager.SENSOR_DELAY_FASTEST);

        mSensorGyr = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mSensorGyr, SensorManager.SENSOR_DELAY_FASTEST);

        mSensorLinAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mSensorLinAcc, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }

    public SensorsService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int sensorType = event.sensor.getType();
        float currentValueX = event.values[0];
        float currentValueY = event.values[1];
        float currentValueZ = event.values[2];

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                valAccX = currentValueX;
                valAccY = currentValueY;
                valAccZ = currentValueZ;

                intent.putExtra("misurazioni_accx", valAccX);
                intent.putExtra("misurazioni_accy", valAccY);
                intent.putExtra("misurazioni_accz", valAccZ);
                intent.putExtra("tipo_sens", sensorType);

                sendBroadcast(intent);
                break;
            case Sensor.TYPE_GYROSCOPE:
                valGyrX = currentValueX;
                valGyrY = currentValueY;
                valGyrZ = currentValueZ;

                intent.putExtra("misurazioni_gyrx", valGyrX);
                intent.putExtra("misurazioni_gyry", valGyrY);
                intent.putExtra("misurazioni_gyrz", valGyrZ);
                intent.putExtra("tipo_sens", sensorType);

                sendBroadcast(intent);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                valLinAccX = currentValueX;
                valLinAccY = currentValueY;
                valLinAccZ = currentValueZ;

                intent.putExtra("misurazioni_linaccx", valLinAccX);
                intent.putExtra("misurazioni_linaccy", valLinAccY);
                intent.putExtra("misurazioni_linaccz", valLinAccZ);
                intent.putExtra("tipo_sens", sensorType);

                sendBroadcast(intent);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}