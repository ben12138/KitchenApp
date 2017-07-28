package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.MyApplication;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.bean.Menu;
import com.jlkj.kitchen.bean.UserInf;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Response;

public class MenuInfActivity extends Activity {

    private Context context;
    private TextView titleTextView;
    private ImageView collectImageView;
    private ImageView backImageView;
    private ImageView coverImageView;
    private TextView categoryTextView;
    private TextView timeTextView;
    private ImageView headImageView;
    private TextView usernameTextView;
    private TextView stroyTextView;
    private TextView foodTextView;
    private TextView step1TextView;
    private TextView step2TextView;
    private TextView step3TextView;
    private ImageView pic1ImageView;
    private ImageView pic2ImageView;
    private ImageView pic3ImageView;
    private boolean isCollected = false;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inf);
        init();
    }

    private void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        Intent intent = getIntent();
        menu = (Menu) intent.getSerializableExtra("menu");
        context = this;
        titleTextView = findViewById(R.id.title);
        collectImageView = findViewById(R.id.collect);
        backImageView = findViewById(R.id.back);
        coverImageView = findViewById(R.id.cover);
        categoryTextView = findViewById(R.id.category);
        headImageView = findViewById(R.id.headimage);
        usernameTextView = findViewById(R.id.username);
        stroyTextView = findViewById(R.id.stroy);
        timeTextView = findViewById(R.id.time);
        foodTextView = findViewById(R.id.food);
        step1TextView = findViewById(R.id.step1);
        step2TextView = findViewById(R.id.step2);
        step3TextView = findViewById(R.id.step3);
        pic1ImageView = findViewById(R.id.pic1);
        pic2ImageView = findViewById(R.id.pic2);
        pic3ImageView = findViewById(R.id.pic3);
        titleTextView.setText(menu.getTitle());
        backImageView.setOnClickListener(backOnClickListener);
        collectImageView.setOnClickListener(collectClickListener);
        loadImageAsyncTask(menu.getCover(),coverImageView);
        loadImageAsyncTask(menu.getPic1(),pic1ImageView);
        loadImageAsyncTask(menu.getPic2(),pic2ImageView);
        loadImageAsyncTask(menu.getPic3(),pic3ImageView);
        OkGo.post(Net.ISCOLLECTEDACTION_IP)
                .params("collection.menuid",menu.getId())
                .params("collection.userid", Global.user.getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result =  JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            collectImageView.setImageResource(R.drawable.collect);
                            isCollected = true;
                        }else{
                            collectImageView.setImageResource(R.drawable.collect_normal);
                            isCollected = false;
                        }
                        collectImageView.setOnClickListener(collectClickListener);
                    }
                });
        OkGo.post(Net.GETUSERINF_IP)
                .params("id",menu.getSenderid())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            JSONObject data = result.getJSONObject("data");
                            Gson gson = new Gson();
                            UserInf user = gson.fromJson(data.toString(),UserInf.class);
                            if(user.getNickname() == null || user.getNickname().equals("")){
                                usernameTextView.setText(user.getUsername());
                            }else{
                                usernameTextView.setText(user.getNickname());
                            }
                            loadImageAsyncTask(user.getImgurl(),headImageView);
                        }
                    }
                });
        step1TextView.setText(menu.getStep1());
        step2TextView.setText(menu.getStep2());
        step3TextView.setText(menu.getStep3());
        timeTextView.setText(menu.getTime());
        StringBuffer sb = new StringBuffer();
        String category1;
        String category2;
        String category3;
        if(menu.getCategory1() == 0){
            category1 = "";
        }else{
            category1 = MyApplication.category1.get(menu.getCategory1()-1);
        }
        if(menu.getCategory2() == 0){
            category2 = "";
        }else{
            category2 = MyApplication.category2.get(menu.getCategory2()-1);
        }
        if(menu.getCategory3() == 0){
            category3 = "";
        }else{
            category3 = MyApplication.category3.get(menu.getCategory3()-1);
        }
        sb.append(category1).append(" | ").append(category2).append(" | ").append(category3);
        categoryTextView.setText(sb.toString());
        stroyTextView.setText(menu.getStory());
        foodTextView.setText(menu.getFood());
    }

    private View.OnClickListener collectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!isCollected){
                OkGo.post(Net.COLLECTACTION_IP)
                        .params("collection.menuid",menu.getId())
                        .params("collection.userid",Global.user.getId())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                JSONObject result = JSONObject.fromString(s);
                                if(result.getInt("code") == 200){
                                    collectImageView.setImageResource(R.drawable.collect);
                                    Toast.makeText(context,"收藏成功，可以在我的收藏中找到",Toast.LENGTH_SHORT).show();
                                    isCollected = true;
                                }else{
                                    collectImageView.setImageResource(R.drawable.collect_normal);
                                    Toast.makeText(context,"收藏失败，请检查网络设置，或稍后重试",Toast.LENGTH_SHORT).show();
                                    isCollected = false;
                                }
                            }
                        });
            }else{
                OkGo.post(Net.CANCELCOLLECTIONACTION)
                        .params("collection.menuid",menu.getId())
                        .params("collection.userid",Global.user.getId())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                JSONObject result = JSONObject.fromString(s);
                                if(result.getInt("code") == 200){
                                    collectImageView.setImageResource(R.drawable.collect_normal);
                                    Toast.makeText(context,"已经取消收藏",Toast.LENGTH_SHORT).show();
                                    isCollected = false;
                                }else{
                                    collectImageView.setImageResource(R.drawable.collect);
                                    Toast.makeText(context,"取消收藏失败，请检查网络设置或者稍后村重试",Toast.LENGTH_SHORT).show();
                                    isCollected = true;
                                }
                            }
                        });
            }
        }
    };

    private View.OnClickListener backOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MenuInfActivity.this.finish();
        }
    };

    public void loadImageAsyncTask(final String imgUrl, final ImageView headImageView){
        new AsyncTask<String,Void,Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... strings) {
                return loadImage(imgUrl);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                headImageView.setBackground(new BitmapDrawable(bitmap));
            }
        }.execute(imgUrl);
    }

    public Bitmap loadImage(String imageUrl){
        Bitmap bitmap;
        URL url;
        try {
            url = new URL(imageUrl);
            InputStream is = url.openStream();
            bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
