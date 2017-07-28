package com.jlkj.kitchen.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.adapter.CourseCommentAdapter;
import com.jlkj.kitchen.bean.CourseComment;
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

public class CourseCommentFragment extends Fragment {

    private static ListView commentsListView;
    private static List<CourseComment> comments;
    private static Context context;
    private static CourseCommentAdapter adapter;

    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(comments == null){
                comments = new ArrayList<CourseComment>();
                comments.add((CourseComment)msg.obj);
                adapter = new CourseCommentAdapter(context,R.layout.answer_item,comments);
                commentsListView.setAdapter(adapter);
            }else{
                comments.add(0,(CourseComment) msg.obj);
                adapter = new CourseCommentAdapter(context,R.layout.answer_item,comments);
                commentsListView.setAdapter(adapter);
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_comment,null);
        init(view);
        return view;
    }

    private void init(View view){
        commentsListView = view.findViewById(R.id.comments);
        getComments();
    }

    private void getComments(){
        OkGo.post(Net.GETKITCHENCOMMENTACTION_IP)
                .params("courseid", Global.course.getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            JSONArray array = result.getJSONArray("data");
                            comments = new ArrayList<CourseComment>();
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                comments.add(gson.fromJson(array.getJSONObject(i).toString(),CourseComment.class));
                            }
                            adapter = new CourseCommentAdapter(context,R.layout.answer_item,comments);
                            commentsListView.setAdapter(adapter);
                        }else{
                            Toast.makeText(context,"暂无评论",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
