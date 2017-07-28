package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.adapter.CourseAdapter;
import com.jlkj.kitchen.bean.Course;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class AllCourseActivity extends Activity {

    private Context context;
    private ImageView backImageView;
    private ListView coursesListView;
    private List<Course> courses;

    private CourseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_course);
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
        backImageView.setOnClickListener(backOnClickListener);
        coursesListView = findViewById(R.id.courses);
        Intent intent = getIntent();
        if("mycourse".equals(intent.getStringExtra("type"))){
            getMyData();
        }else{
            getData();
        }
    }

    private View.OnClickListener backOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AllCourseActivity.this.finish();
        }
    };

    private void getData(){
        OkGo.post(Net.GETALLKITCHENACTION_IP)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            courses = new ArrayList<Course>();
                            JSONArray array = result.getJSONArray("data");
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                courses.add(gson.fromJson(array.getJSONObject(i).toString(),Course.class));
                            }
                            adapter = new CourseAdapter(context,R.layout.course_item,courses);
                            coursesListView.setAdapter(adapter);
                            coursesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(context,ShowVideoActivity.class);
                                    Global.course=courses.get(i);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
    }

    private void getMyData(){
        OkGo.post(Net.GETMYCOURSEACTION_IP)
                .params("userid",Global.user.getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            courses = new ArrayList<Course>();
                            JSONArray array = result.getJSONArray("data");
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                courses.add(gson.fromJson(array.getJSONObject(i).toString(),Course.class));
                            }
                            adapter = new CourseAdapter(context,R.layout.course_item,courses);
                            coursesListView.setAdapter(adapter);
                            coursesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(context,ShowVideoActivity.class);
                                    Global.course=courses.get(i);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
    }

}
