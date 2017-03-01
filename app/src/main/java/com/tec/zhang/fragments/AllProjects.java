package com.tec.zhang.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tec.zhang.AccountData;
import com.tec.zhang.BaseActivity;
import com.tec.zhang.Projectdetails;
import com.tec.zhang.R;
import com.tec.zhang.Transaction;
import com.tec.zhang.adapter.ItemAdapter;
import com.tec.zhang.adapter.ProjItem;
import com.tec.zhang.adapter.Separator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhang on 2017/2/25.
 */

public class AllProjects extends Fragment {
    private Context mcontext;
    private OkHttpClient client = new OkHttpClient();
    public ItemAdapter adapter;
    private SwipeRefreshLayout srl;
    private ProgressBar progressBar;
    private List<ProjItem> datas=new ArrayList<>();
    private static final String TAG= "Transaction";
    private String projDetail;
    private int  projectNum;
    public String attendNames;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_projects,container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.pro);
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recyclerView);
        adapter = new ItemAdapter(datas,getActivity());
        recycler.setAdapter(adapter);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.addItemDecoration(new Separator(getActivity(), LinearLayoutManager.VERTICAL));
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        adapter.setOnClickListener(new ItemAdapter.OnItemClickListener(){

            @Override
            public void onClick(View v, int position) {
                //int i = Integer.parseInt(datas.get(position).fullNme);
                int i = Integer.parseInt(datas.get(position).fullNme.substring(0,5));
                attendNames = datas.get(position).orderMan;
                projectNum = i;
                new ShowDetailItem().execute(i);
            }
        });
        srl = (SwipeRefreshLayout) view.findViewById(R.id.srl);
        srl.setColorSchemeColors(ContextCompat.getColor(getContext(),R.color.colorAccent));
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                showAll();
                srl.setRefreshing(false);
                Toasty.success(getContext(),"刷新成功",Toast.LENGTH_LONG).show();
            }
        });
        showAll();
        return view;
    }
    private class CheckAll extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Request request= new Request.Builder().get().url(BaseActivity.SERVER_ADDRESS
                    + "seekForAll?userName=zhang&password=00000000").build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String respond = URLDecoder.decode(response.body().string(),"GBK");
                    datas.clear();
                    try {
                        JSONObject jo = new JSONObject(respond);
                        JSONArray ja = jo .getJSONArray("alldatas");
                        for(int i = 0; i<ja.length(); i++){
                            JSONObject joo = ja.getJSONObject(i);
                            ProjItem pi = new ProjItem();
                            switch (joo.getInt("companyNum")){
                                case 1:
                                    pi.imageView = R.drawable.frecia;
                                    break;
                                case 2:
                                    pi.imageView = R.drawable.bmw;
                                    break;
                                case 3:
                                    pi.imageView = R.drawable.benz;
                                    break;
                                case 4:
                                    pi.imageView = R.drawable.dazhong;
                                    break;
                                default:
                                    pi.imageView = R.drawable.b;
                                    break;
                            }
                            pi.state = joo.getString("stateNow");
                            pi.fullNme = joo.getString("projName");
                            String s = joo.getString("attendNames");
                            pi.orderMan = s;
                            datas.add(pi);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "onPostExecute: " + datas.size());
            adapter.notifyDataSetChanged();
        }
    }
    private class ShowDetailItem extends AsyncTask<Integer,Void,Boolean>{
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
                    Log.d(TAG, "onFailure: 服务器无响应");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String respond = response.body().string();
                    projDetail = URLDecoder.decode(respond,"GBK");
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
            bundle.putCharSequence("pro",projDetail);
            bundle.putInt("projectNum",projectNum);
            bundle.putCharSequence("attendNames",attendNames);
            Intent intent = new Intent(getContext(),Projectdetails.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
    public void ownTasks(){
        AccountData me = DataSupport.findLast(AccountData.class);
        String realName = me.getRealName();
        List<ProjItem> myTasks = new ArrayList<>();
        for (ProjItem items:datas){
            String[] names = items.orderMan.split("，");
            String responsibleMan = names[names.length - 1];
            if (realName.equals(responsibleMan)){
                myTasks.add(items);
            }
        }
        if (myTasks.size() != 0) {
            Log.d(TAG, "onNavigationItemSelected: "+ myTasks.size());
            datas.clear();
            for (ProjItem items:myTasks){
                datas.add(items);
            }
            adapter.notifyDataSetChanged();
        } else {
            Toasty.info(getContext(),"您的任务列表为空",Toast.LENGTH_LONG).show();
        }
    }
    public void showAll(){
        new CheckAll().execute();
    }
}
