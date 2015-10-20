package com.jackass.memento;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Vikrant on 19-10-2015.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public static final int MOVESPEED = -5;

    private long smokeStartTime ;
    private long missileStart;
    private long missileElapsed;
    private ArrayList<Missile> missiles;
    private ArrayList<SmokePuff> smokePuffs;
    private MainThread thread;
    private Background bg;
    private Player player;
    private Random rand;


    public GamePanel(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bg = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.helicopter),65,25,3);
        rand = new Random();
        smokePuffs = new ArrayList<>();
        missiles = new ArrayList<>();
        smokeStartTime = System.nanoTime();
        missileStart  = smokeStartTime;
        thread = new MainThread(holder,this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            thread.join();
            thread.setRunning(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!player.getPlaying()) {
                player.setPlaying(true);
            } else {
                player.setUp(true);
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            player.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void update() {
        if (player.getPlaying()) {
            bg.update();
            player.update();

            missileElapsed = (System.nanoTime()-missileStart)/1000000;
            if (missileElapsed > (2000 - player.getScore())/4) {
                if (missiles.size() == 0) {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10,HEIGHT/2,45,15,player.getScore(),13));
                } else {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10,(int)((rand.nextDouble()*((HEIGHT)))),45,15,player.getScore(),13));
                }

                missileStart = System.nanoTime();
            }

            for (int i = 0; i < missiles.size(); i++) {
                missiles.get(i).update();
                if (collision(missiles.get(i),player)) {
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }
                if (missiles.get(i).getX() < -100) {
                    missiles.remove(i);
                }
            }

            long elapsed = (System.nanoTime()-smokeStartTime)/1000000;
            if (elapsed > 120) {
                smokePuffs.add(new SmokePuff(player.getX(),player.getY()+10));
                smokeStartTime = System.nanoTime();
            }

            for ( int i=0 ; i < smokePuffs.size() ; i++ ) {
                smokePuffs.get(i).update();
                if (smokePuffs.get(i).getX() < -10) {
                    smokePuffs.remove(i);
                }
            }
        }
    }

    private boolean collision(GameObject missile, GameObject player) {
        if (Rect.intersects(missile.getRectangle(),player.getRectangle())) {
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        final float scaleX = getWidth()/(WIDTH*1.0f);
        final float scaleY = getHeight()/(HEIGHT*1.0f);
        if(canvas != null) {
            final int save = canvas.save();
            canvas.scale(scaleX, scaleY);
            bg.draw(canvas);
            player.draw(canvas);
            for (int i=0 ; i < smokePuffs.size() ; i++ ) {
                smokePuffs.get(i).draw(canvas);
            }
            for (int i = 0; i < missiles.size() ; i++) {
                missiles.get(i).draw(canvas);
            }
            canvas.restoreToCount(save);
        }
    }
}
