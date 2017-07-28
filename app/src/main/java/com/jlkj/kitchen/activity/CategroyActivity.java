package com.jlkj.kitchen.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.*;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jlkj.kitchen.R;
import com.jlkj.kitchen.adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategroyActivity extends AppCompatActivity {

    private Context context;

    private ImageView backImageView;
    private RecyclerView recycleListView1;
    private RecyclerView recycleListView2;
    private RecyclerView recycleListView3;

    private List<Integer> category1s;
    private List<Integer> category2s;
    private List<Integer> category3s;
    CategoryAdapter adapter1;
    CategoryAdapter adapter2;
    CategoryAdapter adapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categroy);
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
        backImageView = (ImageView)findViewById(R.id.back);
        backImageView.setOnClickListener(backOnCLickListener);
        initData();
        recycleListView1 = (RecyclerView) findViewById(R.id.category1);
        recycleListView2 = (RecyclerView) findViewById(R.id.category2);
        recycleListView3 = (RecyclerView) findViewById(R.id.category3);
        StaggeredGridLayoutManager layoutManager1 = new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        StaggeredGridLayoutManager layoutManager3 = new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        recycleListView1.setLayoutManager(layoutManager1);
        recycleListView2.setLayoutManager(layoutManager2);
        recycleListView3.setLayoutManager(layoutManager3);
        adapter1 = new CategoryAdapter(category1s,context,1);
        adapter2 = new CategoryAdapter(category2s,context,2);
        adapter3 = new CategoryAdapter(category3s,context,3);
        recycleListView1.setAdapter(adapter1);
        recycleListView2.setAdapter(adapter2);
        recycleListView3.setAdapter(adapter3);
    }

    private View.OnClickListener backOnCLickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CategroyActivity.this.finish();
        }
    };

    private void initData(){
        category1s = new ArrayList<>();
        category1s.add(R.drawable.lu);
        category1s.add(R.drawable.zhe);
        category1s.add(R.drawable.chuan);
        category1s.add(R.drawable.yue);
        category1s.add(R.drawable.su);
        category1s.add(R.drawable.min);
        category1s.add(R.drawable.xiang);
        category1s.add(R.drawable.hui);
        category1s.add(R.drawable.jia);
        category2s = new ArrayList<>();
        category2s.add(R.drawable.liang);
        category2s.add(R.drawable.re);
        category2s.add(R.drawable.tian);
        category2s.add(R.drawable.tang);
        category2s.add(R.drawable.zhu);
        category2s.add(R.drawable.zi);
        category2s.add(R.drawable.yin);
        category3s = new ArrayList<>();
        category3s.add(R.drawable.shui);
        category3s.add(R.drawable.dan);
        category3s.add(R.drawable.rou);
        category3s.add(R.drawable.nai);
        category3s.add(R.drawable.yan);
        category3s.add(R.drawable.you);
        category3s.add(R.drawable.tiao);
    }

}
