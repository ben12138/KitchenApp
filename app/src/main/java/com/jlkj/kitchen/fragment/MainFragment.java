package com.jlkj.kitchen.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.activity.AllCourseActivity;
import com.jlkj.kitchen.activity.CategroyActivity;
import com.jlkj.kitchen.activity.PlusActivity;
import com.jlkj.kitchen.activity.RankActivity;
import com.jlkj.kitchen.activity.SearchMenuActivity;
import com.jlkj.kitchen.activity.ShowVideoActivity;
import com.jlkj.kitchen.bean.Course;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGAMoocStyleRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import okhttp3.Call;
import okhttp3.Response;

public class MainFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate{

    private ImageView plusImageView;
    private TextView searchEditText;
    private ImageView categoryImageView;

    private BGARefreshLayout mRefreshLayout;

    private Context context;

    private boolean mIsNetworkEnabled = true;
    private View view;

    private ImageView menuImageView;
    private ImageView productionImageView;
    private TextView allCourseTextView;

    private ImageView courseimg1ImageView;
    private ImageView courseimg2ImageView;
    private ImageView courseimg3ImageView;
    private ImageView courseimg4ImageView;

    private TextView coursename1TextView;
    private TextView coursename2TextView;
    private TextView coursename3TextView;
    private TextView coursename4TextView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main,null);
        init(view);
        return view;
    }

    private void initRefreshLayout(View view) {
        mRefreshLayout = (BGARefreshLayout) view.findViewById(R.id.scroll);
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(context,false);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);


        // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项  -------------START
        // 设置正在加载更多时不显示加载更多控件
        // mRefreshLayout.setIsShowLoadingMoreView(false);
        // 设置正在加载更多时的文本
        refreshViewHolder.setLoadingMoreText("正在刷新");
        // 设置整个加载更多控件的背景颜色资源 id
        refreshViewHolder.setLoadMoreBackgroundColorRes(R.color.title_background);
        // 设置整个加载更多控件的背景 drawable 资源 id
        refreshViewHolder.setLoadMoreBackgroundDrawableRes(R.drawable.drawer_menu_background);
        // 设置下拉刷新控件的背景颜色资源 id
        refreshViewHolder.setRefreshViewBackgroundColorRes(R.color.background);
        // 设置下拉刷新控件的背景 drawable 资源 id
        refreshViewHolder.setRefreshViewBackgroundDrawableRes(R.drawable.drawer_menu_background);
        // 可选配置  -------------END
    }

    private void init(View view){
        plusImageView = view.findViewById(R.id.plus);
        searchEditText = view.findViewById(R.id.search);
        categoryImageView = view.findViewById(R.id.category);
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchMenuActivity.class);
                context.startActivity(intent);
            }
        });
        plusImageView.setOnClickListener(plusOnClickListener);
        menuImageView = view.findViewById(R.id.menu);
        productionImageView =  view.findViewById(R.id.production);
        menuImageView.setOnClickListener(menuOnClickListener);
        productionImageView.setOnClickListener(productionClickListener);
        courseimg1ImageView =  view.findViewById(R.id.course_img1);
        courseimg2ImageView =  view.findViewById(R.id.course_img2);
        courseimg3ImageView =  view.findViewById(R.id.course_img3);
        courseimg4ImageView =  view.findViewById(R.id.course_img4);
        coursename1TextView = view.findViewById(R.id.course_name1);
        coursename2TextView = view.findViewById(R.id.course_name2);
        coursename3TextView = view.findViewById(R.id.course_name3);
        coursename4TextView = view.findViewById(R.id.course_name4);
        allCourseTextView = view.findViewById(R.id.all_course);
        allCourseTextView.setOnClickListener(allCourseOnClickListener);
        categoryImageView = view.findViewById(R.id.category);
        categoryImageView.setOnClickListener(categoryOnClickListener);
        initRefreshLayout(view);
        getCourseData(null);
    }

    private View.OnClickListener allCourseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, AllCourseActivity.class);
            context.startActivity(intent);
        }
    };

    private View.OnClickListener menuOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, RankActivity.class);
            intent.putExtra("name","menu");
            intent.putExtra("type","all");
            context.startActivity(intent);
        }
    };

    private View.OnClickListener productionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, RankActivity.class);
            intent.putExtra("name","production");
            intent.putExtra("type","all");
            context.startActivity(intent);
        }
    };

    @Override
    public void onBGARefreshLayoutBeginRefreshing(final BGARefreshLayout refreshLayout) {
        // 在这里加载最新数据

        if (mIsNetworkEnabled) {
            // 如果网络可用，则加载网络数据
            getCourseData(refreshLayout);
        } else {
            // 网络不可用，结束下拉刷新
            Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
            refreshLayout.endRefreshing();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        // 在这里加载更多数据，或者更具产品需求实现上拉刷新也可以
        return false;
    }

    // 通过代码方式控制进入正在刷新状态。应用场景：某些应用在 activity 的 onStart 方法中调用，自动进入正在刷新状态获取最新数据
    public void beginRefreshing() {
        mRefreshLayout.beginRefreshing();
    }

    // 通过代码方式控制进入加载更多状态
    public void beginLoadingMore() {
        mRefreshLayout.beginLoadingMore();
    }

    private View.OnClickListener plusOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, PlusActivity.class);
            context.startActivity(intent);
        }
    };

    private View.OnClickListener categoryOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, CategroyActivity.class);
            context.startActivity(intent);
        }
    };

    public void getCourseData(final BGARefreshLayout refreshLayout){
        OkGo.post(Net.GETRECOMMENDCOURSEACTION_IP)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            Gson gson = new Gson();
                            final List<Course> courses = new ArrayList<Course>();
                            JSONArray array = result.getJSONArray("data");
                            for(int i=0;i<array.length();i++){
                                courses.add(gson.fromJson(array.getJSONObject(i).toString(),Course.class));
                            }
                            loadImageAsyncTask(courses.get(0).getCourseimgurl(),courseimg1ImageView);
                            loadImageAsyncTask(courses.get(1).getCourseimgurl(),courseimg2ImageView);
                            loadImageAsyncTask(courses.get(2).getCourseimgurl(),courseimg3ImageView);
                            loadImageAsyncTask(courses.get(3).getCourseimgurl(),courseimg4ImageView);
                            coursename1TextView.setText(courses.get(0).getCoursename());
                            courseimg1ImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, ShowVideoActivity.class);
                                    Global.course = courses.get(0);
                                    context.startActivity(intent);
                                }
                            });
                            coursename2TextView.setText(courses.get(1).getCoursename());
                            courseimg2ImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, ShowVideoActivity.class);
                                    Global.course = courses.get(1);
                                    context.startActivity(intent);
                                }
                            });
                            coursename3TextView.setText(courses.get(2).getCoursename());
                            courseimg3ImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, ShowVideoActivity.class);
                                    Global.course = courses.get(2);
                                    context.startActivity(intent);
                                }
                            });
                            coursename4TextView.setText(courses.get(3).getCoursename());
                            courseimg4ImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, ShowVideoActivity.class);
                                    Global.course = courses.get(3);
                                    context.startActivity(intent);
                                }
                            });
                        }
                        if(refreshLayout != null){
                            refreshLayout.endRefreshing();
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
