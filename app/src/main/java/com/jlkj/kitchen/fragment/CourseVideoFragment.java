package com.jlkj.kitchen.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.activity.ShowVideoActivity;
import com.jlkj.kitchen.adapter.VideoAdapter;
import com.jlkj.kitchen.bean.CourseVideo;
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

public class CourseVideoFragment extends Fragment {

    private VideoAdapter adapter;
    private ListView videosListView;
    private List<CourseVideo> videos;

    private Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_video,null);
        init(view);
        return view;
    }

    private void init(View view){
        videosListView = view.findViewById(R.id.course_videos);
        getVideos();
    }

    private void getVideos(){
        OkGo.post(Net.GETKITCHENVIDEOACTION_IP)
                .params("courseid", Global.course.getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            JSONArray array = result.getJSONArray("data");
                            videos = new ArrayList<CourseVideo>();
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                videos.add(gson.fromJson(array.getJSONObject(i).toString(),CourseVideo.class));
                            }
                            adapter = new VideoAdapter(context,R.layout.video_item,videos);
                            videosListView.setAdapter(adapter);
                            videosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Message msg = new Message();
                                    msg.obj = videos.get(i).getCourseurl();
                                    ShowVideoActivity.handler.sendMessage(msg);
                                }
                            });
                        }
                    }
                });
    }

}
