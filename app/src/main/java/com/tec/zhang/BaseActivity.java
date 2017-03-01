package com.tec.zhang;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * Created by zqd on 2017/1/30.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String INTENT_FINISHBROAD = "com.tec.zhang.finish";
    private String className;
    private static final String TAG = "广播信息";
    private static final String NOTICE = "再点击一点推出本软件！";
    private FinishBroadcastReceiver mFinishBroadcastReceiver;
    private ServiceCheckReceiver receiver;
    public static final String SERVER_ADDRESS = "http://192.168.1.109:8080/OkHttpDemo/";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        className = getClass().getName();
        mFinishBroadcastReceiver = new FinishBroadcastReceiver();
        registerReceiver(mFinishBroadcastReceiver,new IntentFilter(INTENT_FINISHBROAD));
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new ServiceCheckReceiver();
        registerReceiver(receiver,new IntentFilter(Intent.ACTION_TIME_TICK));
        Log.d(TAG, "onCreate: 广播注册成功");
    }

    protected void finishThisActivity(Class c){
        Intent intent = new Intent(INTENT_FINISHBROAD);
        intent.putExtra("name",c.getName());
        sendBroadcast(intent);
    }
    protected void finishAllActivities(){
        Intent intent = new Intent(INTENT_FINISHBROAD);
        intent.putExtra("name","all");
        sendBroadcast(intent);
    }
    private long currentTime =0L;
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - currentTime >2000){
            Toasty.warning(this,NOTICE,Toast.LENGTH_LONG).show();
            currentTime = System.currentTimeMillis();
        }else {
            finishThisActivity(this.getClass());
            finishAllActivities();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver !=null) {
            unregisterReceiver(receiver);
            receiver =null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFinishBroadcastReceiver!=null){
            unregisterReceiver(mFinishBroadcastReceiver);
        }
    }

    protected  class FinishBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (INTENT_FINISHBROAD.equals(intent.getAction())){
                String name = intent.getStringExtra("name");
                if (name.equals(className)||name.equals("all")){
                    finish();
                }
            }
        }
    }
    public boolean isServiceRunning(){
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info: manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.tec.zhang.CheckNews".equals(info.service.getClassName())){
                return true;
            }
        }
        return  false;
    }
    public class ServiceCheckReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: 收到系统每分钟一次的广播");
            if (!isServiceRunning()){
                Intent i = new Intent (context,CheckNews.class);
                context.startService(i);
                Toasty.success(getApplication(),"后台服务启动成功",Toast.LENGTH_LONG).show();
            }
        }
    }
}
