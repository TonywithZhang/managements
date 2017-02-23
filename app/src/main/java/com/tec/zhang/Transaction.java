package com.tec.zhang;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tec.zhang.adapter.ItemAdapter;
import com.tec.zhang.adapter.ProjItem;
import com.tec.zhang.adapter.Separator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Transaction extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private OkHttpClient client = new OkHttpClient();
    private NavigationView navi;
    private RecyclerView recycler;
    private ItemAdapter adapter;
    private TextView engineerName;
    private TextView engineerJob;
    private CircleImageView engineerPicture;
    private SwipeRefreshLayout srl;
    private ProgressBar progressBar;
    private List<ProjItem> datas=new ArrayList<>();
    private static final String TAG= "Transaction";
    private String projDetail;
    private int  projectNum;
    public String attendNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        navi = (NavigationView) findViewById(R.id.nav_view);
        View header = navi.getHeaderView(0);
        engineerName = (TextView) header.findViewById(R.id.engineer_name);
        engineerName.setText(DataSupport.findLast(AccountData.class).getRealName());
        engineerJob = (TextView) header.findViewById(R.id.engineer_job);
        String headImage = DataSupport.findLast(AccountData.class).getHeadImange();
        engineerPicture = (CircleImageView) header.findViewById(R.id.engineer_picture);
        if (headImage != null){
            Picasso.with(this).load(new File(headImage)).fit().into(engineerPicture);
        }
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.pro);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_list_white_24dp);
        }
        navi.setCheckedItem(R.id.all_project);
        navi.setNavigationItemSelectedListener(this);
        engineerPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPicture = new Intent("android.intent.action.GET_CONTENT");
                pickPicture.setType("image/*");
                startActivityForResult(pickPicture,0x0001);
            }
        });
        //new CheckAll().execute();
        recycler = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new ItemAdapter(datas,Transaction.this);
        recycler.setAdapter(adapter);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.addItemDecoration(new Separator(this,LinearLayoutManager.VERTICAL));
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
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
        srl = (SwipeRefreshLayout) findViewById(R.id.srl);
        srl.setColorSchemeColors(ContextCompat.getColor(Transaction.this,R.color.colorAccent));
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                new CheckAll().execute();
                srl.setRefreshing(false);
                Toast.makeText(Transaction.this, "刷新成功", Toast.LENGTH_SHORT).show();
            }
        });
        new CheckAll().execute();
    }

    /**private void innitdatas() {
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0;i<100;i++){
            ProjItem p = new ProjItem();
            p.imageView = R.drawable.f;
            p.fullNme = random.nextInt(800)+"";
            p.orderMan = random.nextBoolean() + "";
            p.state = random.nextFloat() + "";
            datas.add(p);
        }
    }*/

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home){
            drawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        navi.setCheckedItem(item.getItemId());
        switch(item.getItemId()){
            case R.id.all_project:
                navi.setCheckedItem(R.id.all_project);
                break;
            case R.id.manage_project:
                navi.setCheckedItem(R.id.manage_project);
                Intent intent = new Intent(this,CreateSingleProject.class);
                startActivity(intent);
                break;
            case R.id.mytask:
                navi.setCheckedItem(R.id.mytask);
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
                        Toast.makeText(this, "您的任务列表为空", Toast.LENGTH_SHORT).show();
                }
                //Intent intent = new Intent(this,MyTask.class);
                //startActivity(intent);
                break;
            case R.id.seek_for:
                navi.setCheckedItem(R.id.seek_for);
                Intent intent1 = new Intent(this,SeekSingle.class);
                startActivity(intent1);
                break;
            case R.id.others:
                navi.setCheckedItem(R.id.others);
                Snackbar.make(navi,"相关功能正在完善", Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Transaction.this, "谢谢您的支持，请耐心等待", Toast.LENGTH_LONG).show();
                    }
                }).show();
                break;
            default:break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 0x0001:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT>=19){
                        handleImageAfterKitkat(data);
                    }else{
                        handleImageBeforeKitkat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void handleImageBeforeKitkat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    @TargetApi(19)
    private void handleImageAfterKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        if (imagePath !=null){
            AccountData ad = new AccountData();
            ad.setHeadImange(imagePath);
            ad.updateAll();
            Picasso.with(this).load(new File(imagePath)).fit().into(engineerPicture);
            new UploadImage(imagePath).start();
        }
        else {
            Toast.makeText(this, "发生错误", Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri externalContentUri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(externalContentUri,null,selection,null,null);
        if (cursor !=null && cursor.moveToFirst()){
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return path;
    }

    private class CheckAll extends AsyncTask<Void,Void,Void>{

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
                    final String s = URLDecoder.decode(respond,"GBK");
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
            bundle.putCharSequence("pro",projDetail);
            bundle.putInt("projectNum",projectNum);
            bundle.putCharSequence("attendNames",attendNames);
            Intent intent = new Intent(Transaction.this,Projectdetails.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
    private class UploadImage extends Thread{
        String imagePath;
        public UploadImage(String imagePath){
            this.imagePath = imagePath;
        }
        @Override
        public void run() {
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("userName",DataSupport.findLast(AccountData.class).getName())
                    .addFormDataPart("headImage",imagePath,
                            RequestBody.create(MediaType.parse("application/octet-string"),
                                    new File(imagePath))).build();
            Request request = new Request.Builder().url(BaseActivity.SERVER_ADDRESS +
                    "uploadHeadImage").post(requestBody).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Transaction.this, "上传头像成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}
