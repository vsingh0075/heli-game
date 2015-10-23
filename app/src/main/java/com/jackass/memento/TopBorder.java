package com.jackass.memento;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Vikrant on 21-10-2015.
 */
public class TopBorder extends GameObject {

    private Bitmap image;

    TopBorder(Bitmap bmp, int x, int y, int h) {
        height = h;
        width = 20;
        image = Bitmap.createBitmap(bmp,0,0,width,height);
        super.x = x;
        super.y = y;
        dx = GamePanel.MOVESPEED;
    }

    public void update() {
        x += dx;
    }

    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(image,x,y,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
