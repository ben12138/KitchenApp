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
import com.jlkj.kitchen.bean.Menu;
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
 * Created by benrui on 2017/7/12.
 */

public class MenuAdapter extends ArrayAdapter<Menu>{

    private Context context;
    private int resourceId;
    private List<Menu> menus;

    public MenuAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Menu> menus) {
        super(context, resource, menus);
        this.menus = menus;
        this.resourceId = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        final ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.coverImageView = view.findViewById(R.id.cover);
            viewHolder.headImageView = view.findViewById(R.id.headimage);
            viewHolder.titleTextView = view.findViewById(R.id.title);
            viewHolder.usernameTextView = view.findViewById(R.id.username);
            viewHolder.timeTextView = view.findViewById(R.id.time);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        Menu menu = getItem(position);
        viewHolder.titleTextView.setText(menu.getTitle());
        viewHolder.timeTextView.setText(menu.getTime());
        loadImageAsyncTask(menu.getCover(),viewHolder.coverImageView);
        OkGo.post(Net.GETUSERINF_IP)
                .params("id",menu.getSenderid())
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
        ImageView coverImageView;
        ImageView headImageView;
        TextView titleTextView;
        TextView timeTextView;
        TextView usernameTextView;
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
