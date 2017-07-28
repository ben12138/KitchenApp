package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
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
import com.jlkj.kitchen.bean.CourseComment;
import com.jlkj.kitchen.fragment.CourseCommentFragment;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Response;

public class SendCourseCommentActivity extends Activity {

    private Context context;
    private CourseComment comment;

    private ImageView backImageView;
    private TextView sendTextView;
    private EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_course_comment);
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
        sendTextView = findViewById(R.id.send);
        commentEditText = findViewById(R.id.comment);
        backImageView.setOnClickListener(backonClickListener);
        sendTextView.setOnClickListener(sendOnClickListener);
    }

    private View.OnClickListener backonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SendCourseCommentActivity.this.finish();
        }
    };

    private View.OnClickListener sendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(commentEditText.getText() == null || commentEditText.getText().toString().trim().equals("")){
                Toast.makeText(context,"输入不能为空",Toast.LENGTH_SHORT).show();
            }else{
                comment = new CourseComment();
                comment.setPraise(0);
                comment.setSenderid(Global.user.getId());
                comment.setComment(commentEditText.getText().toString().trim());
                comment.setCourseid(Global.course.getId());
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                comment.setTime(sdf.format(date));
                OkGo.post(Net.ADDCOURSECOMMENTACTION_IP)
                        .params("comment.courseid",comment.getCourseid())
                        .params("comment.senderid",comment.getSenderid())
                        .params("comment.comment",comment.getComment())
                        .params("comment.praise",comment.getPraise())
                        .params("comment.time",comment.getTime())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                JSONObject result = JSONObject.fromString(s);
                                if(result.getInt("code") == 200){
                                    Message msg = new Message();
                                    msg.obj = comment;
                                    CourseCommentFragment.handler.sendMessage(msg);
                                    Toast.makeText(context,"发布成功",Toast.LENGTH_SHORT).show();
                                    SendCourseCommentActivity.this.finish();
                                }
                            }
                        });
            }
        }
    };

}
