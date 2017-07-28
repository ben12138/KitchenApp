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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.adapter.ProductionCommentAdapter;
import com.jlkj.kitchen.bean.Production;
import com.jlkj.kitchen.bean.ProductionComment;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ProductionInfActivity extends Activity {

    private ProductionCommentAdapter adapter;

    private Context context;
    private ListView productionCommentsListView;
    private ImageView backImageView;
    private EditText commentEditText;
    private TextView sendTextView;

    private Production production;
    private List<ProductionComment> productionComments;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_inf);
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
        production = (Production) intent.getSerializableExtra("production");
        context = this;
        backImageView = findViewById(R.id.back);
        commentEditText = findViewById(R.id.comment);
        sendTextView = findViewById(R.id.send);
        sendTextView.setOnClickListener(sendOnClickListener);
        backImageView.setOnClickListener(backOnClickListener);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        OkGo.post(Net.GETPRODUCTIONCOMMENTACTION_IP)
                .params("productionid",production.getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            productionCommentsListView = findViewById(R.id.production_comments);
                            productionComments = new ArrayList<>();
                            JSONArray array = result.getJSONArray("data");
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                productionComments.add(gson.fromJson(array.getJSONObject(i).toString(),ProductionComment.class));
                            }
                            productionComments.add(new ProductionComment());
                            adapter = new ProductionCommentAdapter(context,R.layout.answer_item,productionComments,production);
                            productionCommentsListView.setAdapter(adapter);
                        }else{
                            productionCommentsListView = findViewById(R.id.production_comments);
                            productionComments = new ArrayList<>();
                            productionComments.add(new ProductionComment());
                            adapter = new ProductionCommentAdapter(context,R.layout.answer_item,productionComments,production);
                            productionCommentsListView.setAdapter(adapter);
                        }
                    }
                });
    }

    private View.OnClickListener backOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ProductionInfActivity.this.finish();
        }
    };

    private View.OnClickListener sendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(commentEditText.getText() == null || commentEditText.getText().toString().trim().equals("")){
                Toast.makeText(context,"请输入文字",Toast.LENGTH_SHORT).show();
            }else{
                sendComment(commentEditText.getText().toString().trim());
            }
        }
    };

    private void sendComment(final String comment){
        final Date date = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        OkGo.post(Net.ADDPRODUCTIONCOMMENTACTION_IP)
                .params("comment.senderid", Global.user.getId())
                .params("comment.productionid",production.getId())
                .params("comment.comment",comment)
                .params("comment.praise",0)
                .params("comment.time",sdf.format(date))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            ProductionComment productionComment = new ProductionComment();
                            productionComment.setSenderid(Global.user.getId());
                            productionComment.setProductionid(production.getId());
                            productionComment.setComment(comment);
                            productionComment.setPraise(0);
                            productionComment.setTime(sdf.format(date));
                            if(productionComments == null){
                                productionComments = new ArrayList<>();
                                productionComments.add(0,productionComment);
                                productionComments.add(0,new ProductionComment());
                            }else{
                                productionComments.add(0,productionComment);
                            }
                            commentEditText.setText("");
                            adapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(context,"发表失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
