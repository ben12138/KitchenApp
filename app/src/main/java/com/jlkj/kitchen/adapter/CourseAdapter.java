package com.jlkj.kitchen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.bean.Course;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by benrui on 2017/7/16.
 */

public class CourseAdapter extends ArrayAdapter<Course> {

    private int resourceId;
    private Context context;
    private List<Course> courses;

    public CourseAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Course> courses) {
        super(context, resource, courses);
        this.context = context;
        this.resourceId = resource;
        this.courses = courses;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        final ViewHolder viewHolder;
        Course course = getItem(position);
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.courseImageView = view.findViewById(R.id.course_img);
            viewHolder.courseIntroTextView = view.findViewById(R.id.course_intro);
            viewHolder.courseNameTextView = view.findViewById(R.id.course_name);
            viewHolder.degreeTextView = view.findViewById(R.id.degree);
            viewHolder.teacherNameTextView = view.findViewById(R.id.teacher_name);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        OkGo.post(Net.GETTEACHERACTION_IP)
                .params("id",course.getTeacherid())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            Gson gson = new Gson();
                            Admin admin = gson.fromJson(result.getJSONObject("data").toString(),Admin.class);
                            viewHolder.teacherNameTextView.setText(admin.getNickname());
                        }
                    }
                });
        loadImageAsyncTask(course.getCourseimgurl(),viewHolder.courseImageView);
        viewHolder.degreeTextView.setText(course.getDegree()+"分");
        viewHolder.courseNameTextView.setText(course.getCoursename());
        viewHolder.courseIntroTextView.setText(course.getIntroduction());
        return view;
    }

    class ViewHolder{
        ImageView courseImageView;
        TextView courseNameTextView;
        TextView courseIntroTextView;
        TextView teacherNameTextView;
        TextView degreeTextView;
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
