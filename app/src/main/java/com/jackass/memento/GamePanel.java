package com.jackass.memento;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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

    private int bestScore;
    private long smokeStartTime ;
    private long missileStart;
    private long missileElapsed;
    private ArrayList<Missile> missiles;
    private ArrayList<SmokePuff> smokePuffs;
    private ArrayList<BotBorder> botBorders;
    private ArrayList<TopBorder> topBorders;
    private MainThread thread;
    private Background bg;
    private Player player;
    private Random rand;
    private Explosion explosion;

    private long startReset;
    private boolean reset;
    private boolean disappear;
    private boolean started;

    private boolean newGameCreated;
    private boolean topDown = true;
    private boolean botDown = true;
    private int maxBorderHeight;
    private int minBorderHeight;
    private int progressDenom = 20;

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
        topBorders = new ArrayList<>();
        botBorders = new ArrayList<>();

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
        boolean retry = true;
        int cnt = 0;
        while (retry && cnt < 1000) {
            cnt++;
            try {
                thread.join();
                thread.setRunning(false);
                retry = false;
                thread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!player.getPlaying() && newGameCreated && reset) {
                player.setPlaying(true);
                player.setUp(true);
            }
            if (player.getPlaying()) {
                if (!started)
                    started = true;
                reset = false;
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

            if(topBorders.isEmpty()) {
                player.setPlaying(false);
                return;
            }

            maxBorderHeight = 30 + player.getScore()/progressDenom;
            if (maxBorderHeight > HEIGHT/4) {
                maxBorderHeight = HEIGHT/4;
            }
            minBorderHeight = 5 + player.getScore()/progressDenom;

            for (int i = 0; i < topBorders.size() ; i++) {
                if(collision(topBorders.get(i),player)) {
                    player.setPlaying(false);
                }
            }

            if (player.getY() > HEIGHT) {
                player.setPlaying(false);
            }
//            for (int i = 0; i < botBorders.size() ; i++) {
//                if(collision(botBorders.get(i),player)) {
//                    System.out.println("GHUS JAA JAMEEN MEIN !!! XD XD LOLXD");
//                    player.setPlaying(false);
//                }
//            }


            this.updateTopBorder();
//            this.updateBottomBorder();

            missileElapsed = (System.nanoTime()-missileStart)/1000000;
            if (missileElapsed > (2000 - player.getScore())/4) {
                if (missiles.size() == 0) {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10,HEIGHT/2,45,15,player.getScore(),13));
                } else {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10,(int)((rand.nextDouble()*((HEIGHT)-maxBorderHeight*2))+maxBorderHeight),45,15,player.getScore(),13));
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
        } else {
            player.resetDY();
            if (!reset) {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                disappear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosion),
                        player.getX(),player.getY()-30,100,100,25);
            }

            explosion.update();
            long resetElapsed = (System.nanoTime()-startReset)/1000000;
            if (resetElapsed > 2500 && !newGameCreated) {
                newGame();
            }
        }
    }

    private boolean collision(GameObject missile, GameObject player) {
        if (Rect.intersects(missile.getRectangle(), player.getRectangle())) {
            disappear = true;
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        //super.draw(canvas);
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

            for (TopBorder tb : topBorders) {
                tb.draw(canvas);
            }

//            for (BotBorder bb : botBorders) {
//                bb.draw(canvas);
//            }
            if(started) {
                explosion.draw(canvas);
            }
            canvas.restoreToCount(save);
            drawText(canvas);
        }
    }



    public void updateTopBorder() {

        if (player.getScore() % 50 == 0) {
            topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                    topBorders.get(topBorders.size()-1).getX()+20,0,
                    (int)((rand.nextDouble()*(maxBorderHeight))+1)));
        }

        for (int i = 0; i < topBorders.size() ; i++) {
            topBorders.get(i).update();
            if (topBorders.get(i).getX() < -20) {
                topBorders.remove(i);

                if(topBorders.get(topBorders.size()-1).getHeight() >= maxBorderHeight) {
                    topDown = false;
                }
                if(topBorders.get(topBorders.size()-1).getHeight() <= minBorderHeight) {
                    topDown = true;
                }

                if(topDown) {
                    topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                            topBorders.get(topBorders.size()-1).getX()+20,0,topBorders.get(topBorders.size()-1).getHeight()+1));
                } else {
                    topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                            topBorders.get(topBorders.size()-1).getX()+20,0,topBorders.get(topBorders.size()-1).getHeight()-1));
                }
            }
        }

    }

//    public void updateBottomBorder() {
//        if (player.getScore() % 40 == 0) {
//            botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
//                    botBorders.get(botBorders.size()-1).getX()+20,
//                    (int)((rand.nextDouble()*(maxBorderHeight)+(HEIGHT-maxBorderHeight)))));
//        }
//
//        for (int i = 0; i < botBorders.size() ; i++) {
//            botBorders.get(i).update();
//
//            if (botBorders.get(i).getX() < -20) {
//                botBorders.remove(i);
//                if (botBorders.get(botBorders.size() - 1).getY() <= HEIGHT - maxBorderHeight) {
//                    botDown = true;
//                }
//                if (botBorders.get(botBorders.size() - 1).getY() >= HEIGHT - minBorderHeight) {
//                    botDown = false;
//                }
//
//                if (botDown) {
//                    botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
//                            botBorders.get(botBorders.size() - 1).getX() + 20, botBorders.get(botBorders.size() - 1).getY() + 1));
//                } else {
//                    botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
//                            botBorders.get(botBorders.size() - 1).getX() + 20, botBorders.get(botBorders.size() - 1).getY() - 1));
//                }
//            }
//        }
//    }

    public void newGame() {
//        botBorders.clear();
        disappear = false;

        topBorders.clear();
        missiles.clear();
        smokePuffs.clear();

        minBorderHeight = 5;
        maxBorderHeight = 30;

        if (player.getScore()*3 > bestScore) {
            bestScore = player.getScore();
        }

        player.resetDY();
        player.resetScore();
        player.setY(HEIGHT/2);

        for (int i=0 ; i*20 < WIDTH+40 ; i++) {
            if (i==0) {
                topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                        i*20,0,10));

//                botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
//                        i*20,HEIGHT-minBorderHeight));
            } else {
                topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                        i*20,0,topBorders.get(i-1).getHeight()+1));

//                botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
//                        i*20,botBorders.get(i-1).getY()-1));
            }
        }

        newGameCreated = true;
    }

    private void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("Distance : " + (player.getScore()*3), 10, 30, paint);
        canvas.drawText("Best : " + bestScore, WIDTH-215, 30, paint);

        if(!player.getPlaying() && newGameCreated && reset) {
            Paint p = new Paint();
            p.setTextSize(40);
            p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            canvas.drawText("PRESS TO START", WIDTH/2 - 50, HEIGHT/2 , p);
            p.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH/2-50, HEIGHT/2+20,p);
            canvas.drawText("RELEASE TO GO DOWN",WIDTH/2-50,HEIGHT/2+40,p);
        }
    }
}
