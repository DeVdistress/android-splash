package com.example.my_test;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

public class SplashDownloader extends Thread implements MediaPlayer.OnCompletionListener {
    View my_view = null;
    boolean tr = false;
    MediaPlayer m_player = null;

    public SplashDownloader(Context cnt, View view, int id_music) {
        my_view = view;
        m_player = MediaPlayer.create(cnt, id_music);
        m_player.setOnCompletionListener(this);
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
            my_view.animate().setDuration(7000);
            my_view.animate().scaleX(6f);
            my_view.animate().scaleY(6f);
            my_view.animate().rotationX(6);
            my_view.animate().rotationY(6);
            my_view.animate().rotation(45);
            my_view.animate().translationX(200);
            my_view.animate().translationY(1500);
            tr = true;
        } else {
            my_view.animate().scaleX(1.1f);
            my_view.animate().scaleY(1.1f);
            my_view.animate().rotationX(-1);
            my_view.animate().rotationY(-1);
            my_view.animate().rotation(-3);
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
};