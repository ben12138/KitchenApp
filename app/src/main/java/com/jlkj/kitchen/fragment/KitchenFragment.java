package com.jlkj.kitchen.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.activity.AnswerActivity;
import com.jlkj.kitchen.activity.SendQuestionActivity;
import com.jlkj.kitchen.adapter.QuestionAdapter;
import com.jlkj.kitchen.bean.Question;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import okhttp3.Call;
import okhttp3.Response;

public class KitchenFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate{

    private EditText searchEditText;
    private ListView questionsListView;
    private TextView sendQuestionTextView;
    private QuestionAdapter adapter;

    private Context context;

    private BGARefreshLayout mRefreshLayout;

    public static Handler handler;

    private boolean mIsNetworkEnabled = true;

    private List<Question> questions;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kitchen, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        searchEditText = view.findViewById(R.id.search);
        searchEditText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                    if(searchEditText.getText().toString().trim().equals("")){
                        Toast.makeText(context, "关键字不能为空", Toast.LENGTH_SHORT).show();
                    }else{
                        searchQuestions(searchEditText.getText().toString());
                    }
                }
                return false;
            }
        });
        questionsListView = view.findViewById(R.id.questions);
        sendQuestionTextView = view.findViewById(R.id.send_queston);
        sendQuestionTextView.setOnClickListener(questionOnClickListener);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        initRefreshLayout(view);
        getQuestions();
    }

    private View.OnClickListener questionOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, SendQuestionActivity.class);
            context.startActivity(intent);
        }
    };

    private void getQuestions(){
        OkGo.post(Net.GETQUESTION_IP)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        questions = new ArrayList<Question>();
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            JSONArray array = result.getJSONArray("data");
                            for(int i=0;i<array.length();i++){
                                Gson gson = new Gson();
                                questions.add(gson.fromJson(array.getJSONObject(i).toString(),Question.class));
                            }
                            adapter = new QuestionAdapter(context,R.layout.question_item,questions);
                            questionsListView.setAdapter(adapter);
                            questionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(context, AnswerActivity.class);
                                    intent.putExtra("question",questions.get(i));
                                    context.startActivity(intent);
                                }
                            });
                        }
                        mRefreshLayout.endRefreshing();
                    }
                });
    }

    private void searchQuestions(String key){
        OkGo.post(Net.SEARCHQUESTIONACTION_IP)
                .params("key",key)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        questions = new ArrayList<Question>();
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            JSONArray array = result.getJSONArray("data");
                            for(int i=0;i<array.length();i++){
                                Gson gson = new Gson();
                                questions.add(gson.fromJson(array.getJSONObject(i).toString(),Question.class));
                            }
                            adapter = new QuestionAdapter(context,R.layout.question_item,questions);
                            questionsListView.setAdapter(adapter);
                            questionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(context, AnswerActivity.class);
                                    intent.putExtra("question",questions.get(i));
                                    context.startActivity(intent);
                                }
                            });
                        }else{
                            Toast.makeText(context, "暂无搜索结果，换个关键词试试吧。", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    @Override
    public void onBGARefreshLayoutBeginRefreshing(final BGARefreshLayout refreshLayout) {
        // 在这里加载最新数据

        if (mIsNetworkEnabled) {
            // 如果网络可用，则加载网络数据
            getQuestions();

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

}
