package com.tec.zhang;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import org.litepal.tablemanager.Connector;

import es.dmoral.toasty.Toasty;

public class welcome extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Connector.getDatabase();
        setContentView(R.layout.activity_welcome);
        if (!isServiceRunning()) {
            Intent intent = new Intent (this,CheckNews.class);
            startService(intent);
            Toasty.success(this, "后台服务启动成功", Toast.LENGTH_SHORT).show();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(welcome.this,LoginActivity.class);
                startActivity(intent);
            }
        },4000);
        Intent intent = new Intent();
        intent.setAction("com.tec.zhang.alarm");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),65*1000,pendingIntent);
    }
}
