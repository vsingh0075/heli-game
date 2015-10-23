package com.jackass.memento;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Vikrant on 23-10-2015.
 */
public class Explosion {
    private int x;
    private int y;
    private int height;
    private int width;
    private int row;
    private Animation animation = new Animation();
    private Bitmap spriteSheet ;
    private Bitmap[] image;

    public Explosion(Bitmap bmp, int x, int y, int w, int h, int numFrames) {
        image = new Bitmap[numFrames];
        this.x = x;
        this.y = y;
        width = w;
        height = h;
        spriteSheet = bmp ;

        for (int i = 0; i < image.length ; i++) {
            if (i % 5 == 0 && i > 0) {
                row++;
            }
            image[i] = Bitmap.createBitmap(bmp,(i-(5*row))*width, row*height, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(10);
    }

    public void draw(Canvas canvas) {
        if(!animation.isPlayedOnce()) {
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }
    }

    public void update() {
        if (!animation.isPlayedOnce()) {
            animation.update();
        }
    }

    public int getHeight() {
        return height;
    }
}
