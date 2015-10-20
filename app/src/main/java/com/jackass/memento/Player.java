package com.jackass.memento;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Vector;

/**
 * Created by Vikrant on 20-10-2015.
 */
public class Player extends GameObject {

    private Bitmap spriteSheet;
    private Bitmap[] image;
    private int score;
    private boolean up;
    private boolean playing;
    private Animation animation = new Animation();
    private long startTime;

    public Player(Bitmap bmp, int w, int h, int numFrames ) {
        x = 100;
        y = GamePanel.HEIGHT/2;
        dy = 0;
        score = 0;
        height = h;
        width = w;
        image = new Bitmap[numFrames];
        spriteSheet = bmp;
        for ( int i = 0 ; i < image.length ; i++ ) {
            image[i] = Bitmap.createBitmap(spriteSheet, i*width, 0 , width, height);
        }
        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();
    }

    public void update() {
        long elapsed = (System.nanoTime()-startTime)/1000000;
        if (elapsed > 100) {
            score++;
            startTime = System.nanoTime();
        }
        animation.update();

        if (up) {
            dy -= 1;
        } else {
            dy += 1;
        }

        if (dy > 14) {
            dy = 14;
        }
        if (dy < -14) {
            dy = -14;
        }

        y += dy*2;
        dy = 0;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }

    public int getScore() {
        return score;
    }

    public boolean getPlaying() {
        return playing;
    }

    public void setPlaying(boolean b) {
        playing = b;
    }

    public void resetScore() {
        score = 0;
    }

    public void setUp(boolean b) {
        up = b;
    }
}
