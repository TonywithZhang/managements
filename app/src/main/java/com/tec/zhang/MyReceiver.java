package com.tec.zhang;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            /*Intent i = new Intent(context,CheckNews.class);
            context.startService(i);*/
            Intent j = new Intent();
            j.setAction("com.tec.zhang.alarm");
            PendingIntent p = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),65*1000,p);
            Toasty.success(context,"启动后台任务成功", Toast.LENGTH_LONG).show();
        }
    }
}
