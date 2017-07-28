package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jlkj.kitchen.R;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

public class RegisterActivity2 extends Activity {

    private ImageView backImageView;
    private TextView emailTextView;
    private EditText verificationEditText;
    private Button checkButton;
    private String verification;

    private String email;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
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
        emailTextView = findViewById(R.id.email);
        verificationEditText = findViewById(R.id.verification);
        checkButton = findViewById(R.id.check);
        checkButton.setOnClickListener(checkOnClickListener);
        backImageView.setOnClickListener(backOnCLickListener);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        emailTextView.setText(email);
        getVerification();
    }

    private View.OnClickListener backOnCLickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RegisterActivity2.this.finish();
        }
    };

    private View.OnClickListener checkOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(verification == null || verification.equals("")){
                Toast.makeText(context,"验证码失效，请重新发送",Toast.LENGTH_SHORT).show();
            }else if(verification.equals(verificationEditText.getText().toString().trim())){
                Intent intent = new Intent(context,RegisterActivity3.class);
                intent.putExtra("email",email);
                startActivity(intent);
            }
        }
    };

    private void getVerification(){
        OkGo.post(Net.REGISTER_URL)
                .params("step",2)
                .params("email",email)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject json = JSONObject.fromString(s);
                        int code = json.getInt("code");
                        if(code == 200){
                            checkButton.setClickable(true);
                            JSONObject jsonObject = json.getJSONObject("data");
                            verification = jsonObject.getString("verification");
                        }else{
                            Toast.makeText(context,"验证码发送失败",Toast.LENGTH_SHORT).show();
                            verification = "";
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context,"验证码发送失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
