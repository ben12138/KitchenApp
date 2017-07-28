package com.jlkj.kitchen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import com.jlkj.kitchen.activity.RankActivity;
import com.jlkj.kitchen.bean.Production;
import com.jlkj.kitchen.bean.Question;
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
 * Created by benrui on 2017/7/12.
 */

public class ProductionAdapter extends ArrayAdapter<Production>{

    private Context context;
    private int resourceId;
    private List<Production> productions;
    private List<Integer> praiselist;

    public ProductionAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Production> productions) {
        super(context, resource, productions);
        this.productions = productions;
        this.resourceId = resource;
        this.context = context;
        praiselist = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        final ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.descriptionTextView = view.findViewById(R.id.description);
            viewHolder.headImageView = view.findViewById(R.id.headimage);
            viewHolder.imgImageView = view.findViewById(R.id.img);
            viewHolder.praiseImageView = view.findViewById(R.id.praise);
            viewHolder.praiseNumTextView = view.findViewById(R.id.praise_num);
            viewHolder.usernameTextView = view.findViewById(R.id.username);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        final Production production = getItem(position);
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
                            if(user.getNickname() == null || user.getNickname().equals("")){
                                viewHolder.usernameTextView.setText(user.getUsername());
                            }else{
                                viewHolder.usernameTextView.setText(user.getNickname());
                            }
                            loadImageAsyncTask(user.getImgurl(),viewHolder.headImageView);
                        }
                    }
                });
        loadImageAsyncTask(production.getImgurl(),viewHolder.imgImageView);
        viewHolder.praiseNumTextView.setText(""+production.getPraise());
        viewHolder.descriptionTextView.setText(production.getDescription());
        viewHolder.praiseImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!praiselist.contains(position)) {
                    viewHolder.praiseImageView
                            .setImageResource(R.drawable.praise_pressed);
                    viewHolder.praiseNumTextView.setText(production.getPraise() + 1 + "");
                    praiselist.add(position);
                    addPraise(production,position);
                }else{
                    Toast.makeText(context, "您已赞过", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private class ViewHolder{
        ImageView imgImageView;
        ImageView headImageView;
        ImageView praiseImageView;
        TextView usernameTextView;
        TextView descriptionTextView;
        TextView praiseNumTextView;
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

    public void addPraise(Production production, final int position) {
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
                        if (result.getInt("code") == 200) {
                            Message msg = new Message();
                            msg.what = position;
                            RankActivity.handler.sendMessage(msg);
                        }
                    }
                });
    }

}
