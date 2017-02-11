package com.tec.zhang;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.litepal.tablemanager.Connector;

public class welcome extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Connector.getDatabase();
        setContentView(R.layout.activity_welcome);
        if (!isServiceRunning()) {
            Intent intent = new Intent (this,CheckNews.class);
            startService(intent);
            Toast.makeText(this, "后台服务启动成功", Toast.LENGTH_SHORT).show();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(welcome.this,LoginActivity.class);
                startActivity(intent);
            }
        },4000);
    }
}
