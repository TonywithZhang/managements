package com.tec.zhang.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tec.zhang.BaseActivity;
import com.tec.zhang.R;
import com.tec.zhang.SimpleDisplay;

import java.io.IOException;
import java.net.URLDecoder;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhang on 2017/2/25.
 */

public class SeekSingle extends Fragment {
    private EditText projNum;
    private FloatingActionButton check;
    private String projDetail;
    private ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.activity_seek_single,container,false);
        projNum = (EditText) v.findViewById(R.id.editText8);
        check = (FloatingActionButton) v.findViewById(R.id.floatingActionButton4);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar5);
        check.setEnabled(false);
        projNum.addTextChangedListener(watcher);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new ShowDetail().execute(Integer.parseInt(projNum.getText().toString()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return v;
    }
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (!projNum.getText().toString().equals("")){
                if (!check.isEnabled()){
                    check.setEnabled(true);
                }
            }else {
                if (check.isEnabled()){
                    check.setEnabled(false);
                }
            }
        }
    };
    private class ShowDetail extends AsyncTask<Integer,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            final boolean[] state = {false};
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().get().url(BaseActivity.SERVER_ADDRESS
                    + "checkProduct?projectNum=" + params[0]).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Toasty.error(getContext(),"查询失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String respond = response.body().string();
                    final String s = URLDecoder.decode(respond, "GBK");
                    projDetail = s;
                    state[0] = true;
                }
            });
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return state[0];
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressBar.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putCharSequence("pro", projDetail);
            Intent intent = new Intent(getContext(), SimpleDisplay.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
