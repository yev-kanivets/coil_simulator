package com.android.coil.logic;

import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;

public class Shaker implements SensorListener {
    private static final int SHAKE_THRESHOLD = 1200;

    @Nullable
    private OnShakeListener shakeListener;

    private long lastUpdate;
    private float lastX;
    private float lastY;
    private float lastZ;

    public Shaker(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
        lastUpdate = -1;
    }

    @Override
    public void onSensorChanged(int i, float[] values) {
        long curTime = System.currentTimeMillis();

        if (lastUpdate != -1) {
            if ((curTime - lastUpdate) > 100) {
                float x = values[SensorManager.DATA_X];
                float y = values[SensorManager.DATA_Y];
                float z = values[SensorManager.DATA_Z];

                long diffTime = (curTime - lastUpdate);
                float speed = Math.abs(x+y+z - lastX - lastY - lastZ) / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD && shakeListener != null) {
                    shakeListener.onShake(speed);
                }

                lastUpdate = curTime;
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        } else {
            lastUpdate = curTime;
        }
    }

    @Override
    public void onAccuracyChanged(int i, int i1) {

    }

    public void setShakeListener(@Nullable OnShakeListener shakeListener) {
        this.shakeListener = shakeListener;
    }

    public interface OnShakeListener {
        void onShake(float speed);
    }
}
