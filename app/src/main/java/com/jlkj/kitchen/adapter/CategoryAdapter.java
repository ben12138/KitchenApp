package com.jlkj.kitchen.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.jlkj.kitchen.MyApplication;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.activity.RankActivity;

import java.util.List;

/**
 * Created by benrui on 2017/7/17.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Integer> categorys;
    private Context context;
    private int type;

    public CategoryAdapter(List<Integer> categorys,Context context,int type){
        this.categorys = categorys;
        this.context = context;
        this.type = type;
    }

    static class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView picimageView;
        View cview;
        public ViewHolder(View view){
            super(view);
            cview = view;
            picimageView = view.findViewById(R.id.pic);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.picimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                Intent intent = new Intent(context, RankActivity.class);
                intent.putExtra("type","category");
                if(type == 1){
                    intent.putExtra("category1",position);
                    intent.putExtra("category2",0);
                    intent.putExtra("category3",0);
                    intent.putExtra("name",MyApplication.category1.get(position));
                }else if(type == 2){
                    intent.putExtra("category1",0);
                    intent.putExtra("category2",position);
                    intent.putExtra("category3",0);
                    intent.putExtra("name",MyApplication.category2.get(position));
                }else{
                    intent.putExtra("category1",0);
                    intent.putExtra("category2",0);
                    intent.putExtra("category3",position);
                    intent.putExtra("name",MyApplication.category3.get(position));
                }
                context.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Integer i = categorys.get(position);
        holder.picimageView.setBackgroundResource(i);
    }

    @Override
    public int getItemCount() {
        return categorys.size();
    }
}
