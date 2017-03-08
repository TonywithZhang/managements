package com.tec.zhang.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.tec.zhang.AccountData;
import com.tec.zhang.BaseActivity;
import com.tec.zhang.Projectdetails;
import com.tec.zhang.R;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhang on 2017/2/27.
 */

public class AddOneProduct extends Fragment {
    private EditText gaugeNum;
    private EditText gaugeFullName;
    private EditText gaugePro;
    private EditText managerName;
    private FloatingActionButton create;
    private OkHttpClient client;
    private Spinner spinner;
    private View view;
    private final String[] CUSTOMERS = {"弗吉亚","宝马","奔驰","大众","其他"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_create_single_project,container,false);
        init();
        return view;
    }

    private void init() {
        gaugeNum = (EditText) view.findViewById(R.id.editText3);
        gaugeFullName = (EditText) view.findViewById(R.id.editText4);
        gaugePro = (EditText) view.findViewById(R.id.editText5);
        managerName = (EditText) view.findViewById(R.id.editText6);
        spinner = (Spinner) view.findViewById(R.id.spinner1);
        ArrayAdapter<String> list = new ArrayAdapter<String>(getContext(),R.layout.spinner_layout,CUSTOMERS);
        spinner.setAdapter(list);
        create = (FloatingActionButton) view.findViewById(R.id.floatingActionButton2);
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
                    Toasty.warning(getContext(),"对不起，您权限不足以执行此操作",Toast.LENGTH_LONG).show();
                }
            }
        });
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
    private void confirmCreating() {
        AlertDialog.Builder notice = new AlertDialog.Builder(getContext());
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
                String frontFive = b.substring(0,5);
                Pattern pattern = Pattern.compile("\\d{5}");
                if (!pattern.matcher(frontFive).matches()){
                    Toasty.error(getActivity(),"输入错误，请确认项目全名前五位是数字！",Toast.LENGTH_LONG).show();
                    return;
                }
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toasty.success(getContext(),"创建成功",Toast.LENGTH_LONG).show();
                                }
                            });
                            getActivity().finish();
                        }else{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toasty.error(getContext(),"创建失败，请检查相关输入内容",Toast.LENGTH_LONG).show();
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
}
