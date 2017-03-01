package com.tec.zhang;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PointingMaster extends AppCompatActivity {
    private EditText orderMan;
    private FloatingActionButton confirm;
    private int projectNum;
    private String method;
    private String taskCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointing_master);
        orderMan = (EditText) findViewById(R.id.editText7);
        confirm = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
        orderMan.addTextChangedListener(textWatcher);
        projectNum = getIntent().getExtras().getInt("projectNum");
        method = getIntent().getExtras().getString("function");
        taskCode = getIntent().getExtras().getString("taskCode");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder notice = new AlertDialog.Builder(PointingMaster.this);
                notice.setTitle("提示");
                notice.setMessage("确认更新进度到下一阶段吗？");
                notice.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateToNext(projectNum,orderMan.getText().toString());
                    }
                });
                notice.setNegativeButton("我再想想", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                notice.show();
            }
        });
    }
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (orderMan.getText().toString().equals("")){
                if (confirm.isEnabled()){
                    confirm.setEnabled(false);
                }
            }
            if ((!orderMan.getText().toString().equals(""))){
                if (!confirm.isEnabled()){
                    confirm.setEnabled(true);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
    private void updateToNext(int project,String man){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get()
                .url(BaseActivity.SERVER_ADDRESS + method + "?projectNum=" + project
                        + "&nameCode="+ Projectdetails.getNameCode(man)).build();
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(confirm,"执行成功",Snackbar.LENGTH_LONG)
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
                            Toasty.error(PointingMaster.this,"对不起，执行失败",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
