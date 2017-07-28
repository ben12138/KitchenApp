package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.MainActivity;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.bean.UserInf;
import com.jlkj.kitchen.database.DatabaseHelper;
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

import static android.os.Environment.getExternalStorageDirectory;

public class WelcomeActivity extends Activity {

    private ImageView welcomeImageView;
    private DatabaseHelper dbHelper;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
    }

    private void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        context = this;
        dbHelper = new DatabaseHelper(context,"user.db3",null,1);
        welcomeImageView = findViewById(R.id.welcome);
        welcomeImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query("user",null,null,null,null,null,null);
                if(cursor.moveToFirst()){
                    cursor.moveToLast();
                    getUser(cursor);
                }else{
                    Intent intent = new Intent(context,LoginActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }
                db.close();
                dbHelper.close();
            }
        },2000);
    }

    public void getUser(Cursor cursor){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info != null && info.isConnected()){
            //联网状态
            trylogin(cursor.getString(2),cursor.getString(3));
        }else{
            //断网状态
            Global.user.setId(cursor.getInt(1));
            Global.user.setUsername(cursor.getString(2));
            Global.user.setPassword(cursor.getString(3));
            Global.user.setNickname(cursor.getString(4));
            Global.user.setImgurl(cursor.getString(5));
            Global.user.setEmail(cursor.getString(6));
            Global.user.setBirthday(cursor.getString(7));
            Global.user.setSex(cursor.getInt(8));
            Global.user.setCompany(cursor.getString(9));
            Global.user.setIntroduction(cursor.getString(10));
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();
        }
    }
    private void trylogin(String username,String password){
        OkGo.post(Net.LOGIN_URL)
                .params("user.username",username)
                .params("user.password",password)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = new JSONObject(s);
                        int code = result.getInt("code");
                        if(code == 200){
                            Toast.makeText(context,"登录成功",Toast.LENGTH_SHORT).show();
                            Gson gson = new Gson();
                            UserInf user= gson.fromJson(result.getString("data"), UserInf.class);
                            Global.copy(user);
                            saveUser();
                            loadImage(user.getImgurl());
                            Global.user.setImgurl(getExternalStorageDirectory()+ "/kitchen/headImage/"+Global.user.getEmail()+".png");
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            WelcomeActivity.this.finish();
                        }else if(code == 210){
                            Toast.makeText(context,"用户名和或密码错误",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context,"登录失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void saveUser(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.execSQL("insert into user values(null,?,?,?,?,?,?,?,?,?,?)", new String[]{
                Global.user.getId()+"",
                Global.user.getUsername(),
                Global.user.getPassword(),
                Global.user.getNickname(),
                getExternalStorageDirectory()+ "/kitchen/headImage/"+Global.user.getEmail()+".png",
                Global.user.getEmail(),
                Global.user.getBirthday(),
                Global.user.getSex()+"",
                Global.user.getCompany(),
                Global.user.getIntroduction()
        });
        db.close();
        dbHelper.close();
    }

    public void loadImage(final String imageUrl){
        new Thread(){
            public void run() {
                Bitmap bitmap;
                URL url;
                try {
                    url = new URL(imageUrl);
                    InputStream is = url.openStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    com.jlkj.kitchen.util.Util.savePhoto(bitmap, Environment.getExternalStorageDirectory()+ "/Lesson/headImage/", Global.user.getUsername()+".png");
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            };
        }.start();
    }

}
