package com.android.coil.logic;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.android.coil.R;

import java.io.IOException;

public class SoundMaker {
    private MediaPlayer mp;
    private AssetFileDescriptor afd;

    public SoundMaker(Context context) {
        mp = new MediaPlayer();
        afd = context.getResources().openRawResourceFd(R.raw.tick);
    }

    public void playTick() {
        if (mp.isPlaying()) {
            return;
        }

        mp.reset();
        try {
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
