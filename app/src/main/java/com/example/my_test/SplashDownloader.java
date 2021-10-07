package com.example.my_test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SplashDownloader extends Thread implements MediaPlayer.OnCompletionListener {
    View my_view = null;
    boolean tr = false;
    MediaPlayer m_player = null;

    public SplashDownloader(Context cnt, View view, int id_music, int id_pic) {
        my_view = view;
        m_player = MediaPlayer.create(cnt, id_music);
        m_player.setOnCompletionListener(this);

        if(view != null) {
            Bitmap pic = BitmapFactory.decodeResource(cnt.getResources(), id_pic);
            Point vSize = new Point();
            WindowManager vWindow = ((Activity)cnt).getWindowManager();
            Display vDisplay = vWindow.getDefaultDisplay();
            vDisplay.getSize(vSize);
            Bitmap scaled_pic = Bitmap.createScaledBitmap(pic, vSize.x, vSize.y, true);
            ((ImageView)view).setImageBitmap(scaled_pic);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        m_player.start();
    }

    public void run() {
        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        if (my_view != null) {
                            my_view.animate().scaleX(1.7f);
                            my_view.animate().scaleY(1.9f);
                            my_view.animate().rotation(0);
                            m_player.start();
                            doItAgain();
                        }
                    }
                });

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                new Handler(Looper.getMainLooper()).post(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (m_player != null)
                                    m_player.stop();
                                if (my_view != null)
                                    my_view.animate().cancel();
                            }
                        });
                break;
            }
        }
    }

    private void doItAgain() {
        if (!tr) {
            float sc = (float)randInt(2,6);
            my_view.animate().setDuration(randInt(2000,10000));
            my_view.animate().scaleX(sc);
            my_view.animate().scaleY(sc);
            my_view.animate().rotationX(randInt(-6,6));
            my_view.animate().rotationY(randInt(-6,6));
            my_view.animate().rotation(randInt(-45,45));
            my_view.animate().translationX(randInt(100,200));
            my_view.animate().translationY(randInt(500,1500));
            tr = true;
        } else {
            float sc = ((float)randInt(2,9)/10.f) + 1.f;
            my_view.animate().scaleX(sc);
            my_view.animate().scaleY(sc);
            my_view.animate().rotationX(randInt(-1, 1));
            my_view.animate().rotationY(randInt(-1, 1));
            my_view.animate().rotation(randInt(-3, 3));
            my_view.animate().translationX(0);
            my_view.animate().translationY(0);
            tr = false;
        }
        my_view.animate().withEndAction(new Runnable() {
            @Override
            public void run() {
                doItAgain();
            }
        });
    }

    public void stopIt() {
        synchronized(this) {
            if (this.isAlive()) {
                this.interrupt();
            }

            try {
                this.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static int randInt(int min, int max) {
        final boolean typ = false;

        if(typ) {
            Random rand = new Random();
            int randomNum = rand.nextInt((max - min) + 1) + min;
            return randomNum;
        } else {
            return ThreadLocalRandom.current().nextInt(min, max + 1);
        }
    }
};