package com.tec.zhang;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class CheckNews extends Service {
    private OkHttpClient client;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(runnable,20,20, TimeUnit.SECONDS);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    client = new OkHttpClient();
                    Request request = new Request.Builder().get().url(BaseActivity.SERVER_ADDRESS + "checkNews").build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException{
                            String callBack = URLDecoder.decode(response.body().string(),"GBK");
                            try {
                                JSONObject jo = new JSONObject(callBack);
                                JSONArray ja =jo.getJSONArray("LatestDatas");
                                AccountData ad = DataSupport.findLast(AccountData.class);
                                String att = ad.getRealName();
                                List<MyTasks> lmt = DataSupport.where("stateCode < ?","10").find(MyTasks.class);
                                List<String> numbers = new ArrayList<>();
                                List<String> codes = new ArrayList<>();
                                for (MyTasks m:lmt) {
                                    numbers.add(m.getProjNum());
                                    codes.add(m.getTaskCode());
                                }
                                for (int i = 0 ; i< ja.length();i++){
                                    JSONObject jo1 = ja.getJSONObject(i);
                                    String a = jo1.getString("projNum");
                                    String b= jo1.getString("stateCode");
                                    String[] attendNames = jo1.getString("attendNames").split("，");
                                    for (String string:attendNames
                                         ) {
                                        Log.d(TAG, "onResponse: " + string);
                                    }
                                    String orderMan = attendNames[attendNames.length -1];
                                    Log.d(TAG, "onResponse:" + orderMan);
                                    if (att.equals(orderMan)) {
                                        if (!numbers.contains(a)){
                                            MyTasks my = new MyTasks();
                                            my.setProjNum(a);
                                            my.setTaskCode(b);
                                            my.setStateCode(1);
                                            my.setReceiveTime(System.currentTimeMillis());
                                            my.save();
                                            noticeUser(a,b);
                                        }else{
                                            int count = 0;
                                            for (int j = 0;j<codes.size();j++) {
                                                if(a.equals(numbers.get(j))){
                                                    if (b.equals(codes.get(j))){count++;}
                                                }
                                            }
                                            if(count == 0){
                                                MyTasks my = new MyTasks();
                                                my.setProjNum(a);
                                                my.setTaskCode(b);
                                                my.setStateCode(1);
                                                my.setReceiveTime(System.currentTimeMillis());
                                                my.save();
                                                noticeUser(a,b);
                                            }
                                        }
                                    } else {
                                        if (jo1.getString("attendNames").contains(att)){
                                            if (!numbers.contains(a)){
                                                MyTasks my = new MyTasks();
                                                my.setProjNum(a);
                                                my.setTaskCode(b);
                                                my.setStateCode(1);
                                                my.save();
                                                Log.d(TAG, "包含名字但是不包含项目号");
                                                noticeComponent(a,b);
                                            }else{
                                                int count = 0;
                                                for (int j = 0;j<codes.size();j++) {
                                                    if(a.equals(numbers.get(j))){
                                                        if (b.equals(codes.get(j))){count++;}
                                                    }
                                                }
                                                if (count ==0){
                                                    MyTasks my = new MyTasks();
                                                    my.setProjNum(a);
                                                    my.setTaskCode(b);
                                                    my.setStateCode(1);
                                                    my.save();
                                                    Log.d(TAG, "包含名字和项目号但是状态码不对");
                                                    noticeComponent(a,b);
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch(NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    });
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
        return super.onStartCommand(intent, flags, startId);
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: 后台服务轮询任务执行了一次");
            client = new OkHttpClient();
            Request request = new Request.Builder().get().url(BaseActivity.SERVER_ADDRESS + "checkNews").build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException{
                    String callBack = URLDecoder.decode(response.body().string(),"GBK");
                    try {
                        JSONObject jo = new JSONObject(callBack);
                        JSONArray ja =jo.getJSONArray("LatestDatas");
                        AccountData ad = DataSupport.findLast(AccountData.class);
                        if (ad != null){
                            String att = ad.getRealName();
                            List<MyTasks> lmt = DataSupport.where("stateCode < ?","10").find(MyTasks.class);
                            if (lmt != null){
                                List<String> numbers = new ArrayList<>();
                                List<String> codes = new ArrayList<>();
                                for (MyTasks m:lmt) {
                                    numbers.add(m.getProjNum());
                                    codes.add(m.getTaskCode());
                                }
                                for (int i = 0 ; i< ja.length();i++){
                                    JSONObject jo1 = ja.getJSONObject(i);
                                    String a = jo1.getString("projNum");
                                    String b= jo1.getString("stateCode");
                                    String[] attendNames = jo1.getString("attendNames").split("，");
                                    for (String string:attendNames
                                            ) {
                                        Log.d(TAG, "onResponse: " + string);
                                    }
                                    String orderMan = attendNames[attendNames.length -1];
                                    Log.d(TAG, "onResponse:" + orderMan);
                                    if (att.equals(orderMan)) {
                                        if (!numbers.contains(a)){
                                            MyTasks my = new MyTasks();
                                            my.setProjNum(a);
                                            my.setTaskCode(b);
                                            my.setStateCode(1);
                                            my.setReceiveTime(System.currentTimeMillis());
                                            my.save();
                                            noticeUser(a,b);
                                        }else{
                                            int count = 0;
                                            for (int j = 0;j<codes.size();j++) {
                                                if(a.equals(numbers.get(j))){
                                                    if (b.equals(codes.get(j))){count++;}
                                                }
                                            }
                                            if(count == 0){
                                                MyTasks my = new MyTasks();
                                                my.setProjNum(a);
                                                my.setTaskCode(b);
                                                my.setStateCode(1);
                                                my.setReceiveTime(System.currentTimeMillis());
                                                my.save();
                                                noticeUser(a,b);
                                            }
                                        }
                                    } else {
                                        if (jo1.getString("attendNames").contains(att)){
                                            if (!numbers.contains(a)){
                                                MyTasks my = new MyTasks();
                                                my.setProjNum(a);
                                                my.setTaskCode(b);
                                                my.setStateCode(1);
                                                my.save();
                                                Log.d(TAG, "包含名字但是不包含项目号");
                                                noticeComponent(a,b);
                                            }else{
                                                int count = 0;
                                                for (int j = 0;j<codes.size();j++) {
                                                    if(a.equals(numbers.get(j))){
                                                        if (b.equals(codes.get(j))){count++;}
                                                    }
                                                }
                                                if (count ==0){
                                                    MyTasks my = new MyTasks();
                                                    my.setProjNum(a);
                                                    my.setTaskCode(b);
                                                    my.setStateCode(1);
                                                    my.save();
                                                    Log.d(TAG, "包含名字和项目号但是状态码不对");
                                                    noticeComponent(a,b);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch(NullPointerException e){
                        e.printStackTrace();
                    }
                }
            });
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (client != null){
                client = null;
            }
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public  void noticeComponent(String projNum,String code){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this ,LoginActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        String c = getNotice(projNum,code);
        Notification notice = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentText(c)
                .setContentTitle("您的项目有更新！")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.f))
                .build();
        manager.notify(1,notice);
    }
    public  void noticeUser(String projNum,String code){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this ,LoginActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        String c = grabNotice(projNum,code);
        Notification notice = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentText(c)
                .setContentTitle("您有新的任务！")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.f)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.b))
                .build();
        manager.notify(1,notice);
    }
    public String grabNotice(String projNum,String code){
        switch (Integer.parseInt(code)){
            case TaskCode.DESIGN :
                return "您获得了" + projNum +"的设计任务";
            case TaskCode.BOOKING_MATERIAL_ALL:
                return "您获得了" + projNum +"的备料任务";
            case TaskCode.DRAWING:
                return "您获得了" + projNum +"的图纸任务";
            case TaskCode.DRAWING_CHECKING:
                return "您获得了" + projNum +"的检查图纸任务";
            case TaskCode.MANUFACTOR:
                return "您获得了" + projNum +"的制造任务";
            case TaskCode.ASSEMBLING:
                return "您获得了" + projNum +"的装配任务";
            case TaskCode.MEASURE:
                return "您获得了" + projNum +"的测量任务";
            case TaskCode.DOCUMENT_ALL:
                return "您获得了" + projNum +"的后续文件任务";
            case TaskCode.DOCUMENT_ICON_ONLY:
                return "您获得了" + projNum +"的准备铭牌标识任务";
            case TaskCode.MANAGE_PROJECT:
                return "您获得了" + projNum +"的相关项目的管理任务";
            case TaskCode.OTHER_ONE:
                return "您获得了" + projNum +"的其他任务";
            case TaskCode.OTHER_TWO:
                return "您获得了" + projNum +"的其他任务";
            case TaskCode.OTHER_THREE:
                return "您获得了" + projNum +"的其他任务";
            case TaskCode.OTHER_FOUR:
                return "您获得了" + projNum +"的其他任务";
            default:
                return "您的项目有更新";
        }
    }
    public String getNotice(String projNum,String code){
        switch (Integer.parseInt(code)){
            case TaskCode.DESIGN :
                return  projNum +"已分配，并且开始准备设计";
            case TaskCode.DESIGN_FINISH :
                return  projNum +"已开始设计";
            case TaskCode.BOOKING_MATERIAL_ALL:
                return projNum +"已完成设计工作";
            case TaskCode.BOOKING_MATERIAL_FINISH:
                return projNum +"已开始备料";
            case TaskCode.DRAWING:
                return projNum +"已结束备料";
            case TaskCode.DRAWING_FINISHED:
                return projNum +"已开始出图纸";
            case TaskCode.DRAWING_CHECKING:
                return projNum +"已结束出图纸";
            case TaskCode.CHECKING_FINISHED:
                return projNum +"已开始检查图纸";
            case TaskCode.MANUFACTOR:
                return projNum +"已结束检查图纸";
            case TaskCode.ASSEMBLING:
                return projNum +"图纸已经下发到车间";
            case TaskCode.ASSEMBLING_FINISH:
                return projNum +"检具已经开始组装";
            case TaskCode.MEASURE:
                return projNum +"检具已经结束装配";
            case TaskCode.MEASURE_FINISH:
                return projNum +"检具已经开始测量";
            case TaskCode.DOCUMENT_ALL:
                return  projNum +"检具已经结束测量";
            case TaskCode.DOC_ALL_FINISH:
                return  projNum +"检具已经开始制作后续文件";
            case TaskCode.EXPORT:
                return  projNum +"检具已经结束后续文件";
            case TaskCode.DOCUMENT_ICON_ONLY:
                return projNum +"已开始准备铭牌标识工作";
            case TaskCode.MANAGE_PROJECT:
                return projNum +"已开始相关项目的管理工作";
            case TaskCode.OTHER_ONE:
                return projNum +"已开始其他工作";
            case TaskCode.OTHER_TWO:
                return projNum +"已开始其他工作";
            case TaskCode.OTHER_THREE:
                return projNum +"已开始其他工作";
            case TaskCode.OTHER_FOUR:
                return projNum +"已开始其他工作";
            default:
                return projNum +"您参与的检具进度有更新";
        }
    }
}
