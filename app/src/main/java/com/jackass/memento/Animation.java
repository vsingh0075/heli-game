package com.jackass.memento;

import android.graphics.Bitmap;

/**
 * Created by Vikrant on 20-10-2015.
 */
public class Animation {

    private Bitmap[] frames;
    private int currentFrame;
    private long startTime;
    private long delay;
    private boolean playedOnce;


    public Animation() {

    }

    public void setFrames(Bitmap[] image) {
        this.frames = image;
        currentFrame = 0;
        startTime = System.nanoTime();
    }

    public void setDelay(long d) {
        delay = d;
    }

    public void setFrame(int i) {
        currentFrame = i;
    }

    public void update() {
        long elapsed = (System.nanoTime()-startTime)/1000000;
        if (elapsed > delay) {
            currentFrame++;
            startTime = System.nanoTime();
        }
        if (currentFrame == frames.length) {
            playedOnce = true ;
            currentFrame = 0;
        }

    }

    public Bitmap getImage() {
        return frames[currentFrame];
    }

    public boolean isPlayedOnce() {
        return playedOnce;
    }
}
