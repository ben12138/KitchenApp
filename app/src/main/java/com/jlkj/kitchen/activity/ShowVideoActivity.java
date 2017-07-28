package com.jlkj.kitchen.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.bean.Course;
import com.jlkj.kitchen.bean.CourseVideo;
import com.jlkj.kitchen.fragment.CourseCommentFragment;
import com.jlkj.kitchen.fragment.CourseInfFragment;
import com.jlkj.kitchen.fragment.CourseVideoFragment;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ShowVideoActivity extends AppCompatActivity {

    private Context context;
    private static NiceVideoPlayer mNiceVideoPlayer;
    private Button submitButton;
    private CourseInfFragment courseInfFragment;
    private CourseVideoFragment courseVideoFragment;
    private CourseCommentFragment courseCommentFragment;
    private TextView sendCommentTextView;

    private TextView courseinfTextView;
    private TextView courseVideoTextView;
    private TextView courseCommentTextView;

    private static int PRESSED = 0;
    private static int NORMAL = 1;

    private FragmentManager fragmentManager = null;
    private FragmentTransaction transaction = null;

    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mNiceVideoPlayer.setUp((String)msg.obj, null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
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
        mNiceVideoPlayer = (NiceVideoPlayer) findViewById(R.id.video);
        sendCommentTextView = (TextView)findViewById(R.id.sendcomment);
        sendCommentTextView.setOnClickListener(sendCommentOnClickListener);
        submitButton = (Button) findViewById(R.id.submit);
        getVideo();
        hasCourse();
        courseCommentFragment = new CourseCommentFragment();
        courseInfFragment = new CourseInfFragment();
        courseVideoFragment = new CourseVideoFragment();
        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        if(!courseInfFragment.isAdded()){
            transaction.add(R.id.body_fragment,courseInfFragment);
        }else{
            transaction.hide(courseCommentFragment);
            transaction.hide(courseVideoFragment);
            transaction.show(courseInfFragment);
        }
        transaction.commit();
        courseVideoTextView = (TextView)findViewById(R.id.course_video);
        courseCommentTextView = (TextView)findViewById(R.id.course_comment);
        courseinfTextView = (TextView)findViewById(R.id.course_inf);
        courseVideoTextView.setOnClickListener(courseVideoOnClickListener);
        courseinfTextView.setOnClickListener(courseInfOnClickListener);
        courseCommentTextView.setOnClickListener(courseCommentOnClickListener);
    }

    private View.OnClickListener courseInfOnClickListener =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            changeColor(PRESSED,NORMAL,NORMAL);
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if(!courseInfFragment.isAdded()){
                transaction.add(R.id.body_fragment,courseInfFragment);
            }else{
                transaction.hide(courseCommentFragment);
                transaction.hide(courseVideoFragment);
                transaction.show(courseInfFragment);
            }
            transaction.commit();
        }
    };

    private View.OnClickListener courseVideoOnClickListener =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            changeColor(NORMAL,PRESSED,NORMAL);
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if(!courseVideoFragment.isAdded()){
                transaction.add(R.id.body_fragment,courseVideoFragment);
            }else{
                transaction.hide(courseCommentFragment);
                transaction.hide(courseInfFragment);
                transaction.show(courseVideoFragment);
            }
            transaction.commit();
        }
    };

    private View.OnClickListener courseCommentOnClickListener =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            changeColor(NORMAL,NORMAL,PRESSED);
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if(!courseCommentFragment.isAdded()){
                transaction.add(R.id.body_fragment,courseCommentFragment);
            }else{
                transaction.hide(courseInfFragment);
                transaction.hide(courseVideoFragment);
                transaction.show(courseCommentFragment);
            }
            transaction.commit();
        }
    };

    private void getVideo(){
        OkGo.post(Net.GETKITCHENVIDEOACTION_IP)
                .params("courseid",Global.course.getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            JSONArray array = result.getJSONArray("data");
                            List<CourseVideo> courseVideos = new ArrayList<CourseVideo>();
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                courseVideos.add(gson.fromJson(array.getJSONObject(i).toString(),CourseVideo.class));
                            }
                            mNiceVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_NATIVE); // or NiceVideoPlayer.TYPE_NATIVE
                            mNiceVideoPlayer.setUp(courseVideos.get(0).getCourseurl(), null);
                            TxVideoPlayerController controller = new TxVideoPlayerController(context);
                            controller.setTitle(Global.course.getCoursename());
                            controller.setImage(R.drawable.studio_logo);
                            mNiceVideoPlayer.setController(controller);
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 在onStop时释放掉播放器
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }
    @Override
    public void onBackPressed() {
        // 在全屏或者小窗口时按返回键要先退出全屏或小窗口，
        // 所以在Activity中onBackPress要交给NiceVideoPlayer先处理。
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }

    private void changeColor(int courseInf,int courseVideo,int courseComment){
        if(courseInf == PRESSED){
            courseinfTextView.setTextColor(context.getResources().getColor(R.color.title_background));
        }else{
            courseinfTextView.setTextColor(context.getResources().getColor(R.color.gray));
        }
        if(courseVideo == PRESSED){
            courseVideoTextView.setTextColor(context.getResources().getColor(R.color.title_background));
        }else{
            courseVideoTextView.setTextColor(context.getResources().getColor(R.color.gray));
        }
        if(courseComment == PRESSED){
            courseCommentTextView.setTextColor(context.getResources().getColor(R.color.title_background));
        }else{
            courseCommentTextView.setTextColor(context.getResources().getColor(R.color.gray));
        }
    }

    private void hasCourse(){
        OkGo.post(Net.ISHASCOURSEACTION_IP)
                .params("courseid",Global.course.getId())
                .params("userid",Global.user.getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            submitButton.setText("取消报名");
                            sendCommentTextView.setVisibility(View.VISIBLE);
                            submitButton.setOnClickListener(cancelSubmitOnClickListener);
                        }else{
                            submitButton.setText("立即报名");
                            sendCommentTextView.setVisibility(View.GONE);
                            submitButton.setOnClickListener(addMyCourseOnClickListener);
                        }
                    }
                });
    }

    private View.OnClickListener cancelSubmitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            OkGo.post(Net.CANCELCOURSEACTION_IP)
                    .params("courseid",Global.course.getId())
                    .params("userid",Global.user.getId())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            JSONObject result = JSONObject.fromString(s);
                            if(result.getInt("code") == 200){
                                submitButton.setText("立即报名");
                                sendCommentTextView.setVisibility(View.GONE);
                                submitButton.setOnClickListener(addMyCourseOnClickListener);
                            }else{
                                Toast.makeText(context,"取消报名失败",Toast.LENGTH_SHORT).show();
                                submitButton.setText("取消报名");
                                sendCommentTextView.setVisibility(View.VISIBLE);
                                submitButton.setOnClickListener(cancelSubmitOnClickListener);
                            }
                        }
                    });
        }
    };

    private View.OnClickListener addMyCourseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            OkGo.post(Net.ADDMYCOURSEACTION_IP)
                    .params("courseid",Global.course.getId())
                    .params("userid",Global.user.getId())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            JSONObject result = JSONObject.fromString(s);
                            if(result.getInt("code") == 200){
                                submitButton.setText("取消报名");
                                sendCommentTextView.setVisibility(View.VISIBLE);
                                submitButton.setOnClickListener(cancelSubmitOnClickListener);
                            }else{
                                Toast.makeText(context,"报名失败",Toast.LENGTH_SHORT).show();
                                submitButton.setText("立即报名");
                                sendCommentTextView.setVisibility(View.GONE);
                                submitButton.setOnClickListener(addMyCourseOnClickListener);
                            }
                        }
                    });
        }
    };

    private View.OnClickListener sendCommentOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,SendCourseCommentActivity.class);
            startActivity(intent);
        }
    };

}
