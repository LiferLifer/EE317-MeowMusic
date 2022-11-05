package com.example.meowmusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {

    private MediaPlayer player;
    private Timer timer;

    public static Integer song = R.raw.song2;
    public static Integer image = R.drawable.cover;
    public static String name = "网抑云音乐";
    private String[] names = {"快乐儿歌", "世界末日", "你比从前快乐", "以父之名", "搁浅"};

    public MusicService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicControl();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MusicControl extends Binder {
        public void play() {
            try {
                player.reset();
                player = MediaPlayer.create(getApplicationContext(), song);
                player.start();
                addTimer();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void nextplay() {
            try {
                player.reset();
                song = song+1;
                Integer n = 0;
                for(String str:names)
                {
                    if(str.indexOf(name)>0){
                        n = str.indexOf(name);
                        break;
                    }
                }
                name = names[n + 1];
                image = image+1;
                player = MediaPlayer.create(getApplicationContext(), song);
                player.start();
                addTimer();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void lastplay() {
            try {
                player.reset();
                song = song-1;
                Integer n = 0;
                for(String str:names)
                {
                    if(str.indexOf(name)>0){
                        n = str.indexOf(name);
                        break;
                    }
                }
                name = names[n - 1];
                image = image-1;
                player = MediaPlayer.create(getApplicationContext(), song);
                player.start();
                addTimer();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void pausePlay() {
            player.pause();
        }

        public void continuePlay() {
            player.start();
        }

        public void stopPlay() {
            player.stop();
            player.release();
            try {
                timer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void seekTo(int progress) {
            player.seekTo(progress);
        }
    }


    public void addTimer() {
        if (timer == null) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (player == null) return;
                    int duration = player.getDuration();
                    int currentDuration = player.getCurrentPosition();

                    Message message = PlayActivity.handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("duration", duration);
                    bundle.putInt("currentDuration", currentDuration);
                    bundle.putString("name", name);
                    bundle.putInt("image", image);
                    message.setData(bundle);

                    PlayActivity.handler.sendMessage(message);

                }
            };
            timer.schedule(task, 5, 500);
        }

    }

    public static Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            Integer song = bundle.getInt("song");
            String name = bundle.getString("name");
            Integer image = bundle.getInt("image");

            MusicService.song = song;
            MusicService.name = name;
            MusicService.image = image;

        }
    };

    // 读取文件内容
    private String loadFromSDFile(String fname) {
        fname = "/" + fname;
        String result = null;
        try {
            File f = new File(Environment.getExternalStorageDirectory().getPath() + fname);
            int length = (int) f.length();
            byte[] buff = new byte[length];
            FileInputStream fin = new FileInputStream(f);
            fin.read(buff);
            fin.close();
            result = new String(buff, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MusicService.this, "FileNotFound", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

}
