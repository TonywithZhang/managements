package com.tec.zhang;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.litepal.crud.DataSupport;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Projectdetails extends AppCompatActivity {
    private TextView detail;
    private com.getbase.floatingactionbutton.FloatingActionButton operation;
    private com.getbase.floatingactionbutton.FloatingActionButton pause;
    private OkHttpClient client = new OkHttpClient();
    private int projectNum;
    private String taskCode;
    private  String attendNames;
    private static final String TAG = "Projectdetails";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectdetails);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionsMenu FABMenu = (FloatingActionsMenu) findViewById(R.id.buttonMenu);
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fabs);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        /*final GestureDetectorCompat compat = new GestureDetectorCompat(this,new MyGestureListener(FABMenu));*/
        operation = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.operation);
        pause = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.pause);
        detail = (TextView) findViewById(R.id.text);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String a = bundle.getString("pro");
        attendNames = bundle.getString("attendNames");
        String[] strings = null;
        String realTaskCode = null;
        try {
            if (a != null) {
                strings = a.split("状态码：");
            }
            if (strings != null) {
                taskCode = strings[strings.length - 1] ;
            }
            realTaskCode = taskCode;
            taskCode = (Integer.parseInt(taskCode)/5)*5 +"";
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        projectNum = bundle.getInt("projectNum");
        detail.setText(a);
        AccountData me = DataSupport.findLast(AccountData.class);
        final int right = me.getAccountRight();
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (right >=5) {
                    Log.d(TAG, "onClick: pause按键有用");
                    AlertDialog.Builder notice = new AlertDialog.Builder(Projectdetails.this);
                    notice.setTitle("提示");
                    notice.setMessage("确认暂停该检具项目吗？");
                    notice.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pauseThisGauge(projectNum);
                            }
                    });
                    notice.setNegativeButton("我再想想", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setCancelable(true);
                    notice.show();
                } else {
                    Toasty.error(Projectdetails.this,"对不起，您无权限执行此操作",Toast.LENGTH_LONG).show();
                }
            }
        });
        final String finalRealTaskCode = realTaskCode;
        operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (right>=5) {
                    operateWithStateCode(Integer.parseInt(finalRealTaskCode));
                }else{
                    Toasty.error(Projectdetails.this,"对不起，你无权限执行此操作",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void pauseThisGauge(final int projectNum){
        Request request = new Request.Builder().get().url(BaseActivity.SERVER_ADDRESS + "pauseThisGauge?projectNum=" + projectNum).build();
        final Call call = client.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    if (response!=null){
                        if (response.body().string().equals("1")) {
                            MyTasks my = new MyTasks();
                            my.setStateCode(5);
                            my.updateAll("projNum=? and taskCode=?",projectNum + "",taskCode);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(pause,"操作成功",Snackbar.LENGTH_LONG)
                                            .setAction("好的", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    finish();
                                                }
                                            }).show();
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void operateWithStateCode(int stateCode){
        switch(stateCode){
            case 0:
                divideGauge(projectNum);
                break;
            case 5:
                startDesign(projectNum);
                break;
            /**case 7:
                waitingForConfirm(projectNum);
                break;*/
            case 8:
                finishDesign(projectNum);
                break;
            case 10:
                BookingMaterialStart(projectNum);
                break;
            case 12:
                finishBookingMaterial(projectNum);
                break;
            case 15:
                startDrawing(projectNum);
                break;
            case 17:
                finishDrawing(projectNum);
                break;
            case 20:
                startDrawingCheck(projectNum);
                break;
            case 22:
                finishDrawingCheck(projectNum);
                break;
            case 25:
                deliverDrawing(projectNum);
                break;
            case 30:
                manufactorStart(projectNum);
                break;
            case 32:
                manufactorFinish(projectNum);
                break;
            case 35:
                measureStart(projectNum);
                break;
            case 37:
                measureFinish(projectNum);
                break;
            /**case 40:
                docIconStart(projectNum);
                break;
            case 42:
                docIconFinish(projectNum);
                break;*/
            case 45:
                docAllStart(projectNum);
                break;
            case 50:
                docAllFinish(projectNum);
                break;
            case 55:
                export(projectNum);
                break;
            case 255:
                Toasty.info(Projectdetails.this,"该检具已出货，加入新功能请联系软件作者",Toast.LENGTH_LONG).show();
                break;
            default:
                Toasty.error(Projectdetails.this,"发生未知错误，请联系软件作者",Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 根据名字生成对应的姓名码
     * 省去转换编码的繁琐
     *
     * */
     public static String getNameCode(String na){
        switch (na){
            case "张庆德":
                return "1";
            case "于万红":
                return "2";
            case "张世营":
                return "3";
            case "计瀚宇":
                return "4";
            case "王海洋":
                return "5";
            case "杨海峰":
                return "6";
            case "陈玉荣":
                return "7";
            case "程智":
                return "8";
            case "金祥":
                return "9";
            case "舒博文":
                return "10";
            case "李晓君":
                return "11";
            case "邓云鹏":
                return "12";
            case "邱宜烨":
                return "13";
            case "金本胜":
                return "14";
            case "黄红景":
                return "15";
            case "夏凯":
                return "16";
            case "吕增林":
                return "17";
            case "吴师傅":
                return "18";
            case "张":
                return "19";
            case "赵修银":
                return "20";
            default:return "255";

        }
    }
    /***
     * 以下的方法为对应的流程中的方法
     *
     *
     */
    //分配项目的方法
    public void divideGauge(int i){
        withMasterName("divideGauge");
    }

    private void withMasterName(String method) {
        Intent intent = new Intent(this,PointingMaster.class);
        Bundle bundle = new Bundle();
        bundle.putInt("projectNum",projectNum);
        bundle.putCharSequence("function",method);
        bundle.putCharSequence("taskCode",taskCode);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    //开始设计的方法
    public void startDesign(int i ){
        operateOnServer("startDesign");
        startProgress();
    }

    private void startProgress() {
        MyTasks me = new MyTasks();
        me.setStartTime(System.currentTimeMillis());
        me.setStateCode(8);
        me.updateAll("projNum=? and taskCode=?",projectNum + "",taskCode);
    }
    private void finishProgress() {
        MyTasks me = new MyTasks();
        me.setFinishTime(System.currentTimeMillis());
        me.setDuration((me.getFinishTime() - me.getStartTime()) + "");
        me.setStateCode(10);
        me.updateAll("projNum=? and taskCode=?",projectNum + "",taskCode);
    }
    private void operateOnServer(String method) {
        Request request = new Request.Builder().get().url(BaseActivity.SERVER_ADDRESS +method
                + "?projectNum=" + projectNum).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respond = response.body().string();
                if(respond.equals("1")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(operation,"执行成功，请返回上个窗口刷新列表",Snackbar.LENGTH_LONG)
                                    .setAction("好的", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            finish();
                                        }
                                    }).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.error(Projectdetails.this,"对不起，执行失败，请自行检查细节或通知软件作者",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    //等待确认的方法
    public void waitingForConfirm(int i){
        operateOnServer("waitingForConfirm");
    }
    //完成设计的方法（需要在完成细化之后使用）
    public void finishDesign(int i){
        withMasterName("finishDesign");
    }
    //开始备料的方法
    public void BookingMaterialStart(int i){
        operateOnServer("BookingMaterialStart");
        startProgress();
    }
    //结束备料的方法
    public void finishBookingMaterial(int i){
        withMasterName("finishBookingMaterial");
    }
    //开始出图纸的方法
    public void startDrawing(int i){
        operateOnServer("startDrawing");
        startProgress();
    }
    //结束出图纸的方法
    public void finishDrawing(int i ){
        String[] names = attendNames.split("，");
        Request request = new Request.Builder().get()
                .url(BaseActivity.SERVER_ADDRESS + "finishDrawing" + "?projectNum=" + projectNum
                        + "&nameCode="+ Projectdetails.getNameCode(names[names.length - 2])).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respond = response.body().string();
                if(respond.equals("1")){
                    MyTasks me = new MyTasks();
                    me.setFinishTime(System.currentTimeMillis());
                    me.setDuration((me.getFinishTime() - me.getStartTime()) + "");
                    me.setStateCode(10);
                    me.updateAll("projNum=? and taskCode=?",projectNum + "",taskCode);
                    Snackbar.make(operation,"执行成功",Snackbar.LENGTH_LONG)
                            .setAction("好的", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            }).show();
                }else{
                    Toasty.error(Projectdetails.this,"对不起，执行失败",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    //开始检查图纸的方法
    public void startDrawingCheck(int i){
        operateOnServer("startDrawingCheck");
        startProgress();
    }
    //结束检查图纸的方法
    public void finishDrawingCheck(int i ){
        operateOnServer("finishDrawingCheck");
        startProgress();
    }
    //下发图纸的方法
    public void deliverDrawing(int i){
        operateOnServer("deliverDrawing");
        startProgress();
    }
    //开始装配的方法
    public void manufactorStart(int i){
        withMasterName("manufactorStart");
    }
    //结束装配的方法
    public void manufactorFinish(int i){
        operateOnServer("manufactorFinish");
        startProgress();
    }
    //开始三坐标测量的方法
    public void measureStart(int i){
        operateOnServer("measureStart");
        startProgress();
    }
    //结束三坐标测量的方法
    public void measureFinish(int i){
        operateOnServer("measureFinish");
        startProgress();
    }
    //制作标牌的方法
    public void docIconStart(int i){
        withMasterName("docIconStart");
    }
    //做完标牌的方法
    public void docIconFinish(int i){
        operateOnServer("docIconFinish");
        startProgress();
    }
    //所有文件的方法
    public void docAllStart(int i){
        withMasterName("docAllStart");
    }
    //做完所有文件的方法
    public void docAllFinish(int i){
        operateOnServer("docAllFinish");
        startProgress();
    }
    //出货的方法
    public void export(int i){
        operateOnServer("export");
        startProgress();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
/*    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private View view;
        private int originalTop;
        public MyGestureListener(View view){
            this.view = view;
            originalTop = view.getTop();
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceY) > Math.abs(distanceX)){
                int buttonTop = view.getTop();
                int buttonBottom = view.getBottom();
                boolean isScrollingDown = e1.getRawY() < e2.getRawY();
                if (!ifNeedScroll(isScrollingDown)) return false;
                if (isScrollingDown){
                    view.setTop(buttonTop -(int)Math.abs(distanceY) );
                    view.setBottom(buttonBottom - (int)Math.abs(distanceY));
                }else {
                    view.setTop(buttonTop +(int)Math.abs(distanceY) );
                    view.setBottom(buttonBottom + (int)Math.abs(distanceY));
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
        public boolean ifNeedScroll(boolean isScrollingDown){
            int nowTop = view.getTop();
            if (isScrollingDown && nowTop <= originalTop) return false;
            if (! isScrollingDown) return isInScreen(view);
            return  true;
        }
        private boolean isInScreen(View view){
            int width,height;
            Point p = new Point();
            getWindowManager().getDefaultDisplay().getSize(p);
            width = p.x;
            height = p.y;
            Rect rect = new Rect(0,0,width,height);
            if (! view.getLocalVisibleRect(rect)) return false;
            return true;
        }
    }*/
}
