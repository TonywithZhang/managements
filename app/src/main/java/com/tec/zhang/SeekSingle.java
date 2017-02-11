package com.tec.zhang;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SeekSingle extends AppCompatActivity {
    private EditText projectNum;
    private FloatingActionButton check;
    private OkHttpClient client = new OkHttpClient();
    private ProgressBar progressBar;
    private String projDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_single);
        projectNum = (EditText) findViewById(R.id.editText8);
        check = (FloatingActionButton) findViewById(R.id.floatingActionButton4);
        progressBar = (ProgressBar) findViewById(R.id.progressBar5);
        check.setEnabled(false);
        projectNum.addTextChangedListener(watcher);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShowDetailItem().execute(Integer.parseInt(projectNum.getText().toString()));
            }
        });
    }
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!projectNum.getText().toString().equals("")){
                if (!check.isEnabled()){
                    check.setEnabled(true);
                }
            }else {
                if (check.isEnabled()){
                    check.setEnabled(false);
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
    private class ShowDetailItem extends AsyncTask<Integer,Void,Boolean> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            final Boolean[] state = {false};
            Request request = new Request.Builder().get().url(BaseActivity.SERVER_ADDRESS
                    + "checkProduct?projectNum=" + params[0]).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
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
            Intent intent = new Intent(SeekSingle.this, SimpleDisplay.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
