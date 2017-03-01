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
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.tec.zhang.fragments.*;
import com.tec.zhang.fragments.SeekSingle;

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
    private TextView engineerJob;
    private CircleImageView engineerPicture;
    private static final String TAG= "Transaction";
    private AllProjects allProjects;
    private com.tec.zhang.fragments.SeekSingle seekSingle;
    private AddOneProduct addOneProduct;
    private FragmentManager fm;
    private FragmentTransaction ft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        allProjects = new AllProjects();
        seekSingle = new SeekSingle();
        addOneProduct = new AddOneProduct();
        if (!allProjects.isAdded()){
            ft.add(R.id.fl,allProjects).commit();
        }else {
            ft.hide(seekSingle).show(allProjects).commit();
        }
        navi = (NavigationView) findViewById(R.id.nav_view);
        View header = navi.getHeaderView(0);
        TextView engineerName = (TextView) header.findViewById(R.id.engineer_name);
        engineerName.setText(DataSupport.findLast(AccountData.class).getRealName());
        engineerJob = (TextView) header.findViewById(R.id.engineer_job);
        String headImage = DataSupport.findLast(AccountData.class).getHeadImange();
        engineerPicture = (CircleImageView) header.findViewById(R.id.engineer_picture);
        if (headImage != null){
            Picasso.with(this).load(new File(headImage)).fit().into(engineerPicture);
        }
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
    }

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
        int id = item.getItemId();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
            case R.id.all_project:
                navi.setCheckedItem(R.id.all_project);
                if (allProjects.isHidden()){
                    reset();
                    ft.hide(seekSingle).hide(addOneProduct).show(allProjects).commit();
                }
                allProjects.showAll();
                break;
            case R.id.manage_project:
                navi.setCheckedItem(R.id.manage_project);
                reset();
                if (addOneProduct.isAdded()){
                    if (addOneProduct.isHidden()) {
                        ft.hide(allProjects).hide(seekSingle).show(addOneProduct).commit();
                    }
                }else{
                    ft.hide(allProjects).hide(seekSingle).add(R.id.fl,addOneProduct).commit();
                }
                /*Intent intent = new Intent(this,CreateSingleProject.class);
                startActivity(intent);*/
                break;
            case R.id.mytask:
                navi.setCheckedItem(R.id.mytask);
                if (allProjects.isHidden()){
                    reset();
                    ft.hide(seekSingle).hide(addOneProduct).show(allProjects).commit();
                }
                allProjects.ownTasks();
                //Intent intent = new Intent(this,MyTask.class);
                //startActivity(intent);
                break;
            case R.id.seek_for:
                navi.setCheckedItem(R.id.seek_for);
                reset();
                if (seekSingle.isAdded()){
                    if (seekSingle.isHidden()) {
                        ft.hide(allProjects).hide(addOneProduct).show(seekSingle).commit();
                    }
                }else {
                    ft.hide(allProjects).hide(addOneProduct).add(R.id.fl,seekSingle).commit();
                }
                /*Intent intent1 = new Intent(this,SeekSingle.class);
                startActivity(intent1);*/
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
    private class UploadImage extends Thread{
        String imagePath;
        UploadImage(String imagePath){
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
    private void reset(){
        ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    }
}
