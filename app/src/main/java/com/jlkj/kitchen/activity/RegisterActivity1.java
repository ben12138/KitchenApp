package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jlkj.kitchen.R;
import com.jlkj.kitchen.net.Net;
import com.jlkj.kitchen.util.Util;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

public class RegisterActivity1 extends Activity {

    private ImageView backImageView;
    private EditText emailEditText;
    private TextView sendTextView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
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
        sendTextView = findViewById(R.id.send);
        sendTextView.setOnClickListener(sendOnClickListenr);
        backImageView.setOnClickListener(backOnclickListener);
    }

    private View.OnClickListener backOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RegisterActivity1.this.finish();
        }
    };

    private View.OnClickListener sendOnClickListenr = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(Util.isEmail(emailEditText.getText().toString().trim())){
                checkEmail();
            }else{
                Toast.makeText(context,"请输入正确的邮箱",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void checkEmail(){
        OkGo.post(Net.REGISTER_URL)
                .params("step",1)
                .params("email",emailEditText.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            Intent intent = new Intent(context,RegisterActivity2.class);
                            intent.putExtra("email",emailEditText.getText().toString().trim());
                            startActivity(intent);
                        }else{
                            Toast.makeText(context,"该邮箱已被注册",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context,"注册失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
