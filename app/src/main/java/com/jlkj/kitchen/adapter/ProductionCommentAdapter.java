package com.jlkj.kitchen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.bean.Comment;
import com.jlkj.kitchen.bean.Production;
import com.jlkj.kitchen.bean.ProductionComment;
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
 * Created by benrui on 2017/7/13.
 */

public class ProductionCommentAdapter extends ArrayAdapter<ProductionComment> {

    private int resourceId;
    private Context context;
    private List<ProductionComment> productionComments;
    private Production production;
    private List<Integer> praiseList;

    public ProductionCommentAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ProductionComment> productionComments,Production production) {
        super(context, resource, productionComments);
        this.resourceId = resource;
        this.productionComments = productionComments;
        this.context = context;
        this.production = production;
        praiseList = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        System.out.println(position);
        if(position == 0){
            view = LayoutInflater.from(context).inflate(R.layout.production_comment_title, null);
            final ImageView headImageView = view.findViewById(R.id.headimage);
            ImageView imgImageView = view.findViewById(R.id.img);
            final TextView usernameTextView = view.findViewById(R.id.username);
            TextView descriptionTextView = view.findViewById(R.id.description);
            ImageView praiseImageView = view.findViewById(R.id.praise);
            TextView praiseNumTextView = view.findViewById(R.id.praise_num);
            loadImageAsyncTask(production.getImgurl(),imgImageView);
            OkGo.post(Net.GETUSERINF_IP)
                    .params("id",production.getSenderid())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            JSONObject result = JSONObject.fromString(s);
                            if(result.getInt("code") == 200){
                                JSONObject data = result.getJSONObject("data");
                                Gson gson = new Gson();
                                UserInf user = gson.fromJson(data.toString(),UserInf.class);
                                System.out.println(user.getNickname());
                                System.out.println(usernameTextView);
                                if(user.getNickname() == null || user.getNickname().equals("")){
                                    usernameTextView.setText(user.getUsername());
                                }else{
                                    usernameTextView.setText(user.getNickname());
                                }
                                loadImageAsyncTask(user.getImgurl(),headImageView);
                            }
                        }
                    });
            descriptionTextView = view.findViewById(R.id.description);
            praiseNumTextView = view.findViewById(R.id.praise_num);
            praiseImageView = view.findViewById(R.id.praise);
            descriptionTextView.setText(production.getDescription());
            praiseNumTextView.setText(production.getPraise()+"");
            final ImageView finalPraiseImageView = praiseImageView;
            final TextView finalPraiseNumTextView = praiseNumTextView;
            praiseImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!praiseList.contains(position)) {
                        finalPraiseImageView.setImageResource(R.drawable.praise_pressed);
                        finalPraiseNumTextView.setText(production.getPraise() + 1 + "");
                        praiseList.add(position);
                        addPraise(production,position);
                    }else{
                        Toast.makeText(context, "您已赞过", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            view = LayoutInflater.from(context).inflate(resourceId, null);
            final ProductionComment comment = getItem(position-1);
            TextView commentTextView = view.findViewById(R.id.answer_content);
            final ImageView headImageView = view.findViewById(R.id.headimage);
            final ImageView praiseImageView = view.findViewById(R.id.praise);
            final TextView praiseTextView = view.findViewById(R.id.praise_num);
            TextView timeTextView = view.findViewById(R.id.time);
            final TextView usernameTextView = view.findViewById(R.id.username);
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
                                    usernameTextView.setText(user.getUsername());
                                }else{
                                    usernameTextView.setText(user.getNickname());
                                }
                                loadImageAsyncTask(user.getImgurl(),headImageView);
                            }
                        }
                    });
            commentTextView.setText(comment.getComment());
            timeTextView.setText(comment.getTime());
            praiseTextView.setText(comment.getPraise()+"");
            if (!praiseList.contains(position)) {
                praiseImageView.setImageResource(R.drawable.praise_normal);
            }else{
                praiseImageView.setImageResource(R.drawable.praise_pressed);
            }
            praiseImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (!praiseList.contains(position)) {
                        praiseImageView.setImageResource(R.drawable.praise_pressed);
                        praiseTextView.setText(comment.getPraise() + 1 + "");
                        praiseList.add(position);
                        addPraise(comment,position);
                    }else{
                        Toast.makeText(context, "您已赞过", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return view;
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

    public void addPraise(final Production production, final int position) {
        production.setPraise(1 + production.getPraise());
        OkGo.post(Net.UPDATEPRODUCTIONACTION_IP)
                .params("production.id", production.getId())
                .params("production.senderid", production.getSenderid())
                .params("production.description", production.getDescription())
                .params("production.imgurl", production.getImgurl())
                .params("production.praise", (1 + production.getPraise()))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                    }
                });
    }

    public void addPraise(final ProductionComment comment, final int position) {
        production.setPraise(1 + production.getPraise());
        OkGo.post(Net.UPDATEPRODUCTIONCOMMENTACTION_IP)
                .params("comment.id", comment.getId())
                .params("comment.senderid", comment.getSenderid())
                .params("comment.comment", comment.getComment())
                .params("comment.time", comment.getTime())
                .params("comment.praise", (1 + comment.getPraise()))
                .params("comment.productionid", (1 + comment.getProductionid()))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                    }
                });
    }

}
