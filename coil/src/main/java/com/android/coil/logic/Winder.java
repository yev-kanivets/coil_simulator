package com.android.coil.logic;

import java.util.Timer;
import java.util.TimerTask;

public class Winder {
    private static final float WIND_FACTOR = 1000;
    private static final float WIND_DECREASE_FACTOR = 0.8f;
    private static final long UPDATE_PERIOD = 50; // in ms

    private Timer timer;

    private OnWindListener windListener;

    public void startWinding(final float speed) {
        stopWinding();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            private float curSpeed = speed;

            @Override
            public void run() {
                curSpeed *= WIND_DECREASE_FACTOR;
                float windMeters = curSpeed / WIND_FACTOR;

                if (windListener != null) {
                    windListener.onWind(windMeters);
                }

                if (windMeters < 0.5) {
                    stopWinding();
                }
            }
        }, 0, UPDATE_PERIOD);
    }

    public void stopWinding() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setWindListener(OnWindListener windListener) {
        this.windListener = windListener;
    }

    public interface OnWindListener {
        void onWind(float meters);
    }
}
