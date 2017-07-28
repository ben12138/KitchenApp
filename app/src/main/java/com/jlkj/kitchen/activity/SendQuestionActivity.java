package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jlkj.kitchen.R;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Response;

import static android.R.attr.max;

public class SendQuestionActivity extends Activity {

    private Context context;

    private EditText questionEditTextView;
    private TextView cancelTextView;
    private TextView sendTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_question);
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
        questionEditTextView = findViewById(R.id.question);
        cancelTextView = findViewById(R.id.cancel);
        sendTextView = findViewById(R.id.send);
        cancelTextView.setOnClickListener(cancelOnClickListener);
        sendTextView.setOnClickListener(sendOnClickListener);
    }

    private View.OnClickListener cancelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SendQuestionActivity.this.finish();
        }
    };

    private View.OnClickListener sendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(questionEditTextView.getText().toString().trim() == null || questionEditTextView.getText().toString().trim().equals("")){
                Toast.makeText(context,"输入不能为空",Toast.LENGTH_SHORT).show();
            }else {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                OkGo.post(Net.ADDQUESRTION_IP)
                        .params("question.senderid", Global.user.getId())
                        .params("question.question",questionEditTextView.getText().toString().trim())
                        .params("question.time",sdf.format(date).toString())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                JSONObject result = JSONObject.fromString(s);
                                if(result.getInt("code") == 200){
                                    Toast.makeText(context,"发表成功",Toast.LENGTH_SHORT).show();
                                    SendQuestionActivity.this.finish();
                                }else{
                                    Toast.makeText(context,"发表失败",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SendQuestionActivity.this.finish();
    }
}
