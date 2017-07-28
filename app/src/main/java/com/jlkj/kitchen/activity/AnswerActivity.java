package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.adapter.AnswerAdapter;
import com.jlkj.kitchen.bean.Answer;
import com.jlkj.kitchen.bean.Question;
import com.jlkj.kitchen.bean.UserInf;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import okhttp3.Call;
import okhttp3.Response;

public class AnswerActivity extends Activity{

    private Question question;
    private ImageView headImageView;
    private Button backButton;
    private TextView usernameTextView;
    private TextView questiontextView;
    private ListView answersListView;

    private EditText answerEditText;
    private TextView sendTextView;

    private List<Answer> answers;

    private Context context;

    private AnswerAdapter adapter;

    private boolean mIsNetworkEnabled = true;
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
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
        Intent intent = getIntent();
        question = (Question) intent.getSerializableExtra("question");
        backButton = findViewById(R.id.back);
        headImageView = findViewById(R.id.headimage);
        usernameTextView = findViewById(R.id.username);
        questiontextView = findViewById(R.id.question_content);
        questiontextView.setText(question.getQuestion());
        answersListView = findViewById(R.id.answers);
        answerEditText = findViewById(R.id.answer);
        sendTextView = findViewById(R.id.send);
        sendTextView.setOnClickListener(sendOnClickListener);
        backButton.setOnClickListener(backOnClickListener);
        getSenderInf(question.getSenderid());
        getAnswers(question.getId());
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what < 0 || msg.what >= answers.size()){
                    answers.get(msg.what).setPraise((answers.get(msg.what).getPraise()+1));
                }
            }
        };
    }

    private void getSenderInf(int id){
        OkGo.post(Net.GETUSERINF_IP)
                .params("id",question.getSenderid())
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
    }

    private void getAnswers(int id){
        OkGo.post(Net.GETANSWERACTION_IP)
                .params("id",id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        answers = new ArrayList<Answer>();
                        if(result.getInt("code") == 200){
                            JSONArray array = result.getJSONArray("data");
                            Log.d("data111",result.getString("data"));
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                answers.add(gson.fromJson(array.get(i).toString(),Answer.class));
                                System.out.println("lalala");
                            }
                            adapter = new AnswerAdapter(context,R.layout.answer_item,answers);
                            answersListView.setAdapter(adapter);
                        }
                    }
                });
    }

    private View.OnClickListener backOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AnswerActivity.this.finish();
        }
    };

    private View.OnClickListener sendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(answerEditText.getText() == null || answerEditText.getText().toString().trim().equals("")){
                Toast.makeText(context,"输入不能为空",Toast.LENGTH_SHORT).show();
            }else{
                final Answer answer = new Answer();
                answer.setAnswer(answerEditText.getText().toString());
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                answer.setTime(sdf.format(new Date()));
                answer.setPraise(0);
                answer.setQuestionid(question.getId());
                answer.setSenderid(Global.user.getId());
                OkGo.post(Net.ADDANSWERACTION_IP)
                        .params("answer.questionid",answer.getQuestionid())
                        .params("answer.senderid",answer.getSenderid())
                        .params("answer.answer",answer.getAnswer())
                        .params("answer.praise",answer.getPraise())
                        .params("answer.time",answer.getTime())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                JSONObject result = JSONObject.fromString(s);
                                if(result.getInt("code") == 200){
                                    answers.add(0,answer);
                                    adapter = new AnswerAdapter(context,R.layout.answer_item,answers);
                                    answersListView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                answerEditText.setText("");
            }
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
                headImageView.setImageBitmap(bitmap);
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
