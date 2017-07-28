package com.jlkj.kitchen.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jlkj.kitchen.R;
import com.jlkj.kitchen.bean.Course;
import com.jlkj.kitchen.bean.CourseVideo;

import java.util.List;

/**
 * Created by benrui on 2017/7/17.
 */

public class VideoAdapter extends ArrayAdapter<CourseVideo> {

    private int resourceId;
    private Context context;

    public VideoAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<CourseVideo> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.videonameTextView = view.findViewById(R.id.video_name);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        CourseVideo video = getItem(position);
        viewHolder.videonameTextView.setText(video.getCoursename());
        return view;
    }

    class ViewHolder{
        TextView videonameTextView;
    }

}
