package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener
        , MediaPlayer.OnCompletionListener
        , SeekBar.OnSeekBarChangeListener {

    private Button btnPlay;
    private Button btnStop;
    private Button btnPause;
    private SeekBar seekVolume;
    private SeekBar seekProgress;
    private AudioManager audioManager;

    private MediaPlayer player;

    private TextView timeRemaining;
    private TextView songTime;
    private GridView gvList;
    public String nameSong  ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String [] musicList = {"song1", "song2"};

        player = MediaPlayer.create(this, R.raw.song);

        initViews();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //Прочитаем какая макс громкость на устройстве разрешена для аудиопотокоа
        seekVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        seekVolume.setOnSeekBarChangeListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this
                , android.R.layout.simple_list_item_1
                , musicList);

        gvList.setAdapter(adapter);

        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                nameSong = parent.getItemAtPosition(position).toString();

                Log.d("myApp", nameSong);

                play(nameSong); ;


            }
        };

        gvList.setOnItemClickListener(itemListener);

    }

    private void initViews() {

        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);
        btnPause = findViewById(R.id.btnPause);
        seekVolume = findViewById(R.id.seekVolume);
        seekProgress = findViewById(R.id.seekProgress);
        timeRemaining = findViewById(R.id.timeRemaining);
        songTime = findViewById(R.id.songTime);
        gvList = findViewById(R.id.gvList);

        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnPause.setOnClickListener(this);

        player.setOnCompletionListener(this);

        gvList = findViewById(R.id.gvList);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay: {
                play(nameSong);
                break;
            }
            case R.id.btnStop: {
                stop();
                break;
            }
            case R.id.btnPause: {
                pause();
                break;
            }
        }

    }

    private void pause() {

        player.pause();

    }

    private void stop() {

        player.stop();

        try {
            player.prepare();

            //Перемотаем песню на начало
//            player.seekTo(0);

        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    private void play(String nameSong) {

            player.start();

            seekProgress.setMax(player.getDuration());

            songTime.setText(TimeConverter(player.getDuration()));

            new ProgressTracker().execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player.isPlaying()) stop();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        stop();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

    }

        

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    public class ProgressTracker extends AsyncTask<Void, Void, Void> {



        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            seekProgress.setProgress(player.getCurrentPosition());

            Log.d("myApp", TimeConverter(player.getCurrentPosition()) + "");


        }

        @Override
        protected Void doInBackground(Void... params) {

            while (player.isPlaying()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                onProgressUpdate();

            }

            return null;

        }
    }

    public static String TimeConverter(int milliseconds) {

        int seconds = 0, sec;
        int minutes = 0;
        String Time = "";

        sec = milliseconds/1000;

            minutes = sec / 60;
            seconds = sec - minutes * 60;
            if (seconds < 10)
                Time = minutes + ":0" + seconds + "";
            else
                Time = minutes + ":" + seconds + "";

            return Time;
    }

}



