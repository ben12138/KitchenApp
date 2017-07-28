package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static android.os.Environment.getExternalStorageDirectory;

public class LoginActivity extends Activity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private Button findpasswordButton;
    private LinearLayout linearLayout;
    private Context context;
    private String username;
    private String password;

    private DatabaseHelper dbHelper;

    private HttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);
        findpasswordButton = findViewById(R.id.find_password);
        linearLayout = findViewById(R.id.login_user);
        dbHelper = new DatabaseHelper(context,"user.db3",null,1);
        initEvents();
    }

    private void initEvents() {
        Animation anim = AnimationUtils.loadAnimation(context,
                R.anim.login_anim);
        anim.setFillAfter(true);
        usernameEditText.startAnimation(anim);
        passwordEditText.startAnimation(anim);
        loginButton.startAnimation(anim);
        registerButton.startAnimation(anim);
        findpasswordButton.startAnimation(anim);
        linearLayout.startAnimation(anim);
        loginButton.setOnClickListener(loginOnClickListener);
        registerButton.setOnClickListener(registerOnClickListener);
        findpasswordButton.setOnClickListener(findpasswordOnClickListener);
    }

    private View.OnClickListener loginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            username = usernameEditText.getText().toString().trim();
            password = passwordEditText.getText().toString().trim();
            if(username == null || username.equals("")){
                Toast.makeText(context,"用户名不能为空",Toast.LENGTH_SHORT).show();
            }else if(password == null || password.equals("")){
                Toast.makeText(context,"密码不能为空",Toast.LENGTH_SHORT).show();
            }else{
                trylogin();
            }
        }
    };

    private View.OnClickListener registerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,RegisterActivity1.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener findpasswordOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,FindPasswordActivity.class);
            startActivity(intent);
        }
    };

    private void trylogin(){
        OkGo.post(Net.LOGIN_URL)
                .params("user.username",usernameEditText.getText().toString().trim())
                .params("user.password",passwordEditText.getText().toString().trim())
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
                    com.jlkj.kitchen.util.Util.savePhoto(bitmap, Environment.getExternalStorageDirectory()+ "/kitchen/headImage/", username);
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
