package com.jackass.memento;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Vikrant on 21-10-2015.
 */
public class BotBorder extends GameObject {

    private Bitmap image;

    BotBorder(Bitmap bmp, int x, int y) {
        super.x = x;
        super.y = y;
        height = 200;
        width = 20;
        dx = GamePanel.MOVESPEED;
        image = Bitmap.createBitmap(bmp,0,0,width,height);
    }

    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(image,x,y,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        x += dx ;
    }
}
