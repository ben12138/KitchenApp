package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.MainActivity;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.bean.UserInf;
import com.jlkj.kitchen.database.DatabaseHelper;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.jlkj.kitchen.util.Util;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

import static com.jlkj.kitchen.R.id.send;

/**
 * 找回密码分成三步
 * 第一步验证邮箱是否被注册过，即是否是注册邮箱
 * 第二步获取验证码
 * 第三步获取用户信息
 */
public class FindPasswordActivity extends Activity {

    private ImageView backImageView;
    private EditText emailEditText;
    private EditText verificationEditText;
    private TextView checkTextView;
    private TextView sendTextView;
    private ImageView headImageView;
    private TextView usernameTextView;
    private String verification;
    private Context context;
    private LinearLayout userLinearLayout;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
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
        backImageView = findViewById(R.id.back);
        emailEditText = findViewById(R.id.email);
        verificationEditText = findViewById(R.id.verification);
        checkTextView = findViewById(R.id.check);
        sendTextView = findViewById(send);
        headImageView = findViewById(R.id.headimage);
        usernameTextView = findViewById(R.id.username);
        userLinearLayout = findViewById(R.id.user);
        backImageView.setOnClickListener(backOnClickListener);
        checkTextView.setOnClickListener(checkOnClickListener);
        headImageView.setOnClickListener(headImageOnClickListener);
        sendTextView.setOnClickListener(sendOnClickListener);
        dbHelper = new DatabaseHelper(context,"user.db3",null,1);
    }

    private View.OnClickListener backOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FindPasswordActivity.this.finish();
        }
    };

    private View.OnClickListener checkOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(verification.equals("")){
                Toast.makeText(context,"验证码错误",Toast.LENGTH_SHORT).show();
            }
            if(verificationEditText.getText().toString().trim().equals(verification)){
                getUserInf();
            }else{
                Toast.makeText(context,"验证码错误",Toast.LENGTH_SHORT).show();
                verification = "";
            }
        }
    };

    private View.OnClickListener headImageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener sendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(Util.isEmail(emailEditText.getText().toString().trim())){
                confirmEmail();
            }else{
                Toast.makeText(context,"请输入正确格式的邮箱",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void confirmEmail(){
        OkGo.post(Net.FINDPASSWORD_URL)
                .params("step",1)
                .params("user.username",emailEditText.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject json = JSONObject.fromString(s);
                        if(json.getInt("code") == 200){
                            checkTextView.setVisibility(View.VISIBLE);
                            verification = "";
                            getVerification();
                        }else{
                            Toast.makeText(context,"该邮箱未被注册过，请填写注册时使用的邮箱",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context,"验证失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getVerification(){
        OkGo.post(Net.FINDPASSWORD_URL)
                .params("step",2)
                .params("user.email",emailEditText.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject json = JSONObject.fromString(s);
                        if(json.getInt("code") == 200){
                            JSONObject jsonObject = json.getJSONObject("data");
                            verification = jsonObject.getString("verification");
                        }else{
                            Toast.makeText(context,"发送邮件错误，请重新发送",Toast.LENGTH_SHORT).show();
                            verification = "";
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context,"验证失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getUserInf(){
        OkGo.post(Net.FINDPASSWORD_URL)
                .params("step",3)
                .params("user.username",emailEditText.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject json = JSONObject.fromString(s);
                        if(json.getInt("code") == 200){
                            Gson gson = new Gson();
                            UserInf user = gson.fromJson(json.getString("data"),UserInf.class);
                            Global.copy(user);
                            saveUser();
                            if(user.getNickname() == null || user.getNickname().equals("")){
                                usernameTextView.setText(user.getUsername());
                            }else{
                                usernameTextView.setText(user.getNickname());
                            }
                            userLinearLayout.setVisibility(View.VISIBLE);
                        }else{
                            Toast.makeText(context,"获取信息失败",Toast.LENGTH_SHORT).show();
                            verification = "";
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context,"获取信息失败",Toast.LENGTH_SHORT).show();
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
                Global.user.getImgurl(),
                Global.user.getEmail(),
                Global.user.getBirthday(),
                Global.user.getSex()+"",
                Global.user.getCompany(),
                Global.user.getIntroduction()
        });
        db.close();
        dbHelper.close();
    }

}
