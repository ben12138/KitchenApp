package com.jlkj.kitchen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.activity.AnswerActivity;
import com.jlkj.kitchen.bean.Answer;
import com.jlkj.kitchen.bean.Comment;
import com.jlkj.kitchen.bean.CourseComment;
import com.jlkj.kitchen.bean.UserInf;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by benrui on 2017/7/10.
 */

public class CourseCommentAdapter extends ArrayAdapter<CourseComment> {

    private int resourceId;
    private Context context;
    private List<Integer> praiselist;

    public CourseCommentAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<CourseComment> comments) {
        super(context, resource, comments);
        this.context = context;
        this.resourceId = resource;
        praiselist = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        final ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.answerTextView = view.findViewById(R.id.answer_content);
            viewHolder.headImageView = view.findViewById(R.id.headimage);
            viewHolder.praiseImageView = view.findViewById(R.id.praise);
            viewHolder.praiseTextView = view.findViewById(R.id.praise_num);
            viewHolder.timeTextView = view.findViewById(R.id.time);
            viewHolder.usernameTextView = view.findViewById(R.id.username);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if(viewHolder != null){
            final CourseComment comment = getItem(position);
            OkGo.post(Net.GETUSERINF_IP)
                    .params("id",comment.getSenderid())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            JSONObject result = JSONObject.fromString(s);
                            if(result.getInt("code") == 200){
                                JSONObject data = result.getJSONObject("data");
                                Gson gson = new Gson();
                                UserInf user = gson.fromJson(data.toString(),UserInf.class);
                                if(user.getNickname() == null || user.getNickname().equals("")){
                                    viewHolder.usernameTextView.setText(user.getUsername());
                                }else{
                                    viewHolder.usernameTextView.setText(user.getNickname());
                                }
                                loadImageAsyncTask(user.getImgurl(),viewHolder.headImageView);
                            }
                        }
                    });
            System.out.println("asdasdasd"+viewHolder);
            Log.d("textview",viewHolder.answerTextView.toString());
            viewHolder.answerTextView.setText(comment.getComment());
            viewHolder.timeTextView.setText(comment.getTime());
            viewHolder.praiseTextView.setText(comment.getPraise()+"");
            viewHolder.praiseImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (!praiselist.contains(position)) {
                        viewHolder.praiseImageView
                                .setImageResource(R.drawable.praise_pressed);
                        viewHolder.praiseTextView.setText(comment.getPraise() + 1 + "");
                        addPraise(comment);
                        praiselist.add(position);
                    }else{
                        Toast.makeText(context, "您已赞过", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return view;
    }

    private class ViewHolder{
        ImageView headImageView;
        TextView usernameTextView;
        TextView timeTextView;
        TextView answerTextView;
        TextView praiseTextView;
        ImageView praiseImageView;
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

    public void addPraise(CourseComment comment){
        OkGo.post(Net.ADDCOURSECOMMENTPRAISEACTION_IP)
                .params("comment.id",comment.getId())
                .params("comment.courseid",comment.getCourseid())
                .params("comment.senderid",comment.getSenderid())
                .params("comment.comment",comment.getComment())
                .params("comment.praise",(1+comment.getPraise()))
                .params("comment.time",comment.getTime())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                    }
                });
    }

}
