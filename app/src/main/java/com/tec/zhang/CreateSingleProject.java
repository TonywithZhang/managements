package com.tec.zhang;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateSingleProject extends AppCompatActivity {
    private EditText gaugeNum;
    private EditText gaugeFullName;
    private EditText gaugePro;
    private EditText managerName;
    private FloatingActionButton create;
    private OkHttpClient client;
    private Spinner spinner;
    private final String[] CUSTOMERS = {"弗吉亚","宝马","奔驰","大众","其他"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_single_project);
        gaugeNum = (EditText) findViewById(R.id.editText3);
        gaugeFullName = (EditText) findViewById(R.id.editText4);
        gaugePro = (EditText) findViewById(R.id.editText5);
        managerName = (EditText) findViewById(R.id.editText6);
        spinner = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter list = new ArrayAdapter(this,R.layout.spinner_layout,CUSTOMERS);
        spinner.setAdapter(list);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        create = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        create.setEnabled(false);
        AccountData me = DataSupport.findLast(AccountData.class);
        final int right = me.getAccountRight();
        gaugeNum.addTextChangedListener(textWatcher);
        gaugeFullName.addTextChangedListener(textWatcher);
        gaugePro.addTextChangedListener(textWatcher);
        managerName.addTextChangedListener(textWatcher);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (right >=9) {
                    confirmCreating();
                }else{
                    Toast.makeText(CreateSingleProject.this, "您权限不足，无法执行该操作", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void confirmCreating() {
        AlertDialog.Builder notice = new AlertDialog.Builder(this);
        notice.setTitle("提示");
        notice.setMessage("确认创建该检具项目吗？");
        notice.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                client = new OkHttpClient();
                AccountData account = DataSupport.findLast(AccountData.class);
                String name = account.getName();
                String a = gaugeNum.getText().toString();
                String b = gaugeFullName.getText().toString();
                String c = gaugePro.getText().toString();
                String d = Projectdetails.getNameCode(managerName.getText().toString());
                String e = spinner.getSelectedItemPosition() + 1 + "";
                Request request = new Request.Builder().get().url(BaseActivity.SERVER_ADDRESS +"insertProject?userName="
                + name + "&projectNum=" + a + "&projectFullName=" + b + "&nameCode=" + d + 
                        "&projectName=" + c + "&comNum=" + e
                ).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String s = response.body().string();
                        if (s.equals("1")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CreateSingleProject.this, "创建成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                            finish();
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CreateSingleProject.this, "创建失败，请检查相关输入内容", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
        notice.setNegativeButton("再看看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        notice.show();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (gaugeNum.getText().toString().equals("")||gaugeFullName.getText().toString().equals("")
                    ||gaugePro.getText().toString().equals("")
                    ||managerName.getText().toString().equals("")
                    ){
                if (create.isEnabled()){
                    create.setEnabled(false);
                }
            }
            if ((!gaugeNum.getText().toString().equals("")) && (!gaugeFullName.getText().toString().equals(""))
                    && (!gaugePro.getText().toString().equals(""))
                    && (!managerName.getText().toString().equals(""))
                    ){
                if (!create.isEnabled()){
                    create.setEnabled(true);
                }
            }
        }
    
        @Override
        public void afterTextChanged(Editable s) {}
    };
    /**
    private String getNameCode(String na){
        switch (na){
            case "张庆德":
                return "1";
            case "于万红":
                return "2";


            default:return "3";

        }
    }*/
}
