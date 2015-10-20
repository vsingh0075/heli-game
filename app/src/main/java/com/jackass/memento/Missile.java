package com.jackass.memento;

import android.graphics.*;

import java.util.Random;

/**
 * Created by Vikrant on 20-10-2015.
 */
public class Missile extends GameObject {

    private int score;
    private Bitmap spritesheet;
    private int speed;
    private Random rand = new Random();
    private Animation animation = new Animation();

    public Missile(Bitmap bmp, int x, int y, int w, int h, int s, int numFrames) {
        super.x = x;
        super.y = y;
        height = h;
        width = w;
        score = s ;
        speed = 7 + (int) (rand.nextDouble()*score/30.0d);

        if (speed >= 40) {
            speed = 40;
        }

        spritesheet = bmp;
        Bitmap[] image = new Bitmap[numFrames];

        for (int i = 0; i < image.length ; i++) {
            image[i] = Bitmap.createBitmap(spritesheet,0,i*height,width,height);
        }

        animation.setFrames(image);
        animation.setDelay(100-speed);

    }

    public void update() {
        x -= speed ;
        animation.update();
    }

    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(animation.getImage(),x,y,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getWidth() {
        return width - 10;
    }

}
