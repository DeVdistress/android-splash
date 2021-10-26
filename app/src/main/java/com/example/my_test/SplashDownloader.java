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
    Point v_size = null;

    public SplashDownloader(Context cnt, View view, int id_music, int id_pic) {
        my_view = view;
        m_player = MediaPlayer.create(cnt, id_music);
        m_player.setOnCompletionListener(this);

        if(view != null) {
            Bitmap pic = BitmapFactory.decodeResource(cnt.getResources(), id_pic);
            final double best_aspect_ratio = (double)(pic.getWidth())/(double)(pic.getHeight());
            v_size = new Point();
            WindowManager vWindow = ((Activity)cnt).getWindowManager();
            Display vDisplay = vWindow.getDefaultDisplay();
            vDisplay.getRealSize(v_size);
            if (best_aspect_ratio < v_size.x/v_size.y) {
                // if needed max
                // v_size.y = (int)(v_size.x/best_aspect_ratio);
                // if needed min
                v_size.x = (int)(best_aspect_ratio * v_size.y);
            } else if (best_aspect_ratio > v_size.x/v_size.y) {
                // if needed max
                // v_size.x = (int)(best_aspect_ratio * v_size.y);
                // if needed min
                v_size.y = (int)(v_size.x/best_aspect_ratio);
            }
            Bitmap scaled_pic = Bitmap.createScaledBitmap(pic, v_size.x, v_size.y, true);
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
                            my_view.animate().scaleX(1.f);
                            my_view.animate().scaleY(1.f);
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
        float sc = ((float) randInt(0, 10))/100.0f + 1.0f;

        my_view.animate().setDuration(randInt(8000, 10000));
        my_view.animate().scaleX(sc);
        my_view.animate().scaleY(sc);
        my_view.animate().rotationX(randInt(-1, 1));
        my_view.animate().rotationY(randInt(-1, 1));
        my_view.animate().rotation(randInt(-1, 1));
        my_view.animate().translationX(randInt(-1, 1));
        my_view.animate().translationY(randInt(-1, 1));

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