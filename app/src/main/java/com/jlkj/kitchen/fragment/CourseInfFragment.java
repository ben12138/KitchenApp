package com.jlkj.kitchen.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.activity.ShowVideoActivity;
import com.jlkj.kitchen.adapter.Admin;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Response;

public class CourseInfFragment extends Fragment {

    private Admin admin;

    private TextView courseNameTextView;
    private TextView degreeTextView;
    private ImageView headImageView;
    private TextView teacherNameTextView;
    private TextView introductionTextView;
    private TextView courseIntroductionTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_inf_layout,null);
        init(view);
        return view;
    }

    private void init(View view){
        courseNameTextView = view.findViewById(R.id.course_name);
        degreeTextView = view.findViewById(R.id.degree);
        headImageView = view.findViewById(R.id.teacher_headimage);
        teacherNameTextView = view.findViewById(R.id.teacher_name);
        introductionTextView = view.findViewById(R.id.teacher_introduction);
        courseIntroductionTextView = view.findViewById(R.id.course_introduction);
        OkGo.post(Net.GETTEACHERACTION_IP)
                .params("id", Global.course.getTeacherid())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            System.out.println("lalalalalala");
                            Gson gson = new Gson();
                            admin = gson.fromJson(result.getJSONObject("data").toString(),Admin.class);
                            loadImageAsyncTask(admin.getImgurl(),headImageView);
                            courseIntroductionTextView.setText(Global.course.getCoursename());
                            degreeTextView.setText("课程评分："+Global.course.getDegree());
                            courseNameTextView.setText(Global.course.getCoursename());
                            teacherNameTextView.setText(admin.getNickname());
                            introductionTextView.setText(admin.getIntroduction());
                            courseIntroductionTextView.setText(Global.course.getIntroduction());
                        }
                    }
                });
    }

    public void loadImageAsyncTask(final String imgUrl, final ImageView headImageView){
        new AsyncTask<String,Void,Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... strings) {
                return loadImage(imgUrl);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                headImageView.setBackground(new BitmapDrawable(bitmap));
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
