package com.jlkj.kitchen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
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
import com.jlkj.kitchen.bean.Question;
import com.jlkj.kitchen.bean.UserInf;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by benrui on 2017/7/10.
 */

public class QuestionAdapter extends ArrayAdapter<Question> {

    private int resourceId;
    private Context context;
    private List<Question> questions;

    public QuestionAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Question> questions) {
        super(context, resource, questions);
        this.context = context;
        this.resourceId = resource;
        this.questions = questions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        final ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.headImageView = view.findViewById(R.id.headimage);
            viewHolder.usernameTextView = view.findViewById(R.id.username);
            viewHolder.questionTextView = view.findViewById(R.id.question_content);
            viewHolder.timeTextView = view.findViewById(R.id.time);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        final Question question = getItem(position);
        viewHolder.questionTextView.setText(question.getQuestion());
        viewHolder.timeTextView.setText(question.getTime());
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
                                viewHolder.usernameTextView.setText(user.getUsername());
                            }else{
                                viewHolder.usernameTextView.setText(user.getNickname());
                            }
                            loadImageAsyncTask(user.getImgurl(),viewHolder.headImageView);
                        }
                    }
                });
        return view;
    }

    private class ViewHolder{
        ImageView headImageView;
        TextView usernameTextView;
        TextView questionTextView;
        TextView timeTextView;
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

}
