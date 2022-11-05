package com.example.meowmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

public class PlayActivity extends AppCompatActivity {
    private static ImageView iv_cover;
    private static TextView iv_name;
    private static SeekBar seekBar;
    private static TextView tv_progress, tv_total;
    private ImageButton btn_play, btn_last, btn_next, btn_exit, btn_extend;

    private boolean play = false;
    private boolean one = false;

    private ObjectAnimator animator;

    private MusicService.MusicControl control;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            control = (MusicService.MusicControl) iBinder;//实例化control。
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        init();
    }

    public void init() {
        iv_cover = findViewById(R.id.iv_cover);
        iv_name = findViewById(R.id.iv_name);
        seekBar = findViewById(R.id.seekbar);
        tv_progress = findViewById(R.id.tv_progress);
        tv_total = findViewById(R.id.tv_total);

        btn_play = findViewById(R.id.btn_play);
        btn_last = findViewById(R.id.btn_last);
        btn_next = findViewById(R.id.btn_next);
        btn_exit = findViewById(R.id.btn_exit);
        btn_extend = findViewById(R.id.btn_extend);

        btn_play.setOnClickListener(Listener);
        btn_last.setOnClickListener(Listener);
        btn_next.setOnClickListener(Listener);
        btn_exit.setOnClickListener(Listener);
        btn_extend.setOnClickListener(Listener);

        seekBarListener msbListener = new seekBarListener();
        seekBar.setOnSeekBarChangeListener(msbListener);

        animator = ObjectAnimator.ofFloat(iv_cover, "rotation", 0.0f, 360.0f);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);

        Intent mintent = new Intent(PlayActivity.this, MusicService.class);
        bindService(mintent, connection, BIND_AUTO_CREATE);//建立意图中MainActivity与MusicService两对象的服务连接
    }

    protected View.OnClickListener Listener = v -> {
        switch (v.getId()) {
            case R.id.btn_play:
                if (play) {
                    control.pausePlay();
                    animator.pause();
                    play = false;
                    btn_play.setBackgroundResource(R.drawable.play);
                } else if (!one) {
                    control.play();
                    animator.start();
                    play = true;
                    one = true;
                    btn_play.setFocusable(true);
                    btn_play.setBackgroundResource(R.drawable.pause);
                } else {
                    control.continuePlay();
                    animator.resume();
                    play = true;
                    btn_play.setFocusable(false);
                    btn_play.setBackgroundResource(R.drawable.pause);
                }
                break;
            case R.id.btn_last:
                control.lastplay();
                animator.start();
                break;
            case R.id.btn_next:
                control.nextplay();
                animator.start();
                break;
            case R.id.btn_extend:

                break;
            case R.id.btn_exit:
                finish();
                Intent intent = new Intent(PlayActivity.this, ListActivity.class);
                startActivity(intent);
                break;

        }
    };

    public static Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int duration = bundle.getInt("duration");
            int currentDuration = bundle.getInt("currentDuration");
            String name = bundle.getString("name");
            Integer image = bundle.getInt("image");

            seekBar.setMax(duration);
            seekBar.setProgress(currentDuration);
//            System.out.println(name);
//            System.out.println(image);
            iv_name.setText(name);
            iv_cover.setBackgroundResource(image);

            //显示总时长
            int minite = duration / 1000 / 60;
            int second = duration / 1000 % 60;
            String strMinite = "";
            String strSecond = "";
            if (minite < 10) {
                strMinite = "0" + minite;
            } else {
                strMinite = minite + "";
            }
            if (second < 10) {
                strSecond = "0" + second;
            } else {
                strSecond = second + "";
            }
            tv_total.setText(strMinite + ":" + strSecond);

            //显示播放时长
            minite = currentDuration / 1000 / 60;
            second = currentDuration / 1000 % 60;

            if (minite < 10) {
                strMinite = "0" + minite;
            } else {
                strMinite = minite + "";
            }
            if (second < 10) {
                strSecond = "0" + second;
            } else {
                strSecond = second + "";
            }
            tv_progress.setText(strMinite + ":" + strSecond);
        }
    };


    class seekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        //进度条行进过程的监听
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (i == seekBar.getMax()) {
                animator.pause();
            }
            if (b) {//判断是否来自用户
                control.seekTo(i);
            }
        }

        @Override
        //用户开始滑动进度条的监听
        public void onStartTrackingTouch(SeekBar seekBar) {
            control.pausePlay();
            animator.pause();
        }

        @Override
        //用户停止滑动进度条的监听
        public void onStopTrackingTouch(SeekBar seekBar) {
            control.continuePlay();
            animator.resume();
        }
    }

    @Override
    protected void onDestroy() {
        control.stopPlay();
        unbindService(connection);
        super.onDestroy();

    }

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
            Toast.makeText(PlayActivity.this, "FileNotFound", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

}


