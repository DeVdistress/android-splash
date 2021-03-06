package com.example.my_test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity  {
    SplashDownloader splash = null;
    private static String TAG = "MY_TEST_DEVDISTRESS";
    MediaPlayer m_player = null;
    boolean was_started = false;
    ProgressBar my_pr = null;
    TextView my_text = null;
    Button btn_1 = null;
    Button btn_2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();

        showDialog("asdasdasdasdaaaaaaaddddddddddddddddddddddd");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        my_pr = findViewById(R.id.progressBar);
        my_text = findViewById(R.id.progressAsPercentage);
        btn_1 = findViewById(R.id.button_stp);
        btn_1.setOnClickListener( new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(splash == null)
                        startSplash();
                    else
                        stopSplash();
                }
            });

        btn_2 = findViewById(R.id.button_rst);
        btn_2.setOnClickListener( new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //use tne Main looper
                final String str = my_text.getText().toString().replaceAll("%", "");

                new Handler(Looper.getMainLooper()).post( new Runnable() {
                    int tik = Integer.parseInt(str);
                    @Override
                    public void run() {
                        if (tik > 100)
                            tik = 0;

                        if((float)my_pr.getMax()>0.0f && (((float)my_pr.getProgress()/(float)my_pr.getMax())*100.0f) > 48.0f)
                            my_text.setTextColor(getResources().getColor(R.color.black));
                        else
                            my_text.setTextColor(getResources().getColor(R.color.white));

                        my_pr.setProgress(tik++);
                        my_text.setText(tik + "%");
                    }
                });
            }
        }, 0, 100);

        was_started = true;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        SetFullScreenMode();
        startSplash();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        stopSplash();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
        SetFullScreenMode();
        startSplash();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
        stopSplash();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        stopSplash();
    }

    private void startSplash() {
        if (splash == null && was_started) {
            splash = new SplashDownloader(this, findViewById(R.id.imageView), R.raw.ui_intro_splash_screen, R.drawable.jurassic);
            splash.start();
            btn_1.setText("stop");
        }

    }

    private void stopSplash() {
        if (splash != null && was_started) {
            splash.stopIt();
            splash = null;
            btn_1.setText("start");
        }
    }

    public void SetFullScreenMode()
    {
        if( Build.VERSION.SDK_INT >= 15 && Build.VERSION.SDK_INT < 19 )
        {
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN );
        }
        else
        if( Build.VERSION.SDK_INT >= 19 )
        {
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        }
    }

    private void showDialog(String aMessage) {
        Log.e(TAG, aMessage);

        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( mActivity, R.style.AlertDialogDark);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this );
        alertDialogBuilder.setTitle( "Error" );
        alertDialogBuilder.setMessage( aMessage );
        //alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNegativeButton("Try again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        alertDialogBuilder.show();
    }
}