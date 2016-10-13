package lanou.share10_14;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    private SeekBar seekBar;
    private Button btn_start;
    private Button btn_pause;
    private Button btn_reset;
    private MediaPlayer mediaPlayer;
    private TextView et_current;
    private TextView et_total;
    int position = 0;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 1){
                String time = msg.obj.toString();
                et_current.setText(time);
            }
            return true;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_current = (TextView) findViewById(R.id.et_current);
        et_total = (TextView) findViewById(R.id.et_total);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_pause.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_start.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);

        mediaPlayer = new MediaPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
 /*
         *
         * add()方法的四个参数，依次是：
         *
         * 1、组别，如果不分组的话就写Menu.NONE,
         *
         * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单
         *
         * 3、顺序，那个菜单现在在前面由这个参数的大小决定
         *
         * 4、文本，菜单的显示文本
         */

        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "打开文件").setIcon(R.mipmap.ic_launcher);

        // setIcon()方法为菜单设置图标，这里使用的是系统自带的图标，以

        // android.R开头的资源是系统提供的，我们自己提供的资源是以R开头的

        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "保存").setIcon(R.mipmap.ic_launcher);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("Download/*");
                startActivityForResult(i, 100);
                break;

            case Menu.FIRST + 2:


                break;


        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                // Data 的uri
                Uri uri = data.getData();
                Log.d("MainActivity", data.toString());
                try {

                    mediaPlayer.setDataSource(MainActivity.this, uri);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                if (mediaPlayer != null) {


                    mediaPlayer.start();
                    int time = mediaPlayer.getDuration();
                    seekBar.setMax(time);
                    et_total.setText(timeConvert(time / 1000));
                    new Thread(new seekBar(handler)).start();
                    position = mediaPlayer.getCurrentPosition();

//                    seekBar.setProgress(position);
                }
                break;
            case R.id.btn_pause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btn_pause.setText("继续");
                } else {
                    mediaPlayer.start();
                    btn_pause.setText("暂停");
                }

                break;
            case R.id.btn_reset:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(0);
                }

                break;
        }
    }

    private String timeConvert(int time) {
        // time second
        int minute = time / 60;
        String m;
        if (minute < 10)

            m = "0" + minute;
        else
            m = minute + "";
        int second = time % 60;
        String s = second < 10 ? "0" + second : second + "";
        String str = m + ":" + s;
        return  str;
    }

    class seekBar implements Runnable {

        int position = 0;
        Handler handler;
        public seekBar(Handler handler) {
            this.handler = handler;

        }

        public void run() {
            while (mediaPlayer != null) {
                position = mediaPlayer.getCurrentPosition();
//
                seekBar.setProgress(position);
                Message msg = new Message();
                msg.obj = timeConvert(position / 1000);

                msg.what = 1;

                handler.sendMessage(msg);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {

                }
            }

        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // fromUser 是否手动拖, 如果手动, 是true
        Log.d("MainActivity", "fromUser:" + fromUser);
        if(fromUser){

            mediaPlayer.seekTo(progress);
        }
           // et_current.setText(timeConvert(progress / 1000));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
